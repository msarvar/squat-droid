package com.chestday.squat_droid.squat.optimization;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.opencv.core.Mat;

import com.chestday.squat_droid.squat.model.Model;

public class ModelFitterOptim implements ModelFitter {
	private static final int MAX_EVALUATIONS = 30;
	
	public void fit(Model model, Mat frame) {
		ModelFitFunction fitFunction = new ModelFitFunction(frame, model);
		
		fitBOBYQA(model, frame, fitFunction);
	}
	
	private void fitBOBYQA(Model model, Mat frame, ModelFitFunction fitFunction) {
		MultivariateOptimizer optim = new BOBYQAOptimizer(2 + model.get().length);
		
		try {
			PointValuePair p = optim.optimize(
					new InitialGuess(model.get()),
					new MaxEval(MAX_EVALUATIONS),
					GoalType.MINIMIZE,
					new ObjectiveFunction(fitFunction),
					new SimpleBounds(model.getLowerBounds(), model.getUpperBounds())
				);
				
				double[] results = p.getPoint();
				model.set(results);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void fitBOBYQAFromMid(Model model, Mat frame, ModelFitFunction fitFunction) {
		MultivariateOptimizer optim = new BOBYQAOptimizer(2 + model.get().length);
		
		try {
			PointValuePair p = optim.optimize(
					new InitialGuess(model.getMidSquatPose()),
					new MaxEval(MAX_EVALUATIONS),
					GoalType.MINIMIZE,
					new ObjectiveFunction(fitFunction),
					new SimpleBounds(model.getLowerBounds(), model.getUpperBounds())
				);
				
				double[] results = p.getPoint();
				model.set(results);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void fitNelderMead(Model model, Mat frame, ModelFitFunction fitFunction) {
		NelderMeadSimplex neld = new NelderMeadSimplex(model.get().length);
		
		SimplexOptimizer optim = new SimplexOptimizer(5,5);
		
		try {
			PointValuePair p = optim.optimize(
					neld,
					new InitialGuess(model.get()),
					new MaxEval(MAX_EVALUATIONS * 10000000),
					new MaxIter(MAX_EVALUATIONS * 10000000),
					GoalType.MINIMIZE,
					new ObjectiveFunction(fitFunction)
				);
				
				double[] results = p.getPoint();
				model.set(results);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void fitPowell(Model model, Mat frame, ModelFitFunction fitFunction) {
		MultivariateOptimizer optim = new PowellOptimizer(5, 5);
		
		try {
			PointValuePair p = optim.optimize(
					new InitialGuess(model.get()),
					new MaxEval(MAX_EVALUATIONS),
					GoalType.MINIMIZE,
					new ObjectiveFunction(fitFunction)
				);
				
				double[] results = p.getPoint();
				model.set(results);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
