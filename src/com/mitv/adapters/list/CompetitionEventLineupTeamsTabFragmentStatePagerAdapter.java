
package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventTabTypeEnum;
import com.mitv.fragments.CompetitionEventTabFragmentLineUpTeams;



public class CompetitionEventLineupTeamsTabFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	private static final String TAG = CompetitionEventLineupTeamsTabFragmentStatePagerAdapter.class.getName();
	
	private long eventID;
	private long homeTeamID;
	private long awayTeamID;
	private List<String> tabs;
	
	private static final int HOME_TEAM_POSITION = 0;
	private static final int AWAY_TEAM_POSITION = 1;
	
	
	public CompetitionEventLineupTeamsTabFragmentStatePagerAdapter(
			final FragmentManager fm,
			final long eventID,
			final String eventHomeTeamName,
			final Long homeTeamID,
			final String eventAwayTeamName,
			final Long awayTeamID)
	{
		super(fm);
		
		this.tabs = new ArrayList<String>();
		this.eventID = eventID;
		this.homeTeamID = homeTeamID;
		this.awayTeamID = awayTeamID;
		
		tabs.add(eventHomeTeamName);
		tabs.add(eventAwayTeamName);
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
			case HOME_TEAM_POSITION:
			{		
				fragment = new CompetitionEventTabFragmentLineUpTeams(eventID, homeTeamID, tab, tab, EventTabTypeEnum.EVENT_LINEUP_HOME_TEAM);
				break;
			}
			
			case AWAY_TEAM_POSITION:
			{
				fragment = new CompetitionEventTabFragmentLineUpTeams(eventID, awayTeamID, tab, tab, EventTabTypeEnum.EVENT_LINEUP_AWAY_TEAM);
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