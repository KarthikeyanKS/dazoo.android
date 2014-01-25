package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.AdListAdapter;
import com.millicom.secondscreen.adapters.TVGuideListAdapter;
import com.millicom.secondscreen.adapters.TVGuideTagListAdapter;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.customviews.SwipeClockBar;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;

public class TVGuideTableFragment extends SSPageFragment {

	private static final String		TAG	= "TVGuideTableFragment";
	private String					mTagStr, token;
	private View					mRootView;
	private Activity				mActivity;
	private ListView				mTVGuideListView;
	private ImageView				mClockIv;
	private ArrayList<Guide>		mGuides;
	private TvDate					mTvDate;
	private Tag						mTag;
	private int						mTvDatePosition;
	private SwipeClockBar			mSwipeClockBar;
	private TVGuideListAdapter		mTVGuideListAdapter;
	private DazooStore				dazooStore;
	private boolean					mIsLoggedIn	= false, mIsToday = false;
	private ArrayList<Broadcast>	mTaggedBroadcasts;
	private TVGuideTagListAdapter	mTVTagListAdapter;
	private int						mHour;
	private TextView				mCurrentHourTv;
	public HashMap<String, AdListAdapter> adapterMap;
	
	public static TVGuideTableFragment newInstance(Tag tag, TvDate date, int position, HashMap<String, AdListAdapter> adapterMap) {

		TVGuideTableFragment fragment = new TVGuideTableFragment();
		fragment.adapterMap = adapterMap;
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		mTagStr = bundle.getString(Consts.FRAGMENT_EXTRA_TAG);
		mTvDate = bundle.getParcelable(Consts.FRAGMENT_EXTRA_TVDATE);
		mTvDatePosition = bundle.getInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION);
		mTag = dazooStore.getTag(mTagStr);
		
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {

			// check if it is a current day
			String tvDateToday = DateUtilities.todayDateAsTvDate();
			if (mTvDate.getDate().equals(tvDateToday)) {
				mIsToday = true;
				if (!SecondScreenApplication.getInstance().getIsOnStartAgain()) {
					mHour = Integer.valueOf(DateUtilities.getCurrentHourString());
					SecondScreenApplication.getInstance().setSelectedHour(mHour);
					SecondScreenApplication.getInstance().setIsOnStartAgain(true);
				} else {
					mHour = SecondScreenApplication.getInstance().getSelectedHour();
				}

			} else mHour = SecondScreenApplication.getInstance().getSelectedHour();

			mRootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
			mTVGuideListView = (ListView) mRootView.findViewById(R.id.tvguide_table_listview);

			mSwipeClockBar = (SwipeClockBar) mRootView.findViewById(R.id.tvguide_swype_clock_bar);
			mSwipeClockBar.setHour(mHour);
		} else {
			mRootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
			mTVGuideListView = (ListView) mRootView.findViewById(R.id.fragment_tvguide_type_tag_listview);
		}
		super.initRequestCallbackLayouts(mRootView);

		// reset the activity whenever the view is recreated
		mActivity = getActivity();
		loadPage();

		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
			// register a receiver for the tv-table fragment to respond to the clock selection
			LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverClock, new IntentFilter(Consts.INTENT_EXTRA_CLOCK_SELECTION));
		}

		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		mGuides = null;
		mTaggedBroadcasts = null;

		// read the data from the DazooStore singleton
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {

			if (mIsLoggedIn) {
				mGuides = dazooStore.getMyGuideTable(mTvDate.getDate());
				Log.d(TAG, "My date: " + mTvDate.getDate());
				Log.d(TAG, "MY mGuides size: " + mGuides.size());

			} else {
				mGuides = dazooStore.getGuideTable(mTvDate.getDate());
				Log.d(TAG, "date: " + mTvDate.getDate());
				Log.d(TAG, "mGuides size: " + mGuides.size());
			}
		} else {
			if (mIsLoggedIn) {
				mTaggedBroadcasts = dazooStore.getMyTaggedBroadcasts(mTvDate, mTag);
				Log.d(TAG, "My tagged broadcasts");

			} else {
				mTaggedBroadcasts = dazooStore.getTaggedBroadcasts(mTvDate, mTag);
				Log.d(TAG, "default tagged broadcasts");
			}
		}

		pageHoldsData();
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
			if (mGuides != null) {
				if (mGuides.isEmpty()) {
					updateUI(REQUEST_STATUS.EMPTY_RESPONSE);

				} else {
					updateUI(REQUEST_STATUS.SUCCESSFUL);
					//focusOnView();
					result = true;
				}
			}
		} else {
			if (mTaggedBroadcasts != null) {
				Log.d(TAG, "size: " + mTaggedBroadcasts.size());
				if (mTaggedBroadcasts.isEmpty() != true) {
					Log.d(TAG, "empty");
					updateUI(REQUEST_STATUS.SUCCESSFUL);
					result = true;
				} else {
					updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
				}
			}

		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
				mTVGuideListAdapter = (TVGuideListAdapter) adapterMap.get(mTagStr);
				if(mTVGuideListAdapter == null) {
					mTVGuideListAdapter = new TVGuideListAdapter(mActivity, mGuides, mTvDate, mHour, mIsToday);
					adapterMap.put(mTagStr, mTVGuideListAdapter);
				}
				
				mTVGuideListView.setAdapter(mTVGuideListAdapter);
				mTVGuideListAdapter.notifyDataSetChanged();
				focusOnView();
			} else {
				final int index = Broadcast.getClosestBroadcastIndex(mTaggedBroadcasts);
				//Remove all broadcasts that already ended
				new Runnable() {
					
					@Override
					public void run() {
						ArrayList<Broadcast> toRemove = new ArrayList<Broadcast>();
						for (int i = index; i < mTaggedBroadcasts.size(); i++) {
							if(index < mTaggedBroadcasts.size()-1 && index >= 0) {
								if (mTaggedBroadcasts.get(i).hasNotEnded() == false) {
									toRemove.add(mTaggedBroadcasts.get(i));
								}
							}
						}
						for (Broadcast broadcast : toRemove) {
							mTaggedBroadcasts.remove(broadcast);
						}
					}
				}.run();
				Log.d(TAG, "index: " + index);
				
				
				mTVTagListAdapter = (TVGuideTagListAdapter) adapterMap.get(mTagStr);
				if(mTVTagListAdapter == null) {
					mTVTagListAdapter = new TVGuideTagListAdapter(mActivity, mTagStr, mTaggedBroadcasts, index, mTvDate);
					adapterMap.put(mTagStr, mTVTagListAdapter);
				}
				
				
				mTVGuideListView.setAdapter(mTVTagListAdapter);
			}
		}
	}
	

	BroadcastReceiver				mBroadcastReceiverClock	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() != null && intent.getAction().equals(Consts.INTENT_EXTRA_CLOCK_SELECTION)) {
				mHour = intent.getExtras().getInt(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE);
				if(mTVGuideListAdapter != null) {
					mTVGuideListAdapter.refreshList(Integer.valueOf(mHour));
				}
			}
		}
	};

	@Override
	public void onDetach() {
		super.onDetach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiverClock);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiverClock);
	}

	private final void focusOnView() {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				if(mCurrentHourTv!=null){
					Log.d(TAG, "FOCUS ON VIEW");
					//TODO do equivivalent!
					//mClockScrollView.smoothScrollTo(0, mCurrentHourTv.getTop());
				}
			}
		});
	}
}
