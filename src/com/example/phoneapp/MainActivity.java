package com.example.phoneapp;

import java.io.File;
import java.io.IOException;

import Enums.ActionEnum;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity
{
	private PhoneBroadcastReceiver mBroadcastReceiver;
	private File audioFile;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Add fragment to record a audio and play a audio.
		addActionFragment(ActionEnum.Answer);
		addActionFragment(ActionEnum.Decline);
		addActionFragment(ActionEnum.Mute);

		// Register a broadcast receiver to receive phone state change event.
		registerPhoneBroadcastReceiver();
	}

	public void addActionFragment(ActionEnum action)
	{
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		// Add the transaction.
		transaction.add(R.id.actionFragmentContainer, new ActionFragment(action));
		transaction.addToBackStack(null);

		// Commit the transaction.
		transaction.commit();
	}

	public void registerPhoneBroadcastReceiver()
	{
		Log.i("z", "register phone broadcast receiver");

		try
		{
			mBroadcastReceiver = new PhoneBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
			intentFilter.setPriority(Integer.MAX_VALUE);
			registerReceiver(mBroadcastReceiver, intentFilter);
		} catch (Exception e)
		{
			Log.e("z", e.toString());
		}
	}

	public void unregisterPhoneBroadcastReceiver()
	{
		Log.i("z", "unregister phone broadcast receiver");

		try
		{
			unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e)
		{
			Log.e("z", e.toString());
		}
	}
	
	
}
