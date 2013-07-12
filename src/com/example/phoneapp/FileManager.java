package com.example.phoneapp;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class FileManager
{
	private final static String fileType = ".3gp";

	public static File createAudioFile(String fileName, String folderName)
			throws IOException
	{
		if (fileName == null)
		{
			throw new IOException("File name cannot be null.");
		}

		if (folderName == null)
		{
			throw new IOException("Folder name cannot be null.");
		}

		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
		{
			throw new IOException("Cannot access SD card.");
		}

		File sdcardDir = Environment.getExternalStorageDirectory();
		String path = sdcardDir.getPath() + "/" + folderName;
		File root = createPath(path);
		File file = File.createTempFile(fileName, fileType, root);

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
}
