package com.millicom.secondscreen.content.homepage;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.ActionBarDropDownCategoryListAdapter;
import com.millicom.secondscreen.adapters.ActionBarDropDownDateListAdapter;
import com.millicom.secondscreen.content.SSChannelPage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSProgramTypePage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.activity.ActivityFragment;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.MyProfileFragment;
import com.millicom.secondscreen.content.search.SearchPageActivity;
import com.millicom.secondscreen.content.tvguide.TVGuideFragment;

public class HomePageActivity extends SSPageFragmentActivity implements View.OnClickListener {

	private static final String		TAG				= "HomePageActivity";
	private TextView				mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed, mTxtTabMore;
	private View					mTabSelectorContainerView;
	private Fragment				mActiveFragment;
	private ArrayList<TvDate>		mTvDates		= new ArrayList<TvDate>();
	private ArrayList<ProgramType>	mProgramTypes	= new ArrayList<ProgramType>();
	private ArrayList<Channel>		mChannels		= new ArrayList<Channel>();

	private ActionBar				mActionBar;
	private boolean					isDateData		= false, isProgramTypesData = false;
	private SSTvDatePage			mTvDatePage;
	private SSProgramTypePage		mProgramTypePage;
	private SSChannelPage			mChannelPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_homepage_activity);

		initViews();

		mTvDatePage = SSTvDatePage.getInstance();
		mProgramTypePage = SSProgramTypePage.getInstance();
		mChannelPage = SSChannelPage.getInstance();
		loadPage();
	}

	private void initViews() {
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);
		mTabSelectorContainerView.setVisibility(View.GONE);

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mActionBar = getSupportActionBar();

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.hide();
		//super.initCallbackLayouts();
	}

	private void initActionBarTvGuide(boolean isDateData, boolean isProgramTypesData) {
		mActionBar.show();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_homepage);

		if (isProgramTypesData == true) {
			final Spinner categorySpinner = (Spinner) findViewById(R.id.actionbar_homepage_category_spinner);
			categorySpinner.setVisibility(View.VISIBLE);

			ActionBarDropDownCategoryListAdapter programTypeAdapter = new ActionBarDropDownCategoryListAdapter(this, mProgramTypes);
			categorySpinner.setAdapter(programTypeAdapter);

			categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
					ProgramType programTypeItem = (ProgramType) categorySpinner.getItemAtPosition(position);
					LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
							new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE, programTypeItem.getId()).putExtra(
									Consts.INTENT_EXTRA_TVGUIDE_SORTING_TYPE, Consts.VALUE_TYPE_PROGRAMTYPE));
				}

				@Override
				public void onNothingSelected(AdapterView<?> anAdapterView) {
					// do nothing
				}
			});
		}

		if (isDateData == true) {
			final Spinner daySpinner = (Spinner) findViewById(R.id.actionbar_homepage_day_spinner);
			daySpinner.setVisibility(View.VISIBLE);

			ActionBarDropDownDateListAdapter dayAdapter = new ActionBarDropDownDateListAdapter(this, mTvDates);
			daySpinner.setAdapter(dayAdapter);

			daySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
					TvDate tvDateItem = (TvDate) daySpinner.getItemAtPosition(position);
					LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
							new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE, tvDateItem.getDate()).putExtra(
									Consts.INTENT_EXTRA_TVGUIDE_SORTING_TYPE, Consts.VALUE_TYPE_TVDATE));
				}

				@Override
				public void onNothingSelected(AdapterView<?> anAdapterView) {
					// do nothing
				}
			});
		}
		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_homepage_search_icon);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// move to the search page
				Intent toSearchPage = new Intent(HomePageActivity.this, SearchPageActivity.class);
				startActivity(toSearchPage);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});
	}

	private void initActionBarActivity() {
		mActionBar.show();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_activitypage);

		final TextView title = (TextView) findViewById(R.id.actionbar_activitypage_title_tv);
		title.setText(getResources().getString(R.string.activity_page_title));

		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_activitypage_search_icon);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// move to the search page
				Intent toSearchPage = new Intent(HomePageActivity.this, SearchPageActivity.class);
				startActivity(toSearchPage);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});
	}

	private void initActionBarMe() {
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_mepage);

		final TextView title = (TextView) findViewById(R.id.actionbar_mepage_title_tv);
		title.setText(getResources().getString(R.string.me_page_title));

		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_mepage_search_icon);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// move to the search page
				Intent toSearchPage = new Intent(HomePageActivity.this, SearchPageActivity.class);
				startActivity(toSearchPage);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});
	}

	void showTVGuide() {
		initActionBarTvGuide(isDateData, isProgramTypesData);

		// mTabSelectorContainerView.setBackgroundResource();

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.black));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));

		mActiveFragment = new TVGuideFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, mChannels);
		bundle.putParcelableArrayList(Consts.PARCELABLE_TV_DATES_LIST, mTvDates);
		bundle.putParcelableArrayList(Consts.PARCELABLE_PROGRAM_TYPES_LIST, mProgramTypes);
		mActiveFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.homepage_contentframe, mActiveFragment).commit();
	}

	void showActivity() {
		initActionBarActivity();

		// mTabSelectorContainerView.setBackgroundResource();

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.black));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));

		mActiveFragment = new ActivityFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.homepage_contentframe, mActiveFragment).commit();

	}

	void showMe() {
		initActionBarMe();

		// mTabSelectorContainerView.setBackgroundResource();

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.black));

		mActiveFragment = new MyProfileFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.homepage_contentframe, mActiveFragment).commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_tvguide:
			showTVGuide();
			break;
		case R.id.show_activity:
			showActivity();
			break;
		case R.id.show_me:
			showMe();
			break;
		default:
			break;
		}
	}

	@Override
	protected void loadPage() {
		// The the initial state to be loading
		updateUI(REQUEST_STATUS.LOADING);

		mTvDatePage.getPage(new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {

				mTvDates = mTvDatePage.getTvDates();
				mProgramTypePage.getPage(new SSPageCallback() {
					@Override
					public void onGetPageResult(SSPageGetResult aPageGetResult) {
						mProgramTypes = mProgramTypePage.getProgramTypes();

						mChannelPage.getPage(new SSPageCallback() {
							@Override
							public void onGetPageResult(SSPageGetResult aPageGetResult) {

								mChannels = mChannelPage.getChannels();
								if (!pageHoldsData()) {
									// Request failed
									updateUI(REQUEST_STATUS.FAILED);
								}
							}
						});
					}
				});
			}
		});
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (mTvDates != null || mProgramTypes != null) {
			if (mTvDates.isEmpty() || mProgramTypes.isEmpty()) {
				updateUI(REQUEST_STATUS.FAILED);
			} else {
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
			result = true;
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (mTvDates != null) {
			isDateData = true;
		} else {
			Log.d(TAG, "No dates are available");
		}

		if(mProgramTypes == null){
			Log.d(TAG,"No separate categories are available");
		}
		
		ProgramType programTypeAll = new ProgramType();
		programTypeAll.setAlias(getResources().getString(R.string.all_categories_alias));
		programTypeAll.setId(getResources().getString(R.string.all_categories_id));
		programTypeAll.setName(getResources().getString(R.string.all_categories_name));

		mProgramTypes.add(0, programTypeAll);
		isProgramTypesData = true;

		if (super.requestIsSuccesfull(status)) {
			mTabSelectorContainerView.setVisibility(View.VISIBLE);
			showTVGuide();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
