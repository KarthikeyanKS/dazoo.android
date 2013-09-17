package com.millicom.secondscreen.content.fragments;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

public abstract class SSPageFragment extends Fragment {
	
	public static final String TAG = "SSPageFragment";

	// Request [Failed]
	public RelativeLayout				mRequestFailedLayout;
	//public Button						mRequestFailedButton;

	// Request [Loading]
	public RelativeLayout				mRequestLoadingLayout;

	// Request [Empty Response]
	public RelativeLayout				mRequestEmptyResponseLayout;
	public Button						mRequestEmptyResponseButton;

	private boolean						mForceReload	= false;
	
	protected abstract void loadPage();
	protected abstract boolean pageHoldsData();
	protected abstract void updateUI(REQUEST_STATUS status);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				Log.d(TAG, "FORCE RELOAD RECEIVED");
				
				mForceReload = true;
			}
		}, new IntentFilter(Consts.BROADCAST_FORCE_RELOAD));
	}

	// Set the initial state of all request layouts to GONE
	public void hideRequestStatusLayouts() {
		if (mRequestFailedLayout != null) mRequestFailedLayout.setVisibility(View.GONE);
		if (mRequestLoadingLayout != null) mRequestLoadingLayout.setVisibility(View.GONE);
		if (mRequestEmptyResponseLayout != null) mRequestEmptyResponseLayout.setVisibility(View.GONE);
	}

	public OnClickListener	mOnEmptyResponseClickListener	= new OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	// Make sure activity is still alive and well
	public boolean activityIsAlive() {
		return (getActivity() != null && !getActivity().isFinishing());
	}

	protected boolean requestIsSuccesfull(REQUEST_STATUS status) {

		// Make sure user didn't leave activity
		if (activityIsAlive()) {

			// Set initial state of layouts
			hideRequestStatusLayouts();

			Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);

			switch (status) {
			case EMPTY_RESPONSE:

				if (mRequestEmptyResponseLayout != null) {
					mRequestEmptyResponseLayout.setVisibility(View.VISIBLE);
					mRequestEmptyResponseLayout.startAnimation(anim);
				}
				break;

			case FAILED:

				if (mRequestFailedLayout != null) {
					mRequestFailedLayout.setVisibility(View.VISIBLE);
					mRequestFailedLayout.startAnimation(anim);
				}
				break;

			case LOADING:

				if (mRequestLoadingLayout != null) mRequestLoadingLayout.setVisibility(View.VISIBLE);
				break;

			case SUCCESFUL:
				return true;
			}
		}
		return false;
	}

	public void initRequestCallbackLayouts(View v) {
		mRequestFailedLayout = (RelativeLayout) v.findViewById(R.id.request_failed_main_layout);
		//mRequestFailedButton = (Button) v.findViewById(R.id.request_failed_reload_button);
		//mRequestFailedButton.setOnClickListener(mClickListener);

		mRequestEmptyResponseLayout = (RelativeLayout) v.findViewById(R.id.request_empty_main_layout);
		mRequestLoadingLayout = (RelativeLayout) v.findViewById(R.id.request_loading_main_layout);
	}
	
	public boolean shouldForceReload() {
		return mForceReload;
	}

	private OnClickListener	mClickListener	= new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//case R.id.request_failed_reload_button:
			//	// Reloads the page again
			//	loadPage();
			//	break;
			}
		};
	};
}
