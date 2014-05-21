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
	
	public SquatRepScorer(final ModelEventManager modelEventManager) {
		scores = new ArrayList<Pair<Double,String>>();
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				if(!stopped) {
					addScore();
					
					scorer = new SquatScorer(modelEventManager);
				}
			}
		});
	}
	
	private void addScore() {
		if(scorer != null) {
			scores.add(new Pair<Double,String>(scorer.getCurrentScore(), scorer.getMainContributor()));
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
