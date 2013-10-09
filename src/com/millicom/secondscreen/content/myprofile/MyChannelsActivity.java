package com.millicom.secondscreen.content.myprofile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.SecondScreenApplication;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

public class MyChannelsActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG			= "ChannelsActivity";
	private ActionBar			mActionBar;
	private boolean				isChange	= false;
	private Button				mGetMyChannelsButton, mAddToMyChannelsButton, mRemoveFromMyChannelsButton;
	private ArrayList<String>	mMyChannelsIds;
	private ArrayList<String> mChannelIdsListToBeAdded;
	private String userToken;

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
		mAddToMyChannelsButton = (Button) findViewById(R.id.mychannels_add_channels_button);
		mAddToMyChannelsButton.setOnClickListener(this);
		mRemoveFromMyChannelsButton = (Button) findViewById(R.id.mychannels_remove_channels_button);
		mRemoveFromMyChannelsButton.setOnClickListener(this);
		mGetMyChannelsButton = (Button) findViewById(R.id.mychannels_get_channels_button);
		mGetMyChannelsButton.setOnClickListener(this);
	}

	// fetch the "My channels" of the logged in user
	private class GetMyChannelsTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet();
				String userToken = params[0];
				String urlWithToken = Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL + Consts.REQUEST_PARAMETER_SEPARATOR;
				List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_TOKEN, userToken));

				String urlParamsString = URLEncodedUtils.format(urlParams, "utf-8");
				urlWithToken += urlParamsString;

				Log.d(TAG, "Get My Channels request url:" + urlWithToken);

				httpGet.setURI(new URI(urlWithToken));
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					// JSONObject jObj = new JSONObject(sb.toString());
					JSONArray jArray = new JSONArray(sb.toString());
					if (jArray != null) {
						mMyChannelsIds = new ArrayList<String>();
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject channelIdJSON = jArray.getJSONObject(i);
							mMyChannelsIds.add(channelIdJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID));
							Log.d(TAG, "ChannelId json: " + channelIdJSON.toString());
						}
					}
					return true;
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}
	}

	// add the channel to the "My channel"
	private class AddChannelToMyChannelsTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

				HttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme(Consts.MILLICON_SECONDSCREEN_HTTP_SCHEME, socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL);
				httpPost.setHeader("Content-type", "application/json; charset=utf-8");
				httpPost.setHeader("Accept", "application/json");

				String token = params[0];
				String myChannelsJSON = params[1];

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_TOKEN, token));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

				StringEntity jsonEntity = new StringEntity(myChannelsJSON);
				httpPost.setEntity(jsonEntity);

				HttpResponse response = httpClient.execute(httpPost);

				return response.getStatusLine().getStatusCode();
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			}
			return 0;
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.mychannels_get_channels_button:
			
			// check if we have registered user and user token is valid
			if (userToken != null && userToken.isEmpty() != true) {
				// get user channels
				GetMyChannelsTask getMyChannelsTask = new GetMyChannelsTask();
				getMyChannelsTask.execute(userToken);
			}
			
			break;
		case R.id.mychannels_add_channels_button:

			// add channels to the user account "My channels" database
			if(userToken != null && userToken.isEmpty() != true){
				AddChannelToMyChannelsTask addChannelToMyChannelsTask = new AddChannelToMyChannelsTask();
				
				//fake list of channelIds to be added
				mChannelIdsListToBeAdded = new ArrayList<String>();
				mChannelIdsListToBeAdded.add("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7");
				mChannelIdsListToBeAdded.add("ba09d322-6164-4457-89c0-64520214ac30");
				
				String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, mChannelIdsListToBeAdded);
				addChannelToMyChannelsTask.execute(userToken, channelsJSON);
			}
			
			break;
		case R.id.mychannels_remove_channels_button:
			break;
		}
	}
}
