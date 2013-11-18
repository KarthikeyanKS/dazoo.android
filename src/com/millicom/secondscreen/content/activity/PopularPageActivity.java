package com.millicom.secondscreen.content.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.PopularListAdapter;
import com.millicom.secondscreen.authentication.SignInActivity;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.http.NetworkUtils;

public class PopularPageActivity extends SSActivity implements OnClickListener {

	private static final String		TAG	= "PopularPageActivity";
	private String					token;
	private TextView				mTxtTabTvGuide, mTxtTabProfile, mTxtTabActivity, mSignInTv;
	private ActionBar				mActionBar;
	private ListView				mListView;
	private PopularListAdapter		mAdapter;
	private ArrayList<Broadcast>	mPopularBroadcasts;

	// EXTENDED VIEW OF THE POPULAR BLOCK AT THE ACTIVITY PAGE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_popular_list_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		initViews();

		super.initCallbackLayouts();
	}

	private void initViews() {
		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabActivity = (TextView) findViewById(R.id.show_activity);
		mTxtTabActivity.setOnClickListener(this);
		mTxtTabProfile = (TextView) findViewById(R.id.show_me);
		mTxtTabProfile.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabActivity.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabProfile.setTextColor(getResources().getColor(R.color.gray));

		mActionBar = getSupportActionBar();

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.popular));
		mListView = (ListView) findViewById(R.id.popular_list_listview);
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			mAdapter = new PopularListAdapter(this, token, mPopularBroadcasts);
			mListView.setAdapter(mAdapter);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		// check if the network connection exists
		if (!NetworkUtils.checkConnection(this)) {
			updateUI(REQUEST_STATUS.FAILED);
		}

		// BACKEND REQUEST TO GET THE LIST OF POPULAR BROADCASTS
		if (mPopularBroadcasts != null) {
			if (mPopularBroadcasts.isEmpty() != true) {
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			} else {
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			}
		} else {
			updateUI(REQUEST_STATUS.FAILED);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(PopularPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentActivity = new Intent(PopularPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			// tab to profile page
			Intent intentMe = new Intent(PopularPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		}
	}
}
