package com.example.phoneapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.mfcc.MFCC;

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

		Log.i("z", "record buffer size" + recordBufferSize);

		playBufferSize = totalSeconds * frequency;

		Log.i("z", "play buffer size" + playBufferSize);

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recordBufferSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufferSize, AudioTrack.MODE_STREAM);

		audioTrack.setStereoVolume(0.7f, 0.7f);
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
				Log.i("z", "start audio record = " + audioRecord);

				if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
				{
					Log.i("z", "AudioRecord has not been initialized.");
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
				Log.i("z", "stop audio record = " + audioRecord);
				if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)
				{
					Log.i("z", "AudioRecord has not been initialized.");
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
		Log.i("z", "play audio track");
		audioTrack.stop();
	}

	private void updateBuffer()
	{
		short[] tmpBuffer = new short[recordBufferSize];
		int bufferReadResult = audioRecord.read(tmpBuffer, 0, recordBufferSize);

		Log.i("z", "buffer read result = " + bufferReadResult);
		buffer = new short[bufferReadResult];
		System.arraycopy(tmpBuffer, 0, buffer, 0, bufferReadResult);
	}

	public short[] getAudioData()
	{
		return buffer;
	}

	public void saveVectorToFile(final String fileName)
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

				Log.i("z", "Save file.");
				MFCC mfcc = new MFCC(13, frequency, 24, 256, true, 22, true);

				ArrayList<Double> arrList = new ArrayList<Double>();

				Log.i("z", "buffer = " + buffer.length);

				mfcc.preprocess(buffer, arrList);

				double[] sample = new double[arrList.size()];
				for (int i = 0; i < arrList.size(); i++)
				{
					sample[i] = arrList.get(i);
				}

				double[][] featureVec = mfcc.doMFCC(sample, 0.02, 0.01);

				File file = FileManager.createAudioFile(fileName, "AutoAnswerCall");

				OutputStreamWriter os;
				try
				{
					os = new OutputStreamWriter(new FileOutputStream(file));
					try
					{
						int rowNumber = featureVec.length;
						int colNumber = featureVec[0].length;
						Log.i("z", "row = " + rowNumber);
						Log.i("z", "col = " + colNumber);

						for (int i = 0; i < rowNumber; ++i)
						{
							for (int j = 0; j < colNumber; ++j)
							{
								os.write(featureVec[i][j] + " ");
							}

							os.write("\r\n");
						}

						Log.i("z", "Save successfully.");
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
