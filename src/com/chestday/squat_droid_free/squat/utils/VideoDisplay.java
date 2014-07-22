package com.chestday.squat_droid_free.squat.utils;

import org.opencv.core.Mat;

public interface VideoDisplay {
	public void show(Mat m);
	
	public void draw();
	
	public void close();
}
