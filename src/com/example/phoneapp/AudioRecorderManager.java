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
	static final int frequency = 44100;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	final int recordBufferSize;
	final int playBufferSize;
	final int sizeFactor = 50;

	AudioRecord audioRecord = null;
	AudioTrack audioTrack = null;

	byte[] buffer = null;

	public AudioRecorderManager()
	{
		recordBufferSize = sizeFactor * AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

		Log.i("z", "record buffer size" + recordBufferSize);

		playBufferSize = sizeFactor * AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

		Log.i("z", "play buffer size" + playBufferSize);

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recordBufferSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufferSize, AudioTrack.MODE_STREAM);

		audioTrack.setStereoVolume(0.7f, 0.7f);
	}

	public void startAudioRecorder()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Log.i("z", "start audio record = " + audioRecord);
				audioRecord.startRecording();
			}

		}).start();
	}

	public void stopAudioRecorder()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Log.i("z", "stop audio record = " + audioRecord);
				updateBuffer();
				audioRecord.stop();
			}
		}).start();
	}

	public void updateBuffer()
	{
		byte[] tmpBuffer = new byte[recordBufferSize];
		int bufferReadResult = audioRecord.read(tmpBuffer, 0, recordBufferSize);

		Log.i("z", "buffer read result = " + bufferReadResult);
		buffer = new byte[bufferReadResult];
		System.arraycopy(tmpBuffer, 0, buffer, 0, bufferReadResult);
	}

	public void playAudioRecord()
	{
		if (audioTrack == null)
		{
			Log.e("z", "Cannot play audio record because audioTrack has not been initialized.");
			return;
		}

		if (buffer == null)
		{
			Log.e("z", "Cannot play audio record because audioTrack does not have data.");
			return;
		}

		audioTrack.flush();
		audioTrack.play();
		audioTrack.write(buffer, 0, buffer.length);
		audioTrack.stop();
	}

	public void saveAudioFile(byte[] buffer, String filePath)
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

	public byte[] getAudioData()
	{
		if (audioRecord == null)
		{
			return null;
		}

		short buffer[] = new short[recordBufferSize];
		int bufferReadResult = audioRecord.read(buffer, 0, recordBufferSize);

		byte[] tmpBuf = new byte[bufferReadResult];
		System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
		return tmpBuf;
	}

	public void Destory()
	{
		if (audioRecord != null)
		{
			audioRecord.stop();
			audioRecord = null;
		}

		if (audioTrack != null)
		{
			audioTrack.stop();
			audioTrack.release();
			audioTrack = null;
		}

		buffer = null;
	}
}
