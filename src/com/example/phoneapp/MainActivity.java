package com.example.phoneapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Classes.Response;
import Classes.Contact;
import Classes.PhoneListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity
{
	// Data.
	private ArrayList<Response> 	userActions;
	private ArrayList<Contact> 		userContacts;

	// Controls.
	private TelephonyManager 		telephonyManager;
	private MediaRecorder 			recorder;

	// Variables.
	private String 					outputFileName;
	private TextView                textView;
	private File audiofile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void Init()
	{
		// Register phone listener.
		telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);  
		telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);  
		
		outputFileName = Environment.getExternalStorageDirectory() + "/Records/record1.3gp";
		
//		try
//		{
//			audiofile = File.createTempFile("test", ".3gp", Environment.getExternalStorageDirectory());
//		} catch (IOException e)
//		{
//			Log.e("AudioRecorder", "sdcard access error");
//			return;
//		}
		
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		recorder.setOutputFile("/sdcard/audio_demo.3gp");
		//recorder.setOutputFile(outputFileName);
		
		Log.i("AudioRecorder", "Recorder initializes successfully!");
		
		textView = (TextView)findViewById(R.id.MessageBox);
	}
	
	public void RecordClick(View v)
	{
		Log.i("event","Record Click!");
		textView.setText("Recording");
		
		try
		{
			recorder.prepare();
			recorder.start();
		}
		catch (IllegalStateException e)
		{
			textView.setText("Failed to prepare.");
			Log.e("AudioRecorder", e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("AudioRecorder", e.getMessage());
		}
		
		Log.i("AudioRecorder", "Record Start!");
	}
	
	public void StopClick(View v)
	{
		Log.i("event", "Stop Click!");
		
		try
		{
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		catch (IllegalStateException e)
		{
			textView.setText("Failed to prepare.");
			Log.e("AudioRecorder", e.getMessage());
		}
		
		Log.i("AudioRecorder", "Recorder Stop!");
	}
	
	
}
