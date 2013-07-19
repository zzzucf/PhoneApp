package com.example.phoneapp;

import java.io.BufferedReader;
import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import android.util.Log;

class SvmRecognizer
{
	public static int nr_class;
	public static svm_model model = null;
	public static svm_node[] nodes;

	public static void init(BufferedReader reader)
	{
		// m is the feature vector dimension
		int dimemsion = 312;

		// Get model
		try
		{
			// TODO: Cannot load the file here. Try to fix it.
			Log.i("z", "try to load model");
			model = svm.svm_load_model(reader);
		} catch (IOException e)
		{
			Log.e("z", e.getMessage());
		}

		Log.i("z", "model = " + model);
		
		// Get all classes.
		nr_class = svm.svm_get_nr_class(model);

		// Get all nodes.
		nodes = new svm_node[dimemsion];

		for (int index = 0; index < dimemsion; index++)
		{
			nodes[index] = new svm_node();
			nodes[index].index = index;
			nodes[index].value = 0.5;
		}
	}

	public static double predict()
	{
		// TODO: Add comments here.
		double[] prob_estimates = null;

		// TODO: Add comments here.
		prob_estimates = new double[nr_class];
		Log.i("z", "prob_estimates = " + prob_estimates);

		double label;
		label = svm.svm_predict_probability(model, nodes, prob_estimates);
		Log.i("z", "label = " + label);
		
		double estimateValue = Math.max(Math.max(prob_estimates[0], 
												 prob_estimates[1]),	
												 prob_estimates[2]);
		Log.i("z", "prob_estimates[0] = " + prob_estimates[0]);
		Log.i("z", "prob_estimates[1] = " + prob_estimates[1]);
		Log.i("z", "prob_estimates[2] = " + prob_estimates[2]);
		
		
		return estimateValue;
	}
}
