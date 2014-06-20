package com.chestday.squat_droid.squat.tracking;

import java.util.ArrayList;
import java.util.List;

import com.chestday.squat_droid.squat.model.Model;
import com.chestday.squat_droid.squat.model.event.ModelEventListener;
import com.chestday.squat_droid.squat.model.event.ModelEventManager;
import com.chestday.squat_droid.squat.model.event.ModelEventType;
import com.chestday.squat_droid.squat.utils.Pair;

public class SquatRepScorer {
	private SquatScorer scorer;
	private List<Pair<Double,String>> scores;
	private ModelEventManager modelEventManager;
	private boolean stopped = false;
	private boolean addedScore = false;
	private int reps = 0;
	
	public SquatRepScorer(final ModelEventManager modelEventManager) {
		scores = new ArrayList<Pair<Double,String>>();
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				if(!stopped) {
					
					addScore();
					
					// Create a new scorer - we have not added this score
					scorer = new SquatScorer(modelEventManager);
					addedScore = false;
				}
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_START, new ModelEventListener() {
			public void onEvent(Model m) {
				if(!stopped && scorer != null) {
					scorer.setHasLockedOut();
					
					addScore();
					addedScore = true;
				}
			}
		});
	}
	
	private void addScore() {
		if(!addedScore && scorer != null) {
			double score = scorer.getCurrentScore();
			String contributor = scorer.getMainContributor();
			scores.add(new Pair<Double,String>(score, contributor));
			if(!stopped) {
				SquatScoreSpeaker.speak(++reps, score, contributor);
			}
		}
	}
	
	public void stop() {
		stopped = true;
		addScore();
	}
	
	public List<Pair<Double,String>> getScores() {
		return scores;
	}
}
