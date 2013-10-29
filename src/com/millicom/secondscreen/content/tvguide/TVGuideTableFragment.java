package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.TVGuideListAdapter;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;

public class TVGuideTableFragment extends SSPageFragment {

	private static final String	TAG	= "TVGuideTableFragment";
	private String				mTagName;
	private View				mRootView;
	private Activity			mActivity;
	private ListView			mTVGuideListView;
	private LinearLayout		mClockIndexView;
	private ArrayList<Guide>	mGuide;
	private ArrayList<Channel> mChannels;
 	private TVGuideListAdapter	mTVGuideListAdapter;

	public static TVGuideTableFragment newInstance(Tag tag, String tvDate, ArrayList<Channel> channels) {

		TVGuideTableFragment fragment = new TVGuideTableFragment();
		Bundle bundle = new Bundle();

		// TO BE DETERMINED
		// bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, channels);
		// bundle.putString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE, tvDate);

		bundle.putString(Consts.INTENT_EXTRA_TAG, tag.getName());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mTagName = bundle.getString(Consts.INTENT_EXTRA_TAG);
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
		Log.d(TAG, "onCreateView");
		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		
		// GET THE DATA FROM CORE LOGIC SINGLETON

		if (!pageHoldsData()) {
			// Request failed
			updateUI(REQUEST_STATUS.FAILED);
		}
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (mGuide != null) {
			if (mGuide.isEmpty()) {
				Log.d(TAG, "EMPTY RESPONSE");
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				Log.d(TAG, "SIZE: " + mGuide.size());
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
			result = true;
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {

			mTVGuideListAdapter = new TVGuideListAdapter(mActivity, mGuide, mChannels);
			mTVGuideListView.setAdapter(mTVGuideListAdapter);

			// ArrayList<ChannelHour> channelHoursForFirst = putBroadcastsInHours(mGuide.get(0).getBroadcasts());
			// Log.d(TAG,"channelHoursForFirst:" + channelHoursForFirst);
			// channelBroadcastsMap = new HashMap<Channel, ArrayList<ChannelHour>>();
			// channelBroadcastsMap = mapChannelsWithBroadcasts(mGuide);
		}
	}

}
