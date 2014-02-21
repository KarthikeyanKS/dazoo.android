package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.authentication.FacebookLoginActivity;
import com.millicom.mitv.activities.authentication.MiTVLoginActivity;
import com.millicom.mitv.activities.authentication.SignUpWithEmailActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.gson.TVFeedItem;
import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.ActivityFeedAdapter;

public class ActivityActivity extends BaseActivity implements OnClickListener, ActivityCallbackListener, OnScrollListener {

	private static final String	TAG				= "ActivityActivity";
	private RelativeLayout		mTabTvGuide, mTabProfile, mTabActivity, mSigninContainer, mFacebookContainer, mSignUpContainer;
	private View 				mTabDividerLeft, mTabDividerRight;
	private TextView			mSignInTv, mGreetingTv;
	private Button				mCheckPopularBtn, mLoginBtn;
	private ActionBar			mActionBar;
//	private ArrayList<TVFeedItem>	activityFeed	= new ArrayList<TVFeedItem>();
	private Boolean				mNoMoreItems = false, mNoTask = true;
//	private int					mStartIndex		= 0, mStep = 10, mNextStep = 5, mEndIndex = 0;
	private ListView			mListView;
//	private RelativeLayout		mListFooter;
	private ActivityFeedAdapter	mAdapter;
	private Activity			mActivity;
//	private RelativeLayout		mContainer;
	private View				mListFooterView;
//	private int					mRequestAge, mRequestMaxAge, mNextRequestTime;
	
	public static Toast 		toast;
//	private boolean 			mIsFromLogin, mIsFromSignup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Intent intent =  getIntent();
//		if (intent.hasExtra(Consts.INTENT_EXTRA_LOG_IN_ACTION)) {
//			mIsFromLogin = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_LOG_IN_ACTION);
//		}
//		else if (intent.hasExtra(Consts.INTENT_EXTRA_SIGN_UP_ACTION)) {
//			mIsFromSignup = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_SIGN_UP_ACTION);
//		}
		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		
		mActivity = this;

