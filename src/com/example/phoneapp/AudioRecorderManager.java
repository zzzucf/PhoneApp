package com.example.phoneapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorderManager
{
	static final int frequency = 8000;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	int recBufSize;
	int playBufSize;
	static final int bufferFactor = 50;

	private AudioRecord audioRecord = null;
	private AudioTrack audioTrack = null;
	private byte[] buffer = null;

	public AudioRecorderManager()
	{
		recBufSize = bufferFactor
				* AudioRecord.getMinBufferSize(frequency, channelConfiguration,
						audioEncoding);

		playBufSize = bufferFactor
				* AudioTrack.getMinBufferSize(frequency, channelConfiguration,
						audioEncoding);

		if (audioRecord == null)
		{
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					frequency, channelConfiguration, audioEncoding, recBufSize);
		}

		if (audioTrack == null)
		{
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
					channelConfiguration, audioEncoding, playBufSize,
					AudioTrack.MODE_STREAM);
		}

		audioTrack.setStereoVolume(0.7f, 0.7f);
	}

	public void startAudioRecorder()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
				{
					buffer = new byte[recBufSize];
					
					audioRecord.startRecording();
					Log.i("z","start = " + audioRecord.getRecordingState());

					int bufferReadResult = audioRecord.read(buffer, 0,
							recBufSize);

					Log.i("z", "buffer length = " + buffer.length);
				}
			}
		}).start();
	}

	public void stopAudioRecorder()
	{
		if (audioRecord != null
				&& audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
		{
			audioRecord.stop();
			Log.i("z", "stop = " + audioRecord.getRecordingState());
		}
	}

	public void playAudioTrack()
	{
		if (buffer == null)
		{
			Log.i("z", "There is no buffer to be played.");
			return;
		}

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Log.i("z", "Start playing audio track.");

				audioTrack.flush();
				audioTrack.play();
				audioTrack.write(buffer, 0, buffer.length);
				audioTrack.stop();
				
				Log.i("z", "Stop playing audio track.");
			}
		}).start();
	}

	public void saveAudioFile(String filePath)
	{
		FileOutputStream outputFile = null;

		try
		{
			Log.i("z", "file path = " + filePath);
			outputFile = new FileOutputStream(filePath);
		} catch (FileNotFoundException e)
		{
			Log.e("z", "Cannot create file");
		}

		try
		{
			if (buffer != null)
			{
				outputFile.write(buffer);
			}
		} catch (IOException e)
		{
			Log.e("z", "Fail to write " + "" + "audio file.");
		}
	}

	public void destory()
	{
		if (audioRecord != null)
		{
			audioRecord.stop();
			audioRecord.release();
			audioRecord = null;
		}

		if (audioTrack != null)
		{
			audioTrack.stop();
			audioTrack.release();
			audioTrack = null;
		}
	}
}
