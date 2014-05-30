package com.chestday.squat_droid.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

public class BackgroundSubtractorNaiveShadow implements BackgroundSubtractor {

	private BackgroundSubtractorNaive bg;
	private BackgroundSubtractorMOG2 bgMog;
	List<Mat> background = new ArrayList<Mat>();
	List<Mat> backgroundHsv = new ArrayList<Mat>();
	double[] maxVals = {360, 100};
	
	public BackgroundSubtractorNaiveShadow(Mat background, double threshold) {
		bg = new BackgroundSubtractorNaive(background, threshold);
		bgMog = new BackgroundSubtractorMOG2();
		Mat rgb = new Mat();
		Imgproc.cvtColor(background, rgb, Imgproc.COLOR_RGBA2RGB);
		bgMog.apply(rgb, new Mat());
		Core.split(background, this.background);
		Mat hsv = VideoTools.toHsv(background);
		Core.split(hsv, this.backgroundHsv);
	}
	
	@Override
	public Mat subtract(Mat frame) {
		Mat foregroundWithShadow = bg.subtract(frame);
		
		Mat shadow = findShadowBgMog(frame);
		Mat result = new Mat();
		Core.subtract(foregroundWithShadow, shadow, result);
		return result;
	}
	
	private Mat findShadowBgMog(Mat frame) {
		Mat rgb = new Mat();
		Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_RGBA2RGB);
		Mat b = new Mat();
		bgMog.apply(rgb, b);
		Mat shadowAbove126 = new Mat();
		Mat shadowBelow128 = new Mat();
		Imgproc.threshold(b, shadowAbove126, 126, 255, Imgproc.THRESH_BINARY);
		Imgproc.threshold(b, shadowBelow128, 128, 255, Imgproc.THRESH_BINARY_INV);
		Mat result = new Mat();
		Core.bitwise_and(shadowBelow128, shadowAbove126, result);
		return result;
	}
	
	private Mat findShadowHsv(Mat frame) {
		List<Mat> frameChannels = new ArrayList<Mat>();
		Core.split(frame, frameChannels);

		Mat result = Mat.ones(frame.size(), CvType.CV_8U);
		for(int i = 0; i < 2; i++) {
			// Calculate the difference between our expected and actual H/S value
			Mat diff = new Mat();
			Core.absdiff(backgroundHsv.get(i), frameChannels.get(i), diff);
			
			// Take pixels that have a difference GREATER than threshold value
			int thresh = 15;
			Imgproc.threshold(diff, diff, thresh, maxVals[i], Imgproc.THRESH_BINARY);
			
			// Negate matrix to give us pixels that have difference LESS than threshhold value
			// Which gives us 1 if the value was as expected, and 0 if not.
			Core.bitwise_not(diff, diff);
			
			// And our shadow predictions together (to make sure H AND S have changed a small amount)
			Core.bitwise_and(result, diff, result);
		}
		
		// Finally only keep parts that have changed by a threshold in their brightness (V part of HSV)
		Mat diff = new Mat();
		Core.absdiff(backgroundHsv.get(2), frameChannels.get(2), diff);
		int thresh = 10;
		Imgproc.threshold(diff, diff, thresh, 100, Imgproc.THRESH_BINARY);
		Core.bitwise_and(result, diff, result);
		
		return result;
	}
	
	private Mat findShadow(Mat frame) {
		List<Mat> frameChannels = new ArrayList<Mat>();
		Mat hsv = VideoTools.toHsv(frame);
		Core.split(frame, frameChannels);
		
		Mat redFactor = new Mat();
		Core.divide(frameChannels.get(0), background.get(0), redFactor);
		
		Mat result = Mat.ones(frame.size(), CvType.CV_8U);
		for(int i = 1; i < 3; i++) {
			// Calculate our expected green/blue values based on red change factor
			Mat expected = new Mat();
			Core.multiply(background.get(i), redFactor, expected);
			
			// Calculate the difference between our expected and actual channel values
			Mat diff = new Mat();
			Core.absdiff(expected, frameChannels.get(i), diff);
			
			// Take pixels that have a difference GREATER than threshold value
			int thresh = 15;
			Imgproc.threshold(diff, diff, thresh, 255, Imgproc.THRESH_BINARY);
			
			// Negate matrix to give us pixels that have difference LESS than threshhold value
			// Which gives us 1 if the value was as expected, and 0 if not.
			Core.bitwise_not(diff, diff);
			
			// And our shadow predictions together (to make sure green AND blue are as expected for shadow)
			Core.bitwise_and(result, diff, result);
		}
		
		return result;
	}

}
