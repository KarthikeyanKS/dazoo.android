package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.TVGuideListAdapter;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

public class TVGuideCategoryFragment extends SSPageFragment {

	private static final String		TAG						= "TVGuideCategoryFragment";
	private View					mRootView;
	private Activity				mActivity;

	private PullToRefreshListView	mTvGuideListView;
	private ListView				actualListView;

	private LinearLayout			mClockIndexView;

	private TVGuideListAdapter		mTvGuideListAdapter;

	private int						mChannelsNum			= 0;
	private int						numOfChannelsShownNow	= 0;
	private String					mPageUrl, mDate, mSortingType, mTagName;

	private ArrayList<Guide>		mGuide;
	private SSStartPage				mPage;
	private Tag						mTag;
	private ArrayList<Channel>		mChannels;
	private ArrayList<String>		mChannelIds;

	public static TVGuideCategoryFragment newInstance(Tag tag, String tvDate, ArrayList<Channel> channels) {

		TVGuideCategoryFragment fragment = new TVGuideCategoryFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, channels);
		bundle.putString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE, tvDate);
		bundle.putString(Consts.INTENT_EXTRA_TAG, tag.getName());
		Log.d(TAG,"TAG:" + tag.getName());
		
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);

		Bundle bundle = getArguments();
		mChannels = bundle.getParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST);
		mDate = bundle.getString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE);
		Log.d(TAG,"mDate:" + mDate);
		
		mTagName = bundle.getString(Consts.INTENT_EXTRA_TAG);
		getAvailableChannelsIds();

		if (mChannelIds != null) {
			mChannelsNum = mChannelIds.size();
		}

		// register a broadcast manager to respond to the new filtering choice
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiver, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));
		
		mPage = SSStartPage.getInstance();
		mPageUrl = getPageUrl(numOfChannelsShownNow, mChannelsNum, mDate);

	}

	BroadcastReceiver	mBroadcastReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE);
			// RELOAD PAGE HERE!!
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.layout_tvguide_fragment, null);

		// reset the activity whenever the view is recreated
		mActivity = getActivity();

		mTvGuideListView = (PullToRefreshListView) mRootView.findViewById(R.id.listview);
		// Set a listener to be invoked when the list should be refreshed.
		mTvGuideListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// String label = DateUtils.formatDateTime(mActivity.getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
				// | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				// refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetDataTask().execute();
			}
		});

		// Add an end-of-list listener
		mTvGuideListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {

			}
		});

		mTvGuideListView.setMode(Mode.PULL_FROM_END);
		mClockIndexView = (LinearLayout) mRootView.findViewById(R.id.side_clock_index);

		super.initRequestCallbackLayouts(mRootView);
	
		loadPage();

		return mRootView;
	}

	private class GetDataTask extends AsyncTask<Void, Void, ArrayList<Guide>> {

		@Override
		protected ArrayList<Guide> doInBackground(Void... params) {
			// Simulates a background job.
			try {
				if (numOfChannelsShownNow < mChannelsNum) {

					mPageUrl = getPageUrl(numOfChannelsShownNow, mChannelsNum, mDate);

					mPage.getPage(mTagName, mPageUrl, new SSPageCallback() {

						@Override
						public void onGetPageResult(SSPageGetResult aPageGetResult) {

							ArrayList<Guide> mGuideUpdate = mPage.getGuide();
							mGuide.addAll(mGuideUpdate);
						}
					});
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
			}
			return mGuide;
		}

		@Override
		protected void onPostExecute(ArrayList<Guide> result) {

			mTvGuideListAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mTvGuideListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		if (!pageHoldsData()) {
			mPage.getPage(mTagName, mPageUrl, new SSPageCallback() {

				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {

					if (!pageHoldsData()) {
						// // Request failed
						updateUI(REQUEST_STATUS.FAILED);
					}
				}
			});
		}
		else {
			Log.d(TAG,"Page holds data");
		}		
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;

		mGuide = new ArrayList<Guide>();
		mGuide = mPage.getGuide();

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
		Log.d(TAG, "update UI :" + status);

		//if (super.requestIsSuccesfull(status)) {
		if(mGuide!=null){
			mTvGuideListAdapter = new TVGuideListAdapter(mActivity, mGuide, mChannels);

			actualListView = mTvGuideListView.getRefreshableView();

			// mTvGuideListView.setAdapter(mTvGuideListAdapter);

			actualListView.setAdapter(mTvGuideListAdapter);
			actualListView.invalidate();

			// ArrayList<ChannelHour> channelHoursForFirst = putBroadcastsInHours(mGuide.get(0).getBroadcasts());
			// Log.d(TAG,"channelHoursForFirst:" + channelHoursForFirst);
			// channelBroadcastsMap = new HashMap<Channel, ArrayList<ChannelHour>>();
			// channelBroadcastsMap = mapChannelsWithBroadcasts(mGuide);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy");

		// Stop listening to broadcast events
		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiver);
	};

	@Override
	public void onPause() {
		super.onPause();

		// Cancel any get page request
		mPage.cancelGetPage();
	}

	private String getPageUrl(int startPosition, int maxSize, String date) {
		StringBuilder sB = new StringBuilder();
		sB.append(Consts.MILLICOM_SECONDSCREEN_GUIDE_PAGE_API);

		sB.append(Consts.REQUEST_QUERY_SEPARATOR);
		sB.append(date);

		sB.append(Consts.REQUEST_PARAMETER_SEPARATOR);
		for (int i = startPosition; i < maxSize && i < startPosition + Consts.MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_CHANNELS_PER_PAGE; i++) {
			if (i > 0) {
				sB.append(Consts.REQUEST_QUERY_AND);
				sB.append(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID);
				sB.append(mChannelIds.get(i));
			} else {
				sB.append(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID);
				sB.append(mChannelIds.get(i));
			}
			numOfChannelsShownNow++;
		}
		return sB.toString();
	}

	private void getAvailableChannelsIds() {
		if (mChannels != null) {
			mChannelIds = new ArrayList<String>();
			for (int i = 0; i < mChannels.size(); i++) {
				mChannelIds.add(mChannels.get(i).getChannelId());
			}
		}
	}
}
