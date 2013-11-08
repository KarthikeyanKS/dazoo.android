//package com.millicom.secondscreen.content.homepage;
//
//import java.util.ArrayList;
//
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.ActionBar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//
//import com.millicom.secondscreen.Consts;
//import com.millicom.secondscreen.Consts.REQUEST_STATUS;
//import com.millicom.secondscreen.R;
//import com.millicom.secondscreen.SecondScreenApplication;
//import com.millicom.secondscreen.adapters.ActionBarDropDownDateListAdapter;
//import com.millicom.secondscreen.content.SSChannelPage;
//import com.millicom.secondscreen.content.SSPageCallback;
//import com.millicom.secondscreen.content.SSPageFragmentActivity;
//import com.millicom.secondscreen.content.SSPageGetResult;
//import com.millicom.secondscreen.content.SSTvDatePage;
//import com.millicom.secondscreen.content.activity.ActivityFragment;
//import com.millicom.secondscreen.content.model.Channel;
//import com.millicom.secondscreen.content.model.TvDate;
//import com.millicom.secondscreen.content.myprofile.MyProfileFragment;
//import com.millicom.secondscreen.content.search.SearchPageActivity;
//import com.millicom.secondscreen.content.tvguide.TVGuideFragmentContainer;
//
//public class HomePageActivity extends SSPageFragmentActivity implements View.OnClickListener, ActionBar.OnNavigationListener {
//
//	private static final String			TAG					= "HomePageActivity";
//	private TextView					mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
//	private View						mTabSelectorContainerView;
//
//	private Fragment					mActiveFragment;
//	private ArrayList<TvDate>			mTvDates			= new ArrayList<TvDate>();
//	private ArrayList<Channel>			mChannels			= new ArrayList<Channel>();
//
//	private ActionBar					mActionBar;
//	private boolean						isDateData			= false;
//	
//	// keep the backstack for easier navigation
//	private ArrayList<Integer>			mBackStack			= new ArrayList<Integer>();
//
//	private ArrayList<Fragment>			mFragments;
//
//	// The position of the first fragment to be selected
//	private int							mStartingPosition	= 0;
//
//	public static int					mBroadcastSelection	= -1;
//	private int							mSelectedIndex		= 0;
//	private ActionBarDropDownDateListAdapter	mDayAdapter;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.layout_homepage_activity);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		initViews();
//
//		// if not saved before, load the page from scratch
//		if (!loadHomePageFromSavedInstanceState(savedInstanceState)) {
//			loadPage();
//		}
//	}
//
//	private void initViews() {
//		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);
//		mTabSelectorContainerView.setVisibility(View.GONE);
//
//		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
//		mTxtTabTvGuide.setOnClickListener(this);
//		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
//		mTxtTabPopular.setOnClickListener(this);
//		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
//		mTxtTabFeed.setOnClickListener(this);
//
//		mActionBar = getSupportActionBar();
//
//		final int actionBarColor = getResources().getColor(R.color.lightblue);
//		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
//		
//		mTabSelectorContainerView.setVisibility(View.VISIBLE);
//
//		super.initCallbackLayouts();
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle presses on the action bar items
//		switch (item.getItemId()) {
//		case R.id.menu_search:
//			Intent toSearchPage = new Intent(HomePageActivity.this, SearchPageActivity.class);
//			startActivity(toSearchPage);
//			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu items for use in the action bar
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_homepage, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	// private void initActionBarTvGuide(boolean isDateData, boolean isProgramTypesData) {
//	private void initActionBarTvGuide(boolean isDateData) {
//		
//		mActionBar.setDisplayShowTitleEnabled(false);
//		mActionBar.setDisplayShowCustomEnabled(true);
//		// mActionBar.setDisplayUseLogoEnabled(false);
//		mActionBar.setDisplayUseLogoEnabled(true);
//		// mActionBar.setDisplayShowHomeEnabled(false);
//		mActionBar.setDisplayShowHomeEnabled(true);
//		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//
//		mDayAdapter = new ActionBarDropDownDateListAdapter(mTvDates);
//		mDayAdapter.setSelectedIndex(mSelectedIndex);
//		mActionBar.setListNavigationCallbacks(mDayAdapter, this);
//	}
//
//	private void initActionBarActivity() {
//		//mActionBar.setDisplayShowTitleEnabled(false);
//		mActionBar.setDisplayShowTitleEnabled(true);
//		mActionBar.setDisplayShowCustomEnabled(true);
//		
//		mActionBar.setDisplayUseLogoEnabled(true);
//		mActionBar.setDisplayShowHomeEnabled(true);
//		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//		
//		mActionBar.setTitle(getResources().getString(R.string.activity_title));
//		
//		/*
//		mActionBar.setDisplayUseLogoEnabled(false);
//		mActionBar.setDisplayShowHomeEnabled(false);
//		
//		mActionBar.setCustomView(R.layout.layout_actionbar_activitypage);
//
//		final TextView title = (TextView) findViewById(R.id.actionbar_activitypage_title_tv);
//		title.setText(getResources().getString(R.string.activity_page_title));
//
//		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_activitypage_search_icon);
//		searchButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// move to the search page
//				Intent toSearchPage = new Intent(HomePageActivity.this, SearchPageActivity.class);
//				startActivity(toSearchPage);
//				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//			}
//		});
//		*/
//	}
//
//	private void initActionBarMe() {
//		//mActionBar.setDisplayShowTitleEnabled(false);
//		mActionBar.setDisplayShowTitleEnabled(true);
//		mActionBar.setDisplayShowCustomEnabled(true);
//		
//		mActionBar.setDisplayUseLogoEnabled(true);
//		mActionBar.setDisplayShowHomeEnabled(true);
//		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//		
//		mActionBar.setTitle(getResources().getString(R.string.myprofile_title));
//		
//		/*
//		mActionBar.setDisplayUseLogoEnabled(false);
//		mActionBar.setDisplayShowHomeEnabled(false);
//		mActionBar.setCustomView(R.layout.layout_actionbar_mepage);
//
//		final TextView title = (TextView) findViewById(R.id.actionbar_mepage_title_tv);
//		title.setText(getResources().getString(R.string.me_page_title));
//
//		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_mepage_search_icon);
//		searchButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// // move to the search page
//				// Intent toSearchPage = new Intent(HomePageActivity.this, SearchPageActivity.class);
//				// startActivity(toSearchPage);
//				// overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//				// move to login page for test purpose
//				Intent toLoginPage = new Intent(HomePageActivity.this, LoginActivity.class);
//				startActivity(toLoginPage);
//				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//			}
//		});
//		*/
//	}
//
//	private void selectNavigationTab(int position, boolean addToBackStack) {
//
//		if (mForceReload) {
//			reloadStartPage();
//			return;
//		}
//
//		if (position == 0) {
//			// tv guide
//			initActionBarTvGuide(isDateData);
//
//			// mTabSelectorContainerView.setBackgroundResource();
//
//			mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.orange));
//			mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
//			mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));
//
//			// mActiveFragment = new TVGuideFragment();
//			mActiveFragment = new TVGuideFragmentContainer();
//			// mActiveFragment = mFragments.get(position);
//
//			Bundle bundle = new Bundle();
//			bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, mChannels);
//			bundle.putParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST, mTvDates);
//			// bundle.putParcelableArrayList(Consts.PARCELABLE_PROGRAM_TYPES_LIST, mProgramTypes);
//			// bundle.putParcelableArrayList(Consts.PARCELABLE_TAGS_LIST, mTags);
//
//			mActiveFragment.setArguments(bundle);
//			getSupportFragmentManager().beginTransaction().replace(R.id.homepage_contentframe, mActiveFragment).commit();
//		} else if (position == 1) {
//			// Activity page
//			initActionBarActivity();
//
//			// mTabSelectorContainerView.setBackgroundResource();
//
//			mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
//			mTxtTabPopular.setTextColor(getResources().getColor(R.color.orange));
//			mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));
//
//			mActiveFragment = new ActivityFragment();
//			// mActiveFragment = mFragments.get(position);
//			getSupportFragmentManager().beginTransaction().replace(R.id.homepage_contentframe, mActiveFragment).commit();
//		} else if (position == 2) {
//			// My Profile
//			initActionBarMe();
//
//			// mTabSelectorContainerView.setBackgroundResource();
//
//			mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
//			mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
//			mTxtTabFeed.setTextColor(getResources().getColor(R.color.orange));
//
//			mActiveFragment = new MyProfileFragment();
//			// mActiveFragment = mFragments.get(position);
//			getSupportFragmentManager().beginTransaction().replace(R.id.homepage_contentframe, mActiveFragment).commit();
//		}
//		if (addToBackStack) mBackStack.add(position);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.show_tvguide:
//			// showTVGuide();
//			selectNavigationTab(0, true);
//			break;
//		case R.id.show_activity:
//			// showActivity();
//			selectNavigationTab(1, true);
//			break;
//		case R.id.show_me:
//			// showMe();
//			selectNavigationTab(2, true);
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	protected void loadPage() {
//		// The the initial state to be loading
//		updateUI(REQUEST_STATUS.LOADING);
//
//		// mTvDatePage.getPage(new SSPageCallback() {
//		SSTvDatePage.getInstance().getPage(new SSPageCallback() {
//			@Override
//			public void onGetPageResult(SSPageGetResult aPageGetResult) {
//
//				mTvDates = SSTvDatePage.getInstance().getTvDates();
//
//				SSChannelPage.getInstance().getPage(Consts.MILLICOM_SECONDSCREEN_CHANNELS_DEFAULT_PAGE_URL, new SSPageCallback() {
//					@Override
//					public void onGetPageResult(SSPageGetResult aPageGetResult) {
//
//						mChannels = SSChannelPage.getInstance().getChannels();	
//						
//						if (!pageHoldsData()) {
//							// Request failed
//							Log.d(TAG, "FAILED");
//							updateUI(REQUEST_STATUS.FAILED);
//						}
//					}
//				});
//			}
//		});
//	}
//
//	@Override
//	protected boolean pageHoldsData() {
//		boolean result = false;
//		if (mTvDates != null) {
//			if (mTvDates.isEmpty()) {
//				Log.d(TAG, "EMPTY RESPONSE");
//				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
//			} else {
//				Log.d(TAG, "SUCCESSFUL");
//				updateUI(REQUEST_STATUS.SUCCESSFUL);
//			}
//			result = true;
//		}
//		return result;
//	}
//
//	@Override
//	protected void updateUI(REQUEST_STATUS status) {
//		if (super.requestIsSuccesfull(status)) {
//			activateFragments();
//		}
//	}
//
//	@Override
//	public void onBackPressed() {
//		if (mBackStack == null || mBackStack.isEmpty()) {
//			// exit
//			super.onBackPressed();
//		} else {
//			mBackStack.remove(mBackStack.size() - 1);
//			if (mBackStack.isEmpty()) {
//				super.onBackPressed();
//			} else {
//				int index = mBackStack.get(mBackStack.size() - 1);
//				selectNavigationTab(index, false);
//			}
//		}
//	}
//
//	private void activateFragments() {
//		mFragments = new ArrayList<Fragment>();
//
//		// create fragments for the navigation tabs
//		mFragments.add(new TVGuideFragmentContainer());
//		mFragments.add(new ActivityFragment());
//		mFragments.add(new MyProfileFragment());
//
//		// Select the first fragment
//		selectNavigationTab(mStartingPosition, true);
//	}
//
//	private void reloadStartPage() {
//		// remove old fragments
//		for (Fragment fragment : mFragments) {
//			getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//		}
//
//		loadPage();
//
//		// no forced loading again
//		super.mForceReload = false;
//		mBackStack.clear();
//	}
//
//	private boolean loadHomePageFromSavedInstanceState(Bundle savedInstanceState) {
//		boolean result = false;
//		if (savedInstanceState != null) {
//			Log.d(TAG, "load from saved instance state");
//
//			mChannels = savedInstanceState.getParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST);
//			mTvDates = savedInstanceState.getParcelableArrayList(Consts.PARCELABLE_TAGS_LIST);
//
//			if (mChannels != null && mTvDates != null) {
//				Log.d(TAG, "Read the channels and dates from the savedInstanceState");
//				mBackStack = savedInstanceState.getIntegerArrayList(Consts.DAZOO_BACK_STACK);
//
//				// refresh the HomePage
//				result = true;
//				updateUI(REQUEST_STATUS.SUCCESSFUL);
//			}
//		}
//		return result;
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		if (mBroadcastSelection != -1) {
//			selectNavigationTab(mBroadcastSelection, true);
//		}
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle savedInstanceState) {
//		savedInstanceState.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, mChannels);
//		savedInstanceState.putParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST, mTvDates);
//		savedInstanceState.putIntegerArrayList(Consts.DAZOO_BACK_STACK, mBackStack);
//
//		super.onSaveInstanceState(savedInstanceState);
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	}
//
//	@Override
//	public boolean onNavigationItemSelected(int position, long id) {
//		mDayAdapter.setSelectedIndex(position);
//		TvDate tvDateItem = mTvDates.get(position);
//		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE, tvDateItem.getDate()));
//		return true;
//	}
//
//}
