package com.chestday.squat_droid.squat.utils.android;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import wseemann.media.FFmpegMediaMetadataRetriever;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.chestday.squat_droid.squat.utils.VideoInput;

public class VideoInputFile implements VideoInput {

	private long position = 0;
	private FFmpegMediaMetadataRetriever retriever;
	private int width, height;
	
	public VideoInputFile(String name) {
		retriever = new FFmpegMediaMetadataRetriever();
		retriever.setDataSource(name);
		Bitmap firstFrame = retriever.getFrameAtTime(0, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
		width = firstFrame.getWidth();
		height = firstFrame.getHeight();
	}
	
	@Override
	public boolean hasNextFrame() {
		return retriever.getFrameAtTime(position, FFmpegMediaMetadataRetriever.OPTION_CLOSEST) != null;
	}

	@Override
	public Mat getNextFrame() {
		Bitmap bmp = retriever.getFrameAtTime(position, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
		position += 100000;
		Mat m = new Mat();
		Utils.bitmapToMat(bmp, m);
		Core.flip(m.t(), m, 1);
		return m;
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
