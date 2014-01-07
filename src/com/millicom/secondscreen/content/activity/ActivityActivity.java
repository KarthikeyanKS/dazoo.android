package com.millicom.secondscreen.content.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
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

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.ActivityFeedAdapter;
import com.millicom.secondscreen.authentication.DazooLoginActivity;
import com.millicom.secondscreen.authentication.FacebookLoginActivity;
import com.millicom.secondscreen.authentication.SignInActivity;
import com.millicom.secondscreen.authentication.SignUpActivity;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;
import com.millicom.secondscreen.http.NetworkUtils;
import com.millicom.secondscreen.manager.ContentParser;
import com.millicom.secondscreen.manager.LoginManager;
import com.millicom.secondscreen.storage.DazooStore;

public class ActivityActivity extends SSActivity implements OnClickListener {

	private static final String	TAG				= "ActivityActivity";
	private RelativeLayout		mTabTvGuide, mTabProfile, mTabActivity, mSigninContainer, mFacebookContainer, mSignUpContainer, mTabDividerLeftContainer, mTabDividerRightContainer;
	private TextView			mSignInTv, mGreetingTv;
	private Button				mCheckPopularBtn, mLoginBtn;
	private ActionBar			mActionBar;
	private ArrayList<FeedItem>	activityFeed	= new ArrayList<FeedItem>();
	private String				token;
	private Boolean				mIsLoggenIn		= false, mNoMoreItems = false, mNoTask = true;
	private int					mStartIndex		= 0, mStep = 10, mNextStep = 5, mEndIndex = 0;
	private ListView			mListView;
	private RelativeLayout		mListFooter;
	private ActivityFeedAdapter	mAdapter;
	private Activity			mActivity;
	private RelativeLayout		mContainer;
	private View				mListFooterView;
	private int					mRequestAge, mRequestMaxAge, mNextRequestTime;
	
	public static Toast 		toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Google Analytics tracking */
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		mActivity = this;

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		if (token != null && TextUtils.isEmpty(token) != true) {
			setContentView(R.layout.layout_activity_activity);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			mIsLoggenIn = true;
			initStandardViews();
			initFeedViews();
			super.initCallbackLayouts();

			if (!NetworkUtils.checkConnection(this)) {
				updateUI(REQUEST_STATUS.FAILED);
			} else {
				loadPage();
			}

		} else {
			setContentView(R.layout.layout_activity_not_logged_in_activity);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			initStandardViews();
			initInactiveViews();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();

		/* Google Analytics tracking */
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	private void initStandardViews() {
		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);

		mTabDividerLeftContainer = (RelativeLayout) findViewById(R.id.tab_left_divider_container);
		mTabDividerRightContainer = (RelativeLayout) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeftContainer.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		mTabDividerRightContainer.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

		mActionBar = getSupportActionBar();

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		mActionBar.setTitle(getResources().getString(R.string.activity_title));

	}

	private void initFeedViews() {
		mListView = (ListView) findViewById(R.id.activity_listview);
		// mListFooter = (RelativeLayout) findViewById(R.id.activity_listview_footer);

		LayoutInflater inflater = getLayoutInflater();
		mListFooterView = (View) inflater.inflate(R.layout.row_loading_footerview, null);
		mListView.addFooterView(mListFooterView);
		mListFooterView.setVisibility(View.GONE);
	}

