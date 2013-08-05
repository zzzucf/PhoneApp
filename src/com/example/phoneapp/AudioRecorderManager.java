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
	private AudioRecord audioRecord = null;
	private AudioTrack audioTrack = null;

	int recBufSize, playBufSize;

	public AudioRecorderManager()
	{
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);

		playBufSize = AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration, audioEncoding, playBufSize,
				AudioTrack.MODE_STREAM);

		audioTrack.setStereoVolume(0.7f, 0.7f);
	}

	public void startAudioRecorder()
	{
		byte[] buffer = new byte[recBufSize];
		audioRecord.startRecording();// 开始录制
		audioTrack.play();// 开始播放

		// 从MIC保存数据到缓冲区
		int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);

		byte[] tmpBuf = new byte[bufferReadResult];
		System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
		// 写入数据即播放
		audioTrack.write(tmpBuf, 0, tmpBuf.length);
		
		audioTrack.stop();
		audioRecord.stop();
	}

	public void stopAudioRecorder()
	{
//		if (audioRecord != null)
//		{
//			audioRecord.stop();
//			audioRecord.release();
//			audioRecord = null;
//		}
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

	public void playAudioTrack(byte[] data)
	{
		
	}

	public byte[] getAudioData()
	{
		if (audioRecord == null)
		{
			return null;
		}

		short buffer[] = new short[recBufSize];
		int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);

		byte[] tmpBuf = new byte[bufferReadResult];
		System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
		return tmpBuf;
	}

	private byte[] short2byte(short[] sData)
	{
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++)
		{
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}

		return bytes;
	}
}
