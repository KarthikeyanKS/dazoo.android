
package com.millicom.mitv.activities.authentication;



import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.millicom.mitv.activities.BaseActivity;
import com.mitv.R;



/*
 * This class hides the search icon for all sign up and sign in related activities
 * */
public abstract class BaseLoginActivity 
	extends BaseActivity 
{
	@SuppressWarnings("unused")
	private static final String TAG = BaseLoginActivity.class.getName();
	
	
	
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
}