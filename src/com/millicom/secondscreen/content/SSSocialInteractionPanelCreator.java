package com.millicom.secondscreen.content;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.ImageLoader;

public class SSSocialInteractionPanelCreator {

	private Activity		mActivity;
	private ImageLoader		mImageLoader;
	private LinearLayout	mContainerView;

	public SSSocialInteractionPanelCreator(Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		this.mContainerView = containerView;
	}
	
	public void createPanel(){
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_social_panel, null);
		ImageView likeButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_like_button_iv);
		TextView likeButtonTv = (TextView) contentView.findViewById(R.id.block_social_panel_like_button_tv);
		ImageView shareButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_share_button_iv);
		ImageView remindButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_remind_button_iv);
		
		mContainerView.addView(contentView);
	}

}
