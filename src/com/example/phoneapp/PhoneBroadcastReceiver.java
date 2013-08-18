package com.example.phoneapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Load preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Check phone state
		String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i("z", "broadcast receive " + phoneState);

		String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

		if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING))
		{
			// TODO: Do I need to put second call on hold when I answer one?
			// Check for "second call" restriction
			if (prefs.getBoolean("no_second_call", false))
			{
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				if (am.getMode() == AudioManager.MODE_IN_CALL)
				{
					return;
				}
			}

			// Start tts service.
			Intent ttsIntent = new Intent(context, TTSService.class);
			ttsIntent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
			context.startService(ttsIntent);

			// Start autoAnswerIntentService.
			context.startService(new Intent(context, AutoAnswerIntentService.class));
		}
	}
}
