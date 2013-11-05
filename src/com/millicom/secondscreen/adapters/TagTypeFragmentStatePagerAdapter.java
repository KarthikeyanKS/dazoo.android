package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.imbryk.viewPager.LoopViewPager;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.TVGuideTableFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideTagTypeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class TagTypeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = "TagTypeFragmentStatePagerAdapter";
	private ArrayList<Tag> mTags;
	private TvDate mTvDate;
	private int mDatePosition;
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Tag> tags, TvDate tvDate, int datePosition) {
		super(fm);
		this.mTags = tags;
		this.mTvDate = tvDate;
		this.mDatePosition = datePosition;
		Log.d(TAG,"!!!!!!!!!!!!!!!!!!! IN ADAPTER: DATE: " + tvDate.getDate());
	}

	@Override
	public Fragment getItem(int position) {
		position = LoopViewPager.toRealPosition(position, getCount());
		Fragment fragment;
		if (position == 0) {
			Log.d(TAG,"TableFragment: Position: " + position + " TAG: " + mTags.get(position).getName() + " DATE: " + mTvDate.getDate());
			return fragment = TVGuideTableFragment.newInstance(mTags.get(position), mTvDate, mDatePosition);
		} else {
			Log.d(TAG,"TagTypeFragment: Position: " + position + " TAG: " + mTags.get(position).getName() + " DATE: " + mTvDate.getDate());
			return fragment = TVGuideTagTypeFragment.newInstance(mTags.get(position), mTvDate, mDatePosition);
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

