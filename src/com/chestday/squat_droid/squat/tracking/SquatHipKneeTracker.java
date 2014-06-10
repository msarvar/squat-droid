package com.chestday.squat_droid.squat.tracking;

import com.chestday.squat_droid.squat.model.Model;
import com.chestday.squat_droid.squat.model.event.ModelEventListener;
import com.chestday.squat_droid.squat.model.event.ModelEventManager;
import com.chestday.squat_droid.squat.model.event.ModelEventType;
import com.chestday.squat_droid.squat.utils.FixedQueue;

public class SquatHipKneeTracker {
	private static final int NUM_ANGLES = 3;
	
	private boolean isAscending = false;
	
	private FixedQueue<Double> hipAngles;
	private FixedQueue<Double> kneeAngles;
	
	public SquatHipKneeTracker(ModelEventManager modelEventManager) {
		hipAngles = new FixedQueue<Double>(NUM_ANGLES);
		kneeAngles = new FixedQueue<Double>(NUM_ANGLES);
		
		modelEventManager.addListener(ModelEventType.SQUAT_ASCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				isAscending = true;
			}
		});
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				isAscending = false;
			}
		});
	}
	
	public void update(Model m) {
		if(isAscending) {
			hipAngles.add(m.getAcuteHipAngle());
			kneeAngles.add(m.getAcuteKneeAngle());
		} else {
			// Reset the queues if we're descending and there are items in the queue
			if(hipAngles.size() > 0) {
				hipAngles = new FixedQueue<Double>(NUM_ANGLES);
				kneeAngles = new FixedQueue<Double>(NUM_ANGLES);
			}
		}
	}
	
	public boolean isHipKneeRateCorrect() {
		// Our hip/knee rate is ok if we are descending
		// Otherwise we check the changes in angles relative to one another
		return !isAscending || isHipAngleRateFasterThanKneeAngleRate();
	}

	private boolean isHipAngleRateFasterThanKneeAngleRate() {
		// Start by assuming we're ok if we haven't collected enough information
		if(hipAngles.size() != NUM_ANGLES) {
			return true;
		}
		
		// Make sure that the hip angle grows at the same or faster a rate than the knee angle
		double hipAngleDifference = Math.abs(hipAngles.get(0) - hipAngles.get(NUM_ANGLES-1));
		double kneeAngleDifference = Math.abs(kneeAngles.get(0) - kneeAngles.get(NUM_ANGLES-1));
		
		return hipAngleDifference >= kneeAngleDifference;
	}
}
