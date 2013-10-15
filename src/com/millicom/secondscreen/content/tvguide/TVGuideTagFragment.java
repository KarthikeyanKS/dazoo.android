package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
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
import com.millicom.secondscreen.adapters.TVGuideTagListAdapter;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;

public class TVGuideTagFragment extends SSPageFragment {

	private static final String		TAG						= "TVGuideCategoryFragment";
	private View					mRootView;
	private ListView				mListView;
	private Activity				mActivity;

	private int						mChannelsNum			= 0;
	private int						numOfChannelsShownNow	= 0;
	private String					mPageUrl, mDate, mSortingType, mTagName;

	private ArrayList<Guide>		mGuide;
	private ArrayList<Guide>		mGuideExtended			= new ArrayList<Guide>();
	private Tag						mTag;
	private ArrayList<Channel>		mChannels;
	private ArrayList<String>		mChannelIds;
	private TVGuideTagListAdapter	mAdapter;
	private SSStartPage mPage;

	public static TVGuideTagFragment newInstance(Tag tag, String tvDate, ArrayList<Channel> channels) {

		TVGuideTagFragment fragment = new TVGuideTagFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, channels);
		bundle.putString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE, tvDate);
		bundle.putString(Consts.INTENT_EXTRA_TAG, tag.getName());
		fragment.setArguments(bundle);

		Log.d(TAG, "Fragment new instance: " + tag.getName());
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		mChannels = bundle.getParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST);
		mDate = bundle.getString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE);
		mTagName = bundle.getString(Consts.INTENT_EXTRA_TAG);
		getAvailableChannelsIds();

		// mPage = SSStartPage.getInstance();
				mPage = new SSStartPage();
		
		if (mChannelIds != null) {
			mChannelsNum = mChannelIds.size();
		}

		mPageUrl = getPageUrl(numOfChannelsShownNow, mChannelsNum, mDate);
		Log.d(TAG, "OnCreate TAG:" + mTagName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide_tag_fragment, null);
		mListView = (ListView) mRootView.findViewById(R.id.listview);

		// reset the activity whenever the view is recreated
		mActivity = getActivity();

		super.initRequestCallbackLayouts(mRootView);

		loadPage();

		Log.d(TAG, "onCreateView");

		return mRootView;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		Log.d(TAG, "loadPage: " + mTagName);

		// if (!pageHoldsData()) {
		mPage.getPage(mTagName, mPageUrl, new SSPageCallback() {

			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {

				Log.d(TAG, "onGetPageResult");
				if (!pageHoldsData()) {
					// // Request failed

					Log.d(TAG, "UpdateUI: FAILED");
					updateUI(REQUEST_STATUS.FAILED);
				}
			}
		});
		// }
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;

		mGuide = mPage.getGuide();

		for (int i = 0; i < mGuide.size(); i++) {
			Guide guideItem = mGuide.get(i);
			int num = guideItem.getBroadcasts().size();
			for (int j = 0; j < num; j++) {

				Guide guideExtended = new Guide();
				ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
				broadcasts.add(guideItem.getBroadcasts().get(j));
				guideExtended.setBroadcasts(broadcasts);
				guideExtended.setId(guideItem.getId());
				guideExtended.setName(guideItem.getName());

				mGuideExtended.add(guideExtended);
			}
		}

		if (mGuideExtended != null) {
			if (mGuideExtended.isEmpty()) {
				Log.d(TAG, "EMPTY RESPONSE");
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				Log.d(TAG, "SIZE: " + mGuideExtended.size());
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
			result = true;
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		Log.d(TAG, "update UI :" + status);
		Log.d(TAG, "super status:" + super.requestIsSuccesfull(status));
		if (super.requestIsSuccesfull(status)) {

			mAdapter = new TVGuideTagListAdapter(mActivity, mGuideExtended);
			mListView.setAdapter(mAdapter);

		}
	}

	// @Override
	// public void onPause() {
	// super.onPause();
	// mPage.cancelGetPage();
	// }

	private String getPageUrl(int startPosition, int maxSize, String date) {
		StringBuilder sB = new StringBuilder();
		sB.append(Consts.MILLICOM_SECONDSCREEN_GUIDE_PAGE_URL);

		sB.append(Consts.REQUEST_QUERY_SEPARATOR);
		sB.append(date);

		sB.append(Consts.REQUEST_PARAMETER_SEPARATOR);
		for (int i = startPosition; i < maxSize && i < startPosition + Consts.MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_CHANNELS_PER_PAGE; i++) {
			if (i > 0) {
				sB.append(Consts.REQUEST_QUERY_AND);
				sB.append(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID_WITH_EQUALS_SIGN);
				sB.append(mChannelIds.get(i));
			} else {
				sB.append(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID_WITH_EQUALS_SIGN);
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