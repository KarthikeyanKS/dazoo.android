package com.mitv.myprofile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.adapters.MyChannelsListAdapter;
import com.mitv.content.SSActivity;
import com.mitv.content.SSChannelPage;
import com.mitv.content.SSPageCallback;
import com.mitv.content.SSPageGetResult;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.manager.MiTVCore;
import com.mitv.model.Channel;
import com.mitv.mychannels.MyChannelsService;
import com.mitv.storage.MiTVStore;
import com.mitv.storage.MiTVStoreOperations;
import com.mitv.utilities.JSONUtilities;

public class MyChannelsActivity extends SSActivity implements MyChannelsCountInterface, OnClickListener {

	private static final String			TAG						= "MyChannelsActivity";
	private ActionBar					mActionBar;
	private boolean						isChange				= false;
	private Button						mGetMyChannelsButton, mAddToMyChannelsButton;
	private static String				userToken;
	private ListView					mListView;
	private TextView					mChannelCountTv;
	private RelativeLayout				mTabTvGuide, mTabProfile, mTabActivity;private View mTabDividerLeft, mTabDividerRight;
	private EditText					mSearchChannelInputEditText;
	private MyChannelsListAdapter		mAdapter;
	private ArrayList<Channel>			mChannels				= new ArrayList<Channel>();
	private ArrayList<String>			mCheckedChannelsIds		= new ArrayList<String>();
	private HashMap<String, Channel>	mChannelsMap			= new HashMap<String, Channel>();
	private boolean[]					mIsCheckedArray;
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
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		// styling bottom navigation tabs

		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);
		
		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		mListView = (ListView) findViewById(R.id.listview);
		mChannelCountTv = (TextView) findViewById(R.id.mychannels_header_counter_tv);
		mSearchChannelInputEditText = (EditText) findViewById(R.id.mychannels_header_search_ev);
	}

	private void populateViews() {
		mChannelsMap = MiTVStore.getInstance().getAllChannels();
		if (mChannelsMap != null && mChannelsMap.isEmpty() != true) {

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
					mAdapter = new MyChannelsListAdapter(this, mChannelInfoToDisplay, mIsCheckedArray, this, mChannelCounter, mCheckedChannelsIds);
					mListView.setAdapter(mAdapter);
				}
			} else {
				// Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show();
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
						in.hideSoftInputFromWindow(mSearchChannelInputEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
					if (search.length() > 0) {
						if (search.length() > 3) {
							search = search.substring(0, 3);
						}
						mChannelInfoToDisplay.clear();
						for (Map.Entry<String, Channel> entry : mChannelInfoMap.entrySet()) {
							String key = entry.getKey();
							Channel channel = entry.getValue();
							if (key.toLowerCase().contains(search.toLowerCase())) {
								// mark the correct channels in the global list

								mChannelInfoToDisplay.add(channel);
								mAdapter.notifyDataSetChanged();
							}
						}
					} else {
						// mark the correct channels in the global list

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
		} else {
			updateUI(REQUEST_STATUS.LOADING);
			loadPage();
		}
	}

	private void updateChannelList() {
		if (mIsChanged) {
			// ArrayList<String> newIdsList = new ArrayList<String>();
			// for (int i = 0; i < mIsCheckedArray.length; i++) {
			// if (mIsCheckedArray[i]) {
			// newIdsList.add(mAllChannelsIds.get(i));
			// }
			// }
			// mCount = newIdsList.size();
			// if (MyChannelsService.updateMyChannelsList(userToken, JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.CHANNEL_CHANNEL_ID, newIdsList))) {

			// do not allow dublications in the list of channels
			HashSet myCheckedChannelsSet = new HashSet();
			myCheckedChannelsSet.addAll(mCheckedChannelsIds);
			mCheckedChannelsIds.clear();
			mCheckedChannelsIds.addAll(myCheckedChannelsSet);

			mCount = mCheckedChannelsIds.size();
			if (MyChannelsService.updateMyChannelsList(userToken, JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.CHANNEL_CHANNEL_ID, mCheckedChannelsIds))) {

				// clear guides
				MiTVStore.getInstance().clearMyGuidesStorage();
				// update the my channels list
				MyChannelsService.getMyChannels(userToken);

				LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Consts.INTENT_EXTRA_MY_CHANNELS_CHANGED));

			} else {
				Log.d(TAG, "Channel list is not updated!");
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
		
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private boolean getUserMyChannelsIdsJSON() {
		if (MyChannelsService.getMyChannels(userToken)) {
			myChannelIds = MiTVStore.getInstance().getMyChannelIds();
			mCheckedChannelsIds = myChannelIds;

			for (int j = 0; j < myChannelIds.size(); j++) {
				if (mAllChannelsIds.contains(myChannelIds.get(j))) {
					mIsCheckedArray[mAllChannelsIds.indexOf(myChannelIds.get(j))] = true;
				}
			}

			return true;
		} else {
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
		case R.id.tab_tv_guide:
			updateChannelList();
			Intent intentHome = new Intent(MyChannelsActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			updateChannelList();
			Intent intentActivity = new Intent(MyChannelsActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
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
		if (super.requestIsSuccesfull(status)) {
			Log.d(TAG, "SUCCESSFUL");
			populateViews();
		}
	}

	@Override
	protected void loadPage() {
		SSChannelPage.getInstance().getPage(Consts.URL_CHANNELS_ALL, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {
				ArrayList<Channel> mAllChannels = SSChannelPage.getInstance().getChannels();

				if (mAllChannels != null && mAllChannels.isEmpty() != true) {
					Log.d(TAG, "ALL Channels: " + mAllChannels.size());
					// store the list channels (used in the my profile/my guide)
					MiTVStoreOperations.saveAllChannels(mAllChannels);

					// get info only about user channels
					if (MyChannelsService.getMyChannels(userToken)) {
						MiTVCore.mIsMyChannels = true;
					}
					MiTVCore.mIsAllChannels = true;
					updateUI(REQUEST_STATUS.SUCCESSFUL);
				}
			}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		// update the channel list on Up/Home button press too
		case android.R.id.home:
			updateChannelList();
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (mIsChanged == true) {
				setResult(Consts.INFO_UPDATE_MYCHANNELS, upIntent);
				upIntent.putExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, mCount);
			}
			NavUtils.navigateUpTo(this, upIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
