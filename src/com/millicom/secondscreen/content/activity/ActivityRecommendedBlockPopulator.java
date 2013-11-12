package com.millicom.secondscreen.content.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.FeedItem;

public class ActivityRecommendedBlockPopulator {

	private static final String	TAG	= "ActivityRecommendedBlockPopulator";

	private Activity			mActivity;
	private LinearLayout		mContainerView;

	public ActivityRecommendedBlockPopulator(Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mContainerView = containerView;
	}

	public void createBlock(FeedItem popularItem) {
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_recommended, null);

		mContainerView.addView(contentView);
	}

}
