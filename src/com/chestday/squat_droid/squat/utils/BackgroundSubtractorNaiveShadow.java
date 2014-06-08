package com.chestday.squat_droid.squat.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

public class BackgroundSubtractorNaiveShadow implements BackgroundSubtractor {

	private BackgroundSubtractorNaive bg;
	private BackgroundSubtractorMOG2 bgMog;
	
	public BackgroundSubtractorNaiveShadow(Mat background, double threshold) {
		bg = new BackgroundSubtractorNaive(background, threshold);
		bgMog = new BackgroundSubtractorMOG2();
		Mat rgb = MatManager.get("bg_sub_shadow_rgb_constructor");
		Imgproc.cvtColor(background, rgb, Imgproc.COLOR_RGBA2RGB);
		bgMog.apply(rgb, new Mat());
	}
	
	@Override
	public void subtract(Mat frame, Mat subtracted) {
		bg.subtract(frame, subtracted);
		
		Mat shadow = MatManager.get("bg_sub_shadow");
		findShadowBgMog(frame, shadow);
		Core.subtract(subtracted, shadow, subtracted);
	}
	
	private void findShadowBgMog(Mat frame, Mat shadow) {
		Mat rgb = MatManager.get("bg_sub_shadow_rgb");
		Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_RGBA2RGB);
		Mat b = MatManager.get("bg_sub_shadow_b");
		bgMog.apply(rgb, b);
		Mat shadowAbove126 = MatManager.get("bg_sub_shadow_above_126");
		Mat shadowBelow128 = MatManager.get("bg_sub_shadow_below_128");
		
		Imgproc.threshold(b, shadowAbove126, 126, 255, Imgproc.THRESH_BINARY);
		Imgproc.threshold(b, shadowBelow128, 128, 255, Imgproc.THRESH_BINARY_INV);

		Core.bitwise_and(shadowBelow128, shadowAbove126, shadow);
	}
}
