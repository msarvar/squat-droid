package com.chestday.squat_droid_free.squat.optimization;

import org.opencv.core.Mat;

import com.chestday.squat_droid_free.squat.model.Model;

public interface ModelFitter {
	public void fit(Model model, Mat frame);
}
