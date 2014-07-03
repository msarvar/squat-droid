package com.chestday.squat_droid.squat.utils.android;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.chestday.squat_droid.squat.utils.MatManager;
import com.chestday.squat_droid.squat.utils.VideoDisplay;
import com.chestday.squat_droid.squat.utils.VideoInput;

public class VideoBridge implements VideoDisplay, VideoInput, CvCameraViewListener2 {

	public static final int LEFT_FACING = 0;
	public static final int RIGHT_FACING = 1;
	
	private volatile Mat inputFrame;
	private volatile Mat outputFrame;
	
	private int width;
	private int height;
	private int origWidth;
	private int origHeight;
	
	private int direction = LEFT_FACING;
	
	private VideoBridgeReadyCallback vbrc;
	
	public VideoBridge() {
		inputFrame = new Mat();
	}
	
	public void setReadyCallback(VideoBridgeReadyCallback vbrc) {
		this.vbrc = vbrc;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	@Override
	public boolean hasNextFrame() {
		return true;
	}

	@Override
	public synchronized void getNextFrame(Mat frame) {
//		while(inputFrame == null) {
//			try {
//				wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		inputFrame.copyTo(frame);
		//inputFrame = null;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public synchronized void show(Mat m) {
		m.copyTo(outputFrame);
	}

	@Override
	public void draw() {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		this.origWidth = width;
		this.origHeight = height;
		this.width = 170;
		this.height = 120;
		outputFrame = MatManager.get("video_bridge_output_frame", this.height, this.width, CvType.CV_8UC1);
	}

	@Override
	public void onCameraViewStopped() {

	}

	private boolean started = false;
	
	@Override
	public synchronized Mat onCameraFrame(CvCameraViewFrame inputFrame) {	
		Mat m = MatManager.get("video_bridge_m");
		
		inputFrame.rgba().convertTo(m, CvType.CV_8UC1);
		
		if(direction == RIGHT_FACING) {
			Core.flip(m, m, 1);
		}
		
		Mat smallM = MatManager.get("video_bridge_small_m");
		System.out.println("SQUAT: m: w" + m.size().width + " h: " + m.size().height);
		Imgproc.resize(m, smallM, new Size(this.height, this.width));

		this.inputFrame = smallM;

		Mat out = MatManager.get("video_bridge_out");
		Mat bigOut = MatManager.get("video_bridge_big_out");
		
		Imgproc.resize(this.outputFrame, bigOut, m.size());
		bigOut.convertTo(out, 24);


		if(direction == RIGHT_FACING) {
			Core.flip(out, out, 1);
		}
		
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
