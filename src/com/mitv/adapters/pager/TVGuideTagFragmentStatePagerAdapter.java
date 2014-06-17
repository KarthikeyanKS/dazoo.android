
package com.mitv.adapters.pager;



import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
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
	private static final String TAG = TVGuideTagFragmentStatePagerAdapter.class.getName();
	
	
	private List<TVTag> tvTags;
	
	
	
	public TVGuideTagFragmentStatePagerAdapter(final FragmentManager fm) 
	{
		super(fm);
		
		tvTags = null;
	}
	
	
	
	@Override
	public Fragment getItem(int position)
	{
		/* This instance will only be created when needed by the pager adapter */
		Fragment fragment;
		
		int realPosition; 
	
		if(getCount() > 0)
		{
			realPosition = LoopViewPager.toRealPosition(position, getCount());
			
			if(realPosition == 0)
			{
				TVTag tvTag = getTVTags().get(realPosition);
				
				fragment = new TVGuideTabFragmentAllPrograms(tvTag);
			}
			else
			{
				TVTag tvTag = getTVTags().get(realPosition);
				
				List<Competition> competitions = ContentManager.sharedInstance().getCacheManager().getVisibleCompetitions();
				
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
		}
		else
		{
			/*
			 * TODO - COntinue to debug and check why getItem() gets called when getCount() is zero or lower
			 */
			Log.d(TAG, "FragmentBugFinder - getCount() value is " + getCount());
			
			TVTag mockTVTag = new TVTag("?", "?");
			
			fragment = new TVGuideTabFragmentBroadcast(mockTVTag);;
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
		return getTVTags().size();
	}

	
	
	private List<TVTag> getTVTags() 
	{
		if(tvTags == null)
		{
			if (ContentManager.sharedInstance().getCacheManager().containsTVTags())
			{
				Log.d(TAG, "FragmentBugFinder - We have the TVTags in Cache.");
				
				tvTags = ContentManager.sharedInstance().getCacheManager().getTVTags();
			} 
			else
			{
				Log.d(TAG, "FragmentBugFinder - The TVTags are empty.");
				
				tvTags = new ArrayList<TVTag>();
			}
			
			notifyDataSetChanged();
		}
		
		return tvTags;
	}
	
	
	
	@Override
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}
}