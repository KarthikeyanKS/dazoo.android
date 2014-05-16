
package com.mitv.utilities;



import android.content.Context;
import android.content.SharedPreferences;
import com.mitv.Constants;



public class AppDataUtils 
{
	@SuppressWarnings("unused")
	private static final String TAG = AppDataUtils.class.getName();
	
	
	
	private static AppDataUtils sharedInstance;
	
	private SharedPreferences appSharedPreferences;
	private SharedPreferences deviceSharedPreferences;
	
	
	
	private AppDataUtils(Context context)
	{
		appSharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE);
		
		deviceSharedPreferences = context.getSharedPreferences(
				Constants.DEVICE_PREFERENCES_FILE, 
				Context.MODE_PRIVATE);
	}
	
	
	
	public static AppDataUtils sharedInstance(Context context) 
	{
		if (sharedInstance == null) 
		{
			sharedInstance = new AppDataUtils(context);
		}
		
		return sharedInstance;
	}
	
	
	
	public String getPreference(
			final String name, 
			final String defaultValue)
	{		
		return appSharedPreferences.getString(name, defaultValue);
	}

	
	
	public void setPreference(
			final String name, 
			final String value,
			final Boolean immediate)
	{
		SharedPreferences.Editor prefEditor = appSharedPreferences.edit();
		
		prefEditor.putString(name, value);
		
		if (immediate) 
		{
			prefEditor.commit();
		} 
		else 
		{
			prefEditor.apply();
		}
	}
	
	
	
	public void clearPreference(
			final String name,
			final boolean immediate)
	{
		setPreference(name, new String(), immediate);
	}
	
	
	
	public void clearAllPreferences(final boolean immediate)
	{
		SharedPreferences.Editor prefEditor = appSharedPreferences.edit();

		prefEditor.clear();
		
		if (immediate) 
		{
			prefEditor.commit();
		} 
		else 
		{
			prefEditor.apply();
		}
	}
	
		
	
	public Boolean getPreference(
			final String key, 
			final Boolean defaultValue)
	{
		return appSharedPreferences.getBoolean(key, defaultValue);
	}

	
	
	public void setPreference(
			final String key, 
			final Boolean value, 
			final Boolean immediate)
	{
		final SharedPreferences.Editor prefEditor = appSharedPreferences.edit();

		prefEditor.putBoolean(key, value);

		if (immediate) 
		{
			prefEditor.commit();
		} 
		else 
		{
			prefEditor.apply();
		}
	}
	
	
	
	public String getPreferenceFromDevice(
			final String key, 
			final String defaultValue)
	{
		return deviceSharedPreferences.getString(key, defaultValue);
	}
}