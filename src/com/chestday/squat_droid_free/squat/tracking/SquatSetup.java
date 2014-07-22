package com.chestday.squat_droid_free.squat.tracking;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.chestday.squat_droid_free.squat.utils.BackgroundSubtractor;
import com.chestday.squat_droid_free.squat.utils.FigureDetector;
import com.chestday.squat_droid_free.squat.utils.MatManager;
import com.chestday.squat_droid_free.squat.utils.MotionDetector;

public class SquatSetup {
	
	private BackgroundSubtractor bg;
	private FigureDetector figureDetector;
	private MotionDetector motionDetector;
	private SquatPipelineListener listener;

	private boolean ready = false;
	
	public SquatSetup(BackgroundSubtractor backgroundSubtractor, Mat initialFrame, SquatPipelineListener listener) {
		bg = backgroundSubtractor;
		Mat initialFrameForeground = MatManager.get("squat_setup_initial_frame_foreground", initialFrame.rows(), initialFrame.cols(), CvType.CV_8U);
		bg.subtract(initialFrame, initialFrameForeground);
		motionDetector = new MotionDetector(initialFrameForeground, listener);
		figureDetector = new FigureDetector();
		this.listener = listener;
	}
	
	public void update(Mat frame) {
		// Cannot use mat manager here as we add mats to the motion detector
		Mat foreground = new Mat(frame.size(), CvType.CV_8U);
		bg.subtract(frame, foreground);
		
		if(figureDetector.hasFigure(foreground)) {
			listener.squatSetupHasFigure();
			ready = motionDetector.stationary(foreground);
		} else {
			listener.squatSetupNotHasFigure();
		}
	}
	
	public boolean ready() {
		return ready;
	}
}
