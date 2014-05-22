package com.chestday.squat_droid.squat.utils;

import org.opencv.core.Mat;

public interface VideoInput {
	public boolean hasNextFrame();
	
	public Mat getNextFrame();
	
	public int getWidth();
	
	public int getHeight();
}
