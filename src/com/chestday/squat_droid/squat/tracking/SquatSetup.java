package com.chestday.squat_droid.squat.tracking;

import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.utils.BackgroundSubtractor;
import com.chestday.squat_droid.squat.utils.FigureDetector;
import com.chestday.squat_droid.squat.utils.FixedQueue;
import com.chestday.squat_droid.squat.utils.MotionDetector;
import com.chestday.squat_droid.squat.utils.VideoTools;

public class SquatSetup {
	
	private BackgroundSubtractor bg;
	private FigureDetector figureDetector;
	private MotionDetector motionDetector;

	private boolean ready = false;
	
	public SquatSetup(BackgroundSubtractor backgroundSubtractor, Mat initialFrame) {
		bg = backgroundSubtractor;
		motionDetector = new MotionDetector(bg.subtract(initialFrame));
		figureDetector = new FigureDetector();
	}
	
	public void update(Mat frame) {
		Mat foreground = bg.subtract(frame);
		
		if(figureDetector.hasFigure(foreground)) {
			ready = motionDetector.stationary(foreground);
		}
	}
	
	public boolean ready() {
		return ready;
	}
}
