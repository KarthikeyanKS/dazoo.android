package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
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
import com.millicom.secondscreen.storage.DazooStore;

public class TVGuideTagTypeFragment extends SSPageFragment{
	
	private static final String TAG = "TVGuideTagTypeFragment";
	private Activity mActivity;
	private String mTagStr, mTvDateStr, token;
	private View mRootView;
	private ListView mListView;
	private ArrayList<Broadcast> mTaggedBroadcasts;
	private TVGuideTagListAdapter	mAdapter;
	private DazooStore dazooStore;
	private Tag mTag;
	private TvDate mTvDate;
	private boolean mIsLoggedIn = false;
	
	public static TVGuideTagTypeFragment newInstance(Tag tag, String tvDate) {

		TVGuideTagTypeFragment fragment = new TVGuideTagTypeFragment();
		Bundle bundle = new Bundle();
		
		bundle.putString(Consts.FRAGMENT_EXTRA_TVDATE, tvDate);
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG, tag.getName());
		
		fragment.setArguments(bundle);

		Log.d(TAG, "FRAGMENT: " + tag.getName() + " ON: " + tvDate);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dazooStore = DazooStore.getInstance();
		
		token = ((SecondScreenApplication) mActivity.getApplicationContext()).getAccessToken();
		if (token !=null && TextUtils.isEmpty(token) !=true){
			mIsLoggedIn = true;
		}
		
		Bundle bundle = getArguments();
		mTagStr = bundle.getString(Consts.FRAGMENT_EXTRA_TAG);
		mTvDateStr = bundle.getString(Consts.FRAGMENT_EXTRA_TVDATE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
		mListView = (ListView) mRootView.findViewById(R.id.fragment_tvguide_type_tag_listview);

		super.initRequestCallbackLayouts(mRootView);
		
		// reset the activity whenever the view is recreated
		mActivity = getActivity();
		loadPage();
		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		
		// GET THE DATA FROM CORE LOGIC SINGLETON
		mTvDate = dazooStore.getDate(mTvDateStr);
		mTag = dazooStore.getTag(mTagStr);
		
		if(mIsLoggedIn)
			mTaggedBroadcasts = dazooStore.getMyTaggedBroadcasts(mTvDate, mTag);
		else 
			mTaggedBroadcasts = dazooStore.getTaggedBroadcasts(mTvDate, mTag);
		
		if (!pageHoldsData()) {
			// // Request failed
			updateUI(REQUEST_STATUS.FAILED);
		}
		
	}

	@Override
	protected boolean pageHoldsData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			mAdapter = new TVGuideTagListAdapter(mActivity, mTaggedBroadcasts);
			mListView.setAdapter(mAdapter);
		}
	}
}
