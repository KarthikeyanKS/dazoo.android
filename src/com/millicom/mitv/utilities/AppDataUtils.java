
package com.millicom.mitv.utilities;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mitv.Consts;
import com.mitv.SecondScreenApplication;



public abstract class AppDataUtils 
{
	private static final String TAG = AppDataUtils.class.getName();
	
	
	
	public static Object loadData(final String filename)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		Object object = null;
		
		ObjectInputStream in = null;
		
        FileInputStream fis = null;
        
        try
        {
            fis = context.getApplicationContext().openFileInput(filename);
            
            in = new ObjectInputStream(fis);
            
            object = in.readObject();
        }
        catch (FileNotFoundException fnfex) 
        {
        	// Do not log this exception
        } 
        catch (Exception e)
        {
        	Log.e(TAG, e.getMessage(), e);
        } 
        finally 
        {
        	try
			{
				if(fis != null)
				{
					fis.close();
				}
				// No need for else
				
				if(in != null)
				{
					in.close();
				}
				// No need for else
			}
			catch(Exception e) 
			{
				Log.e(TAG, e.getMessage(), e);
			}
        }

        return object;
	}
	
	
	
	public static void saveData(
			final String filename,
			final Object object)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		FileOutputStream fout = null;
		
		ObjectOutputStream out = null;
		
		try
		{
			fout = context.openFileOutput(
					filename, 
					Context.MODE_PRIVATE);
		   
			out = new ObjectOutputStream(fout);

			out.writeObject(object);
		   
			out.flush();
		}
		catch(IOException ioex) 
		{
			Log.e(TAG, ioex.getMessage(), ioex);
		}
		finally 
		{
			try
			{
				if(out != null)
				{
					out.close();
				}
				// No need for else
				
				if(fout != null)
				{
					fout.close();
				}
				// No need for else
			}
			catch(Exception e)
			{
				// Do nothing here
			}
		}
	}
	
	
	
	public static Set<String> getPreference(
			final String name,
			final Set<String> defaultValue)
	{		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE);
		
		return sharedPreferences.getStringSet(name, defaultValue);
	}

	
	
	public static void setPreference(
			final String name, 
			final Set<String> value)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putStringSet(name, value);
		
		prefEditor.commit();
	}
	

	
	public static String getPreference(
			final String name, 
			final String defaultValue)
	{		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE);
		
		return sharedPreferences.getString(name, defaultValue);
	}

	
	
	public static void setPreference(
			final String name, 
			final String value)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putString(name, value);
		
		prefEditor.commit();
	}
	
	
	
	public static void clearPreference(final String name)
	{
		setPreference(name, new String());
	}
	
	
	
	public static void clearAllPreferences()
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		SharedPreferences.Editor prefEditor = sharedPreferences.edit();

		prefEditor.clear();
		prefEditor.commit();
	}
	
		
	
	public static Boolean getPreference(
			final String key, 
			final Boolean defaultValue)
	{		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	
	
	public static void setPreference(
			final String key, 
			final Boolean value)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Consts.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putBoolean(key, value);
		
		prefEditor.commit();
	}
}

