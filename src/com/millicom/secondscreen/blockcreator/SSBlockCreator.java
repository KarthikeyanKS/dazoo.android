package com.millicom.secondscreen.blockcreator;

import com.millicom.secondscreen.SecondScreenApplication;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class SSBlockCreator {

	protected Activity						mActivity;
	//protected ImageLoader					mImageLoader;
	protected Animation						mAnimation;
	protected final String					TAG	= "SSBlockPopulator";

	protected RelativeLayout.LayoutParams	mPortraitParams;
	protected RelativeLayout.LayoutParams	mLandscapeParams;

	public SSBlockCreator(Activity activity) {

		this.mActivity = activity;
		//mImageLoader = new ImageLoader(activity, R.drawable.list_row_thumbnail);
		mAnimation = AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_in);
		this.mPortraitParams = new RelativeLayout.LayoutParams(SecondScreenApplication.sImageSizePosterWidth, SecondScreenApplication.sImageSizePosterHeight);
		this.mLandscapeParams = new RelativeLayout.LayoutParams(SecondScreenApplication.sImageSizeLandscapeWidth, SecondScreenApplication.sImageSizeLandscapeHeight);
	}
	
}
