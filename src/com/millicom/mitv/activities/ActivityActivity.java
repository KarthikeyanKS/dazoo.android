package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ActivityWithTabs;
import com.millicom.mitv.models.gson.TVFeedItem;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.adapters.ActivityFeedAdapter;


public class ActivityActivity 
	extends BaseActivity 
	implements ActivityCallbackListener, OnClickListener, OnScrollListener 
{
	private static final String TAG = ActivityActivity.class.getName();

	private RelativeLayout facebookContainer;
	private RelativeLayout signUpContainer;
	private TextView greetingTv;
	private Button checkPopularBtn;
	private Button loginBtn;
	private ActionBar actionBar;
	private Boolean noMoreItems = false;
	private Boolean noTask = true;
	private ListView listView;
	private ActivityFeedAdapter adapter;
	private Activity activity;
	private View listFooterView;

	public static Toast toast;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		activity = this;

		if (ContentManager.sharedInstance().isLoggedIn()) 
		{
			setContentView(R.layout.layout_activity_activity);

			initStandardViews();
			
			initFeedViews();

			super.initCallbackLayouts();

			// String signupTitle = String.format("%s %s", getResources().getString(R.string.success_account_created_title),
			// SecondScreenApplication.getInstance().getUserFirstName());
			//
			// if (mIsFromLogin)
			// {
			// Toast toast = Toast.makeText(this, signupTitle, Toast.LENGTH_LONG);
			//
			// ((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
			//
			// toast.show();
			// }
			// else if (mIsFromSignup)
			// {
			// String signupText = getResources().getString(R.string.success_account_created_text);
			//
			// Toast toast = Toast.makeText(this, signupTitle + "\n" + signupText, Toast.LENGTH_LONG);
			//
			// ((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
			//
			// toast.show();
			// }
		} 
		else 
		{
			setContentView(R.layout.layout_activity_not_logged_in_activity);

			initStandardViews();
			
			initInactiveViews();
		}
	}

	private void initStandardViews() {
		actionBar = getSupportActionBar();
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.activity_title));
	}

	
	
	private void initFeedViews() 
	{
		listView = (ListView) findViewById(R.id.activity_listview);
		
		// mListFooter = (RelativeLayout) findViewById(R.id.activity_listview_footer);

		LayoutInflater inflater = getLayoutInflater();
		
		listFooterView = (View) inflater.inflate(R.layout.row_loading_footerview, null);
		
		listView.addFooterView(listFooterView);
		
		listFooterView.setVisibility(View.GONE);
	}

	
	
	private void initInactiveViews() 
	{
		// sign in
		facebookContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_facebook_container);
		facebookContainer.setOnClickListener(this);

		signUpContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_signup_email_container);
		signUpContainer.setOnClickListener(this);

		loginBtn = (Button) findViewById(R.id.activity_not_logged_in_login_btn);
		loginBtn.setOnClickListener(this);
	}
	
	

	private void setAdapter() 
	{
		ArrayList<TVFeedItem> activityFeed = ContentManager.sharedInstance().getFromStorageActivityFeedData();
		
		FeedItemTypeEnum itemType = activityFeed.get(0).getItemType();
		
		switch (itemType) 
		{
			case POPULAR_BROADCASTS:
			{
				View header = getLayoutInflater().inflate(R.layout.block_feed_no_likes, null);
				
				listView.addHeaderView(header);

				greetingTv = (TextView) findViewById(R.id.block_feed_no_likes_greeting_tv);
				
				checkPopularBtn = (Button) findViewById(R.id.block_feed_no_likes_btn);
				
				checkPopularBtn.setOnClickListener(this);

				StringBuilder sb = new StringBuilder();
				sb.append(activity.getResources().getString(R.string.hello));
				sb.append(" ");
				sb.append(ContentManager.sharedInstance().getFromStorageUserFirstname());
				sb.append(" ");
				sb.append(ContentManager.sharedInstance().getFromStorageUserLastname());
				sb.append(",");
				
				greetingTv.setText(sb.toString());
				break;
			}

			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}

		listView.setOnScrollListener(this);
		
		adapter = new ActivityFeedAdapter(this, activityFeed);
		
		listView.setAdapter(adapter);
		
		listView.setVisibility(View.VISIBLE);
	}
	
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceActivityFeedData(this, false);
	}

	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				setAdapter();
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) 
	{
		super.onResult(fetchRequestResult);
		
		adapter.notifyDataSetChanged();
		
		switch(fetchRequestResult)
		{
			case SUCCESS:
			{
				updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
				
				break;
			}
			
			default:
			{
				updateUI(UIStatusEnum.FAILED);
				
				break;
			}
		}
	}
	
	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}
	

	
	private void showScrollSpinner(boolean aShow) 
	{
		if (listFooterView != null) 
		{
			// Show/hide the scroll spinner
			listFooterView.setVisibility(aShow ? View.VISIBLE : View.GONE);
		}
	}

	
	
	@Override
	public void onClick(View v) {
		/* Important to call Super, since that handles tab selection */
		super.onClick(v);
		
		int id = v.getId();

		switch (id) {
		case R.id.activity_not_logged_in_facebook_container: {
			Intent intent = new Intent(ActivityActivity.this, FacebookLoginActivity.class);

			startActivity(intent);

			finish();

			break;
		}

		case R.id.activity_not_logged_in_signup_email_container: {
			Intent intentSignUp = new Intent(ActivityActivity.this, SignUpWithEmailActivity.class);

			intentSignUp.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

			startActivity(intentSignUp);

			break;
		}

		case R.id.activity_not_logged_in_login_btn: {
			Intent intentLogin = new Intent(ActivityActivity.this, MiTVLoginActivity.class);

			intentLogin.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

			startActivity(intentLogin);

			break;
		}

		case R.id.block_feed_no_likes_btn: {
			Intent checkPopular = new Intent(ActivityActivity.this, PopularPageActivity.class);

			startActivity(checkPopular);

			break;
		}

		default: {
			Log.w(TAG, "Unknown activity id: " + id);

			break;
		}
		}
	}
	

	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) 
	{
		// TODO NewArc - Do something here?
	}

	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
	{
		if (totalItemCount > 0) 
		{
			// If scrolling past bottom and there is a next page of products to fetch
			if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !noMoreItems && noTask) 
			{
				Log.d(TAG, "reached last item");
				
				// Show the scroll spinner
				showScrollSpinner(true);

				if (noTask) 
				{
					ContentManager.sharedInstance().fetchFromServiceMoreActivityData(this);
				}
				
				noTask = false;
			} 
			else 
			{
				// Hide the scroll spinner
				showScrollSpinner(false);
			}
		} 
		else 
		{
			// Hide the scroll spinner
			showScrollSpinner(false);
		}
	}
}
