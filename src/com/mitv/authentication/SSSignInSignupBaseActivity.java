package com.mitv.authentication;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.content.SSActivity;

/*
 * This class hides the search icon for all sign up and sign in related activities
 * */
public class SSSignInSignupBaseActivity extends SSActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);

		MenuItem startSearchMenuItem = menu.findItem(R.id.action_start_search);
		startSearchMenuItem.setVisible(false);
		
		MenuItem searchField = menu.findItem(R.id.searchfield);
		searchField.setVisible(false);
		
		return true;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
	}

	@Override
	protected void loadPage() {	
	}
}
