package com.example.phoneapp;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditActionActivity extends Activity {
	
	private MediaRecorder recorder; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_action);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_action, menu);
		return true;
	}
	
	public void InitRecorder()
	{
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		
		// TODO: Choose a file format.
		recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
	}
	
	public void Record()
	{
		
	}
	
	public void Play()
	{
		
	}
	
	public void Pause()
	{
		
	}
	
	public void Save()
	{
		
	}
	
	public void Load()
	{
		
	}
}
