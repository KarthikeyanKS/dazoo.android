package com.millicom.mitv.fragments;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVGuide;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.AdListAdapter;
import com.mitv.adapters.TVGuideListAdapter;
import com.mitv.adapters.TVGuideTagListAdapter;
import com.mitv.customviews.SwipeClockBar;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.OldDateUtilities;

public class TVGuideTableFragment extends BaseFragment implements ActivityCallbackListener {

	private static final String		TAG	= "TVGuideTableFragment";
	private String					mTagStr;
	private View					mRootView;
	private Activity				mActivity;
	private ListView				mTVGuideListView;
	private ImageView				mClockIv;
	private ArrayList<TVChannelGuide>		mGuides;
//	private TVDate					mTvDate;
	private String						mTag;
//	private int						mTvDatePosition;
	private SwipeClockBar			mSwipeClockBar;
	private TVGuideListAdapter		mTVGuideListAdapter;
	private MiTVStore				mitvStore;
	private boolean					mIsToday = false;
	private ArrayList<TVBroadcastWithChannelInfo>	mTaggedBroadcasts;
	private TVGuideTagListAdapter	mTVTagListAdapter;
	private int						mHour;
	private TextView				mCurrentHourTv;
	public HashMap<String, AdListAdapter> adapterMap;
	private BroadcastReceiver		mBroadcastReceiverClock;
	
	private void initBroadcastReceivers() {
		mBroadcastReceiverClock = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction() != null && intent.getAction().equals(Consts.INTENT_EXTRA_CLOCK_SELECTION)) {
					mHour = intent.getExtras().getInt(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE);
					if(mTVGuideListAdapter != null) 
					{
						mTVGuideListAdapter.refreshList(Integer.valueOf(mHour));
					}
				}
			}
		};
	}
	
	private void registerBroadcastReceivers() {
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverClock, new IntentFilter(Consts.INTENT_EXTRA_CLOCK_SELECTION));
	}
	
	private void unregisterBroadcastReceivers() {
		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiverClock);
	}
	
	public static TVGuideTableFragment newInstance(TVTag tag, TVDate date, HashMap<String, AdListAdapter> adapterMap) {

		TVGuideTableFragment fragment = new TVGuideTableFragment();
		fragment.adapterMap = adapterMap;
		Bundle bundle = new Bundle();
//		bundle.putParcelable(Consts.FRAGMENT_EXTRA_TVDATE, date.getId());
//		bundle.putInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION, position);
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG_DISPLAY_NAME, tag.getDisplayName());
		bundle.putString(Consts.FRAGMENT_EXTRA_TAG_ID, tag.getId());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initBroadcastReceivers();

//		mitvStore = MiTVStore.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		mTagStr = bundle.getString(Consts.FRAGMENT_EXTRA_TAG_DISPLAY_NAME);
		mTag = bundle.getString(Consts.FRAGMENT_EXTRA_TAG_ID);
