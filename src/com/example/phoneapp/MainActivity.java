package com.example.phoneapp;

import java.util.Locale;

import Enums.ActionEnum;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends Activity
{
	private PhoneBroadcastReceiver mBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Configuration config = getResources().getConfiguration();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		config.locale = Locale.SIMPLIFIED_CHINESE;
		getResources().updateConfiguration(config, dm);

		// Add fragment to record a audio and play a audio.
		addActionFragment(ActionEnum.Answer);
		addActionFragment(ActionEnum.Decline);
		addActionFragment(ActionEnum.Mute);

		// Register a broadcast receiver to receive phone state change event.
		registerPhoneBroadcastReceiver();

		// Change language test.
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener()
		{
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key)
			{
				Log.i("z", key);
			}
		});
	}

	public void addActionFragment(ActionEnum action)
	{
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		// Add the transaction.
		transaction.add(R.id.actionFragmentContainer,
				new ActionFragment(action));
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			moveTaskToBack(true);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void test(View v)
	{
		Log.i("z", "");
		Context context = getApplicationContext();
		Intent ttsIntent = new Intent(context, TTSIntentService.class);
		ttsIntent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, "4076835923");
		context.startService(ttsIntent);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		unregisterPhoneBroadcastReceiver();
	}
}
