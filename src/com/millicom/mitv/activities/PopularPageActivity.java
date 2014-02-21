package com.millicom.mitv.activities;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.PopularListAdapter;

public class PopularPageActivity extends BaseActivity implements OnClickListener, ActivityCallbackListener {

	@SuppressWarnings("unused")
	private static final String TAG = PopularPageActivity.class.getName();
	
//	private String					token;
	private RelativeLayout			mTabTvGuide, mTabProfile, mTabActivity;
	private View					mTabDividerLeft, mTabDividerRight;
	private TextView				mSignInTv;
	private ActionBar				mActionBar;
	private ListView				mListView;
	private PopularListAdapter		mAdapter;
	private ArrayList<TVBroadcastWithChannelInfo>	mPopularBroadcasts;

	// EXTENDED VIEW OF THE POPULAR BLOCK AT THE ACTIVITY PAGE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_popular_list_activity);

		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

//		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		initViews();

		super.initCallbackLayouts();
		
		/* Fetch popular broadcasts */
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
			Collections.sort(mPopularBroadcasts, new TVBroadcast.BroadcastComparatorByTime());
			mAdapter = new PopularListAdapter(this, mPopularBroadcasts);
			mListView.setAdapter(mAdapter);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) {
		if(fetchRequestResult.wasSuccessful()) {
			mPopularBroadcasts = ContentManager.sharedInstance().getFromStoragePopularBroadcasts();
			updateUI(REQUEST_STATUS.SUCCESSFUL);
		} else {
			//TODO handle this
			updateUI(REQUEST_STATUS.FAILED);
		}
	}
	
	@Override
	protected void loadPage() 
	{
		updateUI(REQUEST_STATUS.LOADING);
		ContentManager.sharedInstance().getElseFetchFromServicePopularBroadcasts(this, false);
	}
	
//	@Override
//	protected void loadPage() 
//	{
//		updateUI(REQUEST_STATUS.LOADING);
//
//		if (NetworkUtils.isConnectedAndHostIsReachable(this))
//		{
//			if (MiTVStore.getInstance().getPopularFeed().size() > 0)
//			{
//				Log.d(TAG, "RESTORED POPULAR");
//				
////				mPopularBroadcasts = MiTVStore.getInstance().getPopularFeed();
//				mPopularBroadcasts = ContentManager.sharedInstance().getFromStoragePo
//				
//				updateUI(REQUEST_STATUS.SUCCESSFUL);
//			} 
//			else
//			{
//				GetPopularTask getPopularTask = new GetPopularTask();
//				
//				getPopularTask.execute();
//			}
//		} 
//		else 
//		{
//			updateUI(REQUEST_STATUS.FAILED);
//		}
//	}

	
	
	@Override
	public void onBackPressed() 
	{
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



//	class GetPopularTask extends AsyncTask<Void, Void, Boolean> {
//
//		protected void onPostExecute(Boolean result) {
//			if (result) {
//				if (mPopularBroadcasts != null) {
//					if (mPopularBroadcasts.isEmpty() != true) {
//						MiTVStore.getInstance().setPopularFeed(mPopularBroadcasts);
//						updateUI(REQUEST_STATUS.SUCCESSFUL);
//					} else {
//						updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
//					}
//				}
//			} else {
//				updateUI(REQUEST_STATUS.FAILED);
//			}
//		}
//
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			boolean result = false;
//			try {
//				HttpClient client = new DefaultHttpClient();
//
//				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//				SchemeRegistry registry = new SchemeRegistry();
//				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//
//				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//				socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//				registry.register(new Scheme("https", socketFactory, 443));
//				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
//
//				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
//				// Set verifier
//				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//
//				List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
//				urlParams.add(new BasicNameValuePair(Consts.API_POPULAR_COUNT, String.valueOf(Consts.API_POPULAR_COUNT_DEFAULT)));
//
//				URI uri = new URI(Consts.URL_POPULAR + "?" + URLEncodedUtils.format(urlParams, "utf-8"));
//
//				HttpGet httpGet = new HttpGet(uri);
//				httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
//				HttpResponse response = httpClient.execute(httpGet);
//
//				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
//					Log.d(TAG, "GOOD RESPONSE");
//					HttpEntity entityHttp = response.getEntity();
//					InputStream inputStream = entityHttp.getContent();
//					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//					StringBuilder sb = new StringBuilder();
//					String line = null;
//					while ((line = reader.readLine()) != null) {
//						sb.append(line + "\n");
//					}
//					inputStream.close();
//					String jsonString = sb.toString();
//					if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
//						JSONArray jsonArray = new JSONArray(jsonString);
//						if (jsonArray != null) {
//							int size = jsonArray.length();
//							for (int i = 0; i < size; i++) {
//								mPopularBroadcasts.add(ContentParser.parseBroadcast(jsonArray.getJSONObject(i)));
//							}
//							return true;
//						}
//					}
//				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
//					Log.d(TAG, "Get Activity Feed: Invalid");
//					ApiClient.forceLogin();
//				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
//					Log.d(TAG, "Get Activity Feed: Missing token");
//					ApiClient.forceLogin();
//				}
//
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
//	}
}
