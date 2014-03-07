
package com.mitv.listadapters;



import java.util.ArrayList;

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
	
	
	private ArrayList<TVTag> tags;
	private TVDate tvDate;
	
	// TODO newArc Complete this
	private ArrayList<TVGuideTableFragment> tvGuideTableFragments;

	
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<TVTag> tags, TVDate tvDate) 
	{
		super(fm);
		
		this.tags = tags;
		this.tvDate = tvDate;
	}

	
	
	@Override
	public Fragment getItem(int position) 
	{
		position = LoopViewPager.toRealPosition(position, getCount());
		
		return TVGuideTableFragment.newInstance(tags.get(position), tvDate);
	}
	
	
	
	@Override
	public CharSequence getPageTitle(int position) 
	{
		TVTag tvTag = tags.get(position % tags.size());
		
		return tvTag.getDisplayName();
	}

	
	
	@Override
	public int getCount() 
	{
		return tags.size();
	}
	
	
	
	@Override
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}
}