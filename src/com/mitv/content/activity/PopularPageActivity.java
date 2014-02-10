package com.mitv.content.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.adapters.PopularListAdapter;
import com.mitv.content.SSActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.manager.ContentParser;
import com.mitv.manager.LoginManager;
import com.mitv.model.Broadcast;
import com.mitv.myprofile.MyProfileActivity;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.NetworkUtils;

public class PopularPageActivity extends SSActivity implements OnClickListener {

	private static final String		TAG					= "PopularPageActivity";
	private String					token;
	private RelativeLayout			mTabTvGuide, mTabProfile, mTabActivity;
	private View					mTabDividerLeft, mTabDividerRight;
	private TextView				mSignInTv;
	private ActionBar				mActionBar;
	private ListView				mListView;
	private PopularListAdapter		mAdapter;
	private ArrayList<Broadcast>	mPopularBroadcasts	= new ArrayList<Broadcast>();

	// EXTENDED VIEW OF THE POPULAR BLOCK AT THE ACTIVITY PAGE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_popular_list_activity);

		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		initViews();

		super.initCallbackLayouts();
		loadPage();
	}

	private void initViews() {
		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);

		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		
		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
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
			Collections.sort(mPopularBroadcasts, new Broadcast.BroadcastComparatorByTime());
			mAdapter = new PopularListAdapter(this, token, mPopularBroadcasts);
			mListView.setAdapter(mAdapter);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);

		// check if the network connection exists
		if (!NetworkUtils.isConnectedAndHostIsReachable(this))
		{
			updateUI(REQUEST_STATUS.FAILED);
		} else {
			if (MiTVStore.getInstance().getPopularFeed().size() > 0) {
				Log.d(TAG, "RESTORED POPULAR");
				mPopularBroadcasts = MiTVStore.getInstance().getPopularFeed();
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			} else {
				GetPopularTask getPopularTask = new GetPopularTask();
				getPopularTask.execute();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(PopularPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentActivity = new Intent(PopularPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
			// tab to profile page
			Intent intentMe = new Intent(PopularPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			
			break;
		}
	}

	class GetPopularTask extends AsyncTask<Void, Void, Boolean> {

		protected void onPostExecute(Boolean result) {
			if (result) {
				if (mPopularBroadcasts != null) {
					if (mPopularBroadcasts.isEmpty() != true) {
						MiTVStore.getInstance().setPopularFeed(mPopularBroadcasts);
						updateUI(REQUEST_STATUS.SUCCESSFUL);
					} else {
						updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
					}
				}
			} else {
				updateUI(REQUEST_STATUS.FAILED);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			try {
				HttpClient client = new DefaultHttpClient();

				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);

				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

				List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
				urlParams.add(new BasicNameValuePair(Consts.API_POPULAR_COUNT, String.valueOf(Consts.API_POPULAR_COUNT_DEFAULT)));

				URI uri = new URI(Consts.URL_POPULAR + "?" + URLEncodedUtils.format(urlParams, "utf-8"));

				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
				HttpResponse response = httpClient.execute(httpGet);

				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "GOOD RESPONSE");
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					String jsonString = sb.toString();
					if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
						JSONArray jsonArray = new JSONArray(jsonString);
						if (jsonArray != null) {
							int size = jsonArray.length();
							for (int i = 0; i < size; i++) {
								mPopularBroadcasts.add(ContentParser.parseBroadcast(jsonArray.getJSONObject(i)));
							}
							return true;
						}
					}
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Invalid");
					LoginManager.forceLogin();
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Missing token");
					LoginManager.forceLogin();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}
