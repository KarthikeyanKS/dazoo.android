
package com.mitv.utilities;



import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;
import com.mitv.Constants;
import com.mitv.SecondScreenApplication;



public abstract class AppDataUtils 
{
	@SuppressWarnings("unused")
	private static final String TAG = AppDataUtils.class.getName();
	
	
	
	public static Set<String> getPreference(
			final String name,
			final Set<String> defaultValue)
	{		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE);
		
		return sharedPreferences.getStringSet(name, defaultValue);
	}

	
	
	public static void setPreference(
			final String name, 
			final Set<String> value)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME,
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
				Constants.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE);
		
		return sharedPreferences.getString(name, defaultValue);
	}

	
	
	public static void setPreference(
			final String name, 
			final String value)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME,
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
				Constants.SHARED_PREFERENCES_NAME,
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
				Constants.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	
	
	public static void setPreference(
			final String key, 
			final Boolean value)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putBoolean(key, value);
		
		prefEditor.commit();
	}
}