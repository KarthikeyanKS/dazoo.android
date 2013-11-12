package com.millicom.secondscreen.content.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.BlockPopularListViewAdapter;
import com.millicom.secondscreen.content.model.FeedItem;

public class ActivityLikedBlockPopulator {
	
	private static final String TAG = "ActivityLikedBlockPopulator";

	private Activity		mActivity;
	private LinearLayout	mContainerView;

	public ActivityLikedBlockPopulator (Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mContainerView = containerView;
	}
	
	public void createBlock(FeedItem popularItem){
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_liked, null);
		
		
		
		mContainerView.addView(contentView);
	}

	
}
