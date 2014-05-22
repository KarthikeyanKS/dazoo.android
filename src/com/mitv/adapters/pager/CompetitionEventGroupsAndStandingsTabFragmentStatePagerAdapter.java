
package com.mitv.adapters.pager;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.fragments.CompetitionEventTabFragmentGroupStage;
import com.mitv.fragments.CompetitionEventTabFragmentGroupStandings;
import com.mitv.ui.elements.CustomViewPager;



public class CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	private static final String TAG = CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter.class.getName();
	
	
	private List<String> tabs;
	private long eventID;
	private long phaseId;
	private CustomViewPager viewPager;
	
	public static final int GROUP_STAGE_POSITION = 0;
	public static final int SECOND_STAGE_POSITION = 1;
	
	
	public CompetitionEventGroupsAndStandingsTabFragmentStatePagerAdapter(
			final FragmentManager fm,
			final CustomViewPager viewPager,
			final long phaseId,
			final String firstTabName,
			final long eventID) 
	{
		super(fm);
		
		Context context = SecondScreenApplication.sharedInstance();
		
		this.tabs = new ArrayList<String>(2);
		this.eventID = eventID;
		this.phaseId = phaseId;
		this.viewPager = viewPager;
		
		tabs.add(firstTabName);
		tabs.add(context.getString(R.string.event_page_tab_standings));
	}
	
	
	
	@Override
	public Fragment getItem(int position)
	{
		/* This instance will only be created when needed by the pager adapter */
		Fragment fragment;

		/* Uncomment if we decide to change back to looping Viewpager */
//		int realPosition = LoopViewPager.toRealPosition(position, getCount());
//		String tab = tabs.get(realPosition);		
		String tab = tabs.get(position);

		
		switch(position)
		{
			case GROUP_STAGE_POSITION:
			{
				fragment = new CompetitionEventTabFragmentGroupStage(viewPager, eventID, tab, tab, EventTabTypeEnum.EVENT_GROUP_STAGE, phaseId);
				break;
			}
			
			case SECOND_STAGE_POSITION:
			{
				fragment = new CompetitionEventTabFragmentGroupStandings(viewPager, eventID, tab, tab, EventTabTypeEnum.EVENT_GROUP_STANDINGS);
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
			/* Uncomment if we decide to change back to looping Viewpager */
//			int realPosition = position % getCount();
//			displayName = tabs.get(realPosition);

			displayName = tabs.get(position);
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