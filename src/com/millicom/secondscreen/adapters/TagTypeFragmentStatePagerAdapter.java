package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.imbryk.viewPager.LoopViewPager;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.tvguide.TVGuideTableFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideTagTypeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class TagTypeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = "TagTypeFragmentStatePagerAdapter";
	private ArrayList<Tag> mTags;
	private String mTvDate;
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Tag> tags, String tvDate) {
		super(fm);
		this.mTags = tags;
		this.mTvDate = tvDate;
	}

	@Override
	public Fragment getItem(int position) {
		position = LoopViewPager.toRealPosition(position, getCount());
		Fragment fragment;
		if (position == 0) {
			Log.d(TAG,"TableFragment: Position: " + position + " TAG: " + mTags.get(position).getName());
			return fragment = TVGuideTableFragment.newInstance(mTags.get(position), mTvDate, position);
		} else {
			Log.d(TAG,"TagTypeFragment: Position: " + position + " TAG: " + mTags.get(position).getName());
			return fragment = TVGuideTagTypeFragment.newInstance(mTags.get(position), mTvDate, position);
		}
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