//		mTvDate = bundle.getParcelable(Consts.FRAGMENT_EXTRA_TVDATE);
//		mTvDatePosition = bundle.getInt(Consts.FRAGMENT_EXTRA_TVDATE_POSITION);
//		mTag = mitvStore.getTag(mTagStr);
		
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {

			// check if it is a current day
//			if (ContentManager.sharedInstance().selectedTVDateIsToday()) {
//				mIsToday = true;
//				if (!SecondScreenApplication.getInstance().getIsOnStartAgain()) {
//					mHour = Integer.valueOf(OldDateUtilities.getCurrentHourString());
//					SecondScreenApplication.getInstance().setSelectedHour(mHour);
//					SecondScreenApplication.getInstance().setIsOnStartAgain(true);
//				} else {
//					mHour = SecondScreenApplication.getInstance().getSelectedHour();
//				}
//
//			} else mHour = SecondScreenApplication.getInstance().getSelectedHour();
			
			mHour = ContentManager.sharedInstance().getFromStorageSelectedHour();

			mRootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
			mTVGuideListView = (ListView) mRootView.findViewById(R.id.tvguide_table_listview);

			mSwipeClockBar = (SwipeClockBar) mRootView.findViewById(R.id.tvguide_swype_clock_bar);
			mSwipeClockBar.setHour(mHour);
			
			//TODO figure this out?
			mSwipeClockBar.setToday(mIsToday);
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
			registerBroadcastReceivers();
		}

		return mRootView;
	}
	
	

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		mGuides = null;
		mTaggedBroadcasts = null;

		// read the data from the mitvStore singleton
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
//			mGuides = mitvStore.getChannelGuides(mTvDate.getDate());
			ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
		} else {
//			mTaggedBroadcasts = mitvStore.getTaggedBroadcasts(mTvDate, mTag);
			ContentManager.sharedInstance().getElseFetchFromServiceTaggedBroadcastsForSelectedTVDate(this, false);
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
					TVDate tvDateSelected = ContentManager.sharedInstance().getFromStorageTVDateSelected();
					mTVGuideListAdapter = new TVGuideListAdapter(mActivity, mGuides, tvDateSelected, mHour, mIsToday);
					adapterMap.put(mTagStr, mTVGuideListAdapter);
				}
				
				mTVGuideListView.setAdapter(mTVGuideListAdapter);
				mTVGuideListAdapter.notifyDataSetChanged();
				focusOnView();
			} 
			else 
			{
				final int index = TVBroadcastWithChannelInfo.getClosestBroadcastIndex(mTaggedBroadcasts, 0);
				
				//Remove all broadcasts that already ended
				new Runnable() {
					
					@Override
					public void run() 
					{
						ArrayList<TVBroadcast> toRemove = new ArrayList<TVBroadcast>();
						
						for (int i = index; i < mTaggedBroadcasts.size(); i++) 
						{
							if(index < mTaggedBroadcasts.size()-1 && index >= 0) 
							{
								if (mTaggedBroadcasts.get(i).hasEnded()) 
								{
									toRemove.add(mTaggedBroadcasts.get(i));
								}
							}
						}
						
						for (TVBroadcast broadcast : toRemove) 
						{
							mTaggedBroadcasts.remove(broadcast);
						}
					}
				}.run();
				
				
				mTVTagListAdapter = (TVGuideTagListAdapter) adapterMap.get(mTagStr);
				
				if(mTVTagListAdapter == null) 
				{
					TVDate tvDateSelected = ContentManager.sharedInstance().getFromStorageTVDateSelected();
					
					mTVTagListAdapter = new TVGuideTagListAdapter(mActivity, mTagStr, mTaggedBroadcasts, index, tvDateSelected);
					
					adapterMap.put(mTagStr, mTVTagListAdapter);
				}
				
				
				mTVGuideListView.setAdapter(mTVTagListAdapter);
			}
		}
	}
	
	
	
	@Override
	public void onPause() {
		unregisterBroadcastReceivers();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerBroadcastReceivers();
		super.onResume();
	}

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

	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) {
		// TODO complete implementation of me
		if(fetchRequestResult.wasSuccessful() ) {
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
//			mGuides = mitvStore.getChannelGuides(mTvDate.getDate());
			TVGuide tvGuideForSelectedDay = ContentManager.sharedInstance().getFromStorageTVGuideForSelectedDay();
			//TODO scrap mGuides variable and use TVGuide instead?
			mGuides = tvGuideForSelectedDay.getTvChannelGuides();
		} else {
//			mTaggedBroadcasts = mitvStore.getTaggedBroadcasts(mTvDate, mTag);
			HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = ContentManager.sharedInstance().getFromStorageTaggedBroadcastsForSelectedTVDate();
			mTaggedBroadcasts = taggedBroadcastForDay.get(mTag);
		}
		} else {
			//TODO fix me!
		}
	}
}
