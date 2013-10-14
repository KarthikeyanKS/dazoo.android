package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import com.millicom.secondscreen.adapters.CategoryFragmentPagerAdapter;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.SSTagsPage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.customviews.InfinitePagerAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;

public class TVGuideFragmentContainer extends SSPageFragment {

	private static final String	TAG				= "TVGuideFragmentContainer";

	// data arraylists
	private ArrayList<TvDate>	mTvDates;
	private ArrayList<Tag>		mTags;
	private ArrayList<Channel>	mChannels;
	private ArrayList<String>	mTabTitles;

	private View				mRootView;
	private ViewPager			mViewPager;
	private PagerTabStrip		mPagerTabStrip;
	//private PagerAdapter		mAdapter;

	private int					mSelectedIndex	= 0;
	private Activity			mActivity;
	private String				mDate;
	//private SSTagsPage			mTagsPage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();

		// mTags = bundle.getParcelableArrayList(Consts.PARCELABLE_TAGS_LIST);

		// create new page for tags
		//mTagsPage = SSTagsPage.getInstance();
		mTvDates = bundle.getParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST);
		mChannels = bundle.getParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST);
		mDate = mTvDates.get(0).getDate().toString();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));
	}

	BroadcastReceiver	mBroadcastReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE);
			Log.d(TAG, "mDate" + mDate);

			reloadPage();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide_fragment_container, container, false);
		super.initRequestCallbackLayouts(mRootView);

		mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mViewPager.setEnabled(false);
		mPagerTabStrip = (PagerTabStrip) mRootView.findViewById(R.id.pager_header);

		mActivity = getActivity();
		
		loadPage();

		Log.d(TAG, "on Create View");
		
		return mRootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy");

		// Stop listening to broadcast events
		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiver);
		//LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
	};

	// @Override
	// public void onPause() {
	// super.onPause();

	// // Cancel any get page request
	// mTagsPage.cancelGetPage();
	// }

	OnPageChangeListener	mOnPageChangeListener	= new OnPageChangeListener() {

		@Override
		public void onPageSelected(int pos) {
			Log.d(TAG, "mSelectedIndex: " + mSelectedIndex + " pos: " + pos);
			mSelectedIndex = pos;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private void reloadPage() {
		//mTagsPage = SSTagsPage.getInstance();

		// Don't allow any swiping gestures while reloading
		mViewPager.setVisibility(View.GONE);
		mPagerTabStrip.setVisibility(View.GONE);
		mTags = null;
		getPage();
	}

	private void createFragments() {
		//if (mTags != null && !mTags.isEmpty()) {
			// insert general Tag category in the list
			Tag tagAll = new Tag();
			tagAll.setId(getResources().getString(R.string.all_categories_id));
			tagAll.setName(getResources().getString(R.string.all_categories_name));
			mTags.add(0, tagAll);

			Log.d(TAG, "mTags SIZE:" + mTags.size());

			mTabTitles = new ArrayList<String>();
			for (Tag tag : mTags) {
				mTabTitles.add(tag.getName());
			}
			Log.d(TAG, "mTagTitles size: " + mTabTitles.size());

		setAdapter();
	}

	private void setAdapter() {
		// if (mAdapter == null)

		final PagerAdapter mAdapter = new CategoryFragmentPagerAdapter(getChildFragmentManager(), mTabTitles){
			@Override
			public Fragment initFragment(int position) {
				Log.d(TAG, "INIT FRAGMENTS");
				Fragment fragment;
				// fragment = TVGuideCategoryFragment.newInstance(mTags.get(position), mTvDates.get(0).getDate().toString(), mChannels);
				fragment = TVGuideCategoryFragment.newInstance(mTags.get(position), mDate, mChannels);
				
				
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, mChannels);
				bundle.putString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE, mDate);
				bundle.putString(Consts.INTENT_EXTRA_TAG, mTags.get(position).getName());

				fragment.setArguments(bundle);
				
				return fragment;
			}
		};
		
		final PagerAdapter wrappedAdapter = new InfinitePagerAdapter(mAdapter, mTabTitles);
		

		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "UPDATE VIEWPAGER");
				mViewPager.setVisibility(View.VISIBLE);
				mPagerTabStrip.setVisibility(View.VISIBLE);
				//mViewPager.setAdapter(mAdapter);
				mViewPager.setAdapter(wrappedAdapter);
				
				//mAdapter.notifyDataSetChanged();
				wrappedAdapter.notifyDataSetChanged();
				
				mViewPager.setCurrentItem(mSelectedIndex);
			}
		});
	}

	private void getPage() {
		updateUI(REQUEST_STATUS.LOADING);
		Log.d(TAG, "LOADING");
		//mTagsPage.getPage(new SSPageCallback() {
		SSTagsPage.getInstance().getPage(new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {
				// mProgramTypes = mProgramTypePage.getProgramTypes();
				mTags = new ArrayList<Tag>();
				//mTags = mTagsPage.getTags();
				mTags = SSTagsPage.getInstance().getTags();
				
				if (!pageHoldsData()) {
					// Request failed
					Log.d(TAG, "UpdateUI: FAILED");

					updateUI(REQUEST_STATUS.FAILED);
				}
			}
		});
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		// if (!pageHoldsData()) {

		getPage();
		// }

	}

	@Override
	protected boolean pageHoldsData() {

		if (mTags != null) {
			Log.d(TAG, "tags are not null");
			if (mTags.isEmpty()) {
				Log.d(TAG, "EMPTY RESPONSE");
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		}
		return mTags != null;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			Log.d(TAG, "CREATE FRAGMENTS");
			createFragments();
		}
	}
}
