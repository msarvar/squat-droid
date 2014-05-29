package com.chestday.squat_droid.squat.tracking;

import java.util.List;

import com.chestday.squat_droid.squat.utils.Pair;

public interface SquatPipelineListener {
	public void onStart();
	public void onReadyToSquat();
	public void onInitialModelFit();
	public void onSquatsComplete(List<Pair<Double,String>>scores);
	public void squatSetupHasFigure();
	public void squatSetupNotHasFigure();
	public void onMotionDetectorValue(double difference);
	public void onSquatBelowParallel();
	public void onTimeToFixCameraSettings();
	public boolean isStartButtonPressed();
	public void onBackgroundStationary(boolean isStationary);
}
