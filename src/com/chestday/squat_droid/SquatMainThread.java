package com.chestday.squat_droid;

import org.opencv.core.Mat;

import android.widget.ImageView;

import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.android.VideoDisplayAndroid;
import com.chestday.squat_droid.squat.utils.android.VideoInputFile;

public class SquatMainThread extends Thread {

	private VideoInput videoInput;
	private VideoDisplay videoDisplay;
	
	public SquatMainThread(VideoInput videoInput, VideoDisplay videoDisplay) {
		super();
		this.videoInput = videoInput;
		this.videoDisplay = videoDisplay;
	}
	
	public void run() {

//		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay, new SquatPipelineListener() {
//			public void onReadyToSquat() {
//				System.out.println("Ready to Squat!");
//			}
//
//			public void onInitialModelFit() {
//				System.out.println("Initial Model Fitted");
//			}
//
//			public void onSquatsComplete(List<Pair<Double, String>> scores) {
//				System.out.println("Reps: " + scores.size());
//				for(int i = 0; i < scores.size(); i++) {
//					System.out.println("Rep " + (i+1) + " {Score: " + scores.get(i).l + "%, Problem: " + scores.get(i).r + "}");
//				}
//			}
//		});
//		
//		squatPipeline.process();
		
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
