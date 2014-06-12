
package com.mitv.adapters.pager;



import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.fragments.TVGuideTabFragmentAllPrograms;
import com.mitv.fragments.TVGuideTabFragmentBroadcast;
import com.mitv.fragments.TVGuideTabFragmentCompetition;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.models.objects.mitvapi.competitions.Competition;



public class TVGuideTagFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	@SuppressWarnings("unused")
	private static final String TAG = TVGuideTagFragmentStatePagerAdapter.class.getName();
	
	
	private List<TVTag> tvTags;

	
	
	public TVGuideTagFragmentStatePagerAdapter(
			final FragmentManager fm, 
			final List<TVTag> tags) 
	{
		super(fm);
		
		this.tvTags = tags;
	}
	
	
	
	@Override
	public Fragment getItem(int position)
	{
		int realPosition; 
	
		if(getCount() > 0)
		{
			realPosition = LoopViewPager.toRealPosition(position, getCount());
		}
		else
		{
			realPosition = 0;
		}
		
		/* This instance will only be created when needed by the pager adapter */
		Fragment fragment;
		
		if(realPosition == 0)
		{
			TVTag tvTag = tvTags.get(realPosition);
			
			fragment = new TVGuideTabFragmentAllPrograms(tvTag);
		}
		else
		{
			TVTag tvTag = tvTags.get(realPosition);
			
			List<Competition> competitions = ContentManager.sharedInstance().getFromCacheVisibleCompetitions();
			
			Competition competition = tvTag.getMatchingCompetition(competitions);
			
			if(competition == null)
			{
				fragment = new TVGuideTabFragmentBroadcast(tvTag);
			}
			else
			{
				fragment = new TVGuideTabFragmentCompetition(competition.getCompetitionId(), competition.getDisplayName());
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
			
			TVTag tvTag = tvTags.get(realPosition);
				
			displayName = tvTag.getDisplayName();
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
		if (tvTags != null) 
		{
			return tvTags.size();
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