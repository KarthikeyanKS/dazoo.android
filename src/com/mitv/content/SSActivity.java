package com.mitv.content;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.http.NetworkUtils;
import com.mitv.manager.GATrackingManager;
import com.mitv.search.SearchPageActivity;

public abstract class SSActivity extends ActionBarActivity {

	private static final String	TAG	= "SSActivity";
	
	private String				mViewName;
	private Tracker 			mTracker;
	private View				mRequestEmptyLayout;
	private View				mRequestFailedLayout;
	private View				mRequestLoadingLayout;
	private Button				mRequestFailedButton;
	private Button				mRequestBadButton;
	private View				mRequestBadLayout;
	private ActionBar			mActionBar;
	private Activity			mActivity;

	protected abstract void updateUI(REQUEST_STATUS status);

	protected abstract void loadPage();

	@Override
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// add to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		/* Google Analytics tracking */	
		this.mViewName = this.getClass().getName();
		EasyTracker.getInstance(this).activityStart(this);
		GATrackingManager.sendView(mViewName);
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);
		
		MenuItem searchIcon = menu.findItem(R.id.action_start_search);
		View seachIconView = MenuItemCompat.getActionView(searchIcon);
		seachIconView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent toSearchPage = new Intent(SSActivity.this, SearchPageActivity.class);
				startActivity(toSearchPage);
				
			}
		});
		
		MenuItem searchFieldItem = menu.findItem(R.id.searchfield);
		searchFieldItem.setVisible(false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items

		// hide search for beta release
		switch (item.getItemId()) {
		case R.id.action_start_search:		//Might be dead with actionView instead of icon...
			Intent toSearchPage = new Intent(SSActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);

			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		/* Google Analytics tracking */
		GATrackingManager.stopTrackingView(this.mViewName);
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	// Init the callback layouts for this page
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		mActivity = this;
		mActionBar = getSupportActionBar();
		
		mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue1)));
		
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		initCallbackLayouts();
	}

	// Check if activity is still running
	public boolean activityIsAlive() {
		return ((this != null && !this.isFinishing()));
	}

	protected boolean requestIsSuccesfull(REQUEST_STATUS status) {

		if (activityIsAlive()) {

			// Set initial state of layouts
			hideRequestStatusLayouts();

			switch (status) {
			case BAD_REQUEST:
				mRequestBadLayout.setVisibility(View.VISIBLE);
				break;

			case EMPTY_RESPONSE:
				mRequestEmptyLayout.setVisibility(View.VISIBLE);
				break;
			case FAILED:
				mRequestFailedLayout.setVisibility(View.VISIBLE);
				break;

			case LOADING:
				mRequestLoadingLayout.setVisibility(View.VISIBLE);
				break;

			case SUCCESSFUL:
				return true;
			}
		}
		return false;
	}

	public void hideRequestStatusLayouts() {
		if (mRequestFailedLayout != null) mRequestFailedLayout.setVisibility(View.GONE);
		if (mRequestLoadingLayout != null) mRequestLoadingLayout.setVisibility(View.GONE);
		if (mRequestBadLayout != null) mRequestBadLayout.setVisibility(View.GONE);
	}

	public void initCallbackLayouts() {

		mRequestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);
		mRequestFailedButton = (Button) findViewById(R.id.request_failed_reload_button);
		if(mRequestFailedButton != null) {
			mRequestFailedButton.setOnClickListener(mOnRequestFailedClickListener);
		}
		
		mRequestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		mRequestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		mRequestBadLayout = (RelativeLayout) findViewById(R.id.bad_request_main_layout);
		mRequestBadButton = (Button) findViewById(R.id.bad_request_reload_button);
		if(mRequestBadButton != null) {
			mRequestBadButton.setOnClickListener(mOnRequestFailedClickListener);
		}
	}

	OnClickListener	mOnRequestFailedClickListener	= new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.request_failed_reload_button:
				if (!NetworkUtils.checkConnection(mActivity)) {
					updateUI(REQUEST_STATUS.FAILED);
				} else {
					loadPage();
				}
				break;
			case R.id.bad_request_reload_button:
				if (!NetworkUtils.checkConnection(mActivity)) {
					updateUI(REQUEST_STATUS.FAILED);
				} else {
					loadPage();
				}
				break;
			}
		}
	};
}
