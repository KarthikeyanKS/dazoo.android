package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.RemindersListAdapter;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Program;

public class RemindersActivity extends ActionBarActivity {

	private static final String	TAG			= "RemindersActivity";
	private ActionBar			mActionBar;
	private boolean				isChange	= false;
	private ListView mListView;
	private RemindersListAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_reminders_activity);
		initActionBar();
		initLayout();
	}

	private void initActionBar() {
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.reminders));
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
		ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
		Channel channel = new Channel();
		channel.setLogoSUrl("http://api.gitrgitr.com/logos/ae.gif");
		channel.setName("A&E MUNDO");
		Program program = new Program();
		program.setTitle("¿Quién da má$?");
		program.setEpisode("Episode 1");
		program.setSeason("Season 1");
		program.setPosterMUrl("http://api.gitrgitr.com/images/quien_da_mas_8.gif");
		Broadcast broadcast = new Broadcast();
		broadcast.setBeginTime("2013-09-17T06:00:00+00:00");
		broadcast.setChannel(channel);
		broadcast.setProgram(program);
		broadcasts.add(broadcast);
		
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new RemindersListAdapter(this, broadcasts);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		if (isChange == true) {
			setResult(Consts.INFO_UPDATE_REMINDERS, returnIntent);
			// Log.d(TAG, "On Back pressed: activity update");
		} else {
			setResult(Consts.INFO_NO_UPDATE_REMINDERS, returnIntent);
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
}
