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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelPageActivity extends ActionBarActivity {

	private static final String		TAG	= "ChannelPageActivity";

	private ActionBar				mActionBar;
	private ImageView				mChannelIconIv, mChannelBroadcastLiveIv;
	private ProgressBar				mChannelBroadcastLiveIvPrB, mDurationProgressBar;
	private TextView				mBroadcastLiveTimeTv, mBroadcastLiveTitleTv, mBroadcastLiveTextTv;
	private ListView				mFollowingBroadcastsLv;
	private ChannelPageListAdapter	mFollowingBroadcastsListAdapter;

	private Guide					mChannelGuide;
	private ArrayList<Broadcast>	mBroadcasts, mFollowingBroadcasts;

	private ImageLoader				mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_channelpage_activity);

		mImageLoader = new ImageLoader(this, R.drawable.loadimage_2x);

		// get the info about the individual channel guide to be displayed from tv-guide listview
		Intent intent = getIntent();
		mChannelGuide = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE);
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
	}

	private void populateViews() {
		mImageLoader.displayImage(mChannelGuide.getLogoLHref(), mChannelIconIv, ImageLoader.IMAGE_TYPE.POSTER);

		final int indexOfNearestBroadcast = Broadcast.getClosestBroadcastIndex(mBroadcasts);
		mFollowingBroadcasts = Broadcast.getBroadcastsStartingFromPosition(indexOfNearestBroadcast + 1, mBroadcasts, mBroadcasts.size());

		mImageLoader.displayImage(mBroadcasts.get(indexOfNearestBroadcast).getProgram().getPosterLUrl(), mChannelBroadcastLiveIv, mChannelBroadcastLiveIvPrB, ImageLoader.IMAGE_TYPE.LANDSCAPE);
		try {
			mBroadcastLiveTimeTv.setText(DateUtilities.isoStringToTimeString(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			mBroadcastLiveTimeTv.setText("");
		}
		mBroadcastLiveTitleTv.setText(mBroadcasts.get(indexOfNearestBroadcast).getProgram().getTitle());

		int duration = mBroadcasts.get(indexOfNearestBroadcast).getDurationInMinutes();
		mDurationProgressBar.setMax(duration);

		int initialProgress = 0;
		long difference = 0;

		try {
			difference = DateUtilities.getAbsoluteTimeDifference(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		if (difference < 0) {
			initialProgress = 0;
			mDurationProgressBar.setProgress(0);
		} else {
			try {
				initialProgress = DateUtilities.getDifferenceInMinutes(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mDurationProgressBar.setProgress(initialProgress);
		}

		// update progress bar value every minute
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					if (DateUtilities.getDifferenceInMinutes(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime()) > 0) {
						mDurationProgressBar.incrementProgressBy(1);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				handler.postDelayed(this, 60 * 1000);
			}
		}, 60 * 1000);
		
		mBroadcastLiveTextTv.setText(mBroadcasts.get(indexOfNearestBroadcast).getProgram().getSynopsisShort());

		mFollowingBroadcastsListAdapter = new ChannelPageListAdapter(this, mFollowingBroadcasts);
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