package com.millicom.secondscreen.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.imbryk.viewPager.LoopViewPager;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.TVGuideTableFragment;

public class TagTypeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = "TagTypeFragmentStatePagerAdapter";
	private ArrayList<Tag> mTags;
	private TvDate mTvDate;
	private int mDatePosition;
	private boolean createBackground;

	private HashMap<String, AdListAdapter> adapterMap = new HashMap<String, AdListAdapter>();
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Tag> tags, TvDate tvDate, int datePosition) {
		super(fm);
		this.mTags = tags;
		this.mTvDate = tvDate;
		this.mDatePosition = datePosition;
		this.createBackground = createBackground;
	}

	@Override
	public Fragment getItem(int position) {
		position = LoopViewPager.toRealPosition(position, getCount());
		return TVGuideTableFragment.newInstance(mTags.get(position), mTvDate, mDatePosition, adapterMap);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return mTags.get(position % mTags.size()).getName();
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

