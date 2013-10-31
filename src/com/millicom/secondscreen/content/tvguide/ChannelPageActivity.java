package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.ActionBarDropDownDateListAdapter;
import com.millicom.secondscreen.adapters.ChannelPageListAdapter;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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

public class ChannelPageActivity extends ActionBarActivity implements OnClickListener, ActionBar.OnNavigationListener {

	private static final String		TAG	= "ChannelPageActivity";

	private ActionBar				mActionBar;
	private ActionBarDropDownDateListAdapter mDayAdapter;
	private ImageView				mChannelIconIv;
	private TextView				mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private ListView				mFollowingBroadcastsLv;
	private ChannelPageListAdapter	mFollowingBroadcastsListAdapter;
	private String					mChannelId, mDate;
	private Guide					mChannelGuide;
	private Channel					mChannel;
	private ArrayList<Broadcast>	mBroadcasts, mFollowingBroadcasts;
	private ArrayList<TvDate> mTvDates;
	private ImageLoader				mImageLoader;
	private int mSelectedIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_channelpage_activity);

		mImageLoader = new ImageLoader(this, R.drawable.loadimage_2x);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Consts.INTENT_EXTRA_CHANNEL_SORTING));

		// get the info about the individual channel guide to be displayed from tv-guide listview
		Intent intent = getIntent();
		mChannelId = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
		
		// GET THE CHANNEL DATA AND DATE DATA FROM DAZOO STORE SINGLETOP BY CHANNEL ID
		
		// mChannelGuide = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE);
		// mChannel = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL);
		// mBroadcasts = mChannelGuide.getBroadcasts();

		initViews();
		populateViews();
	}

	BroadcastReceiver	mBroadcastReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_SORTING_VALUE);
			Log.d(TAG, "mDate" + mDate);

			// RELOAD THE PAGE WITH NEW DATE
			reloadPage();
		}
	};

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		mDayAdapter.setSelectedIndex(position);
		TvDate tvDateItem = mTvDates.get(position);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(Consts.INTENT_EXTRA_CHANNEL_SORTING).putExtra(Consts.INTENT_EXTRA_CHANNEL_SORTING_VALUE, tvDateItem.getDate()));
		return true;
	}
	
	private void reloadPage(){
		// RELOAD THE PAGE WITH THE NEW DATE AND CONTENT OF THE LISTVIEW
		
		mFollowingBroadcastsListAdapter.notifyDataSetChanged();
	}
	
	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mDayAdapter = new ActionBarDropDownDateListAdapter(mTvDates);
		mDayAdapter.setSelectedIndex(mSelectedIndex);
		mActionBar.setListNavigationCallbacks(mDayAdapter, this);
		
		mActionBar.setTitle(mChannel.getName());
		
		mChannelIconIv = (ImageView) findViewById(R.id.channelpage_channel_icon_iv);

		mFollowingBroadcastsLv = (ListView) findViewById(R.id.listview);

		// styling bottom navigation tabs
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
		
		// UPDATE THE PAGE ON SELECTING NEW DATE FROM THE ACTIONBAR DROP DOWN LIST
		
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
					
					//intent.putExtra(Consts.INTENT_EXTRA_BROADCAST, mFollowingBroadcasts.get(position));
					//intent.putExtra(Consts.INTENT_EXTRA_CHANNEL, mChannel);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, mFollowingBroadcasts.get(position).getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, mChannel.getChannelId());
					
					
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
		switch (id) {
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(ChannelPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentActivity = new Intent(ChannelPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			// tab to activity page
			Intent intentMe = new Intent(ChannelPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		}
	}
}