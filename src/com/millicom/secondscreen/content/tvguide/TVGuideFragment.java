package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.fragments.SSPageFragment;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.TvDate;

public class TVGuideFragment extends SSPageFragment {

	private static final String	TAG	= "TVGuideFragment";
	private View				mRootView;
	private RelativeLayout		mTvGuideContainerLayout;
	private ListView			mTvGuideListView;
	private TVGuideListAdapter	mTvGuideListAdapter;
	private SSStartPage			mPage;
	private ArrayList<Guide>	mGuide;

	private Activity			mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mActivity = getActivity();
		mPage = SSStartPage.getInstance();

		// Register for when a child selects a new sorting
		// LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSortingUpdatedReceiver, new IntentFilter(mSectionId));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide, container, false);
		mTvGuideContainerLayout = (RelativeLayout) mRootView.findViewById(R.id.tvguide_list_container);
		mTvGuideListView = (ListView) mRootView.findViewById(R.id.listview);

		super.initRequestCallbackLayouts(mRootView);	
		
		// Forced reload will be loaded in onResume
		if (!shouldForceReload()) loadPage();
		return mRootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Stop listening to broadcast events
		// LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mSortingUpdatedReceiver);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Cancel any get page request
		mPage.cancelGetPage();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Should Force Reload : " + shouldForceReload());
		if (shouldForceReload()) {

			// Create a new page
			// mPage = new VPSectionPage((VPSection) getArguments().getParcelable(VPConsts.INTENT_EXTRA_SECTION));

			// Reload the new page
			loadPage();
		}
	}

	BroadcastReceiver	mSortingUpdatedReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// Reload the page with new sorting
			// TODO
		}
	};

	private void getPage() {
		mPage.getPage(new SSPageCallback() {

			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {

				if (!pageHoldsData()) {
					// // Request failed
					updateUI(REQUEST_STATUS.FAILED);
					Log.d(TAG, "loadStartPage - failed");
				}
			}
		});
	}

	@Override
	protected void loadPage() {
		updateUI(Consts.REQUEST_STATUS.LOADING);

		if (!pageHoldsData()) {
			getPage();
		}
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		mGuide = mPage.getGuide();
		Log.d(TAG, "Guide : " + mGuide);
		if (mGuide != null) {
			if (mGuide.isEmpty()) {
				updateUI(REQUEST_STATUS.FAILED);
				Log.d(TAG, "pageHoldsData: Failed");
			} else {
				updateUI(REQUEST_STATUS.SUCCESFUL);
				Log.d(TAG, "pageHoldsData: Successful");
			}
			result = true;
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		Log.d(TAG, "update UI :" + status);
		if (super.requestIsSuccesfull(status)) {
			mTvGuideListAdapter = new TVGuideListAdapter(mActivity, mGuide);
			mTvGuideListView.setAdapter(mTvGuideListAdapter);
		}
	}
}
