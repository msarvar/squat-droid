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
		
		if(contributor.equals(SquatScorer.ABOVE_PARALLEL)) {
			text += " Try squatting deeper.";
		} else if(contributor.equals(SquatScorer.NO_LOCKOUT)) {
			text += " Try to stand up straight at the top.";
		} else if(contributor.equals(SquatScorer.BAD_BACK_ANGLE)) {
			text += " Make sure your back isn't too far forward or backward.";
		} else if(contributor.equals(SquatScorer.KNEES_FORWARD)) {
			text += " Try not to let your knees go as far forward.";
		} else if(contributor.equals(SquatScorer.WEIGHT_DISTRIBUTION_BACKWARD)) {
			text += " Try to shift your weight forward.";
		} else if(contributor.equals(SquatScorer.WEIGHT_DISTRIBUTION_FORWARD)) {
			text += " Try to shift your weight backward.";
		}
		
		Speaker.speak(text);
	}
}
