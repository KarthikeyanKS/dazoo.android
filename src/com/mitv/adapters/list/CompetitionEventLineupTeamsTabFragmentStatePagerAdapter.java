package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.fragments.CompetitionEventTabFragmentLineUp;
import com.mitv.fragments.CompetitionEventTabFragmentLineUpTeams;



public class CompetitionEventLineupTeamsTabFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	private static final String TAG = CompetitionEventLineupTeamsTabFragmentStatePagerAdapter.class.getName();
	
	private long eventID;
	private List<String> tabs;
	
	private static final int HOME_TEAM_POSITION = 0;
	private static final int AWAY_TEAM_POSITION = 1;
	
	
	public CompetitionEventLineupTeamsTabFragmentStatePagerAdapter(
			final FragmentManager fm,
			final long eventID,
			final String eventHomeTeamName,
			final String eventAwayTeamName)
	{
		super(fm);
		
		this.tabs = new ArrayList<String>();
		this.eventID = eventID;
		
		tabs.add(eventHomeTeamName);
		tabs.add(eventAwayTeamName);
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
			case HOME_TEAM_POSITION:
			{
				fragment = new CompetitionEventTabFragmentLineUpTeams(eventID, tab, tab, EventTabTypeEnum.EVENT_LINEUP_HOME_TEAM);
				break;
			}
			
			case AWAY_TEAM_POSITION:
			{
				fragment = new CompetitionEventTabFragmentLineUpTeams(eventID, tab, tab, EventTabTypeEnum.EVENT_LINEUP_AWAY_TEAM);
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