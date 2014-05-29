package com.chestday.squat_droid;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.chestday.squat_droid.squat.tracking.SquatPipeline;
import com.chestday.squat_droid.squat.tracking.SquatPipelineListener;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractor;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractorOpenCV;
import com.chestday.squat_droid.squat.utils.Pair;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.VideoTools;
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

		// Dump the first few frames to ensure that we get a good white balance/exposure before fixing
		int frameDumpCount = 0;
		while(frameDumpCount < 10 && videoInput.hasNextFrame()) {
			videoInput.getNextFrame();
			frameDumpCount++;
		}
		
		listener.onTimeToFixCameraSettings();
		
		// This loop is for activating/deactivating the button - wait for still background
		BackgroundSubtractor bg = new BackgroundSubtractorOpenCV(0.05, 10);
		while(!listener.isStartButtonPressed() && videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			videoDisplay.show(frame);
			videoDisplay.draw();
			Mat rgb = new Mat();
			Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_RGBA2RGB);
			Mat b = bg.subtract(rgb);
			double percentage = VideoTools.percentageNonZero(b);
			listener.onBackgroundStationary(percentage < 0.3);
		}
		
		// The button has been pressed, let's go!
		listener.onStart();
		
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
