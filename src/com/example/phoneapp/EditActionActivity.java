package com.example.phoneapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class EditActionActivity extends Activity
{
	private MediaRecorder recorder;
	private MediaPlayer mediaPlayer;
	private File audioFile;
	private String fileName = "test";
	private boolean isRecording = false;

	// Variables.
	private TextView textView;
	private String logText;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_action);

		initRecorder();
		initPlayer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_action, menu);
		return true;
	}

	public void initRecorder()
	{
		isRecording = false;

		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		Log.v("sdcard Access", sdcardExist + "");

		try
		{
			audioFile = File.createTempFile(fileName, ".3gp",
					Environment.getExternalStorageDirectory());
		} catch (IOException e)
		{
			Log.e("z", "sdcard access error");
		}

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audioFile.getPath());

		Log.i("z", "Recorder initializes successfully!");
	}

	public void initPlayer()
	{
		// TODO: If file is empty then we cannot create a media player object.
		if (audioFile.length() == 0)
		{
			Log.e("z", "audio file does not exist.");
		} else
		{

			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			try
			{
				ScreenLog("Audio File path is " + audioFile.getPath());
				Log.i("z", "Audio File path is " + audioFile.getPath());
				FileInputStream inputStream = new FileInputStream(audioFile);
				mediaPlayer.setDataSource(inputStream.getFD());

				ScreenLog("Player initializes successfully!");
				Log.i("z", "Player initializes successfully!");
			} catch (IllegalArgumentException e)
			{
				ScreenLog(e.getMessage());
				Log.e("z", e.getMessage());
			} catch (SecurityException e)
			{
				ScreenLog(e.getMessage());
				Log.e("z", e.getMessage());
			} catch (IllegalStateException e)
			{
				ScreenLog("z" + e.getMessage());
				Log.e("z", e.getMessage());
			} catch (IOException e)
			{
				ScreenLog(e.getMessage());
				Log.e("z", e.getMessage());
			}
		}
	}

	public void RecordClick(View v)
	{
		Log.i("Event", "Record Click!");

		if (!isRecording)
		{
			try
			{
				Log.i("z", "Record prepare!");
				recorder.prepare();

				Log.i("z", "Record Start!");
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
	}

	public void StopClick(View v)
	{
		Log.i("z", "Stop click!");
		if (isRecording)
		{
			try
			{
				Log.i("z", "Record Start!");
				recorder.stop();

				Log.i("z", "Release recorder!");
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
		// TODO: Testing code need to be deleted.
		initPlayer();

		Log.i("z", "Play the save file");
		try
		{
			Log.i("z", "player prepare");
			mediaPlayer.prepare();
			
			Log.i("z", "player start");
			//mediaPlayer.start();
		} catch (IllegalStateException e)
		{
			//ScreenLog(e.getMessage());
			Log.e("z", e.getMessage());
		} catch (IOException e)
		{
			//ScreenLog(e.getMessage());
			Log.e("z", e.getMessage());
		}
	}

	public void Pause(View v)
	{
		mediaPlayer.stop();
	}

	public void Save()
	{

	}

	public void Load()
	{

	}

	public void ScreenLog(String text)
	{
		if (textView == null)
		{
			textView = (TextView) findViewById(R.id.MessageBox);
		}

		logText = logText + text + "\n";
		textView.setText(logText);
	}
}
