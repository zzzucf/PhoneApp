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

public class TTSIntentService extends IntentService implements OnInitListener
{
	private TextToSpeech tts;
	private String number;
	
	public TTSIntentService()
	{
		super("ttsIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Context context = getApplicationContext();

		number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		if (number != null && number != "")
		{
			tts = new TextToSpeech(context, this);
		}
	}

	@Override
	public void onInit(int status)
	{
		if (status == TextToSpeech.SUCCESS)
		{
			// TODO: Read the language from share preference.
			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Log.e("TTS", "This Language is not supported");
			}
			else 
			{
				speak("Calling from " + number);
			}
		} else
		{
			Log.e("TTS", "Initilization Failed!");
		}
	}
	
	private void speak(String text)
	{
		Context context = getApplicationContext();
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(true);
		
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}