		if (ContentManager.sharedInstance().isLoggedIn()) 
		{
			setContentView(R.layout.layout_activity_activity);
			
			initStandardViews();
			initFeedViews();
			
			super.initCallbackLayouts();
			
			getActivityFeedData();
			
//			if (NetworkUtils.isConnectedAndHostIsReachable(this) == false)
//			{
//				updateUI(REQUEST_STATUS.FAILED);
//				return;
//			}
			// No need for else
			
			// TODO: Check if the api is the correct one
//			boolean isAPIUpToDate = SecondScreenApplication.getInstance().checkApiVersion();
//
//			if(isAPIUpToDate == false)
//			{
//				updateUI(REQUEST_STATUS.FAILED);
//				return;
//			}

//			String signupTitle = String.format("%s %s", getResources().getString(R.string.success_account_created_title), SecondScreenApplication.getInstance().getUserFirstName());
//
//			if (mIsFromLogin) 
//			{
//				Toast toast = Toast.makeText(this, signupTitle, Toast.LENGTH_LONG);
//
//				((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
//
//				toast.show();
//			}
//			else if (mIsFromSignup) 
//			{
//				String signupText = getResources().getString(R.string.success_account_created_text);
//
//				Toast toast = Toast.makeText(this, signupTitle + "\n" + signupText, Toast.LENGTH_LONG);
//
//				((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
//
//				toast.show();
//			}
//
//			loadPage();
		} 
		else 
		{
			setContentView(R.layout.layout_activity_not_logged_in_activity);
			
			initStandardViews();
			initInactiveViews();
		}
	}
	
	private void getActivityFeedData() {
		updateUI(REQUEST_STATUS.LOADING);
		ContentManager.sharedInstance().getElseFetchFromServiceActivityFeedData(this, false);
	}
	
	private void initStandardViews() {
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
		ArrayList<TVFeedItem> activityFeed = ContentManager.sharedInstance().getFromStorageActivityFeedData();
		if (activityFeed.get(0).getItemType().equals(Consts.FEED_ITEM_TYPE_POPULAR_BROADCASTS)) {
			View header = getLayoutInflater().inflate(R.layout.block_feed_no_likes, null);
			mListView.addHeaderView(header);

			mGreetingTv = (TextView) findViewById(R.id.block_feed_no_likes_greeting_tv);
			mCheckPopularBtn = (Button) findViewById(R.id.block_feed_no_likes_btn);
			mCheckPopularBtn.setOnClickListener(this);

			mGreetingTv.setText(mActivity.getResources().getString(R.string.hello) + " " + ContentManager.sharedInstance().getFromStorageUserFirstname() + " "
					+ ContentManager.sharedInstance().getFromStorageUserLastname() + ",");
		}

		mListView.setOnScrollListener(this);
		mAdapter = new ActivityFeedAdapter(this, activityFeed);
		mListView.setAdapter(mAdapter);
		mListView.setVisibility(View.VISIBLE);
	}

	
	
	@Override
	protected void loadPage() 
	{
//		updateUI(REQUEST_STATUS.LOADING);
//
//		if (NetworkUtils.isConnectedAndHostIsReachable(this)) 
//		{
//			if (MiTVStore.getInstance().getActivityFeed().size() > 0) 
//			{
//				Log.d(TAG, "READ FROM STORAGE");
//				
//				activityFeed = MiTVStore.getInstance().getActivityFeed();
//				
//				updateUI(REQUEST_STATUS.SUCCESSFUL);
//			} 
//			else 
//			{
//				new GetFeedTask(0, 5).execute();
//			}
//		} 
//		else 
//		{
//			updateUI(REQUEST_STATUS.FAILED);
//		}
	}
	
	

	@Override
	public void onBackPressed() 
	{
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

	private void showScrollSpinner(boolean aShow) {
		if (mListFooterView != null) {
			// Show/hide the scroll spinner
			mListFooterView.setVisibility(aShow ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(ActivityActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// we are here: do nothing
			break;
		case R.id.tab_me:
			// tab to activity page
			Intent intentMe = new Intent(ActivityActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
						
			break;
		case R.id.activity_not_logged_in_facebook_container:
			// facebook sign in
			Intent intent = new Intent(ActivityActivity.this, FacebookLoginActivity.class);
			startActivity(intent);
			finish();
			
			break;
		case R.id.activity_not_logged_in_signup_email_container:
			Intent intentSignUp = new Intent(ActivityActivity.this, SignUpWithEmailActivity.class);
			intentSignUp.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
			startActivity(intentSignUp);
			
			break;
		case R.id.activity_not_logged_in_login_btn:
			Intent intentLogin = new Intent(ActivityActivity.this, MiTVLoginActivity.class);
			intentLogin.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
			startActivity(intentLogin);
			
			break;
		case R.id.block_feed_no_likes_btn:
			Intent checkPopular = new Intent(ActivityActivity.this, PopularPageActivity.class);
			startActivity(checkPopular);
			
			break;
		}
	}

//	class GetFeedMoreTask extends AsyncTask<Void, Void, Boolean> {
//
//		ArrayList<TVFeedItem>	moreFeedItems	= new ArrayList<TVFeedItem>();
//
//		protected void onPostExecute(Boolean result) {
//			Log.d(TAG, "result: " + result);
//			if (result) {
//				MiTVStore.getInstance().addItemsToActivityFeed(moreFeedItems);
//				mAdapter.addItems(moreFeedItems);
//				mStartIndex = mStartIndex + mNextStep;
//			} else {
//				if (mNoMoreItems) {
//					mListView.removeFooterView(mListFooterView);
//				}
//			}
//			moreFeedItems.clear();
//			mNoTask = true;
//			showScrollSpinner(false);
//		}

//		@Override
//		protected Boolean doInBackground(Void... arg0) {
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
//				urlParams.add(new BasicNameValuePair(Consts.API_SKIP, String.valueOf(mStartIndex)));
//				urlParams.add(new BasicNameValuePair(Consts.API_LIMIT, String.valueOf(mNextStep)));
//
//				URI uri = new URI(Consts.URL_ACTIVITY_FEED + "?" + URLEncodedUtils.format(urlParams, "utf-8"));
//
//				Log.d(TAG, "mStartIndex: " + String.valueOf(mStartIndex) + " mNextStep: " + String.valueOf(mNextStep));
//				Log.d(TAG, "Feed more items: " + uri.toString());
//
//				HttpGet httpGet = new HttpGet(uri);
//				httpGet.setHeader("Authorization", "Bearer " + SecondScreenApplication.getInstance().getAccessToken());
//				// header to accept the json in a correct encoding
//				httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
//				HttpResponse response = httpClient.execute(httpGet);
//
//				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
//					Log.d(TAG, "GOOD RESPONSE");
//
//					// comment for beta
//					// if (response.getFirstHeader("Age") != null) {
//					// mRequestAge = Integer.valueOf(response.getFirstHeader("Age").getValue());
//					// }
//					// if (response.getFirstHeader("Cache-Control").getValue().substring(8) != null) {
//					// mRequestMaxAge = Integer.valueOf(response.getFirstHeader("Cache-Control").getValue().substring(8));
//					// }
//					// mNextRequestTime = mRequestMaxAge - mRequestAge;
//
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
//
//					if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
//						JSONArray feedListJsonArray;
//						try {
//							feedListJsonArray = new JSONArray(jsonString);
//							int size = feedListJsonArray.length();
//							Log.d(TAG, "FEED MORE ITEMS SIZE: " + String.valueOf(size));
//
//							if (size == 0) {
//								mNoMoreItems = true;
//								return result;
//							}
//							int endIndex = 0;
//							if (mNextStep < size) endIndex = mNextStep;
//							else endIndex = size;
//							Log.d(TAG, "endIndex:" + endIndex + " mStartIndex: " + mStartIndex + " mStep: " + mNextStep);
//
//							for (int i = 0; i < endIndex; i++) {
//								moreFeedItems.add(ContentParser.parseFeedItem(feedListJsonArray.getJSONObject(i)));
//								result = true;
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
//					Log.d(TAG, "Get Activity Feed: Invalid");
//					ApiClient.forceLogin();
//				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
//					Log.d(TAG, "Get Activity Feed: Missing token");
//					ApiClient.forceLogin();
//				}
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//			return result;
//		}
//
//	}

//	private void scheduleFeedRefresh() {
//		if (mNextRequestTime != 0) {
//
//			final Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					MiTVStore.getInstance().reinitializeFeed();
//					activityFeed.clear();
//					activityFeed = new ArrayList<TVFeedItem>();
//					loadPage();
//				}
//			}, mNextRequestTime * 1000);
//		}
//	}
	
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
					ContentManager.sharedInstance().fetchFromServiceMoreActivityData(this);
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

	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) {
		if(fetchRequestResult == FetchRequestResultEnum.SUCCESS) {
			updateUI(REQUEST_STATUS.SUCCESSFUL);
		} else {
			updateUI(REQUEST_STATUS.FAILED);
		}
	}
}
