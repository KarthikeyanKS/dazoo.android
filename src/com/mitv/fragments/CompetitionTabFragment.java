
package com.mitv.fragments;



import android.app.Activity;
import android.view.View;

import com.mitv.enums.EventTabTypeEnum;
import com.mitv.fragments.base.BaseFragment;
import com.mitv.interfaces.ViewCallbackListener;



public abstract class CompetitionTabFragment 
	extends BaseFragment
	implements ViewCallbackListener
{
	@SuppressWarnings("unused")
	private static final String TAG = CompetitionTabFragment.class.getName();
	
	
	private String tabId;
	private String tabTitle;
	private EventTabTypeEnum tabType;
	
	protected Activity activity;
	protected View rootView;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public CompetitionTabFragment()
	{
		this.tabId = "";
		this.tabTitle = "";
		this.tabType = EventTabTypeEnum.COMPETITION_GROUP_STAGE;
	}
	
	
	
	public CompetitionTabFragment(
			final String tabId, 
			final String tabTitle, 
			final EventTabTypeEnum tabType)
	{
		this.tabId = tabId;
		this.tabTitle = tabTitle;
		this.tabType = tabType;
	}


	
	public EventTabTypeEnum getType()
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
}
