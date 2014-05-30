package com.chestday.squat_droid.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtractorNaiveHSV implements BackgroundSubtractor {

	List<Mat> background = new ArrayList<Mat>();
	double[] threshold = {40, 40};
	double[] maxVals = {360, 100};
	
	public BackgroundSubtractorNaiveHSV(Mat background) {
		Mat hsv = VideoTools.toHsv(background);
		Core.split(hsv, this.background);
		//this.threshold = threshold;
	}
	
	@Override
	public Mat subtract(Mat frame) {
		Mat hsv = VideoTools.toHsv(frame);
		List<Mat> frameChannels = new ArrayList<Mat>();
		Core.split(hsv, frameChannels);
		
		List<Mat> results = new ArrayList<Mat>();
		Mat result = Mat.zeros(frame.size(), CvType.CV_8U);
		
		for(int i = 0; i < 2; i++) {
			results.add(new Mat());
			Core.absdiff(background.get(i), frameChannels.get(i), results.get(i));
			Imgproc.threshold(results.get(i), results.get(i), threshold[i], maxVals[i], Imgproc.THRESH_BINARY);
			Core.bitwise_or(result, results.get(i), result);
		}

		return result;
	}

}
