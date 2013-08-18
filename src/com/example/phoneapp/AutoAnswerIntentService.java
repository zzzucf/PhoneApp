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

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AutoAnswerIntentService extends TelephonyIntentService
{
	private int MAXIMUMTRIES = 5;

	public AutoAnswerIntentService()
	{
		super("AutoAnswerIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("z", "start auto answer intent service");

		Context context = getBaseContext();

		// If the phone is not ringing then return.
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING)
		{
			return;
		}

		AudioRecorderManager.getInstance().startAudioRecorder();

		// Try matching the file.
		int result = tryMatchResult();

		AudioRecorderManager.getInstance().stopAudioRecorder();

		if (result == AudioMatchingManager.RESULT_NONE)
		{

		}
		else if (result == AudioMatchingManager.RESULT_ANSWER)
		{
			// Answer the phone
			try
			{
				answerPhoneCall(context);
			}
			catch (Exception e)
			{
				answerPhoneHeadsethook(context);
			}
		}
		else if (result == AudioMatchingManager.RESULT_DECLINE)
		{
			endPhoneCall(context);
		}
		else if (result == AudioMatchingManager.RESULT_MUTE)
		{
			mutePhoneCall(context);
		}

		return;
	}

	private int tryMatchResult()
	{
		for (int i = 0; i < MAXIMUMTRIES; ++i)
		{
			short[] buffer = AudioRecorderManager.getInstance().GetAudioBuffer();

			Log.i("z", "try match!");

			int result = AudioMatchingManager.getInstance().match(buffer);
			AppLog.i("matching result = " + result);

			if (result != AudioMatchingManager.RESULT_NONE)
			{
				return result;
			}

			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				AppLog.e(e.getMessage());
			}
		}

		return AudioMatchingManager.RESULT_NONE;
	}

	@Override
	public void onDestroy()
	{
		AudioRecorderManager.getInstance().Destory();
	}
}