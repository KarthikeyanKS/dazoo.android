package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.support.v7.app.ActionBar;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVDate;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.adapters.ActionBarDropDownDateListAdapter;

public abstract class TVDateSelectionActivity extends BaseActivity implements ActionBar.OnNavigationListener {


	private ActionBarDropDownDateListAdapter dayAdapter;
	protected ActivityCallbackListener activityCallbackListener;
	private boolean onNavigationItemSelectedHasBeenCalledByOSYet = false;

	protected abstract void setActivityCallbackListener();
		
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		ArrayList<TVDate> tvDates = ContentManager.sharedInstance().getFromStorageTVDates();
		dayAdapter = new ActionBarDropDownDateListAdapter(tvDates);
		int selectedDayIndex = ContentManager.sharedInstance().getFromStorageTVDateSelectedIndex();
		dayAdapter.setSelectedIndex(selectedDayIndex);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(dayAdapter, this);
		
		setActivityCallbackListener();
	}
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		if (!onNavigationItemSelectedHasBeenCalledByOSYet) {
			int selectedTvDateIndex = ContentManager.sharedInstance().getFromStorageTVDateSelectedIndex();
			dayAdapter.setSelectedIndex(selectedTvDateIndex);
			actionBar.setSelectedNavigationItem(selectedTvDateIndex);
			onNavigationItemSelectedHasBeenCalledByOSYet = true;
			return true;
		} else {
			dayAdapter.setSelectedIndex(position);
			ContentManager.sharedInstance().setTVDateSelectedUsingIndexAndFetchGuideForDay(activityCallbackListener, position);
			return true;
		}
	}
		
	@Override
	protected void updateUI(REQUEST_STATUS status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadPage() {
		// TODO Auto-generated method stub
		
	}

}
