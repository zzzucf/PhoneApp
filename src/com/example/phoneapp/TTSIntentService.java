package com.example.phoneapp;

import java.util.Locale;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TTSIntentService extends IntentService
{
	private TextToSpeech tts;
	
	public TTSIntentService()
	{
		super("ttsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("z", "start tts intent service");
		Context context = getApplicationContext();
		tts = new TextToSpeech(context, new OnInitListener()
		{
			@Override
			public void onInit(int status)
			{
				if (status == TextToSpeech.SUCCESS)
				{
					// TODO: Read the language from share preference.
					int result = tts.setLanguage(Locale.US);

					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED)
					{
						Log.e("TTS", "This Language is not supported");
					}
					else 
					{
						readText("Hello world");
					}
				} else
				{
					Log.e("TTS", "Initilization Failed!");
				}
			}
		});
		
		String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		if (number != null)
		{
			readText(number);
		}
	}
	
	private void readText(String text)
	{
		Context context = getApplicationContext();
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(true);
		
		Log.i("z", "Ready to speak something");
		tts.speak("Hello world", TextToSpeech.QUEUE_FLUSH, null);
	}
	
	@Override
	public void onDestroy()
	{
		if (tts != null)
		{
			tts.stop();
			tts.shutdown();
		}

		super.onDestroy();
	}
}
