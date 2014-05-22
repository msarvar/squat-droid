package com.chestday.squat_droid.squat.utils.android;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import com.chestday.squat_droid.squat.utils.VideoInput;

public class VideoInputCamera implements VideoInput {
	
	private VideoCapture capture;
	private List<Mat> frames;
	
	public VideoInputCamera() {
		frames = new ArrayList<Mat>();
		
		capture = new VideoCapture(Highgui.CV_CAP_ANDROID);
		if(!capture.isOpened()) {
			System.err.println("Unable to open camera :(");
		}
	}
	
	public boolean hasNextFrame() {
		Mat frame = new Mat();
		boolean success = capture.read(frame);
		if(success) {
			frames.add(frame);
		}
		return success;
	}
	
	public Mat getNextFrame() {
		if(frames.size() > 0) {
			return frames.remove(0);
		} else {
			return null;
		}
	}
	
	public int getWidth() {
		int widthFlag = Highgui.CV_CAP_PROP_FRAME_WIDTH;
		return (int)capture.get(widthFlag);
	}
	
	public int getHeight() {
		int heightFlag = Highgui.CV_CAP_PROP_FRAME_HEIGHT;
		return (int)capture.get(heightFlag);
	}
}
