package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.TVGuideListAdapter;
import com.millicom.secondscreen.adapters.TVGuideTagListAdapter;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.storage.DazooStore;

public class TVGuideTableFragment extends SSPageFragment {

	private static final String		TAG			= "TVGuideTableFragment";
	private String					mTagStr, token;
	private View					mRootView;
	private Activity				mActivity;
	private ListView				mTVGuideListView;
	private LinearLayout			mClockIndexView;
	private ArrayList<Guide>		mGuides;
	private ArrayList<Channel>		mChannels;
	private TvDate					mTvDate;
	private Tag						mTag;
	private int						mTvDatePosition;
	private TVGuideListAdapter		mTVGuideListAdapter;
	private DazooStore				dazooStore;
	private boolean					mIsLoggedIn	= false;
	private ArrayList<Broadcast>	mTaggedBroadcasts;
	private boolean					mCreateBackground;
	private TVGuideTagListAdapter	mTVTagListAdapter;

	public static TVGuideTableFragment newInstance(Tag tag, TvDate date, int position) {

		TVGuideTableFragment fragment = new TVGuideTableFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Consts.FRAGMENT_EXTRA_TVDATE, date);
		bundle.putInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION, position);
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG, tag.getName());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dazooStore = DazooStore.getInstance();

		token = ((SecondScreenApplication) getActivity().getApplicationContext()).getAccessToken();
		if (token != null && TextUtils.isEmpty(token) != true) {
			mIsLoggedIn = true;
		}

		Bundle bundle = getArguments();
		mTagStr = bundle.getString(Consts.FRAGMENT_EXTRA_TAG);
		mTvDate = bundle.getParcelable(Consts.FRAGMENT_EXTRA_TVDATE);
		mTvDatePosition = bundle.getInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION);
		mTag = dazooStore.getTag(mTagStr);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {

			mRootView = inflater.inflate(R.layout.fragment_tvguide_table, null);

			mTVGuideListView = (ListView) mRootView.findViewById(R.id.tvguide_table_listview);
			mClockIndexView = (LinearLayout) mRootView.findViewById(R.id.tvguide_table_side_clock_index);
		} else {

			mRootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
			mTVGuideListView = (ListView) mRootView.findViewById(R.id.fragment_tvguide_type_tag_listview);
		}
		super.initRequestCallbackLayouts(mRootView);

		// reset the activity whenever the view is recreated
		mActivity = getActivity();
		mGuides = null;
		loadPage();
		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		// GET THE DATA FROM CORE LOGIC SINGLETON
		// if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
		mGuides = null;
		mTaggedBroadcasts = null;
		if (mIsLoggedIn) {
			mGuides = dazooStore.getMyGuideTable(mTvDate.getDate());
			Log.d(TAG, "My date: " + mTvDate.getDate());
			Log.d(TAG, "MY mGuides size: " + mGuides.size());
			
		} else {
			mGuides = dazooStore.getGuideTable(mTvDate.getDate());
			Log.d(TAG, "date: " + mTvDate.getDate());
			Log.d(TAG, "mGuides size: " + mGuides.size());
		}
		// }

		pageHoldsData();
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
			if (mGuides != null) {
				if (mGuides.isEmpty()) {
					Log.d(TAG,"GET GUIDE:");
					DazooCore.getGuide(mTvDatePosition, false);
				} else {
					updateUI(REQUEST_STATUS.SUCCESSFUL);
					result = true;
				}
			}
		} else {
			mTaggedBroadcasts = null;
			if (mIsLoggedIn) {
				mTaggedBroadcasts = DazooStore.getInstance().getMyTaggedBroadcasts(mTvDate, mTag);
				Log.d(TAG, "I GOT MY!!!!!!!!");

			} else {
				mTaggedBroadcasts = DazooStore.getInstance().getTaggedBroadcasts(mTvDate, mTag);
				Log.d(TAG, "I GOT DEFAULT!!!!!!!!!");
			}

			if (mTaggedBroadcasts != null) {
				Log.d(TAG, "size: " + mTaggedBroadcasts.size());
				if (mTaggedBroadcasts.isEmpty() != true) {
					Log.d(TAG,"empty");
					updateUI(REQUEST_STATUS.SUCCESSFUL);
					result = true;
				} else {
					updateUI(REQUEST_STATUS.LOADING);
				}
			}

		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
				mTVGuideListAdapter = new TVGuideListAdapter(mActivity, mGuides, mTvDate);
				mTVGuideListView.setAdapter(mTVGuideListAdapter);
			} else {
				int index = Broadcast.getClosestBroadcastIndex(mTaggedBroadcasts);
				Log.d(TAG, "index: " + index);
				mTVTagListAdapter = new TVGuideTagListAdapter(mActivity, mTaggedBroadcasts, index, mTvDate);
				mTVGuideListView.setAdapter(mTVTagListAdapter);
			}

			// ArrayList<ChannelHour> channelHoursForFirst = putBroadcastsInHours(mGuide.get(0).getBroadcasts());
			// Log.d(TAG,"channelHoursForFirst:" + channelHoursForFirst);
			// channelBroadcastsMap = new HashMap<Channel, ArrayList<ChannelHour>>();
			// channelBroadcastsMap = mapChannelsWithBroadcasts(mGuide);
		}
	}

}
