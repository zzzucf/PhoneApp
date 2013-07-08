package com.example.phoneapp;

import java.io.File;
import java.io.IOException;

import Enums.ActionEnum;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ActionFragment extends Fragment
{
	private ActionEnum actionName;
	private File audioFile;
	private String fileName;
	private MediaRecorder recorder;
	private boolean isRecording = false;

	// TODO: Check why this code need SupressLint.
	public ActionFragment(ActionEnum actionName)
	{
		super();

		this.actionName = actionName;
		this.fileName = actionName + "_clip";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.action_fragment, container, false);

		// Setup action name label.
		TextView actionNameLabel = (TextView) v
				.findViewById(R.id.LblActionName);
		actionNameLabel.setText(actionName + "");

		// Setup button on touch event.
		Button button = (Button) v.findViewById(R.id.BtnRecord);
		button.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
					{
						Log.i("z", "key down");
						
						Button btnRecord = (Button) v
								.findViewById(R.id.BtnRecord);
						btnRecord.setText("@string/label_stop");
						
						startRecording();
						break;
					}
					case MotionEvent.ACTION_UP:
					{
						Log.i("z", "key up");
						
						Button btnRecord = (Button) v
								.findViewById(R.id.BtnRecord);
						btnRecord.setText("@string/label_record");
						
						stopRecording();
						break;
					}
				}
				return true;
			}
		});

		return v;
	}

	public void startRecording()
	{
		Log.i("z", "Record");

		// If sdcard exists.
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		Log.v("sdcard Access", sdcardExist + "");

		// Create new file in sd card.
		try
		{
			audioFile = File.createTempFile(fileName, ".3gp",
					Environment.getExternalStorageDirectory());
		} catch (IOException e)
		{
			Log.e("z", "sdcard access error");
		}

		// Create new recorder.
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audioFile.getPath());

		Log.i("z", "Recorder initializes successfully!");

		try
		{
			Log.i("z", "Recorder prepare!");
			recorder.prepare();

			Log.i("z", "Recorder Start!");
			recorder.start();

			isRecording = true;
		} catch (IllegalStateException e)
		{
			Log.e("z", e.getMessage());
		} catch (IOException e)
		{
			Log.e("z", e.getMessage());
		}
	}

	public void stopRecording()
	{
		Log.i("z", "stop recording");

		if (recorder != null)
		{
			try
			{
				Log.i("z", "Stop!");
				recorder.stop();

				Log.i("z", "Release!");
				recorder.release();
				recorder = null;
			} catch (IllegalStateException e)
			{
				Log.e("z", e.getMessage());
			}
		}
	}

	public void PlayClick(View v)
	{
		if (audioFile == null)
		{

		} else if (audioFile.length() == 0)
		{
			Log.e("z", "audio file does not exist.");
		} else if (!audioFile.canRead())
		{
			Log.e("z", "audio file can not be read.");
		} else
		{
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			try
			{
				Log.v("z", "Audio File path is " + audioFile.getPath());

				mediaPlayer.setDataSource(audioFile.getPath());
				mediaPlayer.prepare();
				mediaPlayer.start();

				Log.i("z", "Player initializes successfully!");
			} catch (IllegalArgumentException e)
			{
				Log.e("z", e.getMessage());
			} catch (SecurityException e)
			{
				Log.e("z", e.getMessage());
			} catch (IllegalStateException e)
			{
				Log.e("z", e.getMessage());
			} catch (IOException e)
			{
				Log.e("z", e.getMessage());
			}
		}
	}
}
