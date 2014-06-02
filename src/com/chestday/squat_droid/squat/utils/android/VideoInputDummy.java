package com.chestday.squat_droid.squat.utils.android;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import com.chestday.squat_droid.squat.model.AngularModel;
import com.chestday.squat_droid.squat.model.Model;
import com.chestday.squat_droid.squat.utils.VideoInput;

public class VideoInputDummy implements VideoInput {

	private int width;
	private int height;
	
	public VideoInputDummy(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public boolean hasNextFrame() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void getNextFrame(Mat frame) {
		// TODO Auto-generated method stub
		Model model = new AngularModel(width/2, height/2);
		model.draw(frame, new Scalar(0,255,0));
		//Core.rectangle(m, new Point(width/2 + 10, height/2 + 10), new Point(width/2 - 10, height/2 - 10), new Scalar(255,0,0));
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

}
