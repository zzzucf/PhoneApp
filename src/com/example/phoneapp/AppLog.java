package com.example.phoneapp;

import android.util.Log;

public class AppLog
{
	// If DEBUG is true, log all the message, else show nothing.
	final static boolean DEBUG = true;

	// Default tag for logging.
	final static String DEFAULT_TAG = "z";

	// Use log to display info message.
	public static void i(String msg)
	{
		if (DEBUG)
		{
			Log.i(DEFAULT_TAG, msg);
		}
	}

	// Use log to display info message.
	public static void i(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.i(tag, msg);
		}
	}

	// Use log to display error message.
	public static void e(String msg)
	{
		if (DEBUG)
		{
			Log.e(DEFAULT_TAG, msg);
		}
	}

	// Use log to display error message.
	public static void e(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.e(tag, msg);
		}
	}
}
