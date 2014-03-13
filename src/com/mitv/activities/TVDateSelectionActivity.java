
package com.mitv.activities;



import java.util.ArrayList;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;

import com.mitv.ContentManager;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.listadapters.ActionBarDropDownDateListAdapter;
import com.mitv.models.TVDate;



public abstract class TVDateSelectionActivity 
	extends BaseContentActivity 
	implements OnNavigationListener 
{
	private ActionBarDropDownDateListAdapter dayAdapter;
	private boolean onNavigationItemSelectedHasBeenCalledByOSYet;
	
	protected ViewCallbackListener activityCallbackListener;
	
	
	/* Abstract methods */
	protected abstract void setActivityCallbackListener();
	
	protected abstract void attachFragment();
	
	protected abstract void removeActiveFragment();
		
	
	
	@Override
	public void setContentView(int layoutResID) 
	{
		super.setContentView(layoutResID);

		initDaySelection(ActionBar.NAVIGATION_MODE_STANDARD);
		
		setActivityCallbackListener();
	}
	
	
	
	protected void hideDaySelection()
	{
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		initDaySelection(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	
	
	protected void showDaySelection()
	{
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		initDaySelection(ActionBar.NAVIGATION_MODE_LIST);
		
		setSelectedDayInAdapter();
	}
	
	
	
	private void initDaySelection(int navigationMode)
	{
		onNavigationItemSelectedHasBeenCalledByOSYet = false;
		
		ArrayList<TVDate> tvDates = ContentManager.sharedInstance().getFromCacheTVDates();

		dayAdapter = new ActionBarDropDownDateListAdapter(this, tvDates);

		actionBar.setNavigationMode(navigationMode);

		actionBar.setListNavigationCallbacks(dayAdapter, this);
	}
	
	
	
	private void setSelectedDayInAdapter()
	{
		int selectedDayIndex = ContentManager.sharedInstance().getFromCacheTVDateSelectedIndex();

		dayAdapter.setSelectedIndex(selectedDayIndex);

		actionBar.setSelectedNavigationItem(selectedDayIndex);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		int currentNavigationMode = actionBar.getNavigationMode();
		
		switch(currentNavigationMode)
		{
			case ActionBar.NAVIGATION_MODE_LIST:
			{
				setSelectedDayInAdapter();
				
				break;
			}
			
			case ActionBar.NAVIGATION_MODE_STANDARD:
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
			
		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{			
				showDaySelection();
				break;
			}
			
			case NO_CONNECTION_AVAILABLE:
			{
				hideDaySelection();
				break;
			}
		}
	}
	
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) 
	{
		if (onNavigationItemSelectedHasBeenCalledByOSYet == false) 
		{
			onNavigationItemSelectedHasBeenCalledByOSYet = true;
		}
		else 
		{
			dayAdapter.setSelectedIndex(position);
			
			switch(actionBar.getNavigationMode())
			{
				case ActionBar.NAVIGATION_MODE_LIST:
				{
					actionBar.setSelectedNavigationItem(position);
					
					fetchGuideForSelectedDay(position);
					break;
				}
				
				case ActionBar.NAVIGATION_MODE_STANDARD:
				default:
				{
					// Do nothing
					break;
				}
			}
		}

		return true;
	}
	
	
	
	private void fetchGuideForSelectedDay(int selectedDayIndex) 
	{
		//TODO NewArc this was added to enable loading indicator when choosing another day in homeactivity, is there a smarter way to do it?
		removeActiveFragment();
		
		updateUI(UIStatusEnum.LOADING);
		
		/* Sets the selected TV Date and fetches the guide */
		ContentManager.sharedInstance().setTVDateSelectedUsingIndexAndFetchGuideForDay(activityCallbackListener, selectedDayIndex);
	}
}
