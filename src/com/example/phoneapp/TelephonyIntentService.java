package com.example.phoneapp;

import java.lang.reflect.Method;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

public class TelephonyIntentService extends IntentService
{
	private ITelephony telephonyService;
	public TelephonyIntentService(String name)
	{
		super("TelephonyIntentService");
		
		try
		{
			telephonyService = getInterface();
		} catch (Exception e)
		{
			Log.e("z", "Cannot initialize telephony service.");
		}
	}

	// TODO: Clean up this code.
	protected void answerPhoneHeadsethook(Context context)
	{
		// Simulate a press of the headset button to pick up the call
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonDown,
				"android.permission.CALL_PRIVILEGED");

		// froyo and beyond trigger on buttonUp instead of buttonDown
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonUp,
				"android.permission.CALL_PRIVILEGED");
	}

	protected void answerPhoneAidl(Context context)
	{
		try
		{
			telephonyService.silenceRinger();
			telephonyService.answerRingingCall();
		} 
		catch (RemoteException e)
		{
			Log.e("z", "Fail to answer the phone");
		}
		
	}

	protected void endPhoneCall(Context context)
	{
		try
		{
			telephonyService.silenceRinger();
			telephonyService.endCall();
		} 
		catch (RemoteException e)
		{
			Log.e("z", "Fail to end the phone");
		}
		
	}

	protected void MutePhoneCall(Context context)
	{
		try
		{
			telephonyService.silenceRinger();
		} 
		catch (RemoteException e)
		{
			Log.e("z", "Fail to mute the phone");
		}
	}

	private ITelephony getInterface() throws Exception
	{
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Class<?> c = Class.forName(tm.getClass().getName());
		Method m = c.getDeclaredMethod("getITelephony");
		m.setAccessible(true);

		ITelephony telephonyService;
		telephonyService = (ITelephony) m.invoke(tm);

		return telephonyService;
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{

	}
}
