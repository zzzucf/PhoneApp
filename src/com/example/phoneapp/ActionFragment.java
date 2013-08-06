package com.example.phoneapp;

import java.io.File;

import Enums.ActionEnum;
import android.annotation.SuppressLint;
import android.app.Fragment;
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

		manager = new AudioRecorderManager();
		Log.i("z", "manager init.");
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

						Button btnRecord = (Button) v
								.findViewById(R.id.BtnRecord);
						btnRecord.setText(R.string.label_stop);

						manager.startAudioRecorder();
						break;
					}
					case MotionEvent.ACTION_UP:
					{
						Log.i("z", "key up");

						Button btnRecord = (Button) v
								.findViewById(R.id.BtnRecord);
						btnRecord.setText(R.string.label_record);

						manager.stopAudioRecorder();
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
				manager.playAudioRecord();
			}
		});

		return v;
	}
}
