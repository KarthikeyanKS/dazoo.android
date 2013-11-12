package com.millicom.secondscreen.content.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.BlockPopularListViewAdapter;
import com.millicom.secondscreen.content.model.FeedItem;

public class ActivityPopularBlockPopulator {

	private Activity		mActivity;
	private LinearLayout	mContainerView;

	public ActivityPopularBlockPopulator (Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mContainerView = containerView;
	}
	
	public void createBlock(FeedItem popularItem){
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular, null);
		ListView popularListView = (ListView) contentView.findViewById(R.id.block_popular_listview);
		
		BlockPopularListViewAdapter adapter = new BlockPopularListViewAdapter(mActivity, popularItem.getBroadcasts());
		popularListView.setAdapter(adapter);	
		
		TextView showAllBtn = (TextView) contentView.findViewById(R.id.block_popular_show_more_btn);
		showAllBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, PopularPageActivity.class);
				// ADD THE URL TO THE POPULAR LIST AS AN ARGUMENT?
				mActivity.startActivity(intent);			
			}
		});
		mContainerView.addView(contentView);
	}
}
	

