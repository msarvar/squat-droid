package com.chestday.squat_droid_free.squat.utils;

import org.opencv.core.Mat;

public interface BackgroundSubtractor {
	public void subtract(Mat frame, Mat subtracted);
}
