package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.LikesListAdapter;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.like.LikeService;

public class LikesActivity extends SSActivity implements LikesCountInterface, OnClickListener {

	private static final String	TAG			= "LikesActivity";
	private ActionBar			mActionBar;
	private boolean				mIsChange	= false;
	private ListView			mListView;
	private LikesListAdapter	mAdapter;
	private String				token;
	private RelativeLayout		mTabTvGuide, mTabActivity, mTabProfile, mTabDividerLeftContainer, mTabDividerRightContainer;
	private TextView mErrorTv;
	private int mCount = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_likes_activity);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		
		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		initLayout();
		super.initCallbackLayouts();
		populateLayout();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.likes));
		mActionBar.setDisplayHomeAsUpEnabled(true);

		// styling bottom navigation tabs
		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);
		
		mTabDividerLeftContainer = (RelativeLayout) findViewById(R.id.tab_left_divider_container);
		mTabDividerRightContainer = (RelativeLayout) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeftContainer.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRightContainer.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));
	
		mListView = (ListView) findViewById(R.id.listview);
		mErrorTv = (TextView) findViewById(R.id.likes_error_tv);
	}

	private void populateLayout() {
		ArrayList<DazooLike> likes = new ArrayList<DazooLike>();
		likes = LikeService.getLikesList(token);
		
		if(likes.isEmpty()){
			mErrorTv.setVisibility(View.VISIBLE);
		}
		
		Collections.sort(likes, new DazooLike.DazooLikeComparatorByTitle());
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
		
		if(count == 0) {
			mErrorTv.setVisibility(View.VISIBLE);
		} else {
			mErrorTv.setVisibility(View.GONE);
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		
	}

	@Override
	protected void loadPage() {
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		// update the likes list on Up/Home button press too
		case android.R.id.home:
			
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_LIKES, upIntent);
				upIntent.putExtra(Consts.INFO_UPDATE_LIKES_NUMBER, mCount);
			}
			NavUtils.navigateUpTo(this, upIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
