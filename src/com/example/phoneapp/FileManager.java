package com.example.phoneapp;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileManager
{
	private final static String fileType = ".txt";

	public static File createAudioFile(String fileName, String folderName)
	{
		if (fileName == null)
		{
			Log.e("z", "File name cannot be null.");
			return null;
		}

		if (folderName == null)
		{
			Log.e("z", "Folder name cannot be null.");
		}

		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
		{
			Log.e("z", "Cannot access SD card.");
		}

		File sdcardDir = Environment.getExternalStorageDirectory();
		String path = sdcardDir.getPath() + "/" + folderName;
		File root = createPath(path);
		File file = null;
		file = new File(root, fileName);

		return file;
	}

	public static File createPath(String path)
	{
		File file = new File(path);
		if (!file.exists())
		{
			file.mkdir();
		}

		return file;
	}

	public static File createFile()
	{
		return null;
	}
}
