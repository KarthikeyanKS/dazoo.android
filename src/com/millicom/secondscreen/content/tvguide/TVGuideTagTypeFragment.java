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
	private String mTagStr, token;
	private View mRootView;
	private ListView mListView;
	private ArrayList<Broadcast> mTaggedBroadcasts;
	private TVGuideTagListAdapter	mAdapter;
	private DazooStore dazooStore;
	private Tag mTag;
	private TvDate mTvDate;
	private int mTvDatePosition;
	private boolean mIsLoggedIn = false;
	
	public static TVGuideTagTypeFragment newInstance(Tag tag, TvDate tvDate, int position) {

		TVGuideTagTypeFragment fragment = new TVGuideTagTypeFragment();
		Bundle bundle = new Bundle();
		
		bundle.putParcelable(Consts.FRAGMENT_EXTRA_TVDATE, tvDate);
		bundle.putInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION, position);
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG, tag.getName());
		
		fragment.setArguments(bundle);

		Log.d(TAG, "FRAGMENT: " + tag.getName() + " ON: " + tvDate.getDate());
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dazooStore = DazooStore.getInstance();
		
		token = ((SecondScreenApplication) getActivity().getApplicationContext()).getAccessToken();
		if (token !=null && TextUtils.isEmpty(token) !=true){
			mIsLoggedIn = true;
		}
		
		Bundle bundle = getArguments();
		mTagStr = bundle.getString(Consts.FRAGMENT_EXTRA_TAG);
		mTvDate = bundle.getParcelable(Consts.FRAGMENT_EXTRA_TVDATE);
		mTvDatePosition = bundle.getInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION);
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
		
		mTag = dazooStore.getTag(mTagStr);
		
		if(mIsLoggedIn)
			mTaggedBroadcasts = dazooStore.getMyTaggedBroadcasts(mTvDate, mTag);
		else 
			mTaggedBroadcasts = dazooStore.getTaggedBroadcasts(mTvDate, mTag);
		
		//Log.d(TAG,"=================TAGGGED BROADCASTS ===============:" + mTaggedBroadcasts.size());
		
		
		if (!pageHoldsData()) {
			// // Request failed
			updateUI(REQUEST_STATUS.FAILED);
		}
		
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (mTaggedBroadcasts != null) {
			if (mTaggedBroadcasts.isEmpty()) {
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				Log.d(TAG, "SIZE: " + mTaggedBroadcasts.size());
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
			result = true;
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
