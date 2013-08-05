package com.example.phoneapp;

import java.io.File;
import java.io.IOException;

import Enums.ActionEnum;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
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

@SuppressLint("ValidFragment")
public class ActionFragment extends Fragment
{
	private ActionEnum actionName;
	private File audioFile;
	private String audiofileName;
	private String featureFileName;
	private MediaRecorder recorder;

	boolean isRecording = false;// 是否录放的标记
	static final int frequency = 44100;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int recBufSize, playBufSize;
	AudioRecord audioRecord;
	AudioTrack audioTrack;

	// TODO: Test code.
	private AudioRecorderManager manager;

	public ActionFragment(ActionEnum actionName)
	{
		super();

		this.actionName = actionName;
		this.audiofileName = actionName + "_clip";
		this.featureFileName = actionName + "_feature";
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);

		playBufSize = AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		// -----------------------------------------
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration, audioEncoding, playBufSize,
				AudioTrack.MODE_STREAM);
		// ------------------------------------------
		audioTrack.setStereoVolume(0.7f, 0.7f);// 设置当前音量大小
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

					Record();
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
				playRecord();
			}
		});

		return v;
	}

	public void Record()
	{
		try
		{
			audioFile = FileManager.createAudioFile(audiofileName,
					"VoiceAnswerCall");
		} catch (IOException e)
		{
			Log.e("z", e.getMessage());
		}

		byte[] buffer = new byte[recBufSize];
		audioRecord.startRecording();// 开始录制
		audioTrack.play();// 开始播放

		while (isRecording)
		{
			// 从MIC保存数据到缓冲区
			int bufferReadResult = audioRecord.read(buffer, 0,
					recBufSize);

			byte[] tmpBuf = new byte[bufferReadResult];
			System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
			// 写入数据即播放
			audioTrack.write(tmpBuf, 0, tmpBuf.length);
		}
		
		audioTrack.stop();
		audioRecord.stop();
	}

	public void Stop()
	{
	}

	public void playRecord()
	{
		isRecording = true;
		new RecordPlayThread().start();
	}
}
