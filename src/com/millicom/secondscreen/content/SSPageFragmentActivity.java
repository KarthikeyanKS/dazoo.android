package com.millicom.secondscreen.content;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.SecondScreenApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.R;

public abstract class SSPageFragmentActivity extends ActionBarActivity {

	private static final String TAG = "SSPageFragmentActivity";
	
	private View	mRequestEmptyLayout;
	private View	mRequestFailedLayout;
	private View	mRequestLoadingLayout;
	private Button	mRequestFailedButton;

	protected boolean	mForceReload	= false;

	protected abstract void loadPage();

	protected abstract boolean pageHoldsData();

	protected abstract void updateUI(REQUEST_STATUS status);

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// add to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				mForceReload = true;
			}
		}, new IntentFilter(Consts.BROADCAST_HOMEPAGE));
	};

	// Init the callback layouts for this page
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
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
	}

	public void initCallbackLayouts() {

		mRequestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);
		mRequestFailedButton = (Button) findViewById(R.id.request_failed_reload_button);
		mRequestFailedButton.setOnClickListener(mOnRequestFailedClickListener);

		mRequestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		mRequestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);
	}

	OnClickListener	mOnRequestFailedClickListener	= new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.request_failed_reload_button:
				loadPage();
				break;
			}
		}
	};

}
