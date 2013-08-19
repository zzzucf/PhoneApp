package com.example.phoneapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import Enums.ActionEnum;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener
{
	private PhoneBroadcastReceiver mBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Init svm
		// initSvm();

		// Add fragment to record a audio and play a audio.
		addActionFragment(ActionEnum.Answer);
		addActionFragment(ActionEnum.Decline);
		addActionFragment(ActionEnum.Mute);

		setContentView(R.layout.activity_main);

		// Register a broadcast receiver to receive phone state change event.
		registerPhoneBroadcastReceiver();

		// Register a listener to apply preference change.
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		if (key == getString(R.string.key_enable))
		{
			onEnableChange(sharedPreferences);
		}
		else if (key == getString(R.string.key_language))
		{
			onLanguageChange(sharedPreferences);
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

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		unregisterPhoneBroadcastReceiver();
		mBroadcastReceiver = null;
	}

	public void addActionFragment(ActionEnum action)
	{
		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		// Add the transaction.
		transaction.add(R.id.actionFragmentContainer, new ActionFragment(action));
		transaction.addToBackStack(null);

		// Commit the transaction.
		transaction.commit();
	}

	public void registerPhoneBroadcastReceiver()
	{
		try
		{
			mBroadcastReceiver = new PhoneBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
			intentFilter.setPriority(Integer.MAX_VALUE);
			registerReceiver(mBroadcastReceiver, intentFilter);
		}
		catch (Exception e)
		{
			AppLog.e(e.getMessage());
		}
	}

	public void unregisterPhoneBroadcastReceiver()
	{
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (Exception e)
		{
			AppLog.e(e.getMessage());
		}
	}

	public void onEnableChange(SharedPreferences sharedPreferences)
	{
		if (sharedPreferences.getBoolean(getString(R.string.key_enable), true))
		{
			registerPhoneBroadcastReceiver();
		}
		else
		{
			unregisterPhoneBroadcastReceiver();
		}
	}
	
	public void onLanguageChange(SharedPreferences sharedPreferences)
	{
		String language = sharedPreferences.getString(getString(R.string.key_language), "");
		Configuration config = getResources().getConfiguration();

		if (language.toString().equals("Cn"))
		{
			config.locale = Locale.CHINESE;
		}
		else if (language.toString().equals("En"))
		{
			config.locale = Locale.ENGLISH;
		}

		getResources().updateConfiguration(config, getResources().getDisplayMetrics());

		restartActivity();
	}

	public void restartActivity()
	{
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	// TODO: Change svm to service.
	public void initSvm()
	{
		InputStream inputStream = getResources().openRawResource(R.raw.svm3);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		AppLog.i("start predicting");

		// Init svm with a separate thread.
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SvmRecognizer.getInstance().init(reader);
				Log.i("z", "class = " + SvmRecognizer.classes);
				Log.i("z", "nodes = " + SvmRecognizer.nodes);
				Log.i("z", "model = " + SvmRecognizer.model);
			}
		}).start();
	}

	// TODO: Testing buttons.
	public void predict(View v)
	{
		Log.i("z", "onclick");
		
	}
}
