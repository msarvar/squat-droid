package com.chestday.squat_droid.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class BackgroundSubtractorCentreLine implements BackgroundSubtractor {

	private BackgroundSubtractor bg;
	private static final int NUM_OBJECTS = 3;
	
	public BackgroundSubtractorCentreLine(Mat background, BackgroundSubtractor bg) {
		this.bg = bg;
		
	}
	
	@Override
	public void subtract(Mat frame, Mat subtracted) {
		bg.subtract(frame, subtracted);

		List<MatOfPoint> objects = VideoTools.largestObjects(subtracted);

		if(objects != null && objects.size() > 1) {
			Moments moments = Imgproc.moments(objects.get(0));
			double centrex = moments.get_m10() / moments.get_m00();
			
			Mat centreLine = MatManager.get("bg_sub_centre_line_centreline", frame.rows(), frame.cols(), subtracted.type());
			centreLine.setTo(new Scalar(0));
			Core.rectangle(centreLine, new Point(centrex, 0), new Point(centrex, frame.rows()-1), new Scalar(255));

			subtracted.setTo(new Scalar(0));
			
			for(int i = 0; i < NUM_OBJECTS && i < objects.size(); i++) {
				Mat contourMat = MatManager.get("bg_sub_centre_line_contourmat", frame.rows(), frame.cols(), subtracted.type());
				contourMat.setTo(new Scalar(0));
				Imgproc.drawContours(contourMat, objects, i, new Scalar(255), -1);
				int overlap = VideoTools.numberOverlappingPixels(contourMat, centreLine);
				
				if(overlap > 0) {
					Core.bitwise_or(subtracted, contourMat, subtracted);
				}
			}
		}
	}

}
