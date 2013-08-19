package com.example.phoneapp;

import java.io.File;

import com.voiceMatch;

public class AudioMatchingManager
{
	private static AudioMatchingManager instance = null;

	private static double[][] answerFeature;
	private static double[][] declineFeature;
	private static double[][] muteFeature;

	private final double threshold = 0.2;

	public final static int RESULT_NONE = -1;
	public final static int RESULT_ANSWER = 0;
	public final static int RESULT_DECLINE = 1;
	public final static int RESULT_MUTE = 2;

	private AudioMatchingManager()
	{
		File answerFeatureFile = FileManager.openFileInFolder(FileManager.ROOT_FOLDER_NAME, FileManager.ANSWER_FEATURE_FILE_NAME);
		File declineFeatureFile = FileManager.openFileInFolder(FileManager.ROOT_FOLDER_NAME, FileManager.DECLINE_FEATURE_FILE_NAME);
		File muteFeatureFile = FileManager.openFileInFolder(FileManager.ROOT_FOLDER_NAME, FileManager.MUTE_FEATURE_FILE_NAME);

		answerFeature = AudioRecorderManager.getInstance().loadFeatureFromFile(answerFeatureFile);
		declineFeature = AudioRecorderManager.getInstance().loadFeatureFromFile(declineFeatureFile);
		muteFeature = AudioRecorderManager.getInstance().loadFeatureFromFile(muteFeatureFile);

		AppLog.i("Answer feature = " + answerFeature);
		AppLog.i("Decline feature = " + declineFeature);
		AppLog.i("Mute feature = " + muteFeature);
	}

	public void UpdateFeatures()
	{
		File answerFeatureFile = FileManager.openFile(FileManager.ROOT_FOLDER_NAME, FileManager.ANSWER_FEATURE_FILE_NAME);
		File declineFeatureFile = FileManager.openFile(FileManager.ROOT_FOLDER_NAME, FileManager.DECLINE_FEATURE_FILE_NAME);
		File muteFeatureFile = FileManager.openFile(FileManager.ROOT_FOLDER_NAME, FileManager.MUTE_FEATURE_FILE_NAME);

		answerFeature = AudioRecorderManager.getInstance().loadFeatureFromFile(answerFeatureFile);
		declineFeature = AudioRecorderManager.getInstance().loadFeatureFromFile(declineFeatureFile);
		muteFeature = AudioRecorderManager.getInstance().loadFeatureFromFile(muteFeatureFile);

		AppLog.i("Answer feature = " + answerFeature);
		AppLog.i("Decline feature = " + declineFeature);
		AppLog.i("Mute feature = " + muteFeature);
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
		if (buffer == null || buffer.length == 0)
		{
			AppLog.i("Wait for buffer.");
			return RESULT_NONE;
		}

		voiceMatch matching = new voiceMatch();

		double answerResult = matching.doDtwMatch(answerFeature, buffer);
		double declineResult = matching.doDtwMatch(declineFeature, buffer);
		double muteResult = matching.doDtwMatch(muteFeature, buffer);

		double result = Math.max(Math.max(answerResult, declineResult), muteResult);

		AppLog.i("result = " + result);

		if (result > threshold)
		{
			return RESULT_NONE;
		} else if (result == answerResult)
		{
			return RESULT_ANSWER;
		} else if (result == declineResult)
		{
			return RESULT_DECLINE;
		} else if (result == muteResult)
		{
			return RESULT_MUTE;
		}

		return RESULT_NONE;
	}

}
