package com.chestday.squat_droid.squat.utils;

import org.opencv.core.Mat;

import com.chestday.squat_droid.SquatPreferences;
import com.chestday.squat_droid.squat.tracking.SquatPipelineListener;

public class MotionDetector {
	private static final int FRAME_SKIP = 4;
	private int NUM_DIFFERENCES = 8;
	private double MOTION_THRESHOLD = 2;
	
	private FixedQueue<Double> differences;
	private Mat prev;
	private boolean moving = false;
	private int frameCount = 0;
	private SquatPipelineListener listener;
	
	public MotionDetector(Mat initialFrame, SquatPipelineListener listener) {
		MOTION_THRESHOLD = SquatPreferences.getDoubleValue("motion_threshold", 2);
		NUM_DIFFERENCES = SquatPreferences.getIntValue("num_differences", 8);
		
		prev = initialFrame;
		differences = new FixedQueue<Double>(NUM_DIFFERENCES);
		this.listener = listener;
	}
	
	public boolean stationary(Mat frame) {
		if(frameCount % FRAME_SKIP == 0) {
			int pixelDifference = VideoTools.countDifference(frame, prev);
			double difference = 100 * (double)pixelDifference / (double)(frame.cols() * frame.rows());
			differences.add(difference);
			
			System.out.println("SQUAT: diff: " + difference);
			listener.onMotionDetectorValue(difference);
			
			moving = differencesBelowThreshold();
			
			prev = frame;
		}
		
		frameCount++;
		
		return moving;
	}
	
	private boolean differencesBelowThreshold() {
		boolean belowThreshold = differences.size() == NUM_DIFFERENCES;
		for(Double d : differences.getList()) {
			belowThreshold &= d < MOTION_THRESHOLD;
		}
		return belowThreshold;
	}
}
