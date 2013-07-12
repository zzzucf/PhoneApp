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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Runnable;

@SuppressLint("ValidFragment")
public class ActionFragment extends Fragment
{
	private ActionEnum actionName;
	private File audioFile;
	private String fileName;

	private MediaRecorder recorder;

	private Thread playRecordThread;

	public ActionFragment(ActionEnum actionName)
	{
		super();

		this.actionName = actionName;
		this.fileName = actionName + "_clip";
		this.initThread();
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

		// Setup button onTouch event.
		Button btnRecord = (Button) v.findViewById(R.id.BtnRecord);
		btnRecord.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
				{
					Log.i("z", "key down");

					Button btnRecord = (Button) v.findViewById(R.id.BtnRecord);
					btnRecord.setText(R.string.label_stop);

					try
					{
						Record();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case MotionEvent.ACTION_UP:
				{
					Log.i("z", "key up");

					Button btnRecord = (Button) v.findViewById(R.id.BtnRecord);
					btnRecord.setText(R.string.label_record);

					Stop();
					break;
				}
				}

				return true;
			}
		});

		// Setup button onClick event.
		Button btnPlay = (Button) v.findViewById(R.id.BtnPlay);
		btnPlay.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!playRecordThread.isAlive())
				{
					playRecordThread.start();
				}
			}
		});

		return v;
	}

	public void initThread()
	{
		playRecordThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				playRecord();
			}
		});
	}

	public void Record() throws IOException
	{
		audioFile = FileManager.createAudioFile(fileName, "VoiceAnswerCall");

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audioFile.getPath());

		recorder.prepare();
		recorder.start();
	}

	public void Stop()
	{
		if (recorder != null)
		{
			recorder.stop();
			recorder.release();
		}
	}

	public void playRecord()
	{
		Log.i("z", "play");

		if (audioFile == null)
		{
			Log.i("z", "audio file has not been created yet.");
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

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		recorder.release();
	}
}
