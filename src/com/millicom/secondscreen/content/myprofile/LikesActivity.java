package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.LikesListAdapter;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.like.LikeService;
import com.millicom.secondscreen.storage.DazooStore;

public class LikesActivity extends ActionBarActivity implements LikesCountInterface, OnClickListener {

	private static final String	TAG			= "LikesActivity";
	private ActionBar			mActionBar;
	private boolean				mIsChange	= false;
	private ListView			mListView;
	private LikesListAdapter	mAdapter;
	private String				token;
	private View				mTabSelectorContainerView;
	private TextView			mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private int mCount = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_likes_activity);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		
		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		initLayout();
		populateLayout();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.likes));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.likes));

		// styling bottom navigation tabs
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTxtTabPopular.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTxtTabFeed.setBackgroundColor(getResources().getColor(R.color.red));
	
		mListView = (ListView) findViewById(R.id.listview);
	}

	private void populateLayout() {
		ArrayList<DazooLike> likes = new ArrayList<DazooLike>();
		likes = LikeService.getLikesList(token);
		mAdapter = new LikesListAdapter(this, likes, token, this);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		if (mIsChange == true) {
			setResult(Consts.INFO_UPDATE_LIKES, returnIntent);
			returnIntent.putExtra(Consts.INFO_UPDATE_LIKES_NUMBER, mCount);
		}
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
			Intent intentHome = new Intent(LikesActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// tab to home page
			Intent intentActivity = new Intent(LikesActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			Intent returnIntent = new Intent();
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_LIKES, returnIntent);
				returnIntent.putExtra(Consts.INFO_UPDATE_LIKES_NUMBER, mCount);
			}
			finish();
			break;
		}
	}

	@Override
	public void setCount(int count) {
		mIsChange = true;
		mCount = count;
	}
}
