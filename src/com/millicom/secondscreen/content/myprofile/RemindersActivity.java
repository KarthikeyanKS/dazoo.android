package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.RemindersListAdapter;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.Season;
import com.millicom.secondscreen.notification.NotificationDataSource;

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
		populateViews();
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
		mActionBar.setCustomView(R.layout.actionbar_mepage);

		final TextView title = (TextView) findViewById(R.id.actionbar_mepage_title_tv);
		title.setText(s);

		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_mepage_search_icon);
		searchButton.setVisibility(View.GONE);
	}
	
	private void initLayout(){
		mListView = (ListView) findViewById(R.id.listview);
	
	}
	
	private void populateViews(){
		ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
		
		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		List<NotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		for(int i = 0; i < notificationList.size(); i++){
			NotificationDbItem item = notificationList.get(i);
			Broadcast broadcast = new Broadcast();
			broadcast.setBeginTime(item.getBroadcastBeginTime());
			broadcast.setBeginTimeMillis(Long.parseLong(item.getBroadcastTimeInMillis()));
			
			Program program = new Program();
			program.setTitle(item.getProgramTitle());
			String programType = item.getProgramType();
			program.setProgramType(programType);
			
			if(Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)){
				program.setEpisode(item.getProgramEpisode());
				Season season = new Season();
				season.setNumber(item.getProgramSeason());
				program.setSeason(season);
			} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)){
				program.setYear(String.valueOf(item.getProgramYear()));
			} 
			
			//program.setTags()
			
			broadcast.setProgram(program);
			
			Channel channel = new Channel();
			channel.setChannelId(item.getChannelId());
			channel.setName(item.getChannelName());
			
			broadcast.setChannel(channel);
			
			broadcasts.add(broadcast);
		}
		Toast.makeText(this, "Currently you have " + notificationList.size() + " being set. List with info is coming soon!", Toast.LENGTH_LONG).show();
		
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
