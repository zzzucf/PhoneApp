/*
 * AutoAnswer
 * Copyright (C) 2010 EverySoft
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   Copyright (C) 2010 Tedd Scofield
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.phoneapp;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class AutoAnswerIntentService extends IntentService
{
	public AutoAnswerIntentService()
	{
		super("AutoAnswerIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("z", "start auto answer intent service");

		Context context = getBaseContext();

		// Make sure the phone is still ringing
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING)
		{
			return;
		}

		// TODO: Get audioRecorder setup and running. Try predict the voice.
		// TODO: Use audio source to predict what kind of voice command it is.
		
		boolean answer = true;
		if (answer)
		{
			// Answer the phone
			try
			{
				answerPhoneAidl(context);
			} catch (Exception e)
			{
				e.printStackTrace();
				Log.d("AutoAnswer",
						"Error trying to answer using telephony service.  Falling back to headset.");
				answerPhoneHeadsethook(context);
			}
		}

		return;
	}

	// TODO: Clean up this code.
	private void answerPhoneHeadsethook(Context context)
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

	private void answerPhoneAidl(Context context) throws Exception
	{
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Class<?> c = Class.forName(tm.getClass().getName());
		Method m = c.getDeclaredMethod("getITelephony");
		m.setAccessible(true);
		ITelephony telephonyService;
		telephonyService = (ITelephony) m.invoke(tm);

		// Silence the ringer and answer the call!
		telephonyService.silenceRinger();
		telephonyService.answerRingingCall();
	}
}