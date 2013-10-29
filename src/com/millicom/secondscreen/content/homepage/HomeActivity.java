package com.millicom.secondscreen.content.homepage;

import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.CategoryFragmentPagerAdapter;
import com.millicom.secondscreen.adapters.DateListNavigationAdapter;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.millicom.secondscreen.content.tvguide.TVGuideOverviewFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideTagFragment;
import com.millicom.secondscreen.customviews.InfinitePagerAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomeActivity extends SSPageFragmentActivity implements OnClickListener, ActionBar.OnNavigationListener {

	private static final String			TAG					= "HomeActivity";
	private TextView					mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private ViewPager					mViewPager;
	private PagerTabStrip				mPagerTabStrip;
	private ActionBar					mActionBar;
	private DateListNavigationAdapter	mDayAdapter;
	public static int					mBroadcastSelection	= -1;
	private int							mSelectedIndex		= 0;
	private ArrayList<TvDate>			mTvDates;
	private ArrayList<Channel>			mChannels;
	private String						mDate;
	private ArrayList<Tag>				mTags;
	private ArrayList<String>			mTabTitles;
	private PagerAdapter				mAdapter;

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
		//loadPage();
		// }
	}

	BroadcastReceiver	mBroadcastReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE);
			Log.d(TAG, "mDate" + mDate);
			
			// RELOAD THE PAGE WITH NEW DATE
			//reloadPage();
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

		mDayAdapter = new DateListNavigationAdapter(this, mTvDates);
		mDayAdapter.setSelectedIndex(mSelectedIndex);
		mActionBar.setListNavigationCallbacks(mDayAdapter, this);

		mViewPager = (ViewPager) findViewById(R.id.home_pager);
		mViewPager.setEnabled(false);
		mPagerTabStrip = (PagerTabStrip) findViewById(R.id.home_pager_header);

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

		mAdapter = new CategoryFragmentPagerAdapter(getSupportFragmentManager(), mTabTitles) {
			@Override
			public Fragment initFragment(int position) {
				Fragment fragment;
				if (position == 0) {
					fragment = TVGuideOverviewFragment.newInstance(mTags.get(position), mDate, mChannels);
				} else {
					fragment = TVGuideTagFragment.newInstance(mTags.get(position), mDate, mChannels);
				}
				Bundle bundle = new Bundle();
				
				// ARGUMENTS TO BE DETERMINED
				
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
				mViewPager.setVisibility(View.VISIBLE);
				mPagerTabStrip.setVisibility(View.VISIBLE);
				// mViewPager.setAdapter(mAdapter);
				mViewPager.setAdapter(wrappedAdapter);

				// mAdapter.notifyDataSetChanged();
				wrappedAdapter.notifyDataSetChanged();

				mViewPager.setCurrentItem(mSelectedIndex);
			}
		});
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

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

		// SAVE ACTIVITY CONTENT FOR

		// savedInstanceState.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, mChannels);
		// savedInstanceState.putParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST, mTvDates);

		super.onSaveInstanceState(savedInstanceState);
	}

	private void reloadPage() {
		// mTagsPage = SSTagsPage.getInstance();

		// Don't allow any swiping gestures while reloading
		// mViewPager.setVisibility(View.GONE);
		// mPagerTabStrip.setVisibility(View.GONE);
		// mTags = null;

		// getPage();

		// NEW REQUEST WITH NEW DATE TO THE CORE LOGIC
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
		mDayAdapter.setSelectedIndex(position);
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
