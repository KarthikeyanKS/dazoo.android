
package com.mitv.utilities;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.os.Environment;



public abstract class FileUtils 
{
	@SuppressWarnings("unused")
	private static final String TAG = AppDataUtils.class.getName();

	
	
	public static File getFile(String fileName) 
	{
		File file = null;

		if (isExternalStorageReadable()) 
		{
			String root = Environment.getExternalStorageDirectory().toString();

			try 
			{
				Locale locale = LanguageUtils.getCurrentLocale();

				String filePath = String.format(locale, "%s/Android/data/", root);

				File myDir = new File(filePath);

				myDir.mkdirs();

				file = new File(myDir, fileName);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		return file;
	}

	public static boolean fileExists(File file) {
		boolean fileExists = false;

		if (isExternalStorageReadable()) {

			if (file != null) {
				fileExists = file.exists();
			}
		}

		return fileExists;
	}

	public static void saveFile(File file) {
		if (file != null) {
			if (!fileExists(file)) {
				if (isExternalStorageWritable()) {
					try {
						FileOutputStream os = new FileOutputStream(file, true);
	
						OutputStreamWriter out = new OutputStreamWriter(os);
	
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		return false;
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}

		return false;
	}
}
