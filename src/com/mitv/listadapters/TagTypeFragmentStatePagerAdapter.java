
package com.mitv.listadapters;



import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.fragments.TVGuideTableFragment;
import com.mitv.models.TVDate;
import com.mitv.models.TVTag;



public class TagTypeFragmentStatePagerAdapter
	extends FragmentStatePagerAdapter 
{
	@SuppressWarnings("unused")
	private static final String TAG = TagTypeFragmentStatePagerAdapter.class.getName();
	
	
	private List<TVTag> tvTags;
	private TVDate tvDate;

	
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, List<TVTag> tags, TVDate tvDate) 
	{
		super(fm);
		
		this.tvTags = tags;
		this.tvDate = tvDate;
	}
	
	
	
	@Override
	public Fragment getItem(int position)
	{
		int realPosition = LoopViewPager.toRealPosition(position, getCount());
		
		TVTag tvTag = tvTags.get(realPosition);
		
		/* This instance will only be created when needed by the pager adapter */
		TVGuideTableFragment tvGuideTableFragment = TVGuideTableFragment.newInstance(tvTag, tvDate);
		
		return tvGuideTableFragment;
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
			// TODO NewArc - Hardcoded string
			displayName = "Unknown";
		}
		
		return displayName;
	}

	
	
	@Override
	public int getCount() 
	{
		return tvTags.size();
	}
	
	
	
	@Override
	public int getItemPosition(Object object) 
	{
	    return POSITION_UNCHANGED;
	}
}