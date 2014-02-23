package com.millicom.mitv.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVTag;
import com.mitv.R;
import com.mitv.adapters.TagTypeFragmentStatePagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class TVHolderFragment extends Fragment implements OnPageChangeListener {

	@SuppressWarnings("unused")
	private static final String TAG = TVHolderFragment.class.getName();
	private static final String BUNDLE_INFO_STARTING_INDEX = "BUNDLE_INFO_STARTING_INDEX";

	private PagerAdapter pagerAdapter;
	private int selectedTabIndex = 0;
	private TabPageIndicator pageTabIndicator;
	private ArrayList<TVTag> tvTags;
	private ViewPager viewPager;
	private static OnViewPagerIndexChangedListener viewPagerIndexChangedListener;

	public interface OnViewPagerIndexChangedListener {
		public void onIndexSelected(int position);
	}

	public static TVHolderFragment newInstance(int startingIndex, OnViewPagerIndexChangedListener listener) {
		viewPagerIndexChangedListener = listener;

		TVHolderFragment fragment = new TVHolderFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_INFO_STARTING_INDEX, startingIndex);

		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		selectedTabIndex = bundle.getInt(BUNDLE_INFO_STARTING_INDEX);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tvguide_holder_layout, null);
		tvTags = ContentManager.sharedInstance().getFromStorageTVTags();

		viewPager = (ViewPager) v.findViewById(R.id.home_pager);
		viewPager.setOffscreenPageLimit(tvTags.size());
		viewPager.setEnabled(false);
		pageTabIndicator = (TabPageIndicator) v.findViewById(R.id.home_indicator);
		setAdapter(selectedTabIndex);

		return v;
	}

	@Override
	public void onPageSelected(int pos) {
		selectedTabIndex = pos;
		viewPagerIndexChangedListener.onIndexSelected(selectedTabIndex);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	private void setAdapter(int selectedIndex) {
		TVDate tvDate = ContentManager.sharedInstance().getFromStorageTVDateSelected();
		pagerAdapter = new TagTypeFragmentStatePagerAdapter(getChildFragmentManager(), tvTags, tvDate);

		viewPager.setAdapter(pagerAdapter);
		pagerAdapter.notifyDataSetChanged();
		viewPager.setCurrentItem(selectedIndex);

		viewPager.setVisibility(View.VISIBLE);
		pageTabIndicator.setVisibility(View.VISIBLE);

		pageTabIndicator.setViewPager(viewPager);
		pageTabIndicator.notifyDataSetChanged();
		pageTabIndicator.setCurrentItem(selectedIndex);

		pageTabIndicator.setOnPageChangeListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