	private void initInactiveViews() {
		// sign in
		mSigninContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_signin_container);
		mFacebookContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_facebook_container);
		mFacebookContainer.setOnClickListener(this);

		mSignUpContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_signup_email_container);
		mSignUpContainer.setOnClickListener(this);

		mLoginBtn = (Button) findViewById(R.id.activity_not_logged_in_login_btn);
		mLoginBtn.setOnClickListener(this);
	}

	private void setAdapter() {
		if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(activityFeed.get(0).getItemType())) {
			View header = getLayoutInflater().inflate(R.layout.block_feed_no_likes, null);
			mListView.addHeaderView(header);

			mGreetingTv = (TextView) findViewById(R.id.block_feed_no_likes_greeting_tv);
			mCheckPopularBtn = (Button) findViewById(R.id.block_feed_no_likes_btn);
			mCheckPopularBtn.setOnClickListener(this);

			mGreetingTv.setText(mActivity.getResources().getString(R.string.hello) + " " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName() + " "
					+ ((SecondScreenApplication) getApplicationContext()).getUserLastName() + ",");
		}

		mListView.setOnScrollListener(mOnScrollListener);
		mAdapter = new ActivityFeedAdapter(this, activityFeed, token);
		mListView.setAdapter(mAdapter);
		mListView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		// check if the network connection exists
		if (!NetworkUtils.checkConnection(this)) {
			updateUI(REQUEST_STATUS.FAILED);
		} else {
			if (DazooStore.getInstance().getActivityFeed().size() > 0) {
				Log.d(TAG, "READ FROM STORAGE");
				activityFeed = DazooStore.getInstance().getActivityFeed();
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			} else {
				new GetFeedTask().execute();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			new SetAdapterTask().execute();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mAdapter.notifyDataSetChanged();
	}

	protected boolean activityIsActive() {
		return mActivity != null && !mActivity.isFinishing();
	}

	class SetAdapterTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			if (activityIsActive()) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setAdapter();
					}
				});
			}
			return null;
		}
	}

	OnScrollListener	mOnScrollListener	= new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			if (totalItemCount > 0) {

				// If scrolling past bottom and there is a next page of products to fetch
				if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !mNoMoreItems && mNoTask) {
					Log.d(TAG, "reached last item");
					// Show the scroll spinner
					showScrollSpinner(true);

					if (mNoTask) {
						GetFeedMoreTask getFeedMoreTask = new GetFeedMoreTask();
						getFeedMoreTask.execute();
					}
					mNoTask = false;
				} else {

					// Hide the scroll spinner
					showScrollSpinner(false);
				}
			} else {
				// Hide the scroll spinner
				showScrollSpinner(false);
			}
		}
	};

	private void showScrollSpinner(boolean aShow) {
		if (mListFooterView != null) {
			// Show/hide the scroll spinner
			mListFooterView.setVisibility(aShow ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		// hide search for beta release
		// case R.id.menu_search:
		// Intent toSearchPage = new Intent(ActivityActivity.this, SearchPageActivity.class);
		// startActivity(toSearchPage);
		// 
		// return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_homepage, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(ActivityActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.show_activity:
			// we are here: do nothing
			break;
		case R.id.show_me:
			// tab to activity page
			Intent intentMe = new Intent(ActivityActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			
			break;
		case R.id.activity_not_logged_in_facebook_container:
			// facebook sign in
			Intent intentFacebookSignIn = new Intent(ActivityActivity.this, FacebookLoginActivity.class);
			startActivity(intentFacebookSignIn);
			
			break;
		case R.id.activity_not_logged_in_signup_email_container:
			Intent intentSignUp = new Intent(ActivityActivity.this, SignUpActivity.class);
			startActivity(intentSignUp);
			
			break;
		case R.id.activity_not_logged_in_login_btn:
			Intent intentLogin = new Intent(ActivityActivity.this, DazooLoginActivity.class);
			startActivity(intentLogin);
			
			break;
		case R.id.block_feed_no_likes_btn:
			Intent checkPopular = new Intent(ActivityActivity.this, PopularPageActivity.class);
			startActivity(checkPopular);
			
			break;
		}
	}

	class GetFeedMoreTask extends AsyncTask<Void, Void, Boolean> {

		ArrayList<FeedItem>	moreFeedItems	= new ArrayList<FeedItem>();

		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "result: " + result);
			if (result) {
				DazooStore.getInstance().addItemsToActivityFeed(moreFeedItems);
				mAdapter.addItems(moreFeedItems);
				mStartIndex = mStartIndex + mNextStep;
			} else {
				if (mNoMoreItems) {
					mListView.removeFooterView(mListFooterView);
				}
			}
			moreFeedItems.clear();
			mNoTask = true;
			showScrollSpinner(false);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
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
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_SKIP, String.valueOf(mStartIndex)));
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_LIMIT, String.valueOf(mNextStep)));

				URI uri = new URI(Consts.MILLICOM_SECONDSCREEN_ACTIVITY_FEED_URL + "?" + URLEncodedUtils.format(urlParams, "utf-8"));

				Log.d(TAG, "mStartIndex: " + String.valueOf(mStartIndex) + " mNextStep: " + String.valueOf(mNextStep));
				Log.d(TAG, "Feed more items: " + uri.toString());

				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("Authorization", "Bearer " + token);
				// header to accept the json in a correct encoding
				httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
				HttpResponse response = httpClient.execute(httpGet);

				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "GOOD RESPONSE");

					// comment for beta
					// if (response.getFirstHeader("Age") != null) {
					// mRequestAge = Integer.valueOf(response.getFirstHeader("Age").getValue());
					// }
					// if (response.getFirstHeader("Cache-Control").getValue().substring(8) != null) {
					// mRequestMaxAge = Integer.valueOf(response.getFirstHeader("Cache-Control").getValue().substring(8));
					// }
					// mNextRequestTime = mRequestMaxAge - mRequestAge;

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
						JSONArray feedListJsonArray;
						try {
							feedListJsonArray = new JSONArray(jsonString);
							int size = feedListJsonArray.length();
							Log.d(TAG, "FEED MORE ITEMS SIZE: " + String.valueOf(size));

							if (size == 0) {
								mNoMoreItems = true;
								return result;
							}
							int endIndex = 0;
							if (mNextStep < size) endIndex = mNextStep;
							else endIndex = size;
							Log.d(TAG, "endIndex:" + endIndex + " mStartIndex: " + mStartIndex + " mStep: " + mNextStep);

							for (int i = 0; i < endIndex; i++) {
								moreFeedItems.add(ContentParser.parseFeedItem(feedListJsonArray.getJSONObject(i)));
								result = true;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Invalid");
					LoginManager.forceLogin();
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Missing token");
					LoginManager.forceLogin();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return result;
		}

	}

	private void scheduleFeedRefresh() {
		if (mNextRequestTime != 0) {

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DazooStore.getInstance().reinitializeFeed();
					activityFeed.clear();
					activityFeed = new ArrayList<FeedItem>();
					loadPage();
				}
			}, mNextRequestTime * 1000);
		}
	}

	class GetFeedTask extends AsyncTask<Void, Void, Boolean> {
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "oN POST EXECUTE");
			if (result) {
				if (activityFeed != null) {
					if (activityFeed.isEmpty() != true) {

						DazooStore.getInstance().setActivityFeed(activityFeed);

						Log.d(TAG, "//////////////");
						updateUI(REQUEST_STATUS.SUCCESSFUL);
						mStartIndex = mStartIndex + mStep;
					} else {
						Log.d(TAG, "EMPTY");
						updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
					}
				}

				// comment for beta
				// schedule the next feed update
				// scheduleFeedRefresh();

			} else {
				Log.d(TAG, "No backend response");
				updateUI(REQUEST_STATUS.BAD_REQUEST);
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
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_SKIP, String.valueOf(mStartIndex)));
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_LIMIT, String.valueOf(mStep)));

				URI uri = new URI(Consts.MILLICOM_SECONDSCREEN_ACTIVITY_FEED_URL + "?" + URLEncodedUtils.format(urlParams, "utf-8"));

				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("Authorization", "Bearer " + token);
				// header to accept the json in a correct encoding
				httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
				HttpResponse response = httpClient.execute(httpGet);

				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
					// Log.d(TAG, "" + response.getFirstHeader("Age"));
					// Log.d(TAG, "" + response.getFirstHeader("Cache-Control"));
					//
					// mRequestAge = Integer.valueOf(response.getFirstHeader("Age").getValue());
					// mRequestMaxAge = Integer.valueOf(response.getFirstHeader("Cache-Control").getValue().substring(8));
					// mNextRequestTime = mRequestMaxAge - mRequestAge;
					//
					// Log.d(TAG, "AGE: " + mRequestAge + " Max Age: " + mRequestMaxAge + " Next time: " + mNextRequestTime);

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
						JSONArray feedListJsonArray;
						try {
							feedListJsonArray = new JSONArray(jsonString);

							int size = feedListJsonArray.length();
							Log.d(TAG, "FEED ITEMS SIZE: " + String.valueOf(size));
							int endIndex = 0;
							if (mStartIndex + mStep < size) endIndex = mStartIndex + mStep;
							else endIndex = size;
							Log.d(TAG, "endIndex:" + endIndex + " mStartIndex: " + mStartIndex + " mStep: " + mStep);

							for (int i = mStartIndex; i < endIndex; i++) {
								activityFeed.add(ContentParser.parseFeedItem(feedListJsonArray.getJSONObject(i)));
								result = true;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Invalid");
					LoginManager.forceLogin();
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Missing token");
					LoginManager.forceLogin();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}
