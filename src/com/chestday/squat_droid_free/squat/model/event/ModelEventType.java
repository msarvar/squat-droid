package com.chestday.squat_droid_free.squat.model.event;

public enum ModelEventType {
	TICK,
	SQUAT_BELOW_PARALLEL_START,
	SQUAT_BELOW_PARALLEL_END,
	SQUAT_LOCKOUT_START,
	SQUAT_LOCKOUT_END,
	SQUAT_KNEE_FORWARD_START,
	SQUAT_KNEE_FORWARD_END,
	SQUAT_KNEE_BACKWARD_START,
	SQUAT_KNEE_BACKWARD_END,
	SQUAT_BAD_WEIGHT_DISTRIBUTION_START,
	SQUAT_BAD_WEIGHT_DISTRIBUTION_END,
	SQUAT_ON_HEEL_OR_TOE_START,
	SQUAT_ON_HEEL_OR_TOE_END,
	SQUAT_BAD_FORM,
	SQUAT_DESCEND_START,
	SQUAT_ASCEND_START,
	SQUAT_BAD_BACK_ANGLE_START,
	SQUAT_BAD_BACK_ANGLE_END,
}