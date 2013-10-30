package com.millicom.secondscreen.content.homepage;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.viewpagerindicator.TabPageIndicator;

public class HomeActivity extends SSPageFragmentActivity implements OnClickListener, ActionBar.OnNavigationListener {

	private static final String			TAG					= "HomeActivity";
	private TextView					mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private ViewPager					mViewPager;
	private ActionBar					mActionBar;
	private ActionBarDropDownDateListAdapter mDayAdapter;
	public static int					mBroadcastSelection	= -1;
	private int							mSelectedIndex		= 0;
	private ArrayList<TvDate>			mTvDates			= new ArrayList<TvDate>();
	private ArrayList<Channel>			mChannels;
	private String						mDate;
	private ArrayList<Tag>				mTags				= new ArrayList<Tag>();
	private ArrayList<String>			mTabTitles;
	private FragmentStatePagerAdapter	mAdapter;
	private TabPageIndicator			mPageTabIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));

		initViews();

		// GET THE DATE FROM THE CORE LOGIC
		// if not saved before, load the page from scratch
		// if (!loadHomeFromSavedInstanceState(savedInstanceState)) {
		loadPage();
		// }
	}

	BroadcastReceiver	mBroadcastReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE);
			Log.d(TAG, "mDate" + mDate);

			// RELOAD THE PAGE WITH NEW DATE
			reloadPage(mSelectedIndex);
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

		TvDate date = new TvDate();
		date.setAlias("today");
		date.setName("Today");
		date.setId("2013-10-29");
		date.setDate("2013-10-29");
		mTvDates.add(date);

		TvDate date1 = new TvDate();
		date1.setAlias("tomorrow");
		date1.setName("Tomorrow");
		date1.setId("2013-10-30");
		date1.setDate("2013-10-30");
		mTvDates.add(date1);

		TvDate date2 = new TvDate();
		date2.setAlias("thursday");
		date2.setName("Thursday");
		date2.setId("2013-10-31");
		date2.setDate("2013-10-31");
		mTvDates.add(date2);
		
		mDayAdapter = new ActionBarDropDownDateListAdapter(this, mTvDates);
		mDayAdapter.setSelectedIndex(mSelectedIndex);
		mActionBar.setListNavigationCallbacks(mDayAdapter, this);

		mViewPager = (ViewPager) findViewById(R.id.home_pager);
		mViewPager.setEnabled(false);

		mPageTabIndicator = (TabPageIndicator) findViewById(R.id.home_indicator);

		super.initCallbackLayouts();
	}

	@Override
	protected void loadPage() {
		// The the initial state to be loading
		updateUI(REQUEST_STATUS.LOADING);

		// SINGLETON CORE LOGIC COMMUNICATION HERE

		if (!pageHoldsData()) {
			// Request failed
			updateUI(REQUEST_STATUS.FAILED);
		}
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;

		// CHECK THE PRESENCE OF THE DATA FROM DAZOOSTORE SINGLETON

		// if (mTvDates != null) {
		// if (mTvDates.isEmpty()) {
		// Log.d(TAG, "EMPTY RESPONSE");
		// updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
		// } else {
		Log.d(TAG, "SUCCESSFUL");
		updateUI(REQUEST_STATUS.SUCCESSFUL);
		// }
		result = true;
		// }
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			Log.d(TAG, "CREATE FRAGMENTS");
			createFragments();
		}
	}

	private void createFragments() {
		// if (mTags != null && !mTags.isEmpty()) {
		// insert general Tag category in the list
		Tag tagAll = new Tag();
		tagAll.setId(getResources().getString(R.string.all_categories_id));
		tagAll.setName(getResources().getString(R.string.all_categories_name));
		Log.d(TAG, "ALL CATEGORIES:" + getResources().getString(R.string.all_categories_name));
		mTags.add(0, tagAll);

		Tag tagOne = new Tag();
		tagOne.setId(getResources().getString(R.string.all_categories_id));
		tagOne.setName("One");
		mTags.add(tagOne);

		Tag tagTwo = new Tag();
		tagTwo.setId(getResources().getString(R.string.all_categories_id));
		tagTwo.setName("Two");
		mTags.add(tagTwo);

		Tag tagThree = new Tag();
		tagThree.setId(getResources().getString(R.string.all_categories_id));
		tagThree.setName("Three");
		mTags.add(tagThree);

		Tag tagFour = new Tag();
		tagFour.setId(getResources().getString(R.string.all_categories_id));
		tagFour.setName("Four");
		mTags.add(tagFour);

		Tag tagFive = new Tag();
		tagFive.setId(getResources().getString(R.string.all_categories_id));
		tagFive.setName("Five");
		mTags.add(tagFive);

		Tag tagSix = new Tag();
		tagSix.setId(getResources().getString(R.string.all_categories_id));
		tagSix.setName("Six");
		mTags.add(tagSix);

		Tag tagSeven = new Tag();
		tagSeven.setId(getResources().getString(R.string.all_categories_id));
		tagSeven.setName("Seven");
		mTags.add(tagSeven);

		Tag tagEight = new Tag();
		tagEight.setId(getResources().getString(R.string.all_categories_id));
		tagEight.setName("Eight");
		mTags.add(tagEight);

		Log.d(TAG, "mTags SIZE:" + mTags.size());

		mTabTitles = new ArrayList<String>();
		for (Tag tag : mTags) {
			mTabTitles.add(tag.getName());
		}
		Log.d(TAG, "mTagTitles size: " + mTabTitles.size());

		setAdapter(mSelectedIndex);
	}

	private void setAdapter(int selectedIndex) {
		mAdapter = new TagTypeFragmentStatePagerAdapter(getSupportFragmentManager(), mTags);
		
		mViewPager.setVisibility(View.VISIBLE);
		mViewPager.setAdapter(mAdapter);

		mViewPager.setCurrentItem(selectedIndex);

		mPageTabIndicator.setViewPager(mViewPager);
		mPageTabIndicator.setVisibility(View.VISIBLE);

		mPageTabIndicator.setCurrentItem(selectedIndex);
		mPageTabIndicator.setOnPageChangeListener(mOnPageChangeListener);
	}

	private boolean loadHomeFromSavedInstanceState(Bundle savedInstanceState) {
		boolean result = false;
		if (savedInstanceState != null) {
			Log.d(TAG, "load from saved instance state");

			// mChannels = savedInstanceState.getParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST);
			// mTvDates = savedInstanceState.getParcelableArrayList(Consts.PARCELABLE_TAGS_LIST);

			if (mChannels != null && mTvDates != null) {

				// refresh the HomePage
				result = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		}
		return result;
	}

	OnPageChangeListener	mOnPageChangeListener	= new OnPageChangeListener() {

		@Override
		public void onPageSelected(int pos) {
			mSelectedIndex = pos;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

		// SAVE ACTIVITY CONTENT FOR

		// savedInstanceState.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, mChannels);
		// savedInstanceState.putParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST, mTvDates);

		super.onSaveInstanceState(savedInstanceState);
	}

	private void reloadPage(int selectedIndex) {
		// mTagsPage = SSTagsPage.getInstance();

		// Don't allow any swiping gestures while reloading
		mViewPager.setVisibility(View.GONE);
		mPageTabIndicator.setVisibility(View.GONE);
		// mTags = null;

		// getPage();

		// NEW REQUEST WITH NEW DATE TO THE CORE LOGIC

		setAdapter(selectedIndex);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		// Stop listening to broadcast events
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Log.d(TAG,"here");
		mDayAdapter.setSelectedIndex(position);
		
		
		
		Log.d(TAG,"AFTER");
		TvDate tvDateItem = mTvDates.get(position);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE, tvDateItem.getDate()));
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
