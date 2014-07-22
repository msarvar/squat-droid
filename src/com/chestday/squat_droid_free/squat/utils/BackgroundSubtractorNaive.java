package com.chestday.squat_droid_free.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtractorNaive implements BackgroundSubtractor {

	List<Mat> background = new ArrayList<Mat>();
	Mat backgroundMat;
	double threshold;
	
	public BackgroundSubtractorNaive(Mat background, double threshold) {
		backgroundMat = background;
		Core.split(background, this.background);
		this.threshold = threshold;
	}
	
	@Override
	public void subtract(Mat frame, Mat subtracted) {
		List<Mat> frameChannels = new ArrayList<Mat>();
		Core.split(frame, frameChannels);
		
		List<Mat> results = new ArrayList<Mat>();
		subtracted.setTo(new Scalar(0,0,0));
		
		for(int i = 0; i < frameChannels.size(); i++) {
			results.add(MatManager.get("bg_sub_naive_" + i));
			Core.absdiff(background.get(i), frameChannels.get(i), results.get(i));
			Imgproc.threshold(results.get(i), results.get(i), threshold, 255, Imgproc.THRESH_BINARY);
			Core.bitwise_or(subtracted, results.get(i), subtracted);
		}
	}
	
	public void setBackground(Mat newBackground) {
		backgroundMat = newBackground;
		Core.split(newBackground, background);
	}
	
	public Mat getBackground() {
		return backgroundMat;
	}

}
