package com.mitv.content;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.millicom.mitv.activities.BaseActivity;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;

public abstract class SSPageFragmentActivity extends BaseActivity {

	private static final String	TAG	= "SSPageFragmentActivity";

	private View				mRequestEmptyLayout;
	private View				mRequestFailedLayout;
	private View				mRequestLoadingLayout;
	private Button				mRequestFailedButton;
	private Button				mRequestBadButton;
	private View				mRequestBadLayout;
	private Activity			mActivity;

	protected abstract void loadPage();

	protected abstract boolean pageHoldsData();

	protected abstract void updateUI(REQUEST_STATUS status);

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// add to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
	};

	// Init the callback layouts for this page
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		mActivity = this;
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
				if (!NetworkUtils.isConnectedAndHostIsReachable(mActivity)) {
					updateUI(REQUEST_STATUS.FAILED);
				} else {
					loadPage();
				}
				break;
			case R.id.bad_request_reload_button:
				if (!NetworkUtils.isConnectedAndHostIsReachable(mActivity)) {
					updateUI(REQUEST_STATUS.FAILED);
				} else {
					loadPage();
				}
				break;
			}
		}
	};

}
