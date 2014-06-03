package com.chestday.squat_droid.squat.tracking;

import java.util.HashMap;
import java.util.Map;

import com.chestday.squat_droid.Speaker;
import com.chestday.squat_droid.SquatPreferences;

public class SquatScoreSpeaker {
	
	private static Map<String, String[]> tips = new HashMap<String, String[]>();
	static {
		tips.put(SquatScorer.ABOVE_PARALLEL, new String[] {
			"Try squatting deeper.",
			"Try squatting below parallel.",
			"Try going lower.",
			"Squat deeper!",
			"Squat a little lower.",
			"You didn't squat low enough.",
			"Try to squat below parallel.",
			"You didn't squat below parallel.",
			"You didn't hit depth.",
			"You should squat below parallel.",
			"You should squat lower.",
			"Check your depth."
		});
		
		tips.put(SquatScorer.NO_LOCKOUT, new String[] {
			"Try to stand up straight at the top.",
			"Try to straighten out at the top.",
			"Try standing up straighter.",
			"Try to lock out.",
			"Stand up straighter.",
			"Make sure you lock out.",
			"You didn't lock out."
		});
		
		tips.put(SquatScorer.BAD_BACK_ANGLE, new String[] {
			"Try to make sure your back isn't too far forward or backward.",
			"Try to keep your back reasonably upright.",
			"Try not to lean forward or backward as much.",
			"Your back was not at an optimal angle.",
			"Your back could have been in a better position."
		});
		
		tips.put(SquatScorer.KNEES_FORWARD, new String[] {
			"Don't let your knees go so far forward.",
			"Try not to let your knees go so far forward.",
			"Watch your knees.",
			"Be careful not to let your knees go too far forward.",
			"Try shifting your knees backward.",
			"Make sure your shins stay vertical.",
			"Keep your shins upright.",
			"Don't let your knees go past your toes.",
			"Your knees went too far forward.",
			"Your shins weren't upright enough."
		});
		
		tips.put(SquatScorer.WEIGHT_DISTRIBUTION_BACKWARD, new String[] {
			"Try to move your weight forward.",
			"You were leaning a little too far back.",
			"Try leaning forward a little more.",
			"You should lean forward a bit more.",
			"Lean forward a little more.",
			"Watch that the bar does not travel past your heel.",
			
			"Make sure your centre of gravity is above your feet.",
			"Make sure the bar stays directly above your feet.",
			"The bar was not directly above your feet.",
		});
		
		tips.put(SquatScorer.WEIGHT_DISTRIBUTION_FORWARD, new String[] {
			"Try to move your weight backwards.",
			"You were leaning a little too far forward.",
			"Try leaning backwards a little more.",
			"You should lean backwards a bit more.",
			"Lean backwards a little more.",
			"Watch that the bar does not travel past your toes.",
			
			"Make sure your centre of gravity is above your feet.",
			"Make sure the bar stays directly above your feet.",
			"The bar was not directly above your feet.",
		});
	}
	
	public static void speak(int rep, double score, String contributor) {
		String text = "";
		
		if(SquatPreferences.getBooleanValue("count_reps")) {
			text += rep + ". ";
		}
		
		if(score < 30) {
			text += "Not very good.";
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
		
		// Tips!
		if(score < 90) {
			text += " " + randomPhrase(contributor);
		}
		
		Speaker.speak(text);
	}
	
	private static String randomPhrase(String contributor) {
		String[] phrases = tips.get(contributor);
		return phrases[(int)Math.floor(Math.random()*phrases.length)];
	}
}
