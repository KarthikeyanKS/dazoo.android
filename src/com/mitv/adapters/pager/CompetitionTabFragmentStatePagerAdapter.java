
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
import com.mitv.fragments.CompetitionTabFragmentGroupStage;
import com.mitv.fragments.CompetitionTabFragmentSecondStage;
import com.mitv.fragments.CompetitionTabFragmentTeamStandings;
import com.mitv.ui.elements.CustomViewPager;



public class CompetitionTabFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	private static final String TAG = CompetitionTabFragmentStatePagerAdapter.class.getName();
	
	
	private List<String> tabs;
	private long competitionID;
	private CustomViewPager viewPager;
	
	public static final int GROUP_STAGE_POSITION = 0;
	public static final int SECOND_STAGE_POSITION = 1;
	public static final int TEAM_STANDINGS_POSITION = 2;
	
	
	public CompetitionTabFragmentStatePagerAdapter(
			final FragmentManager fm,
			final CustomViewPager viewPager,
			final long competitionID) 
	{
		super(fm);
		
		Context context = SecondScreenApplication.sharedInstance();
		
		this.tabs = new ArrayList<String>();
		this.competitionID = competitionID;
		this.viewPager = viewPager;
		
		tabs.add(context.getString(R.string.competition_page_tab_group_stage));
		tabs.add(context.getString(R.string.competition_page_tab_second_stage));
		tabs.add(context.getString(R.string.competition_page_tab_standings));
	}
	
	
	
	@Override
	public Fragment getItem(int position)
	{
		/* This instance will only be created when needed by the pager adapter */
		Fragment fragment;
		
		/* Uncomment if we decide to change back to looping Viewpager */
//		int realPosition = LoopViewPager.toRealPosition(position, getCount());
//		String tab = tabs.get(position);
		
		String tab = tabs.get(position);
		
		switch(position)
		{
			case GROUP_STAGE_POSITION:
			{
				fragment = new CompetitionTabFragmentGroupStage(viewPager, competitionID, tab, tab, EventTabTypeEnum.COMPETITION_GROUP_STAGE);
				break;
			}
			
			case SECOND_STAGE_POSITION:
			{
				fragment = new CompetitionTabFragmentSecondStage(viewPager, competitionID, tab, tab, EventTabTypeEnum.COMPETITION_SECOND_STAGE);
				break;
			}
			
			case TEAM_STANDINGS_POSITION:
			{
				fragment = new CompetitionTabFragmentTeamStandings(viewPager, competitionID, tab, tab, EventTabTypeEnum.COMPETITION_STANDINGS);
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