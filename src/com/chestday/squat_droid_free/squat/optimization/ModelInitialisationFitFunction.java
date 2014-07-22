
package com.chestday.squat_droid_free.squat.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Mat;

import com.chestday.squat_droid_free.squat.model.Model;
import com.chestday.squat_droid_free.squat.utils.MatManager;
import com.chestday.squat_droid_free.squat.utils.VideoTools;

public class ModelInitialisationFitFunction implements MultivariateFunction {

	private Mat frame;
	private Model model;
	private Mat m;

	public ModelInitialisationFitFunction(Mat frame, Model model) {
		this.frame = frame;
		this.model = model;
		this.m = MatManager.get("model_init_fit_function_m", frame.rows(), frame.cols(), frame.type());
	}
	
	@Override
	public double value(double[] feet) {
		
		model.setInitParams(feet);
		
		model.draw(m);
		
		// We want the most overlap
		int overlap = VideoTools.numberOverlappingPixels(frame, m);
		
		// We want to minimise non overlap
		int nonOverlap = frame.rows() * frame.cols() - overlap;
		
		return nonOverlap;
	}
}
