package com.example.phoneapp;

import java.io.BufferedReader;
import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import android.util.Log;

class SvmRecognizer
{
	public static int classes;
	public static svm_model model;
	public static svm_node[] nodes;
	private static SvmRecognizer instance;

	private SvmRecognizer()
	{
		// Make the constructor private and use the singleton method.
		classes = -1;
		model = null;
		nodes = null;
	}

	public static SvmRecognizer getInstance()
	{
		if (instance == null)
		{
			instance = new SvmRecognizer();
		}

		return instance;
	}

	public boolean init(BufferedReader reader)
	{
		// dimension for feature vector.
		final int dimemsion = 312;

		// Get model
		try
		{
			model = svm.svm_load_model(reader);
		}
		catch (IOException e)
		{
			Log.e("z", "Fail to load model.");
		}

		if (model == null)
		{
			return false;
		}

		// Get all classes.
		classes = svm.svm_get_nr_class(model);

		// Get all nodes.
		nodes = new svm_node[dimemsion];

		for (int index = 0; index < dimemsion; index++)
		{
			nodes[index] = new svm_node();
			nodes[index].index = index;
			nodes[index].value = 0.5;
		}

		return true;
	}

	public double predict()
	{
		if (model == null || nodes == null || classes == -1)
		{
			return -1;
		}

		// TODO: Add comments here.
		double[] prob_estimates = new double[classes];

		// TODO: Add comments here.
		// double label = svm.svm_predict_probability(model, nodes, prob_estimates);

		// TODO: Add comments here.
		double estimateValue = Math.max(Math.max(prob_estimates[0], prob_estimates[1]), prob_estimates[2]);

		Log.i("z", "prob_estimates[0] = " + prob_estimates[0]);
		Log.i("z", "prob_estimates[1] = " + prob_estimates[1]);
		Log.i("z", "prob_estimates[2] = " + prob_estimates[2]);

		return estimateValue;
	}
}
