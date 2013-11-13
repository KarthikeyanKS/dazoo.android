package com.millicom.secondscreen.content.activity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.ActivityFeedListAdapter;
import com.millicom.secondscreen.authentication.SignInActivity;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.feed.FeedService;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;
import com.millicom.secondscreen.http.NetworkUtils;

public class ActivityActivity extends SSActivity implements OnClickListener, FeedScrollViewListener {

	private static final String	TAG			= "ActivityActivity";
	private TextView			mTxtTabTvGuide, mTxtTabProfile, mTxtTabActivity, mSignInTv;
	private ActionBar			mActionBar;
	private ArrayList<FeedItem>	activityFeed;
	private String				token;
	private Boolean				mIsLoggenIn	= false;
	private LinearLayout		mContainer;
	private FeedScrollView		mScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		if (token != null && TextUtils.isEmpty(token) != true) {
			mIsLoggenIn = true;
			initStandardViews();
			loadPage();
		} else {
			Log.d(TAG, "Not Logged In Layout!");
			initStandardViews();
			initInactiveViews();
		}
	}

	private void initStandardViews() {
		mTxtTabTvGuide = (TextView) findViewById(R.id.go_to_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabActivity = (TextView) findViewById(R.id.go_to_activity);
		mTxtTabActivity.setOnClickListener(this);
		mTxtTabProfile = (TextView) findViewById(R.id.go_to_profile);
		mTxtTabProfile.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabActivity.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabProfile.setTextColor(getResources().getColor(R.color.gray));

		mActionBar = getSupportActionBar();

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.activity_title));

		mScrollView = (FeedScrollView) findViewById(R.id.activity_feed_scrollview);
		mScrollView.setScrollViewListener(this);
		// mScrollView.setVisibility(View.GONE);
		mContainer = (LinearLayout) findViewById(R.id.activity_populator_container);

		// DO THE CALLBACK AND LOADING FUNCTIONALITY AND BEHAVIOR
		super.initCallbackLayouts();
	}

	private void initInactiveViews() {
		View notLoggedInView = LayoutInflater.from(this).inflate(R.layout.layout_activity_not_logged_in_activity, null);
		mSignInTv = (TextView) notLoggedInView.findViewById(R.id.activity_not_logged_in_btn);
		mSignInTv = (TextView) findViewById(R.id.activity_not_logged_in_btn);

		mSignInTv.setOnClickListener(this);
		mContainer.addView(notLoggedInView);
	}

	private static class AddBlocksDynamically extends AsyncTask<Context, Void, Boolean> {

		LinearLayout	mBlocksContainer;
		Activity		mActivity;
		int				mStartIndex;
		int				mStep;
		String			mToken;
		FeedItem		mFeedItem;
		FeedScrollView	mScrollView;

		public AddBlocksDynamically(LinearLayout container, FeedScrollView scrollView, Activity activity, String token, int startIndex, int step, FeedItem feedItem) {
			this.mBlocksContainer = container;
			this.mActivity = activity;
			this.mStartIndex = startIndex;
			this.mStep = step;
			this.mToken = token;
			this.mFeedItem = feedItem;
			this.mScrollView = scrollView;
		}

		protected void onPreExecute() {
			Log.d(TAG, "ON PRE EXECUTE");
		}

		@Override
		protected Boolean doInBackground(Context... params) {
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					String feedItemType = mFeedItem.getItemType();
					if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(feedItemType)) {
						ActivityPopularBlockPopulator popularBlock = new ActivityPopularBlockPopulator(mActivity, mBlocksContainer);
						popularBlock.createBlock(mFeedItem);
					} else if (Consts.DAZOO_FEED_ITEM_TYPE_BROADCAST.equals(feedItemType)) {
						ActivityLikedBlockPopulator likedBlock = new ActivityLikedBlockPopulator(mActivity, mBlocksContainer, mToken);
						likedBlock.createBlock(mFeedItem);
					} else if (Consts.DAZOO_FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(feedItemType)) {
						ActivityRecommendedBlockPopulator recommendedBlock = new ActivityRecommendedBlockPopulator(mActivity, mBlocksContainer, mToken);
						recommendedBlock.createBlock(mFeedItem);
					}
				}
			});
			return true;
		}

		protected void onPostExecute(Boolean result) {

		}
	}

	private boolean getFeedItems(int startIndex, int step) {
		boolean result = false;
		activityFeed = FeedService.getActivityFeed(token);
		if (activityFeed != null) {
			if (activityFeed.isEmpty() != true) {
				for (int i = startIndex; i < startIndex + step; i++) {
					AddBlocksDynamically blocksTask = new AddBlocksDynamically(mContainer, mScrollView, this, token, startIndex, step, activityFeed.get(i));
					blocksTask.execute(this);
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		// check if the network connection exists
		if (!NetworkUtils.checkConnection(this)) {
			updateUI(REQUEST_STATUS.FAILED);
		}

		if(getFeedItems(0, 10)){
			updateUI(REQUEST_STATUS.SUCCESSFUL);
		}

	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			Log.d(TAG, "ACTIVITY FEED SIZE:" + String.valueOf(activityFeed.size()));
			// mAdapter = new ActivityFeedListAdapter(this, activityFeed);
			// mListView.setAdapter(mAdapter);
			// mScrollView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent toSearchPage = new Intent(ActivityActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			return true;
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
		case R.id.go_to_tvguide:
			// tab to home page
			Intent intentHome = new Intent(ActivityActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.go_to_activity:
			// we are here: do nothing
			break;
		case R.id.go_to_profile:
			// tab to activity page
			Intent intentMe = new Intent(ActivityActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		case R.id.activity_not_logged_in_btn:
			Intent intentSignIn = new Intent(ActivityActivity.this, SignInActivity.class);
			startActivity(intentSignIn);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		}
	}

	@Override
	public void onScrollChanged(FeedScrollView scrollView, int x, int y, int oldx, int oldy) {
		// We take the last son in the scrollview
		View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
		int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

		// if diff is zero, then the bottom has been reached
		if (diff == 0) {
			int startIndex = 11;
			int step = 3;
			for (int i = startIndex; i < startIndex + step; i++) {
			Log.d(TAG, "WE ARE AT THE BOTTOM: LOAD THE NEXT PAGE");
			AddBlocksDynamically blocksTask = new AddBlocksDynamically(mContainer, mScrollView, this, token, startIndex, step, activityFeed.get(i));
			blocksTask.execute(this);
			}
		}
	}
}
