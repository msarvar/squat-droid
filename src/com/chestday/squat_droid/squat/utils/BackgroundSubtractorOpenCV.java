package com.chestday.squat_droid.squat.utils;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG;

public class BackgroundSubtractorOpenCV implements BackgroundSubtractor {
	private BackgroundSubtractorMOG subtractor;
	
	double learningRate = 0.01;
	double history = 100;
	
	public BackgroundSubtractorOpenCV(double learningRate, int history) {
		subtractor = new BackgroundSubtractorMOG(history, 5, 0.7);
		this.learningRate = learningRate;
	}
	
	public Mat subtract(Mat frame) {
		Mat mask = new Mat();
		subtractor.apply(frame, mask, learningRate);
		return mask;
	}
}
