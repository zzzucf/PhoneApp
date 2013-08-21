package com.example.phoneapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.mfcc.MFCC;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class AudioRecorderManager
{
	static final int frequency = 8000;
	@SuppressWarnings("deprecation")
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	final int recordBufferSize;
	final int playBufferSize;
	final int totalSeconds = 5;

	AudioRecord audioRecord = null;
	AudioTrack audioTrack = null;

	short[] buffer = null;

	boolean saveFeatureSuccess = false;
	boolean saveClipSuccess = false;

	static AudioRecorderManager instance;

	// Constructor.
	private AudioRecorderManager()
	{
		recordBufferSize = totalSeconds * frequency;
		playBufferSize = totalSeconds * frequency;

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recordBufferSize);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufferSize, AudioTrack.MODE_STREAM);
	}

	// Singleton get instance object.
	public static AudioRecorderManager getInstance()
	{
		if (instance == null)
		{
			instance = new AudioRecorderManager();
		}

		return instance;
	}

	// Start audio recorder with a separate thread.
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

	// Stop audio recorder with a separate thread.
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

				if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
				{
					short[] tmpBuffer = new short[recordBufferSize];
					int bufferReadResult = audioRecord.read(tmpBuffer, 0, recordBufferSize);

					AppLog.i("buffer read result = " + bufferReadResult);
					buffer = new short[bufferReadResult];
					System.arraycopy(tmpBuffer, 0, buffer, 0, bufferReadResult);

					audioRecord.stop();
				}
			}
		}).start();
	}

	// Play audio buffer.
	public void playAudioRecord(short[] data)
	{
		AppLog.i("Start play audio.");

		if (audioTrack == null)
		{
			AppLog.e("Cannot play audio record because audioTrack has not been initialized.");
			return;
		}

		if (data == null)
		{
			AppLog.e("Cannot play audio record because audioTrack does not have data.");
			return;
		}

		// Wait audioRecord to stop.
		while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED)
		{
			try
			{
				Thread.sleep(200);
			} catch (InterruptedException e)
			{
				AppLog.e(e.getMessage());
			}
		}

		audioTrack.flush();
		audioTrack.play();
		audioTrack.write(data, 0, data.length);
		audioTrack.stop();

		AppLog.i("Play audio success.");
	}

	public void playAudioRecord()
	{
		playAudioRecord(buffer);
	}
	
	// Return buffer.
	public short[] GetAudioBuffer()
	{
		short[] tmpBuffer = new short[recordBufferSize];
		int bufferReadResult = audioRecord.read(tmpBuffer, 0, recordBufferSize);

		return tmpBuffer;
	}

	// Save feature to a file.
	public void saveFeatureToFile(final File file)
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				saveFeatureSuccess = false;

				while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED)
				{
					try
					{
						Thread.sleep(200);
					} catch (InterruptedException e)
					{
						AppLog.e(e.getMessage());
					}
				}

				MFCC mfcc = new MFCC(13, frequency, 24, 256, true, 22, true);
				AppLog.i("buffer = " + buffer.length);

				ArrayList<Double> arrList = new ArrayList<Double>();
				mfcc.preprocess(buffer, arrList);

				double[] sample = new double[arrList.size()];
				for (int i = 0; i < arrList.size(); i++)
				{
					sample[i] = arrList.get(i);
				}

				double[][] featureVec = mfcc.doMFCC(sample, 0.02, 0.01);

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

						os.write(rowNumber + "\r\n");
						os.write(colNumber + "\r\n");

						for (int i = 0; i < rowNumber; ++i)
						{
							for (int j = 0; j < colNumber; ++j)
							{
								os.write(featureVec[i][j] + " ");
							}

							os.write("\r\n");
						}

					} catch (IOException e)
					{
						AppLog.e(e.getMessage());
					}

					os.close();
				} catch (FileNotFoundException e)
				{
					AppLog.e(e.getMessage());
				} catch (IOException e)
				{
					AppLog.e(e.getMessage());
				}

				saveFeatureSuccess = true;
				AppLog.i("Feature saved successfully.");
			}
		});
		t.start();
		
		AudioMatchingManager.getInstance().UpdateFeatures();
	}

	// Save audio buffer to a file.
	public void saveAudioBufferToFile(final File file)
	{
		AppLog.i("Start!!!");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				saveClipSuccess = false;

				while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED)
				{
					try
					{
						Thread.sleep(200);
					} catch (InterruptedException e)
					{
						AppLog.e(e.getMessage());
					}
				}

				AppLog.i("Start saving.");
				OutputStreamWriter os = null;
				try
				{
					os = new OutputStreamWriter(new FileOutputStream(file));
					AppLog.i("os = " + os);
				} catch (FileNotFoundException e)
				{
					AppLog.e(e.getMessage());
				}

				for (int i = 0; i < buffer.length; ++i)
				{
					try
					{
						os.write(buffer[i] + " ");
					} catch (IOException e)
					{
						AppLog.e(e.getMessage());
					}
				}

				try
				{
					os.close();
				} catch (IOException e)
				{
					AppLog.i(e.getMessage());
				}

				saveClipSuccess = true;
				AppLog.i("Feature saved successfully.");
			}
		}).start();
	}

	// Load feature from file.
	public double[][] loadFeatureFromFile(File file)
	{
		AppLog.i("Start loading feature " + file.getName() + ".");
		InputStream input;
		try
		{
			input = new FileInputStream(file);
		} catch (FileNotFoundException e)
		{
			AppLog.e(e.getMessage());
			return null;
		}

		InputStreamReader streamReader = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(streamReader);

		String line = "";
		int rowNumber = 0;
		int colNumber = 0;
		double[][] featureVector = null;

		try
		{
			rowNumber = Integer.parseInt(reader.readLine());
			colNumber = Integer.parseInt(reader.readLine());

			featureVector = new double[rowNumber][colNumber];
			int index = 0;
			do
			{
				line = reader.readLine();
				if (line == null)
				{
					break;
				}
				
				String[] data = line.split(" ");
				for (int i = 0; i < data.length; ++i)
				{
					featureVector[index][i] = Double.parseDouble(data[i]);
				}

				index++;
			} while (line != null);
		} catch (IOException e)
		{
			AppLog.e(e.getMessage());
		}

		// Close reader.
		try
		{
			reader.close();
		} catch (IOException e)
		{
			AppLog.e(e.getMessage());
		}

		AppLog.i("Load feature successfully.");
		return featureVector;
	}

	// Load audio buffer from file.
	public void loadAudioBufferFromFile(File file)
	{
		AppLog.i("loadAudioBufferFromFile start.");

		InputStream input;
		try
		{
			input = new FileInputStream(file);
		} catch (FileNotFoundException e)
		{
			return;
		}

		InputStreamReader streamReader = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(streamReader);

		String line = "";
		short[] tmpBuffer = null;
		try
		{
			line = reader.readLine();
			String[] data = line.split(" ");
			tmpBuffer = new short[data.length];
			for (int i = 0; i < data.length; ++i)
			{
				tmpBuffer[i] = Short.parseShort(data[i]);
			}

			buffer = tmpBuffer;

			reader.close();
		} catch (IOException e)
		{
			AppLog.e(e.getMessage());
		}

		AppLog.i("loadAudioBufferFromFile success.");
	}

	// Release all resource created.
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
