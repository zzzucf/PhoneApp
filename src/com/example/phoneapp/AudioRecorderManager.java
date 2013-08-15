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
	
	public void playAudioRecord()
	{
		if (audioTrack == null)
		{
			AppLog.e("Cannot play audio record because audioTrack has not been initialized.");
			return;
		}
		
		if (buffer == null)
		{
			AppLog.e("Cannot play audio record because audioTrack does not have data.");
			return;
		}
		
		// Wait audioRecord to stop.
		while(audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED)
		{
			try
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				AppLog.e(e.getMessage());
			}
		}
		
		audioTrack.flush();
		audioTrack.play();
		audioTrack.write(buffer, 0, buffer.length);
		audioTrack.stop();
	}
	
	public void saveVectorToFile(final File file)
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED)
				{
					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException e)
					{
						AppLog.e(e.getMessage());
					}
				}
				
				AppLog.i("Save file.");
				
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
						
						AppLog.i("Save successfully.");
					}
					catch (IOException e)
					{
						AppLog.e(e.getMessage());
					}
					os.close();
				}
				catch (FileNotFoundException e)
				{
					AppLog.e(e.getMessage());
				}
				catch (IOException e)
				{
					AppLog.e(e.getMessage());
				}
			}
		});
		t.start();
		
	}
	
	public void saveAudioBufferToFile(final File file)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				OutputStreamWriter os = null;
				try
				{
					os = new OutputStreamWriter(new FileOutputStream(file));
				}
				catch (FileNotFoundException e)
				{
					AppLog.e(e.getMessage());
				}
				
				try
				{
					os.write("abcd");
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				for (int i = 0; i < buffer.length; ++i)
//				{
//					try
//					{
//						os.write(buffer[i] + " ");
//					}
//					catch (IOException e)
//					{
//						AppLog.e(e.getMessage());
//					}
//				}
				
			}
		}).start();
	}
	
	public double[] loadVectorFromFile(File file)
	{
		InputStream input;
		try
		{
			input = new FileInputStream(file);
			
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		
		InputStreamReader streamReader = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(streamReader);
		
		String line = "";
		int rowNumber = 0;
		int colNumber = 0;
		try
		{
			rowNumber = Integer.parseInt(reader.readLine());
			colNumber = Integer.parseInt(reader.readLine());
			
			AppLog.i("row = " + rowNumber);
			AppLog.i("col = " + colNumber);
			
			double[][] featureVector = new double[rowNumber][colNumber];
			int index = 0;
			do
			{
				line = reader.readLine();
				String[] data = line.split(" ");
				for (int i = 0; i < data.length; ++i)
				{
					featureVector[index][i] = Double.parseDouble(data[i]);
				}
				
				index++;
			}
			while (line != null);
		}
		catch (IOException e)
		{
			AppLog.e(e.getMessage());
		}
		
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			AppLog.e(e.getMessage());
		}
		return null;
	}
	
	// Load audio buffer from file.
	public void loadAudioBufferFromFile(File file)
	{
		InputStream input;
		try
		{
			input = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
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
		}
		catch (IOException e)
		{
			AppLog.e(e.getMessage());
		}
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
