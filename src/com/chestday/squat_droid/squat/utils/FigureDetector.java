package com.chestday.squat_droid.squat.utils;


import org.opencv.core.Core;
import org.opencv.core.Mat;

public class FigureDetector {
	public boolean hasFigure(Mat frame) {
		int foregroundPixels = Core.countNonZero(frame);
		double foregroundProportion = (double)foregroundPixels / (double)(frame.cols() * frame.rows());
		return foregroundProportion > 0.08;
	}
}
