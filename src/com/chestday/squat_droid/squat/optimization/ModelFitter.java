package com.chestday.squat_droid.squat.optimization;

import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.model.Model;

public interface ModelFitter {
	public void fit(Model model, Mat frame);
}
