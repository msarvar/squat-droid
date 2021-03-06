package com.chestday.squat_droid.squat.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.model.Model;
import com.chestday.squat_droid.squat.utils.MatManager;
import com.chestday.squat_droid.squat.utils.VideoTools;

public class ModelFitFunction implements MultivariateFunction {

	private Mat frame;
	private Model model;
	private Mat m;

	public ModelFitFunction(Mat frame, Model model) {
		this.frame = frame;
		this.model = model;
		this.m = MatManager.get("model_fit_function_m", frame.rows(), frame.cols(), frame.type());
	}
	
	@Override
	public double value(double[] modelAsDouble) {
		
		model.set(modelAsDouble);
		
		model.draw(m);
		
		// We want the most overlap
		int overlap = VideoTools.numberOverlappingPixels(frame, m);
		
		// We want to minimise non overlap
		int nonOverlap = frame.rows() * frame.cols() - overlap;
		
		return nonOverlap;
	}
}
