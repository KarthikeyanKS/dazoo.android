package com.mitv.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.imbryk.viewPager.LoopViewPager;
import com.millicom.mitv.fragments.TVGuideTableFragment;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.TVTag;

public class TagTypeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = "TagTypeFragmentStatePagerAdapter";
	private ArrayList<TVTag> mTags;
	private TVDate mTvDate;
//	private int mDatePosition;
//	private boolean createBackground;
	private FragmentManager fm;

	private HashMap<String, AdListAdapter> adapterMap = new HashMap<String, AdListAdapter>();
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<TVTag> tags, TVDate tvDate) {
		super(fm);
		this.fm = fm;
		this.mTags = tags;
		this.mTvDate = tvDate;
//		this.createBackground = createBackground;
	}

	@Override
	public Fragment getItem(int position) {
		position = LoopViewPager.toRealPosition(position, getCount());
		
		return TVGuideTableFragment.newInstance(mTags.get(position), mTvDate, adapterMap);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		TVTag tvTag = mTags.get(position % mTags.size());
		return tvTag.getDisplayName();
	}

	@Override
	public int getCount() {
		return mTags.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
}

