package com.example.phoneapp;

import android.media.MediaRecorder;

public class AppManager
{
	// Media Recorder.
	private MediaRecorder recorder; 
	
	// Constructor.
	public AppManager()
	{
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		
		// TODO: Choose a file format.
		recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
	}
	
	// Get the data from device.
	public boolean GetData()
	{
		// If get the data successfully, return true; else return false.
		return true;
	}
	
	// Save the data on device.
	public boolean SaveData()
	{
		return true;
	}
}
