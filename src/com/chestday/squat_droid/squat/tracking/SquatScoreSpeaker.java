package com.chestday.squat_droid.squat.tracking;

import com.chestday.squat_droid.Speaker;

public class SquatScoreSpeaker {
	public static void speak(double score, String contributor) {
		String text = "";
		if(score < 30) {
			text += "Bad.";
		} else if(score < 50) {
			text += "Ok.";
		} else if(score < 70) {
			text += "Good.";
		} else if(score < 90) {
			text += "Very Good.";
		} else if(score < 100) {
			text += "Excellent.";
		} else {
			text += "Perfect.";
		}
		
		Speaker.speak(text);
	}
}
