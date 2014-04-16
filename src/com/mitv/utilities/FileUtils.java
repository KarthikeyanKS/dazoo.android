
package com.mitv.utilities;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import android.os.Environment;
import android.util.Log;




public abstract class FileUtils 
{
	private static final String TAG = AppDataUtils.class.getName();

	
	public static final String ANDROID_FONTS_PATH = "fonts/";
	public static final String ANDROID_DATA_PATH = "/Android/data/";
	
	
			
	public static File getFile(String fileName) 
	{
		File file = null;

		if (fileName != null && isExternalStorageReadable()) 
		{
			StringBuilder filePathSB = new StringBuilder();
			
			String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
			
			filePathSB.append(externalStorageDirectory);
			filePathSB.append(ANDROID_DATA_PATH);
			
			try 
			{
				File myDir = new File(filePathSB.toString());

				myDir.mkdirs();

				file = new File(myDir, fileName);
			} 
			catch (Exception e) 
			{
				Log.e(TAG, e.getMessage());
			}
		}
		else
		{
			Log.w(TAG, "Filename is null or external storage not readable.");
		}

		return file;
	}

	
	
	public static void saveFile(File file) 
	{
		if(file != null) 
		{
			if(fileExists(file) == false) 
			{
				if(isExternalStorageWritable()) 
				{
					FileOutputStream fos;
					OutputStreamWriter outsw;
					
					try 
					{
						fos = new FileOutputStream(file, true);
	
						outsw = new OutputStreamWriter(fos);
	
						outsw.close();
					} 
					catch (Exception e) 
					{
						Log.e(TAG, e.getMessage());
					}
				}
			}
		}
	}
	
	
	
	public static boolean fileExists(File file) 
	{
		boolean fileExists = false;

		if (isExternalStorageReadable()) 
		{
			if (file != null) 
			{
				fileExists = file.exists();
			}
		}

		return fileExists;
	}

	
	
	private static boolean isExternalStorageWritable() 
	{
		String state = Environment.getExternalStorageState();

		boolean isExternalStorageWritable = Environment.MEDIA_MOUNTED.equals(state);
		
		return isExternalStorageWritable;
	}

	
	
	private static boolean isExternalStorageReadable() 
	{
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		{
			return true;
		}

		return false;
	}
}
