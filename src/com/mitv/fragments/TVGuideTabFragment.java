
package com.mitv.fragments;



import android.app.Activity;
import android.util.Log;
import android.view.View;
import com.mitv.R;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.interfaces.SwipeClockTimeSelectedCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.elements.SwipeClockBar;
import com.mitv.utilities.DateUtils;



public abstract class TVGuideTabFragment 
	extends BaseFragment
	implements ViewCallbackListener, SwipeClockTimeSelectedCallbackListener
{
	private static final String TAG = TVGuideTabFragment.class.getName();
	
	
	private String tabId;
	private String tabTitle;
	private TVGuideTabTypeEnum tabType;
	private SwipeClockBar swipeClockBar;
	private int selectedHour;
	
	protected Activity activity;
	protected View rootView;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public TVGuideTabFragment()
	{
		this.tabId = "";
		this.tabTitle = "";
		this.tabType = TVGuideTabTypeEnum.PROGRAM_CATEGORY;
	}
	
	
	
	public TVGuideTabFragment(String tabId, String tabTitle, TVGuideTabTypeEnum tabType)
	{
		this.tabId = tabId;
		this.tabTitle = tabTitle;
		this.tabType = tabType;
		
		//Bundle bundle = new Bundle();
		
		//bundle.putString(Constants.FRAGMENT_EXTRA_TAG_DISPLAY_NAME, tvTagName);
		//bundle.putString(Constants.FRAGMENT_EXTRA_TAG_ID, tvTagId);
		
		//setArguments(bundle);
	}
	
//	public static TVGuideTabFragment newInstance(TVTag tag, TVDate date)
//	{
//		TVGuideTabFragment fragment = new TVGuideTabFragment();
//		
//		
//		
//		return fragment;
//	}
//	
	
	
	
	protected void setSwipeClockBar()
	{
		swipeClockBar = (SwipeClockBar) rootView.findViewById(R.id.tvguide_swype_clock_bar);
		
		FontTextView selectedHourTextView = (FontTextView) rootView.findViewById(R.id.timebar_selected_hour_textview);
		
		swipeClockBar.setSelectedHourTextView(selectedHourTextView);
		
		swipeClockBar.setTimeSelectedListener(this);
	}
	
	
	
	protected void updateSwipeClockBarWithDayAndTime() 
	{
		if (swipeClockBar != null) 
		{
			boolean isToday = ContentManager.sharedInstance().selectedTVDateIsToday();
			
			int currentHour = DateUtils.getCurrentHourOn24HourFormat();
			
			int firstHourOfTVDay = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();
			
			Integer selectedHourFromCache = ContentManager.sharedInstance().getFromCacheSelectedHour();

			if (isToday) 
			{
				/* If today, default to currentHour */
				selectedHour = currentHour;
				
				if (selectedHourFromCache != null && selectedHourFromCache >= currentHour) 
				{
					selectedHour = selectedHourFromCache;
				}
			} 
			else 
			{
				if (selectedHourFromCache != null) 
				{
					selectedHour = selectedHourFromCache;
				} 
				else 
				{
					selectedHour = firstHourOfTVDay;
				}
			}

			swipeClockBar.setHour(selectedHour);
			swipeClockBar.setToday(isToday);
		}
		else
		{
			Log.w(TAG, "SwipeClockBar is null");
		}
	}
	
	
	
	protected void setSwipeCLockbackground()
	{
		if (swipeClockBar != null) 
		{
			swipeClockBar.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
		}
		else
		{
			Log.w(TAG, "SwipeClockBar is null");
		}
	}
	
	
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		Log.d(TAG, "Fragment view " + tabTitle + " was destroyed,");
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();

		Log.d(TAG, "Fragment view " + tabTitle + " was resumed");
		
		updateSwipeClockBarWithDayAndTime();
	}
	
	
	
	public TVGuideTabTypeEnum getType()
	{
		return tabType;
	}
	
	
	
	public String getTabTitle() 
	{
		return tabTitle;
	}



	public String getTabId() 
	{
		return tabId;
	}
	
	
	
	public int getSelectedHour()
	{
		return selectedHour;
	}
}
