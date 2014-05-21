package com.chestday.squat_droid.squat.utils;

import org.opencv.core.Mat;

public interface BackgroundSubtractor {
	public Mat subtract(Mat frame);
}
