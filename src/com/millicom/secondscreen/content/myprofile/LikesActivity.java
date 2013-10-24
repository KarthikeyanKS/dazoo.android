package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.LikesListAdapter;
import com.millicom.secondscreen.content.homepage.HomePageActivity;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.like.LikeService;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.SecondScreenApplication;

public class LikesActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG			= "LikesActivity";
	private ActionBar			mActionBar;
	private boolean				isChange	= false;
	private ListView			mListView;
	private LikesListAdapter	mAdapter;
	private String				token;
	private View				mTabSelectorContainerView;
	private TextView mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_likes_activity);

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		initLayout();

		populateLayout();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.likes));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
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

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.orange));

		mListView = (ListView) findViewById(R.id.listview);
	}

	private void populateLayout() {
		ArrayList<DazooLike> likes = new ArrayList<DazooLike>();
		likes = LikeService.getLikesList(token);
		mAdapter = new LikesListAdapter(this, likes, token);
		Toast.makeText(this, "You have " + likes.size() + " likes now! List is coming", Toast.LENGTH_SHORT).show();

		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		if (isChange == true) {
			setResult(Consts.INFO_UPDATE_LIKES, returnIntent);
			// Log.d(TAG, "On Back pressed: activity update");
		} else {
			setResult(Consts.INFO_NO_UPDATE_LIKES, returnIntent);
			// Log.d(TAG, "On Back pressed: no activity update");
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
		switch(id){
		case R.id.show_tvguide:
			
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
