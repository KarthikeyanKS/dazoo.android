
package com.mitv.fragments;



import android.app.Activity;
import android.view.View;

import com.mitv.enums.EventTabTypeEnum;
import com.mitv.interfaces.ViewCallbackListener;



public abstract class EventTabFragment 
	extends BaseFragment
	implements ViewCallbackListener
{
	@SuppressWarnings("unused")
	private static final String TAG = EventTabFragment.class.getName();
	
	
	private String tabId;
	private String tabTitle;
	private EventTabTypeEnum tabType;
	
	protected Activity activity;
	protected View rootView;
	
	
	
	public EventTabFragment(
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
