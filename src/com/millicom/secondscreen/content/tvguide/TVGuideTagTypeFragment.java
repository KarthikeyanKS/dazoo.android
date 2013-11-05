package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.TVGuideTagListAdapter;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.storage.DazooStore;

public class TVGuideTagTypeFragment extends SSPageFragment{
	
	private static final String TAG = "TVGuideTagTypeFragment";
	private Activity mActivity;
	private String mTagStr, token;
	private View mRootView;
	private ListView mListView;
	private ArrayList<Broadcast> mTaggedBroadcasts;
	private TVGuideTagListAdapter	mAdapter;
	private DazooStore dazooStore;
	private Tag mTag;
	private TvDate mTvDate;
	private int mTvDatePosition;
	private boolean mIsLoggedIn = false, mIsReady = false;
	
	public static TVGuideTagTypeFragment newInstance(Tag tag, TvDate tvDate, int position) {

		TVGuideTagTypeFragment fragment = new TVGuideTagTypeFragment();
		Bundle bundle = new Bundle();
		
		bundle.putParcelable(Consts.FRAGMENT_EXTRA_TVDATE, tvDate);
		bundle.putInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION, position);
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG, tag.getName());
		
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dazooStore = DazooStore.getInstance();
		
		// broadcast receiver for date selection
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiverContent, new IntentFilter(Consts.INTENT_EXTRA_TAG_GUIDE_AVAILABLE));
		
		token = ((SecondScreenApplication) getActivity().getApplicationContext()).getAccessToken();
		if (token !=null && TextUtils.isEmpty(token) !=true){
			mIsLoggedIn = true;
		}
		
		Bundle bundle = getArguments();
		mTagStr = bundle.getString(Consts.FRAGMENT_EXTRA_TAG);
		mTvDate = bundle.getParcelable(Consts.FRAGMENT_EXTRA_TVDATE);
		mTvDatePosition = bundle.getInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION);
		
		mTag = dazooStore.getTag(mTagStr);
	}
	
	
	BroadcastReceiver	mBroadcastReceiverContent	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_TAG_GUIDE_AVAILABLE_VALUE, false);
			Log.d(TAG, "content for tagged TvGuide is ready: " + mIsReady);

			if (mIsReady) {
				loadPage();
			} 
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
		mListView = (ListView) mRootView.findViewById(R.id.fragment_tvguide_type_tag_listview);

		super.initRequestCallbackLayouts(mRootView);
		
		// reset the activity whenever the view is recreated
		mActivity = getActivity();
		
		updateUI(REQUEST_STATUS.LOADING);
		
		mTaggedBroadcasts = null;
		if(mIsLoggedIn)
			mTaggedBroadcasts = dazooStore.getMyTaggedBroadcasts(mTvDate, mTag);
		else 
			mTaggedBroadcasts = dazooStore.getTaggedBroadcasts(mTvDate, mTag);
		
		if(mTaggedBroadcasts !=null){
			pageHoldsData();
		} else {
			updateUI(REQUEST_STATUS.LOADING);
		}	
		return mRootView;
	}

	@Override
	protected void loadPage() {

		mTaggedBroadcasts = null;
		if(mIsLoggedIn)
			mTaggedBroadcasts = dazooStore.getMyTaggedBroadcasts(mTvDate, mTag);
		else 
			mTaggedBroadcasts = dazooStore.getTaggedBroadcasts(mTvDate, mTag);
		
		pageHoldsData();
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (mTaggedBroadcasts != null) {
			if (mTaggedBroadcasts.isEmpty()) {
				DazooCore.getGuide(mTvDatePosition, false);
			} else {
				updateUI(REQUEST_STATUS.SUCCESSFUL);
				result = true;
			}
		} 
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			mAdapter = new TVGuideTagListAdapter(mActivity, mTaggedBroadcasts);
			mListView.setAdapter(mAdapter);
		}
	}
}
