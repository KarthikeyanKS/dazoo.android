
package com.millicom.mitv.activities.base;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.millicom.mitv.activities.HomeActivity;
import com.mitv.Consts;
import com.mitv.R;



/*
 * This class hides the search icon for all sign up and sign in related activities
 * */
public abstract class BaseLoginActivity 
	extends BaseActivity 
{
	private static final String TAG = BaseLoginActivity.class.getName();

	
	private Class<?> returnActivity;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		if (intent.hasExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME)) 
		{
			String returnActivityClassName = intent.getExtras().getString(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME);
			
			try 
			{
				returnActivity = Class.forName(returnActivityClassName);
			} 
			catch (ClassNotFoundException cnfex) 
			{
				Log.e(TAG, cnfex.getMessage(), cnfex);
				
				returnActivity = HomeActivity.class;
			}
		}
		else
		{
			returnActivity = HomeActivity.class;
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);

		MenuItem startSearchMenuItem = menu.findItem(R.id.action_start_search);
		startSearchMenuItem.setVisible(false);
		
		MenuItem searchField = menu.findItem(R.id.searchfield);
		searchField.setVisible(false);
		
		return true;
	}
	

	
	public Class<?> getReturnActivity()
	{
		return returnActivity;
	}
}