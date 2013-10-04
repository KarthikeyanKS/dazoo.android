package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import com.millicom.secondscreen.adapters.CategoryFragmentPagerAdapter;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;

public class TVGuideFragmentContainer extends Fragment {

	private static final String	TAG				= "TVGuideFragmentContainer";

	// data arraylists
	private ArrayList<TvDate>	mTvDates;
	private ArrayList<Tag>		mTags;
	private ArrayList<Channel>	mChannels;
	private ArrayList<String>	mTabTitles;

	private View				mRootView;
	private ViewPager			mViewPager;
	private PagerTabStrip		mPagerTabStrip;
	private PagerAdapter		mAdapter;

	private int					mSelectedIndex	= 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);

		Bundle bundle = getArguments();

		mTags = bundle.getParcelableArrayList(Consts.PARCELABLE_TAGS_LIST);
		mTvDates = bundle.getParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST);
		mChannels = bundle.getParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide_fragment_container, container, false);
		mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mViewPager.setEnabled(false);
		mPagerTabStrip = (PagerTabStrip) mRootView.findViewById(R.id.pager_header);

		setupFragments();

		return mRootView;
	}

	OnPageChangeListener	mOnPageChangeListener	= new OnPageChangeListener() {

		@Override
		public void onPageSelected(int pos) {
			Log.d(TAG, "onPage was selected : " + pos);

			mSelectedIndex = pos;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	// Create tabs for all categories
	private void setupFragments() {

		// Make sure we have categories
		if (mTags != null && !mTags.isEmpty()) {

			mTabTitles = new ArrayList<String>();

			for (Tag tag : mTags) {
				mTabTitles.add(tag.getName());
			}

			// Attach the adapter to the ViewPager
			setAdapter();

		}
	}

	private void setAdapter() {
		
		if (mAdapter == null) mAdapter = new CategoryFragmentPagerAdapter(getChildFragmentManager(), mTabTitles) {

			@Override
			public Fragment initFragment(int position) {

				Fragment fragment;

				fragment = TVGuideCategoryFragment.newInstance(mTags.get(position), mTvDates.get(0).getDate().toString(), mChannels);

				return fragment;
			}
		};

		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		// Have to set the adapter in a handler
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {

				mViewPager.setVisibility(View.VISIBLE);
				mPagerTabStrip.setVisibility(View.VISIBLE);
				mViewPager.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();

				Log.d(TAG, "mStarting @ Index : " + mSelectedIndex);

				// Set the current item
				mViewPager.setCurrentItem(mSelectedIndex);
			}
		});
	}
}
