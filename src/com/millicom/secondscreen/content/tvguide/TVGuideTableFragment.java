package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
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
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.storage.DazooStore;

public class TVGuideTableFragment extends SSPageFragment {

	private static final String	TAG	= "TVGuideTableFragment";
	private String				mTagStr, mTvDateStr, token;
	private View				mRootView;
	private Activity			mActivity;
	private ListView			mTVGuideListView;
	private LinearLayout		mClockIndexView;
	private ArrayList<Guide>	mGuides;
	private ArrayList<Channel> mChannels;
	private TvDate mTvDate;
	private Tag mTag;
 	private TVGuideListAdapter	mTVGuideListAdapter;
 	private DazooStore dazooStore;
 	private boolean mIsLoggedIn = false;

	public static TVGuideTableFragment newInstance(Tag tag, String date) {

		TVGuideTableFragment fragment = new TVGuideTableFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Consts.FRAGMENT_EXTRA_TVDATE, date);
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG, tag.getName());
		fragment.setArguments(bundle);
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
		mRootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
		mTVGuideListView = (ListView) mRootView.findViewById(R.id.tvguide_table_listview);
		mClockIndexView = (LinearLayout) mRootView.findViewById(R.id.tvguide_table_side_clock_index);

		super.initRequestCallbackLayouts(mRootView);

		// reset the activity whenever the view is recreated
		mActivity = getActivity();
		loadPage();
		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		
		mTvDate = dazooStore.getDate(mTvDateStr);
		mTag = dazooStore.getTag(mTagStr);
		
		// GET THE DATA FROM CORE LOGIC SINGLETON
		if(mIsLoggedIn)
			mGuides = dazooStore.getMyGuideTable(mTvDate);
		else 
			mGuides = dazooStore.getGuideTable(mTvDate);
			
		if (!pageHoldsData()) {
			// Request failed
			updateUI(REQUEST_STATUS.FAILED);
		}
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (mGuides != null) {
			if (mGuides.isEmpty()) {
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				Log.d(TAG, "SIZE: " + mGuides.size());
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
			result = true;
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {

			mTVGuideListAdapter = new TVGuideListAdapter(mActivity, mGuides, mTvDateStr);
			mTVGuideListView.setAdapter(mTVGuideListAdapter);

			// ArrayList<ChannelHour> channelHoursForFirst = putBroadcastsInHours(mGuide.get(0).getBroadcasts());
			// Log.d(TAG,"channelHoursForFirst:" + channelHoursForFirst);
			// channelBroadcastsMap = new HashMap<Channel, ArrayList<ChannelHour>>();
			// channelBroadcastsMap = mapChannelsWithBroadcasts(mGuide);
		}
	}

}
