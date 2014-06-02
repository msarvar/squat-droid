package com.chestday.squat_droid.squat.utils;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.core.Size;

public class MatManager {
	private static MatManager self;
	
	private Map<String, Mat> matMap;
	
	private MatManager() {
		matMap = new HashMap<String, Mat>();
	}
	
	public static void init() {
		self = new MatManager();
	}
	
	public static Mat get(String key, int rows, int cols, int type) {
		if(!self.matMap.containsKey(key)) {
			self.matMap.put(key, Mat.zeros(rows, cols, type));
		}
		return self.matMap.get(key);
	}
}
