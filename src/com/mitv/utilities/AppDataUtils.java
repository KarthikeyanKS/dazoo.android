
package com.mitv.utilities;



import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.mitv.Constants;



public class AppDataUtils 
{
	@SuppressWarnings("unused")
	private static final String TAG = AppDataUtils.class.getName();
	
	
	private static AppDataUtils sharedInstance;
	
	private SharedPreferences sharedPreferences;
	
	
	
	private AppDataUtils(Context context)
	{
		sharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_NAME, 
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
	
	
	public Set<String> getPreference(
			final String name,
			final Set<String> defaultValue)
	{		
		return sharedPreferences.getStringSet(name, defaultValue);
	}

	
	
	public void setPreference(
			final String name, 
			final Set<String> value)
	{
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putStringSet(name, value);
		
		prefEditor.apply();
	}
	

	
	public String getPreference(
			final String name, 
			final String defaultValue)
	{		
		return sharedPreferences.getString(name, defaultValue);
	}

	
	
	public void setPreference(
			final String name, 
			final String value)
	{
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putString(name, value);
		
		prefEditor.apply();
	}
	
	
	
	public void clearPreference(final String name)
	{
		setPreference(name, new String());
	}
	
	
	
	public void clearAllPreferences()
	{
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();

		prefEditor.clear();
		prefEditor.apply();
	}
	
		
	
	public Boolean getPreference(
			final String key, 
			final Boolean defaultValue)
	{
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	
	
	public void setPreference(
			final String key, 
			final Boolean value)
	{
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		
		prefEditor.putBoolean(key, value);
		
		prefEditor.apply();
	}
}