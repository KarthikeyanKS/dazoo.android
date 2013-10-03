package com.millicom.secondscreen.content.tvguide;

import com.millicom.secondscreen.adapters.CategoryFragmentPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.millicom.secondscreen.R;

public class TVGuideFragmentContainer extends Fragment {

	private static final String	TAG	= "TVGuideFragmentContainer";

	private View				mRootView;
	private ViewPager			mViewPager;
	private PagerTabStrip		mPagerTabStrip;
	private PagerAdapter		pagerAdapter;

	private Activity			mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		mActivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide_fragment_container, container, false);

		mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mPagerTabStrip = (PagerTabStrip) mRootView.findViewById(R.id.pager_header);
		pagerAdapter = new CategoryFragmentPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(pagerAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				Log.d(TAG, "onPageSelected, position = " + position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		return mRootView;
	}
}
