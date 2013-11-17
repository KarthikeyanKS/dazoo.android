package com.millicom.secondscreen.content.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.utilities.ImageLoader;

public class ActivityRecommendedBlockPopulator {

	private static final String	TAG	= "ActivityRecommendedBlockPopulator";

	private Activity			mActivity;
	private LinearLayout		mContainerView;
	private String mToken;
	private ImageLoader mImageLoader;
	
	public ActivityRecommendedBlockPopulator(Activity activity, LinearLayout containerView, String token) {
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mToken = token;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
	}

	public void createBlock(FeedItem popularItem) {
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_recommended, null);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, 20);
		
		mContainerView.addView(contentView, layoutParams);
	}

}
