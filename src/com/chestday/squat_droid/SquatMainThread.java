package com.chestday.squat_droid;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import com.chestday.squat_droid.squat.tracking.SquatPipeline;
import com.chestday.squat_droid.squat.tracking.SquatPipelineListener;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractor;
import com.chestday.squat_droid.squat.utils.BackgroundSubtractorOpenCV;
import com.chestday.squat_droid.squat.utils.MatManager;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.VideoTools;

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
		Mat frame = MatManager.get("squat_main__thread_frame");
		while(frameDumpCount < 10 && videoInput.hasNextFrame()) {
			videoInput.getNextFrame(frame);
			frameDumpCount++;
		}
		
		listener.onInitialised();
		
		// This loop is for activating/deactivating the button - wait for still background
		BackgroundSubtractor bg = new BackgroundSubtractorOpenCV(0.01, 50);

		while(!listener.isStartButtonPressed() && videoInput.hasNextFrame()) {
			videoInput.getNextFrame(frame);
			videoDisplay.show(frame);
			videoDisplay.draw();
			Mat b = MatManager.get("squat_main_thread_b", frame.rows(), frame.cols(), CvType.CV_8U);
			bg.subtract(frame, b);
			double percentage = VideoTools.percentageNonZero(b);
			listener.onBackgroundStationary(percentage < 0.3);
		}
		
		listener.onTimeToFixCameraSettings();
		
		// The button has been pressed, let's go!
		listener.onStart();
		
		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay, listener);
		
		squatPipeline.process();
		
		listener.onTimeToUnFixCameraSettings();
		
		// Cycle through frames until we press reset
		while(!listener.isStartButtonPressed() && videoInput.hasNextFrame()) {
			videoInput.getNextFrame(frame);
			// Darken the frame to show the scores better
			frame.convertTo(frame, frame.type(), 1, -100);
			videoDisplay.show(frame);
			videoDisplay.draw();
		}
		
		listener.onFinish();
		
		//videoDisplay.close();
		
		System.out.println("done");
	}
}
