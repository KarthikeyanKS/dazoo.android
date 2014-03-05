package com.millicom.mitv.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mitv.Consts;
import com.mitv.SecondScreenApplication;

public class FileUtils {

	private static final String TAG = AppDataUtils.class.getName();

	public static File getFile(String fileName) {
		File file = null;

		if (isExternalStorageReadable()) {
			String root = Environment.getExternalStorageDirectory().toString();

			try {
				Locale locale = SecondScreenApplication.getCurrentLocale();

				if (locale == null) {
					locale = Locale.getDefault();
				}

				String filePath = String.format(locale, "%s/Android/data/", root);

				File myDir = new File(filePath);

				myDir.mkdirs();

				file = new File(myDir, fileName);
			} catch (Exception e) {
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
