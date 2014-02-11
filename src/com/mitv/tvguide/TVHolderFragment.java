package com.mitv.tvguide;

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

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.TagTypeFragmentStatePagerAdapter;
import com.mitv.manager.ApiClient;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;
import com.mitv.storage.MiTVStore;
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

		mDates = MiTVStore.getInstance().getTvDates();
		mTags = MiTVStore.getInstance().getTags();
		
		mViewPager = (ViewPager) v.findViewById(R.id.home_pager);
		mViewPager.setOffscreenPageLimit(mTags.size());
		mViewPager.setEnabled(false);
		mPageTabIndicator = (TabPageIndicator) v.findViewById(R.id.home_indicator);
		setAdapter(mTabSelectedIndex);

		return v;
	}

	private void setAdapter(int selectedIndex) {

		mAdapter = new TagTypeFragmentStatePagerAdapter(getChildFragmentManager(), mTags, mDates.get(mDateSelectedIndex), mDateSelectedIndex);

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

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
}
