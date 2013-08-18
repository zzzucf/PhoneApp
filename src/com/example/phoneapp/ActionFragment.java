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
	private String featureFileName = "_Feature";
	private String clipFileName = "_Clip";

	public ActionFragment(ActionEnum actionName)
	{
		super();

		this.actionName = actionName;
		this.featureFileName = actionName + featureFileName;
		this.clipFileName = actionName + clipFileName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.action_fragment, container, false);

		// Setup action name label.
		TextView actionNameLabel = (TextView) v.findViewById(R.id.LblActionName);
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

					AudioRecorderManager.getInstance().startAudioRecorder();
					break;
				}
				case MotionEvent.ACTION_UP:
				{
					Log.i("z", "key up");

					Button btnRecord = (Button) v.findViewById(R.id.BtnRecord);
					btnRecord.setText(R.string.label_record);

					File featureFile = FileManager.createFolderAndFile(FileManager.ROOT_FOLDER_NAME, featureFileName);
					File clipFile = FileManager.createFolderAndFile(FileManager.ROOT_FOLDER_NAME, clipFileName);

					AudioRecorderManager.getInstance().stopAudioRecorder();

					AudioRecorderManager.getInstance().saveFeatureToFile(featureFile);
					AudioRecorderManager.getInstance().saveAudioBufferToFile(clipFile);
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
				File file = FileManager.openFileInFolder(FileManager.ROOT_FOLDER_NAME, clipFileName);
				AudioRecorderManager.getInstance().loadAudioBufferFromFile(file);
				AudioRecorderManager.getInstance().playAudioRecord();
			}
		});

		return v;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		AudioRecorderManager.getInstance().Destory();
	}
}
