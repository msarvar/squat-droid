package com.chestday.squat_droid_free.squat.tracking;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.chestday.squat_droid_free.squat.model.Model;
import com.chestday.squat_droid_free.squat.model.event.ModelEventManager;
import com.chestday.squat_droid_free.squat.optimization.ModelFitter;
import com.chestday.squat_droid_free.squat.optimization.ModelFitterOptim;
import com.chestday.squat_droid_free.squat.utils.BackgroundSubtractor;
import com.chestday.squat_droid_free.squat.utils.FixedQueue;
import com.chestday.squat_droid_free.squat.utils.MatManager;
import com.chestday.squat_droid_free.squat.utils.Pair;

public class SquatTracker {
	
	private static final int FOOT_MOVEMENT_FRAMES = 6;
	
	private ModelFitter fitter;
	private SquatRepScorer squatScorer;
	private SquatRepCounter sqrc;
	private ModelEventManager modelEventManager;
	private BackgroundSubtractor bg;
	private Model model;
	private FixedQueue<Boolean> footMovement;
	
	public SquatTracker(Model model, ModelEventManager modelEventManager, BackgroundSubtractor backgroundSubtractor) {
		
		squatScorer = new SquatRepScorer(modelEventManager);
		sqrc = new SquatRepCounter(modelEventManager);
		fitter = new ModelFitterOptim();
		//fitter = new ModelFitterManual(width, height);
		bg = backgroundSubtractor;
		footMovement = new FixedQueue<Boolean>(FOOT_MOVEMENT_FRAMES);
		this.model = model;
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		sqrc.start();
		squatScorer.start();
	}
	
	public void stop() {
		squatScorer.stop();
	}
	
	public void update(Mat frame) {
		Mat foreground = MatManager.get("squat_tracker_foreground", frame.rows(), frame.cols(), CvType.CV_8U);
		bg.subtract(frame, foreground);
		
		fitter.fit(model, foreground);
		
		modelEventManager.update(model);
		
		// Check whether the foot has become unstuck - that means we've finished squatting
		double[] footPosition = model.getInitParams();
		int SIZE = 10;
		boolean allPixelsInBoxEmpty = true;
		for(int i = (int)footPosition[1] - SIZE; i < (int)footPosition[1] + SIZE; i++) {
			for(int j = (int)footPosition[0] - SIZE; j < (int)footPosition[0] + SIZE; j++) {
				double[] pix = foreground.get(i, j);
				if(pix != null && pix.length > 0) {
					allPixelsInBoxEmpty &= pix[0] < 1;
				}
			}
		}

		footMovement.add(allPixelsInBoxEmpty);
	}
	
	public boolean finished() {
		boolean finished = footMovement.size() == FOOT_MOVEMENT_FRAMES;
		for(Boolean b : footMovement.getList()) {
			finished &= b;
		}
		return finished;
	}
	
	public int getReps() {
		return sqrc.getReps();
	}
	
	public List<Pair<Double,String>> getScores() {
		return squatScorer.getScores().subList(0, sqrc.getReps());
	}
}
