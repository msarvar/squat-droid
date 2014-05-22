package com.chestday.squat_droid.squat.utils.android;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.utils.VideoDisplay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class VideoDisplayAndroid implements VideoDisplay {

	private Mat frame;
	private ImageView imageView;
	private Activity activity;
	
	public VideoDisplayAndroid(Activity activity, ImageView imageView) {
		this.imageView = imageView;
		this.activity = activity;
	}
	
	@Override
	public void show(Mat m) {
		frame = m;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		final Bitmap bm = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(frame, bm);
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				imageView.setImageBitmap(bm);
				System.out.println("Drawing frame");
			}
		});
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		frame = Mat.zeros(frame.size(), frame.type());
	}

}
