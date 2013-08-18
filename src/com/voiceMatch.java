package com;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.dtw.TimeWarpInfo;
import com.mfcc.MFCC;
import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;

public class voiceMatch
{
	private final boolean m_typeofmatch; // if 'true', matching is done in mfcc
	private final int m_FDtwRadius; // search radius for fast DTW
	private final int m_featurenum;
	private final DistanceFunction m_distFn; // distance function for DTW
	private final MFCC mfcc;

	public voiceMatch()
	{
		this(false, 13, 8000, 24, 256, 10, "EuclideanDistance");
	}

	public voiceMatch(boolean typeofmatch, int nMFCCs, double fs, int nFilters, int FFTlength, int radius, String distFn)
	{
		m_typeofmatch = typeofmatch;
		m_featurenum = nMFCCs;
		m_FDtwRadius = radius;
		m_distFn = DistanceFunctionFactory.getDistFnByName(distFn);

		mfcc = new MFCC(nMFCCs, fs, nFilters, FFTlength, true, 22, true);
	}

	public double doDtwMatch(double[][] feature, short[] audio)
	{
		ArrayList<Double> arrList2 = new ArrayList<Double>();
		if (mfcc.preprocess(audio, arrList2) > 0)
		{
			return Double.POSITIVE_INFINITY;
		}
		else
		{
			double[] sample2 = new double[arrList2.size()];

			for (int i = 0; i < arrList2.size(); ++i)
			{
				sample2[i] = arrList2.get(i);
			}
			double[][] ts1 = new double[feature.length][m_featurenum];
			for (int i = 0; i < feature.length; ++i)
			{
				ts1[i] = Arrays.copyOfRange(feature[i], 0, m_featurenum - 1);
			}

			// double[][] ts2 = new double[sample2.length][];
			double[][] ts2 = mfcc.doMFCC(sample2, 0.02, 0.01);

			final TimeSeries tsI = new TimeSeries(ts1);
			final TimeSeries tsJ = new TimeSeries(ts2);
			final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, m_distFn);

			return info.getDistance();
		}
	}

	public double doDtwMatch(short[] audio1, short[] audio2)
	{
		ArrayList<Double> arrList1 = new ArrayList<Double>();
		ArrayList<Double> arrList2 = new ArrayList<Double>();
		if (mfcc.preprocess(audio1, arrList1) > 0 || mfcc.preprocess(audio2, arrList2) > 0)
		{
			return Double.POSITIVE_INFINITY;
		}
		else
		{
			double[] sample1 = new double[arrList1.size()];
			double[] sample2 = new double[arrList2.size()];

			for (int i = 0; i < arrList1.size(); i++)
			{
				sample1[i] = arrList1.get(i);
			}
			for (int i = 0; i < arrList2.size(); i++)
			{
				sample2[i] = arrList2.get(i);
			}
			final TimeSeries tsI;
			final TimeSeries tsJ;
			if (m_typeofmatch)
			{
				double[][] ts1 = new double[sample1.length][m_featurenum];
				double[][] ts2 = new double[sample2.length][m_featurenum];
				ts1 = mfcc.doMFCC(sample1, 0.02, 0.01);
				ts2 = mfcc.doMFCC(sample2, 0.02, 0.01);
				tsI = new TimeSeries(ts1);
				tsJ = new TimeSeries(ts2);
			}
			else
			{
				double[][] ts1 = new double[sample1.length][m_featurenum];
				double[][] ts2 = new double[sample2.length][m_featurenum];
				for (int i = 0; i < sample1.length; ++i)
				{
					ts1[i][0] = sample1[i];
				}
				for (int i = 0; i < sample2.length; ++i)
				{
					ts2[i][0] = sample2[i];
				}
				tsI = new TimeSeries(ts1);
				tsJ = new TimeSeries(ts2);
			}
			final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, m_distFn);
			return info.getDistance();
		}
	}

	public double doFDtwMatch(double[][] feature1, short[] audio2)
	{
		ArrayList<Double> arrList2 = new ArrayList<Double>();
		if (mfcc.preprocess(audio2, arrList2) > 0)
		{
			return Double.POSITIVE_INFINITY;
		}
		else
		{

			double[] sample2 = new double[arrList2.size()];

			for (int i = 0; i < arrList2.size(); ++i)
			{
				sample2[i] = arrList2.get(i);
			}
			double[][] ts1 = new double[feature1.length][m_featurenum];
			for (int i = 0; i < feature1.length; ++i)
			{
				ts1[i] = Arrays.copyOfRange(feature1[i], 0, m_featurenum - 1);
			}

			// double[][] ts2 = new double[sample2.length][];
			double[][] ts2 = mfcc.doMFCC(sample2, 0.02, 0.01);

			final TimeSeries tsI = new TimeSeries(ts1);
			final TimeSeries tsJ = new TimeSeries(ts2);
			final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, m_FDtwRadius, m_distFn);
			return info.getDistance();
		}
	}

	public double doFDtwMatch(short[] audio1, short[] audio2)
	{
		ArrayList<Double> arrList1 = new ArrayList<Double>();
		ArrayList<Double> arrList2 = new ArrayList<Double>();
		if (mfcc.preprocess(audio1, arrList1) > 0 || mfcc.preprocess(audio2, arrList2) > 0)
		{
			return Double.POSITIVE_INFINITY;
		}
		else
		{
			double[] sample1 = new double[arrList1.size()];
			double[] sample2 = new double[arrList2.size()];

			for (int i = 0; i < arrList1.size(); i++)
			{
				sample1[i] = arrList1.get(i);
				System.out.println(Integer.toString(i) + ' ' + Double.toString(sample1[i]));
			}
			for (int i = 0; i < arrList2.size(); i++)
			{
				sample2[i] = arrList2.get(i);
				System.out.println(Integer.toString(i) + ' ' + Double.toString(sample2[i]));
			}
			final TimeSeries tsI;
			final TimeSeries tsJ;

			if (m_typeofmatch)
			{

				double[][] ts1 = new double[sample1.length][m_featurenum];
				double[][] ts2 = new double[sample2.length][m_featurenum];
				ts1 = mfcc.doMFCC(sample1, 0.02, 0.01);
				ts2 = mfcc.doMFCC(sample2, 0.02, 0.01);
				tsI = new TimeSeries(ts1);
				tsJ = new TimeSeries(ts2);
			}
			else
			{
				double[][] ts1 = new double[sample1.length][1];
				double[][] ts2 = new double[sample2.length][1];
				for (int i = 0; i < sample1.length; ++i)
				{
					ts1[i][0] = sample1[i];
				}
				for (int i = 0; i < sample2.length; ++i)
				{
					ts2[i][0] = sample2[i];
				}
				tsI = new TimeSeries(ts1);
				tsJ = new TimeSeries(ts2);
			}
			final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, m_FDtwRadius, m_distFn);
			return info.getDistance();
		}
	}

	// TODO: Implement this.
	public static void saveBufferToFile(byte[] buffer, File file)
	{

	}

	// TODO: Implement this.
	public static byte[] loadBufferFromFile(File file)
	{
		return null;
	}
}