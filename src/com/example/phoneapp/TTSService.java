package com.example.phoneapp;

import java.util.Locale;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TTSService extends Service implements OnInitListener
{
	private TextToSpeech tts;
	private String number;
	private String contactName;
	
	@Override
	public void onStart(Intent intent, int reslut)
	{
		number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		contactName = getContactName(number);

		if (number != null && number !="")
		{
			tts = new TextToSpeech(getApplicationContext(), this);
		}
	}
	
	private void speak(String text)
	{
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	private String getContactName(String number) 
	{
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    String name = "";

	    ContentResolver contentResolver = getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    try
	    {
	        if (contactLookup != null && contactLookup.getCount() > 0) 
	        {
	            contactLookup.moveToNext();
	            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
	        }
	    } 
	    finally 
	    {
	        if (contactLookup != null) 
	        {
	            contactLookup.close();
	        }
	    }

	    return name;
	}
	
	private String numberToString(String number)
	{
		// TODO: This function only works for english version. But for chinese version, we need to add something more.
		
		number = number.replaceAll("1", "one ");
		number = number.replaceAll("2", "two ");
		number = number.replaceAll("3", "three ");
		number = number.replaceAll("4", "four ");
		number = number.replaceAll("5", "five ");
		number = number.replaceAll("6", "six ");
		number = number.replaceAll("7", "seven ");
		number = number.replaceAll("8", "eight ");
		number = number.replaceAll("9", "nine ");
		number = number.replaceAll("0", "zero ");
		
		return number;
	}
	
	private void enableSpeaker()
	{
		Context context = getApplicationContext();
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(true);
	}
	
	@Override
	public void onDestroy() 
	{
		Log.i("z", "destory tts.");
		
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
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
				enableSpeaker();
				speak("Calling from " + contactName + numberToString(number));
			}
		} 
		else
		{
			Log.e("TTS", "Initilization Failed!");
		}
	}
}
