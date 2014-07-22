package com.chestday.squat_droid_free.squat.tracking;

import com.chestday.squat_droid_free.squat.model.Model;
import com.chestday.squat_droid_free.squat.model.event.ModelEventListener;
import com.chestday.squat_droid_free.squat.model.event.ModelEventManager;
import com.chestday.squat_droid_free.squat.model.event.ModelEventType;

public class SquatRepCounter {

	private int halfReps;
	private ModelEventManager modelEventManager;
	
	public SquatRepCounter(ModelEventManager modelEventManager) {
		halfReps = 0;
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		modelEventManager.addListener(ModelEventType.SQUAT_ASCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				halfReps++;
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				halfReps++;
			}
		});
	}

	public int getReps() {
		// Integer division means reps rounded down, so only complete reps count
		return halfReps / 2;
	}
}
