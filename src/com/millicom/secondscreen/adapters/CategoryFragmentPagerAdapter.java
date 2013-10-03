package com.millicom.secondscreen.adapters;

import com.millicom.secondscreen.content.tvguide.TVGuideFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

public class CategoryFragmentPagerAdapter extends FragmentPagerAdapter{

	public CategoryFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position){
		return TVGuideFragment.newInstance(position);
	}
	
	@Override
	public int getCount(){
		// TODO: NUMBER OF PAGES EQUALS THE NUMBER OF AVAILABLE TAGS + 1 (for "All categories page")
	return 10;	
	}

}
