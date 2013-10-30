package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.TVGuideTagListAdapter;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;

public class TVGuideTagTypeFragment extends SSPageFragment{
	
	private static final String TAG = "TVGuideTagTypeFragment";
	private Activity mActivity;
	private String mTagName;
	private View mRootView;
	private ListView mListView;
	private ArrayList<Guide> mGuide;
	private TVGuideTagListAdapter	mAdapter;
	
	public static TVGuideTagTypeFragment newInstance(Tag tag) {

		TVGuideTagTypeFragment fragment = new TVGuideTagTypeFragment();
		Bundle bundle = new Bundle();
		
		// TO BE DETERMINED
		//bundle.putParcelableArrayList(Consts.PARCELABLE_CHANNELS_LIST, channels);
		//bundle.putString(Consts.INTENT_EXTRA_TVGUIDE_TVDATE, tvDate);
		
		bundle.putString(Consts.INTENT_EXTRA_TAG, tag.getName());
		
		fragment.setArguments(bundle);

		Log.d(TAG, "Fragment new instance: " + tag.getName());
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		mTagName = bundle.getString(Consts.INTENT_EXTRA_TAG);
		
		Log.d(TAG, "OnCreate TAG:" + mTagName);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_tvguide_tag_type, null);
		mListView = (ListView) mRootView.findViewById(R.id.fragment_tvguide_type_tag_listview);

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
			mAdapter = new TVGuideTagListAdapter(mActivity, mGuide);
			mListView.setAdapter(mAdapter);
		}
	}
}
