package com.example.phoneapp;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditActionActivity extends Activity 
{
	private MediaRecorder 	recorder; 
	private MediaPlayer		mediaPlayer;
	private String 			filepath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_action);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_action, menu);
		return true;
	}
	
	public void Init(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException
	{
		// Initialize file path.
		filepath = path;
		
		// Initialize media recorder.
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// TODO: Choose a file format.
		recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		recorder.setOutputFile(filepath);
		
		// Initialize media player
		mediaPlayer.setDataSource(filepath);

	}
	
	public void Record() throws IllegalStateException, IOException
	{
		recorder.prepare();
		recorder.start();
	}
	
	public void Stop()
	{
		recorder.stop();
		recorder.release();
	}
	
	public void Play() throws IllegalStateException, IOException
	{
		mediaPlayer.prepare();
		mediaPlayer.start();
	}
	
	public void Pause()
	{
		mediaPlayer.pause();
	}
	
	public void Save()
	{
		
	}
	
	public void Load()
	{
		
	}
}
