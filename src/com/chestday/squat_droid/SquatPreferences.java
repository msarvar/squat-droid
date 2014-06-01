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
	
	public static String getValue(String key) {
		return self.sharedPreferences.getString(key, "NULL");
	}
}
