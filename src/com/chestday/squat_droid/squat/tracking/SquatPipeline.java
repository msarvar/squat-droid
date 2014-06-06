package com.chestday.squat_droid.squat.tracking;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.chestday.squat_droid.SquatPreferences;
import com.chestday.squat_droid.squat.model.AngularModel;
import com.chestday.squat_droid.squat.model.Model;
import com.chestday.squat_droid.squat.model.event.ModelEventListener;
import com.chestday.squat_droid.squat.model.event.ModelEventManager;
import com.chestday.squat_droid.squat.model.event.ModelEventType;
import com.chestday.squat_droid.squat.optimization.ModelFitter;
import com.chestday.squat_droid.squat.optimization.ModelInitialisationFitterOptim;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractor;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractorLargestObject;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractorNaive;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractorNaiveShadow;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractorOpenCV;
import com.chestday.squat_droid.squat.utils.MatManager;
import com.chestday.squat_droid.squat.utils.MotionDetector;
import com.chestday.squat_droid.squat.utils.Value;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.VideoTools;

public class SquatPipeline {
	private static final int INIT_FITTING_ITERATIONS = 3;
	
	private static final int DISPLAY_MODE_NORMAL_VIDEO = 1;
	private static final int DISPLAY_MODE_NO_BACKGROUND = 2;
	
	private VideoInput videoInput;
	private VideoDisplay videoDisplay;
	private SquatPipelineListener listener;
	
	public SquatPipeline(VideoInput videoInput, VideoDisplay videoDisplay, SquatPipelineListener listener) {
		this.videoInput = videoInput;
		this.videoDisplay = videoDisplay;
		this.listener = listener;
	}
	
	public void process() {
		final Scalar modelColour = new Scalar(255,255,255);
		
		Mat firstFrame = MatManager.get("pipeline_first_frame");
		if(videoInput.hasNextFrame()) {
			videoInput.getNextFrame(firstFrame);
		}

		int bgThreshold = SquatPreferences.getIntValue("background_threshold");
		
		BackgroundSubtractor bg;
		if(SquatPreferences.getBooleanValue("remove_shadows")) {
			bg = new BackgroundSubtractorNaiveShadow(firstFrame, bgThreshold);
		} else {
			bg = new BackgroundSubtractorNaive(firstFrame, bgThreshold);
		}
		
		if(SquatPreferences.getBooleanValue("largest_object")) {
			bg = new BackgroundSubtractorLargestObject(firstFrame, bg);
		}
		
		SquatSetup squatSetup = new SquatSetup(bg, firstFrame, listener);
		Mat readyFrame = MatManager.get("pipeline_ready_frame");
		while(!squatSetup.ready() && videoInput.hasNextFrame()) {
			videoInput.getNextFrame(readyFrame);
			videoDisplay.show(readyFrame);
			videoDisplay.draw();
			squatSetup.update(readyFrame);
		}
		
		listener.onReadyToSquat();
		
		// We have got to the point where the lifter is ready to squat
		
		// Find where to put the model and how large to make it
		Mat readyForeground = MatManager.get("squat_pipeline_ready_foreground", readyFrame.rows(), readyFrame.cols(), CvType.CV_8U);
		bg.subtract(readyFrame, readyForeground);
		
		// Calculate the height and centre point of the foreground blob - ie. the figure
		MatOfPoint figureContours = VideoTools.largestObject(readyForeground);
		Rect figureBound = Imgproc.boundingRect(figureContours);
		int figureHeight = figureBound.height;
		Point figureCentre = new Point(figureBound.x + figureBound.width / 2, figureBound.y + figureBound.height / 2);
		
		// Initialise the model in a sensible location
		AngularModel model = new AngularModel(figureCentre.x + figureBound.width / 2, figureCentre.y + figureHeight / 2);
		Mat modelScaleMat = MatManager.get("squat_pipeline_model_scale_mat", readyForeground.rows(), readyForeground.cols(), readyForeground.type());
		model.draw(modelScaleMat);
		
		// Calculate the height of the model
		MatOfPoint modelContours = VideoTools.largestObject(modelScaleMat);
		Rect modelBound = Imgproc.boundingRect(modelContours);
		int modelHeight = modelBound.height;
		
		// Calculate the scaling factor to best fit the model to the figure
		double scale = (double)figureHeight / (double)modelHeight;
		model.setScale(scale);
		
		// Initial fitting!!
		ModelFitter initFit = new ModelInitialisationFitterOptim();
		
		if(videoInput.hasNextFrame()) {
			videoInput.getNextFrame(readyFrame);
		}
		
		Mat initFitBackground = MatManager.get("squat_pipeline_init_fit_background", readyFrame.rows(), readyFrame.cols(), CvType.CV_8U);
		for(int i = 0; i < INIT_FITTING_ITERATIONS; i++) {
			bg.subtract(readyFrame, initFitBackground);
			initFit.fit(model, initFitBackground);
		}
		
		listener.onInitialModelFit();
		
		// We have the initial model fitted
		// Start the main squat analysis
		
		final ModelEventManager modelEventManager = new ModelEventManager();
		final Value<Boolean> drawWeightDistroLine = new Value<Boolean>();
		drawWeightDistroLine.set(false);
		
		modelEventManager.addListener(ModelEventType.SQUAT_BELOW_PARALLEL_START, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{0, 255, 0});
				listener.onSquatBelowParallel();
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_BELOW_PARALLEL_END, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{255, 255, 255});
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_START, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{0, 255, 0});
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_END, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{255, 255, 255});
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_WEIGHT_DISTRIBUTION_START, new ModelEventListener() {
			public void onEvent(Model m) {
				drawWeightDistroLine.set(true);
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_WEIGHT_DISTRIBUTION_END, new ModelEventListener() {
			public void onEvent(Model m) {
				drawWeightDistroLine.set(false);
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_ASCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				listener.onAscendStart();
			}
		});
		
		SquatTracker squatTracker = new SquatTracker(model, modelEventManager, bg);
		squatTracker.start();
		
		int displayMode = SquatPreferences.getIntValue("display_mode");

		Mat frame = MatManager.get("pipeline_main_frame");
		// Main loop
		while(videoInput.hasNextFrame() && !squatTracker.finished()) {
			videoInput.getNextFrame(frame);
			
			squatTracker.update(frame);
			
			// Choose what to show depending on preferences
			if(displayMode == DISPLAY_MODE_NO_BACKGROUND) {
				Mat noBgFrame = MatManager.get("squat_pipeline_no_bg_frame", frame.rows(), frame.cols(), frame.type());
				noBgFrame.setTo(new Scalar(0,0,0));
				Mat foreground = MatManager.get("squat_pipeline_foreground", frame.rows(), frame.cols(), CvType.CV_8U);
				bg.subtract(frame, foreground);
				frame.copyTo(noBgFrame, foreground);
				noBgFrame.copyTo(frame);
			}
			
			model.drawSkeleton(frame, modelColour);
			
			if(drawWeightDistroLine.get()) {
				model.drawWeightDistributionLine(frame, new Scalar(255,0,0));
			}

			videoDisplay.show(frame);
			videoDisplay.draw();
		}
		
		squatTracker.stop();
		
		listener.onSquatsComplete(squatTracker.getScores());		
	}
}
