package com.mitv.listadapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mitv.fragments.UserTutorialFragment;

/**
 * A pager adapter that represents 5 ScreenSlidePageFragment objects, in sequence.
 */
public class TutorialScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

	private static final int NUM_PAGES = 5;
	
	public TutorialScreenSlidePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return new UserTutorialFragment(position);
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

}	
