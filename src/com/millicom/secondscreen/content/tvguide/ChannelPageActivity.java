package com.millicom.secondscreen.content.tvguide;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.ChannelPageListAdapter;
import com.millicom.secondscreen.content.model.Channel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelPageActivity extends ActionBarActivity {

	private ActionBar mActionBar;
	private ImageView mChannelIconIv, mChannelBroadcastLiveIv;
	private ProgressBar mChannelBroadcastLiveIvPrB, mDurationProgressBar;
	private TextView mBroadcastLiveTimeTv, mBroadcastLiveTitleTv, mBroadcastLiveTextTv;
	private ListView mFollowingBroadcastsLv;
	private ChannelPageListAdapter mFollowingBroadcastsListAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_channelpage_activity);
		
		// get the info about the channel to be displayed from tv-guide listview
		Intent intent = getIntent(); 
		String channelPageLink = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_PAGE_LINK);
		
		//parse individual channel page
		//TODO
		
		//Channel channel = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL);
		//String channelName = channel.getName();
		//Toast.makeText(this, "Channel " + channelName + " is to be shown" , Toast.LENGTH_LONG).show();
		
		initViews();
	}

	private void initViews() {
		// styling the Action Bar
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_channelpage);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		
		mChannelIconIv = (ImageView) findViewById(R.id.channelpage_channel_icon_iv);
		mChannelBroadcastLiveIv = (ImageView) findViewById(R.id.channelpage_broadcast_iv);
		mChannelBroadcastLiveIvPrB = (ProgressBar) findViewById(R.id.channelpage_broadcast_iv_progressbar);
		
		mBroadcastLiveTimeTv = (TextView) findViewById(R.id.channelpage_broadcast_details_time_tv);
		mBroadcastLiveTitleTv = (TextView) findViewById(R.id.channelpage_broadcast_details_title_tv);
		mDurationProgressBar = (ProgressBar) findViewById(R.id.channelpage_broadcast_details_progressbar);
		mBroadcastLiveTextTv = (TextView) findViewById(R.id.channelpage_broadcast_details_text_tv);
		
		mFollowingBroadcastsLv = (ListView) findViewById(R.id.listview);
		
		// TODO: INITIALIZE ADAPTER WITH DATA
		
		mFollowingBroadcastsLv.setAdapter(mFollowingBroadcastsListAdapter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}