package com.chestday.squat_droid.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtractorNaiveAdaptive implements BackgroundSubtractor {

	BackgroundSubtractorNaive bg;
	
	public BackgroundSubtractorNaiveAdaptive(Mat background, double threshold) {
		bg = new BackgroundSubtractorNaive(background, threshold);
	}
	
	@Override
	public void subtract(Mat frame, Mat subtracted) {
		bg.subtract(frame, subtracted);
		
		// Update our background frame
		Mat background = bg.getBackground();
		Mat rest = MatManager.get("bg_adaptive_rest");
		Imgproc.dilate(subtracted, rest, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
		Core.bitwise_not(rest, rest);
		frame.copyTo(background, rest);
		bg.setBackground(background);
	}

}
