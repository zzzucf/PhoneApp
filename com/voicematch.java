package com;

import java.util.ArrayList;

import com.dtw.TimeWarpInfo;
import com.mfcc.MFCC;
import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;
public class voicematch {

	/**
	 * @param args
	 */
	private final boolean m_typeofmatch; // if 'true', matching is done in mfcc feature space, otherwise raw samples are used 
	private final int m_FDtwRadius; // search radius for fast DTW
	
	private final DistanceFunction m_distFn; // distance function for DTW
	private MFCC mfcc; 
	
	
	public voicematch() {
		this(false, 13, 44100.0, 24, 256, "EuclideanDistance", 10);
	}
	
	public voicematch(boolean typeofmatch, int nMFCCs, double fs, int nFilters, int FFTlength, String distFn, int radius) {
		m_typeofmatch = typeofmatch;
		m_distFn = DistanceFunctionFactory.getDistFnByName(distFn);
		m_FDtwRadius = radius;
		mfcc = new MFCC(nMFCCs, fs, nFilters, FFTlength, true, 22, true);
	}
	public double doDtwMatch(short[] audio1, short[] audio2) {
		ArrayList<Double> arrList1 = new ArrayList<Double>();
		ArrayList<Double> arrList2 = new ArrayList<Double>();
		if(mfcc.preprocess(audio1, arrList1) > 0 || mfcc.preprocess(audio2, arrList2) > 0) {
			return Double.POSITIVE_INFINITY;
		} else {
			double[] sample1 = new double[arrList1.size()];
			double[] sample2 = new double[arrList2.size()]; 

			for (int i = 0; i < arrList1.size(); i ++) {
				sample1[i] = arrList1.get(i);
			}
			for (int i = 0; i < arrList2.size(); i ++) {
				sample2[i] = arrList2.get(i);
			}
			double[][] ts1 = new double[sample1.length][];
			double[][] ts2 = new double[sample2.length][];
			if (m_typeofmatch) {
				ts1 = mfcc.doMFCC(sample1, 0.02, 0.01);
				ts2 = mfcc.doMFCC(sample2, 0.02, 0.01);
			} else {
				for (int i = 0; i < sample1.length; ++i) {
					ts1[i][0] = sample1[i];
				}
				for (int i = 0; i < sample2.length; ++i) {
					ts2[i][0] = sample2[i];
				}
			}
			final TimeSeries tsI = new TimeSeries(ts1);
			final TimeSeries tsJ = new TimeSeries(ts2);
			final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, m_distFn);
			return info.getDistance();
		}
	}
	public double doFDtwMatch(short[] audio1, short[] audio2) {
		ArrayList<Double> arrList1 = new ArrayList<Double>();
		ArrayList<Double> arrList2 = new ArrayList<Double>();
		if(mfcc.preprocess(audio1, arrList1) > 0 || mfcc.preprocess(audio2, arrList2) > 0) {
			return Double.POSITIVE_INFINITY;
		} else {
			double[] sample1 = new double[arrList1.size()];
			double[] sample2 = new double[arrList2.size()]; 

			for (int i = 0; i < arrList1.size(); i ++) {
				sample1[i] = arrList1.get(i);
				System.out.println(Integer.toString(i) + ' ' + Double.toString(sample1[i]));
			}
			for (int i = 0; i < arrList2.size(); i ++) {
				sample2[i] = arrList2.get(i);
				System.out.println(Integer.toString(i) + ' ' + Double.toString(sample2[i]));
			}
			final TimeSeries tsI;
			final TimeSeries tsJ;
			
			if (m_typeofmatch) {
				
				double[][] ts1 = new double[sample1.length][13];
				double[][] ts2 = new double[sample2.length][13];
				ts1 = mfcc.doMFCC(sample1, 0.02, 0.01);
				ts2 = mfcc.doMFCC(sample2, 0.02, 0.01);
				tsI = new TimeSeries(ts1);
				tsJ = new TimeSeries(ts2);
			} else {
				double[][] ts1 = new double[sample1.length][1];
				double[][] ts2 = new double[sample2.length][1];
				for (int i = 0; i < sample1.length; ++i) {
					ts1[i][0] = sample1[i];
				}
				for (int i = 0; i < sample2.length; ++i) {
					ts2[i][0] = sample2[i];
				}
				tsI = new TimeSeries(ts1);
				tsJ = new TimeSeries(ts2);
			}
			final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, m_FDtwRadius, m_distFn);
			return info.getDistance();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		short[] audio1 = {0, 0, 0, 0 , 0 , 0 , 0, 1, 2, 3, 4, 5, 6, 9, 10, 3, 5, 6, 9 , 2 , 3, 0, 0, 0, 0, 0};
		short[] audio2 = {0, 0, 0, 0, 0, 0, 1, 2, 4, 4, 5, 6, 8, 9, 10, 10, 8, 8 , 9, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0};
		voicematch vm = new voicematch(false, 13, 10.0, 24, 32, "EuclideanDistance", 10);
		
		long startTime = System.nanoTime(); 
		vm.doFDtwMatch(audio1, audio2);	
		long endTime = System.nanoTime();
        
        long duration = endTime - startTime;
        System.out.println("Computing Time: " + String.valueOf(duration/1e6) + "ms");
	}

}
