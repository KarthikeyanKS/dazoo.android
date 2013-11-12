package com.millicom.secondscreen.content.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.ActivityFeedListAdapter;
import com.millicom.secondscreen.authentication.SignInActivity;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.feed.FeedService;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;
import com.millicom.secondscreen.http.NetworkUtils;

public class ActivityActivity extends SSActivity implements OnClickListener {

	private static final String	TAG			= "ActivityActivity";
	private TextView			mTxtTabTvGuide, mTxtTabProfile, mTxtTabActivity, mSignInTv;
	private ActionBar			mActionBar;
	private ArrayList<FeedItem>	activityFeed;
	private String				token;
	private Boolean				mIsLoggenIn	= false;
	private ListView mListView;
	private ActivityFeedListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		if (token != null && TextUtils.isEmpty(token) != true) {
			mIsLoggenIn = true;
			setContentView(R.layout.layout_activity_activity);
			initStandardViews();
			initFeedViews();
			loadPage();
		} else {
			Log.d(TAG,"Not Logged In Layout!");
			setContentView(R.layout.layout_activity_not_logged_in_activity);
			initStandardViews();
			initInactiveViews();
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private void initStandardViews() {
		mTxtTabTvGuide = (TextView) findViewById(R.id.go_to_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabActivity = (TextView) findViewById(R.id.go_to_activity);
		mTxtTabActivity.setOnClickListener(this);
		mTxtTabProfile = (TextView) findViewById(R.id.go_to_profile);
		mTxtTabProfile.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabActivity.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabProfile.setTextColor(getResources().getColor(R.color.gray));

		mActionBar = getSupportActionBar();

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.activity_title));
		
		
		// DO THE CALLBACK AND LOADING FUNCTIONALITY AND BEHAVIOR
		super.initCallbackLayouts();
	}

	private void initInactiveViews(){
		mSignInTv = (TextView) findViewById(R.id.activity_not_logged_in_btn);
		mSignInTv.setOnClickListener(this);
	}
	
	private void initFeedViews(){
		mListView = (ListView) findViewById(R.id.activity_listview);
	}
	
	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		// check if the network connection exists
		if (!NetworkUtils.checkConnection(this)) {
			updateUI(REQUEST_STATUS.FAILED);
		}

		activityFeed = FeedService.getActivityFeed(token);
		if (activityFeed != null) {
			if (activityFeed.isEmpty() != true) {
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		} else {
			updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			Log.d(TAG,"ACTIVITY FEED SIZE:" + String.valueOf(activityFeed.size()));
			mAdapter = new ActivityFeedListAdapter(this, activityFeed);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent toSearchPage = new Intent(ActivityActivity.this, SearchPageActivity.class);
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
		case R.id.go_to_tvguide:
			// tab to home page
			Intent intentHome = new Intent(ActivityActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.go_to_activity:
			// we are here: do nothing
			break;
		case R.id.go_to_profile:
			// tab to activity page
			Intent intentMe = new Intent(ActivityActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		case R.id.activity_not_logged_in_btn:
			Intent intentSignIn = new Intent(ActivityActivity.this, SignInActivity.class);
			startActivity(intentSignIn);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		}
	}
}
