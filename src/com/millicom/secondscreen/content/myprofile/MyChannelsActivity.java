package com.millicom.secondscreen.content.myprofile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.SecondScreenApplication;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyChannelsActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG			= "ChannelsActivity";
	private ActionBar			mActionBar;
	private boolean				isChange	= false;
	private Button				mGetMyChannelsButton, mAddToMyChannelsButton;
	private String				userToken;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mychannels_activity);
		initActionBar();
		initLayout();

		userToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
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

	private void initLayout() {
		mAddToMyChannelsButton = (Button) findViewById(R.id.mychannels_update_channels_button);
		mAddToMyChannelsButton.setOnClickListener(this);
		mGetMyChannelsButton = (Button) findViewById(R.id.mychannels_get_channels_button);
		mGetMyChannelsButton.setOnClickListener(this);
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

				Log.d(TAG, "TOKEN" + params[0]);
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

	private void getUserMyChannelsIdsJSON() {
		GetMyChannelsTask getMyChannelsTask = new GetMyChannelsTask();
		try {
			String responseStr = getMyChannelsTask.execute(userToken).get();
			if (responseStr != null && responseStr.isEmpty() != true && responseStr != Consts.ERROR_STRING) {
				// the extra check for ERROR_STRING was added to distinguish between empty response (there are no stored channels to this user) and empty response in case of error
				Log.d(TAG, "My Channels: GET: " + responseStr);

				// save the list of channels as json-string
				((SecondScreenApplication) getApplicationContext()).setUserMyChannelsIdsasJSON(responseStr);

			} else {
				Toast.makeText(getApplicationContext(), "List of MY CHANNELS cannot be read", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "List of Channels cannot be read");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
			// } catch (JSONException e) {
			// e.printStackTrace();
		}
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.mychannels_get_channels_button:

			// check if we have registered user and user token is valid
			if (userToken != null && userToken.isEmpty() != true) {
				// get user channels
				getUserMyChannelsIdsJSON();
			} else {
				Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Login action is required to be done before this");
			}

			break;
		case R.id.mychannels_update_channels_button:
			isChange = !isChange;

			// add channels to the list
			if (isChange == true) {
				// add channels to the user account "My channels" database
				if (userToken != null && userToken.isEmpty() != true) {
					// check if we have fetched the userChannelsIds
					String myChannelsIdsJSON = ((SecondScreenApplication) getApplicationContext()).getUserMyChannelsIdsJSON();
					if (myChannelsIdsJSON != null && myChannelsIdsJSON.isEmpty() != true) {

						LinkedHashSet<String> newMyChannelsList  = JSONUtilities.stringWithJSONtoOrderedSet(myChannelsIdsJSON);
						newMyChannelsList.add("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7");
						newMyChannelsList.add("ba09d322-6164-4457-89c0-64520214ac30");

						String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, newMyChannelsList);
						updateUserMyChannels(channelsJSON);
					} else {
						// fetch the list of channels first
						getUserMyChannelsIdsJSON();

						String json = ((SecondScreenApplication) getApplicationContext()).getUserMyChannelsIdsJSON();
						// if (json != Consts.ERROR_STRING) {
						// if (json != null && json.isEmpty() != true) {

						LinkedHashSet<String> newMyChannelsList = JSONUtilities.stringWithJSONtoOrderedSet(json);
						newMyChannelsList.add("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7");
						newMyChannelsList.add("ba09d322-6164-4457-89c0-64520214ac30");

						String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, newMyChannelsList);
						updateUserMyChannels(channelsJSON);
						// }
						// }
					}
				} else {
					Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Login action is required to be done before this");
				}
				
				// remove channels from the list
			} else if (isChange == false) {
				// fetch the list of channels first
				getUserMyChannelsIdsJSON();

				String json = ((SecondScreenApplication) getApplicationContext()).getUserMyChannelsIdsJSON();
				// if (json != Consts.ERROR_STRING) {
				// if (json != null && json.isEmpty() != true) {
				
				LinkedHashSet<String> newMyChannelsList = JSONUtilities.stringWithJSONtoOrderedSet(json);
				newMyChannelsList.remove("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7");

				String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, newMyChannelsList);
				updateUserMyChannels(channelsJSON);
				
				// get channels to make a check
				getUserMyChannelsIdsJSON();
			}

			break;
		}
	}
}
