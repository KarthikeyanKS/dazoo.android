package com.millicom.secondscreen.content.myprofile;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyChannelsActivity extends ActionBarActivity{

	private static final String	TAG			= "ChannelsActivity";
	private ActionBar			mActionBar;
	private boolean				isChange	= false;
	private ListView mListView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mychannels_activity);
		initActionBar();
		initLayout();
	}

	private void initActionBar() {
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.my_channels));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_mepage);

		final TextView title = (TextView) findViewById(R.id.actionbar_mepage_title_tv);
		title.setText(s);

		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_mepage_search_icon);
		searchButton.setVisibility(View.GONE);
	}
	
	private void initLayout(){
		mListView = (ListView) findViewById(R.id.listview);
	}

	// if the attendee was marked/unmarked as favorite - update the list layout by displaying the star at his entry in the list
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
