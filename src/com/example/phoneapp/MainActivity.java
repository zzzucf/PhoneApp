package com.example.phoneapp;

import java.util.ArrayList;

import Classes.Response;
import Classes.Contact;
import Classes.PhoneListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;

public class MainActivity extends Activity
{
	// Data.
	private ArrayList<Response> 	userActions;
	private ArrayList<Contact> 		userContacts;

	// Control.
	private TelephonyManager 		telephonyManager;
	

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
	}
}
