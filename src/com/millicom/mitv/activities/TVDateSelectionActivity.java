package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.support.v7.app.ActionBar;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVDate;
import com.mitv.adapters.ActionBarDropDownDateListAdapter;

public abstract class TVDateSelectionActivity 
	extends BaseContentActivity 
	implements ActionBar.OnNavigationListener 
{
	private ActionBarDropDownDateListAdapter dayAdapter;
	protected ActivityCallbackListener activityCallbackListener;
	private boolean onNavigationItemSelectedHasBeenCalledByOSYet = false;

	protected abstract void setActivityCallbackListener();
	
	protected abstract void attachFragment();
	protected abstract void removeActiveFragment();
		
	
	
	@Override
	public void setContentView(int layoutResID) 
	{
		super.setContentView(layoutResID);

		ArrayList<TVDate> tvDates = ContentManager.sharedInstance().getFromStorageTVDates();
		dayAdapter = new ActionBarDropDownDateListAdapter(tvDates);
	
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(dayAdapter, this);
		
		setActivityCallbackListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		int selectedDayIndex = ContentManager.sharedInstance().getFromStorageTVDateSelectedIndex();
		dayAdapter.setSelectedIndex(selectedDayIndex);
		actionBar.setSelectedNavigationItem(selectedDayIndex);
	}
	
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) 
	{
		if (!onNavigationItemSelectedHasBeenCalledByOSYet) 
		{
			onNavigationItemSelectedHasBeenCalledByOSYet = true;
		} else {
			dayAdapter.setSelectedIndex(position);
			actionBar.setSelectedNavigationItem(position);
			fetchGuideForSelectedDay(position);
		}

		return true;
	}
	
	
	private void fetchGuideForSelectedDay(int selectedDayIndex) 
	{
		//TODO NewArc this was Added by Cyon to enable loading indicator when choosing another day in homeactivity, is there a smarter way to do it?
		removeActiveFragment();
		
		updateUI(UIStatusEnum.LOADING);
		
		/* Sets the selected TV Date and fetches the guide */
		ContentManager.sharedInstance().setTVDateSelectedUsingIndexAndFetchGuideForDay(activityCallbackListener, selectedDayIndex);
	}
}
