package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSBroadcastBlockPopulator;
import com.millicom.secondscreen.content.SSCastCrewBlockPopulator;
import com.millicom.secondscreen.content.SSSocialInteractionPanelCreator;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class BroadcastPageActivity extends ActionBarActivity {

	private static final String	TAG	= "BroadcastPageActivity";
	private ImageLoader			mImageLoader;
	private Broadcast			mBroadcast;
	private Channel				mChannel;
	private Program				mProgram;
	private LinearLayout		mBlockContainer;
	private ActionBar			mActionBar;
	private LayoutInflater		mLayoutInflater;
	private String				mBroadcastUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_broadcastpage_activity);
		
		Log.d(TAG,"Are you from notification");

		mImageLoader = new ImageLoader(this, R.drawable.loadimage_2x);
		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// get the info about the program to be displayed from tv-guide listview
		Intent intent = getIntent();
		mBroadcast = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL_BROADCAST);
		mChannel = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL);
		mProgram = mBroadcast.getProgram();

		initViews();
		populateBlocks();
	}

	private void initViews() {
		// styling the Action Bar
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_programpage);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);
	}

	private void populateBlocks() {
		// add the current broadcast top block
		SSBroadcastBlockPopulator broadcastBlockPopulator = new SSBroadcastBlockPopulator(this, mBlockContainer);
		broadcastBlockPopulator.createBlock(mBroadcast, mChannel);

		// add the button block
		SSSocialInteractionPanelCreator socialPanelCreator = new SSSocialInteractionPanelCreator(this, mBlockContainer, mBroadcast, mChannel);
		socialPanelCreator.createPanel();

		// add the cast and crew block
		// SSCastCrewBlockPopulator castCrewBlockPopulator = new SSCastCrewBlockPopulator(this, mBlockContainer);
		// castCrewBlockPopulator.createBlock(mCast);

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
