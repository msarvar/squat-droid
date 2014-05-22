package com.chestday.squat_droid.squat.utils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class VideoDisplayAndroid implements VideoDisplay {

	private Mat frame;
	private ImageView imageView;
	
	public VideoDisplayAndroid(ImageView imageView) {
		this.imageView = imageView;
	}
	
	@Override
	public void show(Mat m) {
		frame = m;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Bitmap bm = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(frame, bm);
		imageView.setImageBitmap(bm);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		frame = Mat.zeros(frame.size(), frame.type());
	}

}
