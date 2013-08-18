package com.example.phoneapp;

import java.io.File;

import com.voiceMatch;

public class AudioMatchingManager
{
	private static AudioMatchingManager instance = null;

	private double[][] answerFeature;
	private double[][] declineFeature;
	private double[][] muteFeature;

	private final double threshold = 0.2;

	public final static int RESULT_NONE = -1;
	public final static int RESULT_ANSWER = 0;
	public final static int RESULT_DECLINE = 1;
	public final static int RESULT_MUTE = 2;

	private AudioMatchingManager()
	{
		File answerFeatureFile = FileManager.openFile(FileManager.ROOT_FOLDER_NAME, FileManager.ANSWER_FEATURE_FILE_NAME);
		File declineFeatureFile = FileManager.openFile(FileManager.ROOT_FOLDER_NAME, FileManager.DECLINE_FEATURE_FILE_NAME);
		File muteFeatureFile = FileManager.openFile(FileManager.ROOT_FOLDER_NAME, FileManager.MUTE_FEATURE_FILE_NAME);

		answerFeature = AudioRecorderManager.getInstance().loadVectorFromFile(answerFeatureFile);
		declineFeature = AudioRecorderManager.getInstance().loadVectorFromFile(declineFeatureFile);
		muteFeature = AudioRecorderManager.getInstance().loadVectorFromFile(muteFeatureFile);
	}

	public static AudioMatchingManager getInstance()
	{
		if (instance == null)
		{
			instance = new AudioMatchingManager();
		}

		return instance;
	}

	public int match(short[] buffer)
	{
		voiceMatch matching = new voiceMatch();
		double answerResult = matching.doDtwMatch(answerFeature, buffer);
		double declineResult = matching.doDtwMatch(declineFeature, buffer);
		double muteResult = matching.doDtwMatch(muteFeature, buffer);

		double result = Math.max(Math.max(answerResult, declineResult), muteResult);
		if (result > threshold)
		{
			return RESULT_NONE;
		}
		else if (result == answerResult)
		{
			return RESULT_ANSWER;
		}
		else if (result == declineResult)
		{
			return RESULT_DECLINE;
		}
		else if (result == muteResult)
		{
			return RESULT_MUTE;
		}

		return -1;
	}

}
