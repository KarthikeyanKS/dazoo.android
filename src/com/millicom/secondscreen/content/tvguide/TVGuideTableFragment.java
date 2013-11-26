package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;

public class TVGuideTableFragment extends SSPageFragment {

	private static final String		TAG			= "TVGuideTableFragment";
	private String					mTagStr, token;
	private View					mRootView;
	private Activity				mActivity;
	private ListView				mTVGuideListView;
	private ImageView				mClockIv;
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
	private int						mHour;
	private TextView				mCurrentHourTv;

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
		
		// register a receiver for the tv-table fragment to respond to the clock selection
		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiverClock, new IntentFilter(Consts.INTENT_EXTRA_CLOCK_SELECTION));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (getResources().getString(R.string.all_categories_name).equals(mTagStr)) {

			// check if it is a current day
			String tvDateToday = DateUtilities.todayDateAsTvDate();
			if (mTvDate.getDate().equals(tvDateToday)) {
				mHour = Integer.valueOf(DateUtilities.getCurrentHourString());
			} else {
				mHour = 6; // set to start of the day
			}
			
			mRootView = inflater.inflate(R.layout.fragment_tvguide_table, null);
			mTVGuideListView = (ListView) mRootView.findViewById(R.id.tvguide_table_listview);
			mClockIndexView = (LinearLayout) mRootView.findViewById(R.id.tvguide_table_side_clock_index);
			mClockIv = (ImageView) mRootView.findViewById(R.id.tvguide_table_side_clock_iv);
			mClockIv.setOnTouchListener(vTouch);

			styleCurrentHourSelection();

		} else {

			mRootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
			mTVGuideListView = (ListView) mRootView.findViewById(R.id.fragment_tvguide_type_tag_listview);
		}
		super.initRequestCallbackLayouts(mRootView);

		// reset the activity whenever the view is recreated
		mActivity = getActivity();
		loadPage();
		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		mGuides = null;
		mTaggedBroadcasts = null;

		// GET THE DATA FROM CORE LOGIC SINGLETON
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
				mTaggedBroadcasts = DazooStore.getInstance().getMyTaggedBroadcasts(mTvDate, mTag);
				Log.d(TAG, "I GOT MY!!!!!!!!");

			} else {
				mTaggedBroadcasts = DazooStore.getInstance().getTaggedBroadcasts(mTvDate, mTag);
				Log.d(TAG, "I GOT DEFAULT!!!!!!!!!");
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
					Log.d(TAG, "GET GUIDE:");
					// DazooCore.getGuide(mTvDatePosition, false);
					updateUI(REQUEST_STATUS.EMPTY_RESPONSE);

				} else {
					updateUI(REQUEST_STATUS.SUCCESSFUL);
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
				mTVGuideListAdapter = new TVGuideListAdapter(mActivity, mGuides, mTvDate, mHour);
				mTVGuideListView.setAdapter(mTVGuideListAdapter);
			} else {
				int index = Broadcast.getClosestBroadcastIndex(mTaggedBroadcasts);
				Log.d(TAG, "index: " + index);
				mTVTagListAdapter = new TVGuideTagListAdapter(mActivity, mTaggedBroadcasts, index, mTvDate);
				mTVGuideListView.setAdapter(mTVTagListAdapter);
			}
		}
	}

	private View.OnTouchListener	vTouch					= new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mClockIndexView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
			} else {
				mClockIndexView.setBackgroundColor(mActivity.getResources().getColor(R.color.red));
			}
			return true;
		}
	};

	BroadcastReceiver				mBroadcastReceiverClock	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "clock tag: " + intent.getExtras().getString(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE));
			mHour = Integer.valueOf(intent.getExtras().getString(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE));
			styleCurrentHourSelection();
			mTVGuideListAdapter.refreshList(Integer.valueOf(mHour));
		}
	};

	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiverClock);
	}

	private static TextView getViewByTag(ViewGroup root, String tag) {
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				TextView textView = (TextView) child;
				return textView;
			}
		}
		return null;
	}

	private void styleCurrentHourSelection() {
		// if the there is styled hour remove styling
		if (mCurrentHourTv != null) {
			mCurrentHourTv.setTextColor(getActivity().getResources().getColor(R.color.grey3));
			mCurrentHourTv.setTextSize(12);
			mCurrentHourTv.setTypeface(Typeface.DEFAULT);
		}

		mCurrentHourTv = getViewByTag(mClockIndexView, String.valueOf(mHour));
		if (mCurrentHourTv != null) {
			mCurrentHourTv.setTextColor(getActivity().getResources().getColor(R.color.red));
			mCurrentHourTv.setTextSize(15);
			mCurrentHourTv.setTypeface(Typeface.DEFAULT_BOLD);
		}
	}
}
