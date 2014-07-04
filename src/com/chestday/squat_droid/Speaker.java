package com.chestday.squat_droid;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Speaker implements OnInitListener {
	private static Speaker self;
	
	private TextToSpeech tts;
	private boolean ready = false;
	
	private Speaker(Context context) {
		tts = new TextToSpeech(context, this);
	}
	
	public static void init(Context context) {
		self = new Speaker(context);
	}
	
	public static void shutdown() {
		self.tts.shutdown();
	}
	
	public static void speak(final String text) {
		boolean sound = SquatPreferences.getBooleanValue("sound", true);
		
		if(self.ready && sound) {
			self.tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onInit(int status) {
		tts.setLanguage(Locale.UK);
		ready = true;
	}
}
