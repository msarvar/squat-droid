
package com.chestday.squat_droid.squat.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.model.Model;
import com.chestday.squat_droid.squat.utils.VideoTools;

public class ModelInitialisationFitFunction implements MultivariateFunction {

	private Mat frame;
	private Model model;

	public ModelInitialisationFitFunction(Mat frame, Model model) {
		this.frame = frame;
		this.model = model;
	}
	
	@Override
	public double value(double[] feet) {
		
		model.setInitParams(feet);
		
		Mat m = new Mat(frame.size(), frame.type());
		model.draw(m);
		
		// We want the most overlap
		int overlap = VideoTools.numberOverlappingPixels(frame, m);
		
		// We want to minimise non overlap
		int nonOverlap = frame.rows() * frame.cols() - overlap;
		
		return nonOverlap;
	}
}
