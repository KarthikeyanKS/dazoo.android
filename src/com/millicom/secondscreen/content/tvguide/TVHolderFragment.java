package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.TagTypeFragmentStatePagerAdapter;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.storage.DazooStore;
import com.viewpagerindicator.TabPageIndicator;

public class TVHolderFragment extends Fragment {

	private static final String TAG = "TVHolderFragment";
	
	private PagerAdapter							mAdapter;
	private int										mTabSelectedIndex	= 0, mDateSelectedIndex;
	private TabPageIndicator						mPageTabIndicator;
	private ArrayList<Tag>							mTags				= new ArrayList<Tag>();
	private ArrayList<String>						mTabTitles;
	private ViewPager								mViewPager;

	private String									mDate;
	private ArrayList<TvDate>						mDates;
	
	private String token;
	private boolean mIsLoggedIn;

	private static OnViewPagerIndexChangedListener	mListener;

	public interface OnViewPagerIndexChangedListener {
		public void onIndexSelected(int position);
	}

	public static TVHolderFragment newInstance(int startingIndex, int dateSelection, OnViewPagerIndexChangedListener listener) {
		mListener = listener;

		TVHolderFragment fragment = new TVHolderFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("holder_date", dateSelection);
		bundle.putInt("starting_index", startingIndex);

		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		mDateSelectedIndex = bundle.getInt("holder_date");
		mTabSelectedIndex = bundle.getInt("starting_index");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_tvguide_holder_layout, null);

		mDates = DazooStore.getInstance().getTvDates();
		mTags = DazooStore.getInstance().getTags();
		
		mViewPager = (ViewPager) v.findViewById(R.id.home_pager);
		mViewPager.setOffscreenPageLimit(mTags.size()-1);
		mViewPager.setEnabled(false);
		mPageTabIndicator = (TabPageIndicator) v.findViewById(R.id.home_indicator);
		setAdapter(mTabSelectedIndex);

		return v;
	}

	private void setAdapter(int selectedIndex) {

		mAdapter = new TagTypeFragmentStatePagerAdapter(getActivity().getSupportFragmentManager(), mTags, mDates.get(mDateSelectedIndex), mDateSelectedIndex);

		mViewPager.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(selectedIndex);

		mViewPager.setVisibility(View.VISIBLE);
		mPageTabIndicator.setVisibility(View.VISIBLE);

		mPageTabIndicator.setViewPager(mViewPager);
		mPageTabIndicator.notifyDataSetChanged();
		mPageTabIndicator.setCurrentItem(selectedIndex);

		mPageTabIndicator.setOnPageChangeListener(mOnPageChangeListener);
	}

	OnPageChangeListener	mOnPageChangeListener	= new OnPageChangeListener() {

		@Override
		public void onPageSelected(int pos) {
			mTabSelectedIndex = pos;
			mListener.onIndexSelected(mTabSelectedIndex);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

}
