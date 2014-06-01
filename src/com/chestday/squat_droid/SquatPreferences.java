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
	
	public static String getStringValue(String key) {
		return self.sharedPreferences.getString(key, "NULL");
	}
	
	public static int getIntValue(String key) {
		return Integer.parseInt(self.sharedPreferences.getString(key, "0"));
	}
	
	public static double getDoubleValue(String key) {
		return Double.parseDouble(self.sharedPreferences.getString(key, "0"));
	}
	
	public static boolean getBooleanValue(String key) {
		return self.sharedPreferences.getBoolean(key, false);
	}
}
