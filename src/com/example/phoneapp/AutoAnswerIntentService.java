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
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING)
		{
			return;
		}

		// Try matching the file.
		int result = tryMatchResult();

		if (result == -1)
		{
			// Answer the phone
			try
			{
				answerPhoneAidl(context);
			} catch (Exception e)
			{
				answerPhoneHeadsethook(context);
			}
		}
		else if (result == 1)
		{
			// 
		}
		else if (result == 2)
		{
			endPhoneCall(context);
		}

		return;
	}

	private int tryMatchResult()
	{
		AudioMatchingManager manager = new AudioMatchingManager();

		for (int i = 0; i < MAXIMUMTRIES; ++i)
		{
			Log.i("z", "try match!");
			if (manager.match() != -1)
			{
				return manager.match();
			}

			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return -1;
	}

}