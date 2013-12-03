package com.millicom.secondscreen.content.myprofile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.MyChannelsListAdapter;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.manager.ContentParser;
import com.millicom.secondscreen.mychannels.MyChannelsService;
import com.millicom.secondscreen.storage.BroadcastKey;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.SecondScreenApplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MyChannelsActivity extends SSActivity implements MyChannelsCountInterface, OnClickListener {

	private static final String			TAG						= "MyChannelsActivity";
	private ActionBar					mActionBar;
	private boolean						isChange				= false;
	private Button						mGetMyChannelsButton, mAddToMyChannelsButton;
	private String						userToken;
	private ListView					mListView;
	private TextView					mChannelCountTv;
	private RelativeLayout				mTabTvGuide, mTabProfile, mTabActivity;
	private EditText					mSearchChannelInputEditText;
	private MyChannelsListAdapter		mAdapter;
	private ArrayList<Channel>			mChannels				= new ArrayList<Channel>();
	private HashMap<String, Channel>	mChannelsMap			= new HashMap<String, Channel>();
	private boolean[]					mIsCheckedArray;
	private View						mTabSelectorContainerView;
	private int							mChannelCounter			= 0;
	private boolean						mIsChanged				= false;
	private int							mCount					= 0;

	private ArrayList<Channel>			mChannelInfoToDisplay	= new ArrayList<Channel>();
	private Map<String, Channel>		mChannelInfoMap			= new HashMap<String, Channel>();

	private ArrayList<String>			myChannelIds			= new ArrayList<String>();
	private ArrayList<String>			mAllChannelsIds			= new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mychannels_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		userToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		initLayout();
		super.initCallbackLayouts();
		populateViews();
	}

	private void initLayout() {

		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.my_channels));

		// styling bottom navigation tabs
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);

		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		mListView = (ListView) findViewById(R.id.listview);
		mChannelCountTv = (TextView) findViewById(R.id.mychannels_header_counter_tv);
		mSearchChannelInputEditText = (EditText) findViewById(R.id.mychannels_header_search_ev);
	}

	private void populateViews() {
		mChannelsMap = DazooStore.getInstance().getAllChannels();

		int allChannelsIndex = 0;
		for (Entry<String, Channel> entry : mChannelsMap.entrySet()) {
			mChannels.add(entry.getValue());
			mAllChannelsIds.add(mChannels.get(allChannelsIndex).getChannelId());
			allChannelsIndex++;
		}

		for (int i = 0; i < mChannels.size(); i++) {
			mChannelInfoMap.put(mChannels.get(i).getName().toLowerCase(Locale.getDefault()), mChannels.get(i));
			mChannelInfoToDisplay.add(mChannels.get(i));
		}

		mIsCheckedArray = new boolean[mAllChannelsIds.size()];

		if (userToken != null && TextUtils.isEmpty(userToken) != true) {
			// get user channels
			if (getUserMyChannelsIdsJSON()) {
				mChannelCounter = myChannelIds.size();
				mChannelCountTv.setText(" " + String.valueOf(mChannelCounter));
				mAdapter = new MyChannelsListAdapter(this, mChannelInfoToDisplay, mIsCheckedArray, this, mChannelCounter);
				mListView.setAdapter(mAdapter);
			}
		} else {
			Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Login action is required to be done before this");
		}

		mSearchChannelInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String search = s.toString();
				if (search.contains(System.getProperty("line.separator"))) {
					search = search.replace(System.getProperty("line.separator"), "");
					mSearchChannelInputEditText.setText(search);
					InputMethodManager in = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(mSearchChannelInputEditText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}
				if (search.length() > 0) {
					mChannelInfoToDisplay.clear();
					for (Map.Entry<String, Channel> entry : mChannelInfoMap.entrySet()) {
						String key = entry.getKey();
						Channel channel = entry.getValue();
						if (key.toLowerCase().contains(search.toLowerCase())) {
							mChannelInfoToDisplay.add(channel);
							mAdapter.notifyDataSetChanged();
						}
					}
				} else {
					mChannelInfoToDisplay.clear();
					mChannelInfoToDisplay.addAll(mChannels);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	private void updateChannelList() {
		if (mIsChanged) {
			ArrayList<String> newIdsList = new ArrayList<String>();
			for (int i = 0; i < mIsCheckedArray.length; i++) {
				if (mIsCheckedArray[i]) {
					newIdsList.add(mAllChannelsIds.get(i));
				}
			}
			mCount = newIdsList.size();
			if (MyChannelsService.updateMyChannelsList(userToken, JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.DAZOO_CHANNEL_CHANNEL_ID, newIdsList))) {
				Toast.makeText(getApplicationContext(), "List of channels is updated!", Toast.LENGTH_SHORT).show();

				// clear guides
				DazooStore.getInstance().clearMyGuidesStorage();
				// update the my channels list
				MyChannelsService.getMyChannels(userToken);

				LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Consts.INTENT_EXTRA_MY_CHANNELS_CHANGED));

			} else {
				Toast.makeText(getApplicationContext(), "Error! List of channels is NOT updated!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// update channel list if user come back to the My Profile via Home Button
		updateChannelList();
	}

	@Override
	public void onBackPressed() {
		updateChannelList();
		Intent returnIntent = new Intent();
		if (mIsChanged == true) {
			setResult(Consts.INFO_UPDATE_MYCHANNELS, returnIntent);
			returnIntent.putExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, mCount);
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

	private boolean getUserMyChannelsIdsJSON() {
		if (MyChannelsService.getMyChannels(userToken)) {
			myChannelIds = DazooStore.getInstance().getMyChannelIds();

			Log.d(TAG, "MY CHANNELS IDS IN THE DAZOO STORE: " + myChannelIds);

			for (int j = 0; j < myChannelIds.size(); j++) {
				if (mAllChannelsIds.contains(myChannelIds.get(j))) {
					Log.d(TAG,"MY CHANNEL ID: " + myChannelIds.get(j));
					int position = mAllChannelsIds.indexOf(myChannelIds.get(j));
					Log.d(TAG,"POSITION IN ALL CHANNELS: " + position);
					Log.d(TAG,"ALL CHANNEL AT POSITION:" + mAllChannelsIds.get(position));
					
					mIsCheckedArray[mAllChannelsIds.indexOf(myChannelIds.get(j))] = true;
				}
			}
			
			return true;
		} else {
			Toast.makeText(getApplicationContext(), "List of MY CHANNELS cannot be read", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "List of Channels cannot be read");
			return false;
		}
	}

	@Override
	public void setValues(int count) {
		mChannelCountTv.setText(" " + String.valueOf(count));
		mIsChanged = true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			updateChannelList();
			Intent intentHome = new Intent(MyChannelsActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			updateChannelList();
			Intent intentActivity = new Intent(MyChannelsActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			updateChannelList();
			Intent returnIntent = new Intent();
			if (mIsChanged == true) {
				setResult(Consts.INFO_UPDATE_MYCHANNELS, returnIntent);
				returnIntent.putExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, mCount);
			}
			finish();
			break;
		}

	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadPage() {
		// TODO Auto-generated method stub

	}
}
