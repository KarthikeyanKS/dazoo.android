
package com.mitv.adapters.pager;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.fragments.EventTabFragmentGroupStage;
import com.mitv.fragments.EventTabFragmentTeams;



public class EventTabFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	private static final String TAG = EventTabFragmentStatePagerAdapter.class.getName();
	
	
	private List<String> tabs;

	
	
	public EventTabFragmentStatePagerAdapter(final FragmentManager fm) 
	{
		super(fm);
		
		Context context = SecondScreenApplication.sharedInstance();
		
		this.tabs = new ArrayList<String>();
		
		tabs.add(context.getString(R.string.event_page_tab_group_stage));
		tabs.add(context.getString(R.string.event_page_tab_second_stage));
		tabs.add(context.getString(R.string.event_page_tab_teams));
	}
	
	
	
	@Override
	public Fragment getItem(int position)
	{
		/* This instance will only be created when needed by the pager adapter */
		Fragment fragment;
		
		int realPosition = LoopViewPager.toRealPosition(position, getCount());
		
		String tab = tabs.get(realPosition);
		
		switch(realPosition)
		{
			case 0:	
			{		
				fragment = new EventTabFragmentGroupStage(tab, tab, EventTabTypeEnum.GROUP_STAGE);
				break;
			}
			
			case 1:
			{
				fragment = new EventTabFragmentGroupStage(tab, tab, EventTabTypeEnum.SECOND_STAGE);
				break;
			}
			
			case 2:
			{
				fragment = new EventTabFragmentTeams(tab, tab, EventTabTypeEnum.TEAM);
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled position");
				fragment = null;
				break;
			}
		}
		
		return fragment;
	}
	
	
	
	@Override
	public CharSequence getPageTitle(int position) 
	{
		String displayName;
		
		if(getCount() > 0)
		{	
			int realPosition = position % getCount();
				
			displayName = tabs.get(realPosition);
		}
		else
		{
			displayName = SecondScreenApplication.sharedInstance().getString(R.string.unknown_tab);
		}
		
		return displayName;
	}

	
	
	@Override
	public int getCount() 
	{
		if (tabs != null) 
		{
			return tabs.size();
		}
		else 
		{
			return 0;
		}
	}
	
	
	
	@Override
	public int getItemPosition(Object object) 
	{
	    return POSITION_UNCHANGED;
	}
}