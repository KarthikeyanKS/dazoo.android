package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.ChannelPageListAdapter;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelPageActivity extends ActionBarActivity implements OnClickListener{

	private static final String		TAG			= "ChannelPageActivity";

	private ActionBar				mActionBar;
	private ImageView				mChannelIconIv;
	private TextView				mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private ListView				mFollowingBroadcastsLv;
	private ChannelPageListAdapter	mFollowingBroadcastsListAdapter;

	private Guide					mChannelGuide;
	private Channel					mChannel;
	private ArrayList<Broadcast>	mBroadcasts, mFollowingBroadcasts;

	private ImageLoader				mImageLoader;
	private View				mTabSelectorContainerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_channelpage_activity);

		mImageLoader = new ImageLoader(this, R.drawable.loadimage_2x);

		// get the info about the individual channel guide to be displayed from tv-guide listview
		Intent intent = getIntent();
		mChannelGuide = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE);
		mChannel = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL);
		mBroadcasts = mChannelGuide.getBroadcasts();
		initViews();
		populateViews();
	}

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.actionbar_channelpage);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mChannelIconIv = (ImageView) findViewById(R.id.channelpage_channel_icon_iv);

		mFollowingBroadcastsLv = (ListView) findViewById(R.id.listview);

		// styling bottom navigation tabs
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);
		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		// the highlighted tab in the Channel activity is TV Guide
		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.black));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));
	}

	private void populateViews() {
		mImageLoader.displayImage(mChannelGuide.getLogoLHref(), mChannelIconIv, ImageLoader.IMAGE_TYPE.POSTER);

		final int indexOfNearestBroadcast = Broadcast.getClosestBroadcastIndex(mBroadcasts);
		if (indexOfNearestBroadcast >= 0) {

			mFollowingBroadcasts = Broadcast.getBroadcastsStartingFromPosition(indexOfNearestBroadcast, mBroadcasts, mBroadcasts.size());
			mFollowingBroadcastsListAdapter = new ChannelPageListAdapter(this, mFollowingBroadcasts);
			mFollowingBroadcastsLv.setAdapter(mFollowingBroadcastsListAdapter);

			// update progress bar value every minute
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						int initialProgress = DateUtilities.getDifferenceInMinutes(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime());
						if (initialProgress > 0) {
							if (DateUtilities.getAbsoluteTimeDifference(mFollowingBroadcasts.get(0).getEndTime()) > 0) {
								mFollowingBroadcastsListAdapter.notifyBroadcastEnded();
							}
							mFollowingBroadcastsListAdapter.notifyDataSetChanged();
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					handler.postDelayed(this, 60 * 1000);
				}
			}, 60 * 1000);

			mFollowingBroadcastsLv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					// open the detail view for the individual broadcast
					Intent intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST, mFollowingBroadcasts.get(position));
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL, mChannel);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});
		}
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

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.show_tvguide:
			// tab to tv guide overview page
			break;
		case R.id.show_activity:
			// tab to activity page
			break;
		case R.id.show_me:
			// tab to my profile page
			break;
		}
	}
}