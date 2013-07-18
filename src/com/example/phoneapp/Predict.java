package com.example.phoneapp;

import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import android.util.Log;

class svm_predict
{
	public static int predict()
	{
		// the directory of the model file
		String path = "src/svm3.model";
		
		// m is the feature vector dimension
		int dimemsion = 312; 

		// TODO: Add comments here.
		svm_model model = null;
		try
		{
			// TODO: Cannot load the file here. Try to fix it.
			model = svm.svm_load_model(path);
		} 
		catch (IOException e)
		{
			Log.e("z", "failed to load svm model");
		}
		
		// TODO: Add comments here.
		int nr_class = svm.svm_get_nr_class(model);
		
		// TODO: Add comments here.
		double[] prob_estimates = null;
		
		// TODO: Add comments here.
		int[] labels = new int[nr_class];
		
		// TODO: Add comments here.
		svm.svm_get_labels(model, labels);
		
		// TODO: Add comments here.
		prob_estimates = new double[nr_class];
		
		// TODO: Add comments here.
		svm_node[] nodes = new svm_node[dimemsion];

		for (int index = 0; index < dimemsion; index++)
		{
			nodes[index] = new svm_node();
			nodes[index].index = index;
			nodes[index].value = 0.5;
		}

		double label;
		label = svm.svm_predict_probability(model, nodes, prob_estimates);
		double estimateValue = Math.max(
				Math.max(prob_estimates[0], prob_estimates[1]),
				prob_estimates[2]);

		Log.i("z", "label = " + label);

		return (int) estimateValue;
	}
}
