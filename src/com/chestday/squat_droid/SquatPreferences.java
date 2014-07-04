package com.chestday.squat_droid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SquatPreferences {
	private static SquatPreferences self;
	
	private SharedPreferences sharedPreferences;
	
	private SquatPreferences(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static void init(Context context) {
		self = new SquatPreferences(context);
	}
	
	public static String getStringValue(String key, String defaultValue) {
		return self.sharedPreferences.getString(key, defaultValue);
	}
	
	public static int getIntValue(String key, int defaultValue) {
		return Integer.parseInt(self.sharedPreferences.getString(key, Integer.toString(defaultValue)));
	}
	
	public static double getDoubleValue(String key, double defaultValue) {
		return Double.parseDouble(self.sharedPreferences.getString(key, Double.toString(defaultValue)));
	}
	
	public static boolean getBooleanValue(String key, boolean defaultValue) {
		return self.sharedPreferences.getBoolean(key, defaultValue);
	}
}
