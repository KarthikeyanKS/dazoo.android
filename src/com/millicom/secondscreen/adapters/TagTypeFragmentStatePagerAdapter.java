package com.millicom.secondscreen.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.imbryk.viewPager.LoopViewPager;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.tvguide.TVGuideTableFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideTagFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideTagTypeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class TagTypeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Tag> mTags;
	
	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Tag> tags) {
		super(fm);
		this.mTags = tags;
	}

	@Override
	public Fragment getItem(int position) {
		position = LoopViewPager.toRealPosition(position, getCount());
		Fragment fragment;
		if (position == 0) {
			return fragment = TVGuideTableFragment.newInstance(mTags.get(position));
		} else {
			return fragment = TVGuideTagTypeFragment.newInstance(mTags.get(position));
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

