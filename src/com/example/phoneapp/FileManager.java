package com.example.phoneapp;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class FileManager
{
	// Create a folder under a path.
	public static File createFolder(String path, String folderName)
	{
		File file = openFolder(path, folderName);

		if (!file.exists())
		{
			file.mkdir();
		}
		
		return file;
	}
	
	// Create a folder inside another folder.
	public static File createFolder(File folder, String folderName)
	{
		File file = openFolder(folder, folderName);
		
		if (!file.exists())
		{
			file.mkdir();
		}
		
		return file;
	}
	
	// Open a folder under a path.
	public static File openFolder(String path, String folderName)
	{
		if (!SDCardMounted())
		{
			return null;
		}
		
		File file = new File(path + "/" + folderName);
		
		return file;
	}
	
	// Open a file inside another folder.
	public static File openFolder(File folder, String folderName)
	{
		if (!SDCardMounted())
		{
			return null;
		}
		
		File file = new File(folder.getPath() + "/" + folderName);
		
		return file;
	}
	
	// Create a file under a path.
	public static File createFile(String path, String fileName)
	{
		File file = openFile(path, fileName);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				AppLog.e("Cannot create file.");
			}
		}
		
		return file;
	}
	
	// Create a file under a folder.
	public static File createFile(File folder, String fileName)
	{
		File file = openFile(folder, fileName);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				AppLog.e("Cannot create file.");
			}
			
		}
		
		return file;
	}
	
	// Open file under a path.
	public static File openFile(String path, String fileName)
	{
		if (!SDCardMounted())
		{
			return null;
		}
		
		File file = new File(path + "/" + fileName);
		return file;
	}

	// Open file under a folder.
	public static File openFile(File folder, String fileName)
	{
		if (!SDCardMounted())
		{
			return null;
		}
		
		File file = new File(folder + "/" + fileName);
		return file;
	}
	
	// Create folder and file.
	public static File createFolderAndFile(String folderName, String fileName)
	{
		String rootDir = Environment.getExternalStorageDirectory().getPath();
		File folder = createFolder(rootDir, folderName);
		File file = createFile(folder, fileName);
		return file;
	}
	
	// Open file inside a folder.
	public static File openFileInFolder(String folderName, String fileName)
	{
		String rootDir = Environment.getExternalStorageDirectory().getPath();
		File folder = openFolder(rootDir, folderName);
		File file = openFile(folder, fileName);
		return file;
	}
	
	// Check if sd card is accessible.
	private static boolean SDCardMounted()
	{
		if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			AppLog.e("SD card has not been mounted.");
		}
		
		return true;
	}
}
