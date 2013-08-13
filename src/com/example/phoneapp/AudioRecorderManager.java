package com.example.phoneapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

import com.mfcc.MFCC;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public class AudioRecorderManager
{
	static final int frequency = 8000;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	final int recordBufferSize;
	final int playBufferSize;
	final int totalSeconds = 5;

	AudioRecord audioRecord = null;
	AudioTrack audioTrack = null;

	short[] buffer = null;

	static AudioRecorderManager instance;

	private AudioRecorderManager()
	{
		recordBufferSize = totalSeconds * frequency;

		AppLog.i("record buffer size" + recordBufferSize);

		playBufferSize = totalSeconds * frequency;

		AppLog.i("play buffer size" + playBufferSize);

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recordBufferSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufferSize, AudioTrack.MODE_STREAM);

		audioTrack.setStereoVolume(0.8f, 0.8f);
	}

	public static AudioRecorderManager getInstance()
	{
		if (instance == null)
		{
			instance = new AudioRecorderManager();
		}

		return instance;
	}

	public void startAudioRecorder()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				AppLog.i("start audio record = " + audioRecord);

				if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
				{
					AppLog.i("AudioRecord has not been initialized.");
					return;
				}

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
				AppLog.i("stop audio record = " + audioRecord);
				if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
				{
					AppLog.i("AudioRecord has not been initialized.");
					return;
				}

				updateBuffer();
				audioRecord.stop();
			}
		}).start();
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
		AppLog.i("play audio track");
		audioTrack.stop();
	}

	private void updateBuffer()
	{
		short[] tmpBuffer = new short[recordBufferSize];
		int bufferReadResult = audioRecord.read(tmpBuffer, 0, recordBufferSize);

		AppLog.i("buffer read result = " + bufferReadResult);
		buffer = new short[bufferReadResult];
		System.arraycopy(tmpBuffer, 0, buffer, 0, bufferReadResult);
	}

	public short[] getAudioData()
	{
		return buffer;
	}
	
	// TODO: Pass in a file object.
	public void saveVectorToFile(final String folderName, final String fileName)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO: add a maximum wait time.
				while (true)
				{
					if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED)
					{
						break;
					}
				}
				
				AppLog.i("Save file.");
				MFCC mfcc = new MFCC(13, frequency, 24, 256, true, 22, true);
				ArrayList<Double> arrList = new ArrayList<Double>();
				AppLog.i("buffer = " + buffer.length);
				mfcc.preprocess(buffer, arrList);

				double[] sample = new double[arrList.size()];
				for (int i = 0; i < arrList.size(); i++)
				{
					sample[i] = arrList.get(i);
				}

				double[][] featureVec = mfcc.doMFCC(sample, 0.02, 0.01);
				
				File file = FileManager.createFolderAndFile(folderName, fileName);
				OutputStreamWriter os;
				try
				{
					os = new OutputStreamWriter(new FileOutputStream(file));
					try
					{
						int rowNumber = featureVec.length;
						int colNumber = featureVec[0].length;
						AppLog.i("row = " + rowNumber);
						AppLog.i("col = " + colNumber);

						for (int i = 0; i < rowNumber; ++i)
						{
							for (int j = 0; j < colNumber; ++j)
							{
								os.write(featureVec[i][j] + " ");
							}

							os.write("\r\n");
						}

						AppLog.i("Save successfully.");
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					os.close();
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	// Implement this function.
	public void readVectorFile(File file)
	{
		
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
		instance = null;
	}
}
