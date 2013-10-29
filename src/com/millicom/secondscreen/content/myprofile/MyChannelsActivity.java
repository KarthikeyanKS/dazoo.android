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
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.MyChannelsListAdapter;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.manager.ContentParser;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.SecondScreenApplication;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyChannelsActivity extends ActionBarActivity implements MyChannelsCountInterface, OnClickListener {

	private static final String		TAG						= "MyChannelsActivity";
	private ActionBar				mActionBar;
	private boolean					isChange				= false;
	private Button					mGetMyChannelsButton, mAddToMyChannelsButton;
	private String					userToken;
	private ListView				mListView;
	private TextView				mChannelCountTv, mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;;
	private EditText				mSearchChannelInputEditText;
	private MyChannelsListAdapter	mAdapter;
	private ArrayList<Channel>		mChannels;
	private boolean[]				mIsCheckedArray;
	private View					mTabSelectorContainerView;
	private int						mChannelCounter			= 0;

	private ArrayList<Channel>		mChannelInfoToDisplay	= new ArrayList<Channel>();
	private Map<String, Channel>	mChannelInfoMap			= new HashMap<String, Channel>();

	private ArrayList<String>		myChannelIds			= new ArrayList<String>();
	private ArrayList<String>		mAllChannelsIds			= new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mychannels_activity);
		userToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		initLayout();
		populateViews();
	}
	
	private void initLayout() {
		
		// for test purposes only
		//ArrayList<String> a = new ArrayList<String>();
		//a.add("7720eff8-2eaf-444c-a032-fefee7be5a02");
		//a.add("79d66b89-4b0e-4223-83bb-79fa5d59971d");
		//a.add("33b97d3b-0251-4e8a-9a07-ecd2a0c7dfd4");
		//updateUserMyChannels(JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.DAZOO_CHANNEL_CHANNEL_ID, a));
		
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.my_channels));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		mActionBar.setTitle(getResources().getString(R.string.my_channels));

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
		mChannelCountTv = (TextView) findViewById(R.id.mychannels_header_counter_tv);
		mSearchChannelInputEditText = (EditText) findViewById(R.id.mychannels_header_search_ev);
	}

	private void populateViews() {
		mChannels = ((SecondScreenApplication) getApplicationContext()).getChannels();

		for (int i = 0; i < mChannels.size(); i++) {
			mChannelInfoMap.put(mChannels.get(i).getName().toLowerCase(Locale.getDefault()), mChannels.get(i));
			mChannelInfoToDisplay.add(mChannels.get(i));
			mAllChannelsIds.add(mChannels.get(i).getChannelId());
			Log.d(TAG,"ID: " + mChannels.get(i).getChannelId());
		}

		mIsCheckedArray = new boolean[mChannels.size()];

		// if (userToken != null && userToken.isEmpty() != true) {
		if (userToken != null && TextUtils.isEmpty(userToken) != true) {
			// get user channels
			if (getUserMyChannelsIdsJSON()) {

				mChannelCounter = myChannelIds.size();
				mChannelCountTv.setText(" " + String.valueOf(mChannelCounter));
				
				mAdapter = new MyChannelsListAdapter(this, mChannelInfoToDisplay, mIsCheckedArray, this, mChannelCounter);

				mListView.setAdapter(mAdapter);
				mListView.setTextFilterEnabled(true);
			}
		} else {
			Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Login action is required to be done before this");
		}

		mSearchChannelInputEditText.addTextChangedListener(new TextWatcher() {
			@SuppressLint("DefaultLocale")
			@Override
			public void afterTextChanged(Editable s) {
				String search = s.toString();
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
		
		
		//SparseBooleanArray checkedItemPositions = mListView.getCheckedItemPositions();
		//int count = 0;
		//for (int i = 0, ei = checkedItemPositions.size(); i < ei; i++) {
		 //   if (checkedItemPositions.valueAt(i)) {
		  //      count++;
		   // }
		//} 

	}

	@Override
	public void onBackPressed() {
		ArrayList<String> newIdsList = new ArrayList<String>();
		for(int i=0; i<mIsCheckedArray.length; i++){
			if (mIsCheckedArray[i]){
				newIdsList.add(mAllChannelsIds.get(i));
			}
		}
		updateUserMyChannels(JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.DAZOO_CHANNEL_CHANNEL_ID,  newIdsList));
		
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	// fetch the "My channels" of the logged in user
	private class GetMyChannelsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet();
				httpGet.setHeader("Authorization", "Bearer " + params[0]);

				httpGet.setURI(new URI(Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL));

				HttpResponse response = httpClient.execute(httpGet);
				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					Log.d(TAG, "Good response on GET ");
					return sb.toString();
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get my channels: Invalid token");
					return Consts.ERROR_STRING;
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get my channels: Missing token");
					return Consts.ERROR_STRING;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return Consts.ERROR_STRING;
		}
	}

	// add the channel to the "My channel"
	private class UpdateMyChannelsTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL);
				httpPost.setHeader("Authorization", "Bearer " + params[0]);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "application/json");
				StringEntity jsonEntity = new StringEntity(params[1]);
				httpPost.setEntity(jsonEntity);

				HttpResponse response = client.execute(httpPost);

				Log.d(TAG, "" + response.getStatusLine().getStatusCode());
				if (Consts.GOOD_RESPONSE_CHANNELS_ARE_ADDED == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Update MY CHANNELS: SUCCESS");
					return true;
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Update MY CHANNELS: Invalid token");
					return false;
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Update MY CHANNELS: Missing token");
					return false;
				} else {
					Log.d(TAG, "Error, but not identified");
				}
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			}
			return false;
		}
	}

	private boolean getUserMyChannelsIdsJSON() {
		GetMyChannelsTask getMyChannelsTask = new GetMyChannelsTask();
		try {
			String responseStr = getMyChannelsTask.execute(userToken).get();

			// if (responseStr != null && responseStr.isEmpty() != true && responseStr != Consts.ERROR_STRING) {
			if (responseStr != null && TextUtils.isEmpty(responseStr) != true && responseStr != Consts.ERROR_STRING) {
				// the extra check for ERROR_STRING was added to distinguish between empty response (there are no stored channels to this user) and empty response in case of error

				myChannelIds = ContentParser.parseChannelIds(new JSONArray(responseStr));
				for (int i = 0; i < mAllChannelsIds.size(); i++) {
					if (myChannelIds.contains(mAllChannelsIds.get(i))) {
						mIsCheckedArray[i] = true;
					}
				}

				// save the list of channels as json-string
				((SecondScreenApplication) getApplicationContext()).setUserMyChannelsIdsasJSON(responseStr);
				return true;

			} else {
				Toast.makeText(getApplicationContext(), "List of MY CHANNELS cannot be read", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "List of Channels cannot be read");
				return false;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void updateUserMyChannels(String channelsJSON) {

		UpdateMyChannelsTask addChannelToMyChannelsTask = new UpdateMyChannelsTask();

		try {
			boolean isAdded = addChannelToMyChannelsTask.execute(userToken, channelsJSON).get();
			if (isAdded == true) {
				Toast.makeText(getApplicationContext(), "MY CHANNELS list is updated", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Channels are updated!");
			} else {
				Toast.makeText(getApplicationContext(), "Error! MY CHANNELS are not updated!", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Error! MY CHANNELS are not updated");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setValues(int count) {
		mChannelCountTv.setText(" " + String.valueOf(count));
	}

	@Override
	public void onClick(View arg0) {
		// Click listener for the bottom tabs
		
	}

	/*
	 * @Override public void onClick(View view) { switch (view.getId()) { case R.id.mychannels_get_channels_button:
	 * 
	 * // check if we have registered user and user token is valid // if (userToken != null && userToken.isEmpty() != true) { if (userToken != null && TextUtils.isEmpty(userToken) != true) { // get user channels getUserMyChannelsIdsJSON(); } else { Toast.makeText(getApplicationContext(),
	 * "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show(); Log.d(TAG, "Login action is required to be done before this"); }
	 * 
	 * break; case R.id.mychannels_update_channels_button: isChange = !isChange;
	 * 
	 * // add channels to the list if (isChange == true) { // add channels to the user account "My channels" database // if (userToken != null && userToken.isEmpty() != true) { if (userToken != null && TextUtils.isEmpty(userToken) != true) { // check if we have fetched the userChannelsIds String
	 * myChannelsIdsJSON = ((SecondScreenApplication) getApplicationContext()).getUserMyChannelsIdsJSON(); // if (myChannelsIdsJSON != null && myChannelsIdsJSON.isEmpty() != true) { if (myChannelsIdsJSON != null && TextUtils.isEmpty(myChannelsIdsJSON) != true) {
	 * 
	 * LinkedHashSet<String> newMyChannelsList = JSONUtilities.stringWithJSONtoOrderedSet(myChannelsIdsJSON); newMyChannelsList.add("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7"); newMyChannelsList.add("ba09d322-6164-4457-89c0-64520214ac30");
	 * 
	 * String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, newMyChannelsList); updateUserMyChannels(channelsJSON); } else { // fetch the list of channels first getUserMyChannelsIdsJSON();
	 * 
	 * String json = ((SecondScreenApplication) getApplicationContext()).getUserMyChannelsIdsJSON(); // if (json != Consts.ERROR_STRING) { // if (json != null && json.isEmpty() != true) {
	 * 
	 * LinkedHashSet<String> newMyChannelsList = JSONUtilities.stringWithJSONtoOrderedSet(json); newMyChannelsList.add("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7"); newMyChannelsList.add("ba09d322-6164-4457-89c0-64520214ac30");
	 * 
	 * String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, newMyChannelsList); updateUserMyChannels(channelsJSON); // } // } } } else { Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)",
	 * Toast.LENGTH_SHORT).show(); Log.d(TAG, "Login action is required to be done before this"); }
	 * 
	 * // remove channels from the list } else if (isChange == false) { // fetch the list of channels first getUserMyChannelsIdsJSON();
	 * 
	 * String json = ((SecondScreenApplication) getApplicationContext()).getUserMyChannelsIdsJSON(); // if (json != Consts.ERROR_STRING) { // if (json != null && json.isEmpty() != true) {
	 * 
	 * LinkedHashSet<String> newMyChannelsList = JSONUtilities.stringWithJSONtoOrderedSet(json); newMyChannelsList.remove("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7");
	 * 
	 * String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, newMyChannelsList); updateUserMyChannels(channelsJSON);
	 * 
	 * // get channels to make a check getUserMyChannelsIdsJSON(); }
	 * 
	 * break; } }
	 */
}
