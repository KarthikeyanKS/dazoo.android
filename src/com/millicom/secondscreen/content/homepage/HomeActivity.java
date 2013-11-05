package com.millicom.secondscreen.content.homepage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.ActionBarDropDownDateListAdapter;
import com.millicom.secondscreen.adapters.TagTypeFragmentStatePagerAdapter;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.storage.DazooStore;
import com.viewpagerindicator.TabPageIndicator;

public class HomeActivity extends SSPageFragmentActivity implements OnClickListener, ActionBar.OnNavigationListener {

	private static final String					TAG					= "HomeActivity";
	private TextView							mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private ViewPager							mViewPager;
	private ActionBar							mActionBar;
	private ActionBarDropDownDateListAdapter	mDayAdapter;
	public static int							mBroadcastSelection	= -1;
	private int									mTabSelectedIndex	= 0, mDateSelectedIndex;
	private ArrayList<TvDate>					mTvDates			= new ArrayList<TvDate>();
	private ArrayList<Channel>					mChannels;
	private String								mDate;
	private ArrayList<Tag>						mTags				= new ArrayList<Tag>();
	private ArrayList<String>					mTabTitles;
	private FragmentStatePagerAdapter			mAdapter;
	private TabPageIndicator					mPageTabIndicator;
	private TvDate								mTvDateSelected;
	private boolean								mIsReady			= false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mDateSelectedIndex = 0;

		// broadcast receiver for date selection
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverDate, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));

		// broadcasst receiver for content availability
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverContent, new IntentFilter(Consts.INTENT_EXTRA_GUIDE_AVAILABLE));

		initViews();

		loadPage();
	}

	BroadcastReceiver	mBroadcastReceiverContent	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, false);
			Log.d(TAG, "content for TvGuide is ready");

			if (mIsReady && (mDateSelectedIndex == 0)) {
				if (!pageHoldsData()) {
					updateUI(REQUEST_STATUS.FAILED);
				}
			} else if (mIsReady && (mDateSelectedIndex != 0)) {
				updateUI(REQUEST_STATUS.LOADING);
				mViewPager.setVisibility(View.GONE);
				setAdapter(mTabSelectedIndex);
			}
		}
	};

	BroadcastReceiver	mBroadcastReceiverDate		= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE);
			Log.d(TAG, "====== mDate" + mDate + "======");

			mDateSelectedIndex = intent.getIntExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, 0);
			Log.d(TAG, "mDateSelectedIndex: " + mDateSelectedIndex);

			// RELOAD THE PAGE WITH NEW DATE
			reloadPage();
		}
	};

	private void initViews() {
		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));

		mActionBar = getSupportActionBar();

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		mViewPager = (ViewPager) findViewById(R.id.home_pager);
		mViewPager.setEnabled(false);

		mPageTabIndicator = (TabPageIndicator) findViewById(R.id.home_indicator);

		TvDate date = new TvDate();
		date.setAlias("today");
		date.setName("Today");
		date.setId("2013-10-29");
		date.setDate("2013-10-29");
		mTvDates.add(date);

		Tag tagAll = new Tag();
		tagAll.setId(getResources().getString(R.string.all_categories_id));
		tagAll.setName(getResources().getString(R.string.all_categories_name));
		mTags.add(0, tagAll);

		mAdapter = new TagTypeFragmentStatePagerAdapter(getSupportFragmentManager(), mTags, date, mDateSelectedIndex);
		mViewPager.setAdapter(mAdapter);
		mPageTabIndicator.setViewPager(mViewPager);

		mViewPager.setCurrentItem(0);
		mPageTabIndicator.setCurrentItem(0);
		mViewPager.setVisibility(View.GONE);
		mPageTabIndicator.setVisibility(View.GONE);
		mPageTabIndicator.setOnPageChangeListener(mOnPageChangeListener);

		Log.d(TAG, "INIT VIEWS");
	}

	@Override
	protected void loadPage() {
		// The the initial state to be loading
		updateUI(REQUEST_STATUS.LOADING);
		Log.d(TAG, "UI: LOADING");

		// SINGLETON CORE LOGIC COMMUNICATION HERE
		DazooCore.getInstance(this, mDateSelectedIndex).fetchContent();
	}

	private void reloadPage() {
		// Don't allow any swiping gestures while reloading

		updateUI(REQUEST_STATUS.LOADING);
		mViewPager.setVisibility(View.GONE);

		// mTags.clear();
		mTags = null;
		mTags = DazooStore.getInstance().getTags();

		Log.d(TAG, "DATE SELECTED INDEX: " + mDateSelectedIndex);

		// check if we have this data already
		setAdapter(mTabSelectedIndex);
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;

		Log.d(TAG, "pageHoldsData()");
		// CHECK THE PRESENCE OF THE DATA FROM DAZOOSTORE SINGLETON
		mTags = null;
		mTvDates = DazooStore.getInstance().getTvDates();
		mTags = DazooStore.getInstance().getTags();

		if (mTvDates != null) {
			if (mTvDates.isEmpty()) {
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				Log.d(TAG, "SUCCESSFUL");
				updateUI(REQUEST_STATUS.SUCCESSFUL);
				result = true;
			}
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			mDayAdapter = new ActionBarDropDownDateListAdapter(mTvDates);
			mDayAdapter.setSelectedIndex(mDateSelectedIndex);
			mActionBar.setListNavigationCallbacks(mDayAdapter, this);
			createFragments();
		}
	}

	private void createFragments() {

		mTabTitles = new ArrayList<String>();
		for (Tag tag : mTags) {
			mTabTitles.add(tag.getName());
		}
		Log.d(TAG, "mTagTitles size: " + mTabTitles.size());

		setAdapter(mTabSelectedIndex);
	}

	private void setAdapter(int selectedIndex) {
		mAdapter = null;
		mAdapter = new TagTypeFragmentStatePagerAdapter(getSupportFragmentManager(), mTags, mTvDates.get(mDateSelectedIndex), mDateSelectedIndex);

		mViewPager.setAdapter(mAdapter);

		mViewPager.setCurrentItem(selectedIndex);

		mPageTabIndicator.notifyDataSetChanged();

		mPageTabIndicator.setCurrentItem(selectedIndex);

		mViewPager.setVisibility(View.VISIBLE);
		mPageTabIndicator.setVisibility(View.VISIBLE);
	}

	OnPageChangeListener	mOnPageChangeListener	= new OnPageChangeListener() {

		@Override
		public void onPageSelected(int pos) {
			mTabSelectedIndex = pos;
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
		Log.d(TAG, "onDestroy");
		// Stop listening to broadcast events
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverDate);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverContent);
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		mDayAdapter.setSelectedIndex(position);
		mTvDateSelected = mTvDates.get(position);
		Log.d(TAG, "ON NAVIGATION ITEM SELECTED: " + position);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE, mTvDateSelected.getDate()).putExtra(
						Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, position));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent toSearchPage = new Intent(HomeActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_homepage, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			// we are already here, nothing happens
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentActivity = new Intent(HomeActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			// tab to activity page
			Intent intentMe = new Intent(HomeActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		}
	}
}
