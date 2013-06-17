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

	private String 					logText;
	
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

		logText = "";
		textView = (TextView)findViewById(R.id.MessageBox);
		
		outputFileName = Environment.getExternalStorageDirectory() + "/Records/record1.3gp";
		
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		
		Log.v("Sdcard Access", sdcardExist+"");
		ScreenLog(sdcardExist+"");
		
		try
		{
			audiofile = File.createTempFile("test", ".3gp", Environment.getExternalStorageDirectory());
		} 
		catch (IOException e)
		{
			Log.e("AudioRecorder", "sdcard access error");
			ScreenLog("sdcard asscess error");
		}
		
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		recorder.setOutputFile(audiofile.getPath());
		//recorder.setOutputFile(outputFileName);
		
		Log.i("AudioRecorder", "Recorder initializes successfully!");
		ScreenLog("Recorder initializes successfully!");
		
		
	}
	
	public void RecordClick(View v)
	{
		Log.i("event","Record Click!");
		ScreenLog("Recording");
		
		// TODO: add if statement to avoid double prepare.
		try
		{
			recorder.prepare();
			recorder.start();
			
			Log.i("AudioRecorder", "Record Start!");
			ScreenLog("Record start!");
		}
		catch (IllegalStateException e)
		{
			Log.e("AudioRecorder", e.getMessage());
			ScreenLog(e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("AudioRecorder", e.getMessage());
			ScreenLog(e.getMessage());
		}
	}
	
	public void StopClick(View v)
	{
		Log.i("event", "Stop Click!");
		ScreenLog("Stop Click!");
		
		try
		{
			recorder.stop();
			recorder.release();
			recorder = null;
			
			Log.i("AudioRecorder", "Recorder Stop!");
			ScreenLog("Recorder Stop!");
		}
		catch (IllegalStateException e)
		{
			Log.e("AudioRecorder", e.getMessage());
			ScreenLog(e.getMessage());
		}
	}
	
	public void ScreenLog(String text)
	{
		logText = logText + text + "\n";
		textView.setText(logText);
	}
	
}
