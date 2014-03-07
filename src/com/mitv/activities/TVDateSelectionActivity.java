
package com.mitv.activities;



import java.util.ArrayList;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;

import com.mitv.ContentManager;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.listadapters.ActionBarDropDownDateListAdapter;
import com.mitv.models.TVDate;



public abstract class TVDateSelectionActivity 
	extends BaseContentActivity 
	implements OnNavigationListener 
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

		ArrayList<TVDate> tvDates = ContentManager.sharedInstance().getFromCacheTVDates();
		
		dayAdapter = new ActionBarDropDownDateListAdapter(this, tvDates);
	
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(dayAdapter, this);
		
		setActivityCallbackListener();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		int selectedDayIndex = ContentManager.sharedInstance().getFromCacheTVDateSelectedIndex();
		
		dayAdapter.setSelectedIndex(selectedDayIndex);
		
		actionBar.setSelectedNavigationItem(selectedDayIndex);
	}
	
	
	
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) 
	{
		if (!onNavigationItemSelectedHasBeenCalledByOSYet) 
		{
			onNavigationItemSelectedHasBeenCalledByOSYet = true;
		}
		else 
		{
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
