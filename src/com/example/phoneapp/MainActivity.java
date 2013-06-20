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
import android.content.Intent;
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

	// Variables.
	private TextView                textView;
	private String 					logText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public void ActionClick(View v)
	{
		Log.i("z", "Action click");
		
		Intent intent = new Intent(this, EditActionActivity.class);
		startActivity(intent);
	}
	
	public void ScreenLog(String text)
	{
		logText = logText + text + "\n";
		textView.setText(logText);
	}
	
}
