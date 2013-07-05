package com.example.phoneapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity
{
	private PhoneBroadcastReceiver mBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Add fragment to record a audio and play a audio.
		addActionFragment();

		// Register a broadcastreceiver to receive phone state change event.
		registerPhoneBroadcastReceiver();
	}

	public void addActionFragment()
	{
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		// TODO: Change replace to add and add more action fragment.
		transaction.replace(R.id.actionFragmentContainer, new ActionFragment());
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	public void registerPhoneBroadcastReceiver()
	{
		Log.i("z", "register phone broadcast receiver");

		mBroadcastReceiver = new PhoneBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		intentFilter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mBroadcastReceiver, intentFilter);
	}

	public void unregisterPhoneBroadcastReceiver()
	{
		Log.i("z", "unregisterThis");
		unregisterReceiver(mBroadcastReceiver);
	}
}
