package com.chestday.squat_droid.squat.utils.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.utils.FixedQueue;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;
import com.chestday.squat_droid.squat.utils.VideoTools;

public class VideoBridge implements VideoDisplay, VideoInput, CvCameraViewListener2 {

	private volatile Mat inputFrame;
	private volatile Mat outputFrame;
	
	private int width;
	private int height;
	
	private VideoBridgeReadyCallback vbrc;
	
	public VideoBridge() {
		inputFrame = new Mat();
	}
	
	public void setReadyCallback(VideoBridgeReadyCallback vbrc) {
		this.vbrc = vbrc;
	}
	
	@Override
	public boolean hasNextFrame() {
		// TODO Auto-generated method stub
		//System.out.println("Do we have a frame? " + inputFrame != null ? "Yes" : "No");
		return inputFrame != null;
	}

	@Override
	public Mat getNextFrame() {
		// TODO Auto-generated method stub
		//System.out.println("Asked for frame");
		return inputFrame;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	@Override
	public void show(Mat m) {
		// TODO Auto-generated method stub
		//System.out.println("Called show for a frame");
		//this.outputFrame.release();
		outputFrame = m;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		System.out.println("Started camera view");
		this.width = width;
		this.height = height;
		outputFrame = Mat.zeros(height, width, CvType.CV_8UC1);
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	private boolean started = false;
	
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		System.gc();
		
		// TODO Auto-generated method stub
		System.out.println("Got a frame!");
		System.out.println("Type: ::: " + inputFrame.rgba().type());
		Mat m = inputFrame.rgba().clone();
		//m = VideoTools.rotate90AndScale(m);
		
		this.inputFrame.release();
		m.convertTo(this.inputFrame, CvType.CV_8UC1);
		
		Mat out = new Mat();
		this.outputFrame.convertTo(out, 24);
		
		if(!started) {
			started = true;
			vbrc.start();
		}
		
		return out;
	}
	
	public interface VideoBridgeReadyCallback {
		public void start();
	}

}
