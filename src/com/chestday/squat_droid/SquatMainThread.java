package com.chestday.squat_droid;

import java.util.List;

import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.tracking.SquatPipeline;
import com.chestday.squat_droid.squat.tracking.SquatPipelineListener;
import com.chestday.squat_droid.squat.utils.Pair;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.android.PortraitCameraView;

public class SquatMainThread extends Thread {

	private VideoInput videoInput;
	private VideoDisplay videoDisplay;
	private SquatPipelineListener listener;
	
	public SquatMainThread(VideoInput videoInput, VideoDisplay videoDisplay, SquatPipelineListener listener) {
		super();
		this.videoInput = videoInput;
		this.videoDisplay = videoDisplay;
		this.listener = listener;
	}
	
	public void run() {

		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay, listener);
		
		squatPipeline.process();
		
		// Cycle through the last few frames
		int i = 0;
		while(videoInput.hasNextFrame()) {
			i++;
			Mat frame = videoInput.getNextFrame();
			videoDisplay.show(frame);
			videoDisplay.draw();
			System.out.println("Frame " + i);
		}
		
		//videoDisplay.close();
		
		System.out.println("done");
	}
}
