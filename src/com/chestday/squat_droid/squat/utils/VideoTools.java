package com.chestday.squat_droid.squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class VideoTools {
	public static Mat toGreyscale(Mat frame) {
		return colourTransform(frame, Imgproc.COLOR_BGR2GRAY);
	}
	
	public static Mat toHsv(Mat frame) {
		return colourTransform(frame, Imgproc.COLOR_BGR2HSV);
	}
	
	public static Mat toHls(Mat frame) {
		return colourTransform(frame, Imgproc.COLOR_BGR2HLS);
	}
	
	private static Mat colourTransform(Mat frame, int type) {
		Mat m = new Mat();
		Imgproc.cvtColor(frame, m, type);
		return m;
	}
	
	public static Mat floodFill(Mat frame, int x, int y, int val) {
		Mat m = new Mat(frame.rows() + 2, frame.cols() + 2, frame.type());
		Imgproc.floodFill(frame, m, new Point(x, y), new Scalar(val));
		return m;
	}
	
	public static Mat blend(Mat m1, Mat m2) {
		Mat blended = new Mat();
		Core.addWeighted(m1, 0.5, m2, 0.5, 0, blended);
		return blended;
	}
	
	public static int countDifference(Mat m1, Mat m2) {
		Mat nm1 = new Mat(m1.size(), m1.type());
		Core.bitwise_not(m1, nm1);
		Mat diff = new Mat(m1.size(), m1.type());
		Core.bitwise_and(m2, nm1, diff);
		return Core.countNonZero(diff);
	}
	
	public static MatOfPoint largestObject(Mat frame) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		System.out.println("Largest object type: " + frame.type());
		frame.convertTo(frame, CvType.CV_8UC3);
		System.out.println("Largest object type: " + frame.type());
		Imgproc.findContours(frame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		if(contours.isEmpty()) {
			return null;
		}
		
		int largestContourIdx = largestContourIndex(contours);
		
		//Mat drawing = Mat.zeros(frame.size(), frame.type());

		//Imgproc.drawContours(drawing, contours, largestContourIdx, new Scalar(255, 255, 255), -1);
		return contours.get(largestContourIdx);
	}
	
	private static int largestContourIndex(List<MatOfPoint> contours) {
		int largestContourIndex = 0;
		double largestContourArea = 0;
		for(int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if(area > largestContourArea) {
				largestContourIndex = i;
				largestContourArea = area;
			}
		}
		return largestContourIndex;
	}
	
	public static int numberOverlappingPixels(Mat m1, Mat m2) {
		Mat res = new Mat();
		int overlap = 0;
		try {
			Core.bitwise_and(m1, m2, res);
			overlap = Core.countNonZero(res);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return overlap;
	}
	
	public static Mat rotate(Mat frame, double angle) {
		Point centre = new Point(frame.cols() / 2, frame.rows() / 2);
		Mat r = Imgproc.getRotationMatrix2D(centre, angle, 1);
		Mat rotated = new Mat();
		Imgproc.warpAffine(frame, rotated, r, new Size(frame.rows(), frame.cols()));
		return rotated;
	}
	
	public static Mat rotate90AndScale(Mat frame) {
		Mat m = rotate90(frame);
		Imgproc.resize(m, m, frame.size());
		return m;
	}
	
	public static Mat rotate90(Mat frame) {
		Mat m = frame.t();
		Core.flip(frame.t(), m, 1);
		return m;
	}
	
	public static double percentageNonZero(Mat frame) {
		int nonZeroPixels = Core.countNonZero(frame);
		return (double)nonZeroPixels / (frame.cols() * frame.rows()) * 100;
	}
}
