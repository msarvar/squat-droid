package com.chestday.squat_droid.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtractorLargestObject implements BackgroundSubtractor {

	private BackgroundSubtractor bg;
	
	public BackgroundSubtractorLargestObject(Mat background, BackgroundSubtractor bg) {
		this.bg = bg;
	}
	
	@Override
	public void subtract(Mat frame, Mat subtracted) {
		bg.subtract(frame, subtracted);

		MatOfPoint figure = VideoTools.largestObject(subtracted);
		
		if(figure != null) {
			subtracted.setTo(new Scalar(0,0,0));
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			contours.add(figure);
			Imgproc.drawContours(subtracted, contours, 0, new Scalar(255,255,255), -1);
		}
	}

}
