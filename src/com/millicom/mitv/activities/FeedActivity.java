
package com.millicom.mitv.activities;



import java.util.ArrayList;

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

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.authentication.LoginWithFacebookActivity;
import com.millicom.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.millicom.mitv.activities.authentication.SignUpWithEmailActivity;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVFeedItem;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.adapters.FeedListAdapter;



public class FeedActivity 
	extends BaseContentActivity 
	implements OnClickListener, OnScrollListener 
{
	private static final String TAG = FeedActivity.class.getName();

	
	private RelativeLayout facebookContainer;
	private RelativeLayout signUpContainer;
	private TextView greetingTv;
	private Button checkPopularBtn;
	private Button loginBtn;
	private Boolean noMoreItems = false;
	private Boolean noTask = true;
	private ListView listView;
	private FeedListAdapter adapter;
	private View listFooterView;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if (isLoggedIn) 
		{
			setContentView(R.layout.layout_activity_activity);

			initStandardViews();
			
			initFeedViews();
		}
		else
		{
			setContentView(R.layout.layout_activity_not_logged_in_activity);

			initStandardViews();
			
			initInactiveViews();
		}
	}

	
	private void initStandardViews() 
	{	
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.activity_title));
	}

	
	
	private void initFeedViews() 
	{
		listView = (ListView) findViewById(R.id.activity_listview);
		
		LayoutInflater inflater = getLayoutInflater();
		
		listFooterView = (View) inflater.inflate(R.layout.row_loading_footerview, null);
		
		listView.addFooterView(listFooterView);
		
		listFooterView.setVisibility(View.GONE);
	}

	
	
	private void initInactiveViews() 
	{
		facebookContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_facebook_container);
		facebookContainer.setOnClickListener(this);

		signUpContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_signup_email_container);
		signUpContainer.setOnClickListener(this);

		loginBtn = (Button) findViewById(R.id.activity_not_logged_in_login_btn);
		loginBtn.setOnClickListener(this);
	}
	
	

	private void setListAdapter() 
	{
		ArrayList<TVFeedItem> activityFeed = ContentManager.sharedInstance().getFromStorageActivityFeedData();
		
		if(activityFeed.isEmpty() == false)
		{
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
					sb.append(getResources().getString(R.string.hello));
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
		}

		listView.setOnScrollListener(this);
		
		adapter = new FeedListAdapter(this, activityFeed);
		
		listView.setAdapter(adapter);
		
		listView.setVisibility(View.VISIBLE);
	}
	
	
	
	@Override
	protected void loadData() 
	{
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if(isLoggedIn)
		{
			updateUI(UIStatusEnum.LOADING);
		
			ContentManager.sharedInstance().getElseFetchFromServiceActivityFeedData(this, false);
		}
		else
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_EMPTY_DATA);
		}
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch (requestIdentifier) 
		{
			case USER_ADD_LIKE:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					// TODO NewArc - Complete likes
//					StringBuilder sb = new StringBuilder();
//					sb.append("");
//					sb.append(getResources().getString(R.string.like_set_text));
//					
//					ToastHelper.createAndShowLikeToast(this, sb.toString());
					
//					holderBC.likeLikeIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_selected));
//				
//					AnimationUtilities.animationSet(holderBC.likeLikeIv);
				}
				else
				{
					// Ignore for now
				}
				break;
			}
			
			case USER_REMOVE_LIKE:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					// TODO NewArc - Complete unlikes
//					holderBC.likeLikeIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_default));
				}
				else
				{
					// Ignore for now
				}
				break;
			}
			
			case USER_ACTIVITY_FEED_ITEM:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
				break;
			}
						
			default:
			{
				Log.w(TAG, "Unknown request identifier");
			}
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				setListAdapter();
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
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}
	

	
	private void showScrollSpinner(boolean show) 
	{
		if (listFooterView != null) 
		{
			if(show)
			{
				listFooterView.setVisibility(View.VISIBLE);
			}
			else
			{
				listFooterView.setVisibility(View.GONE);
			}
		}
		else
		{
			Log.w(TAG, "ListFooterView is null.");
		}
	}

	
	
	@Override
	public void onClick(View v) 
	{
		/* Important to call Super, since that handles tab selection */
		super.onClick(v);
		
		int id = v.getId();

		switch (id)
		{
			case R.id.activity_not_logged_in_facebook_container: 
			{
				Intent intent = new Intent(FeedActivity.this, LoginWithFacebookActivity.class);
	
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, this.getClass().getName());
				
				startActivity(intent);
	
				finish();
	
				break;
			}
	
			case R.id.activity_not_logged_in_signup_email_container:
			{
				Intent intent = new Intent(FeedActivity.this, SignUpWithEmailActivity.class);
	
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, this.getClass().getName());
	
				startActivity(intent);
	
				break;
			}
	
			case R.id.activity_not_logged_in_login_btn: 
			{
				Intent intent = new Intent(FeedActivity.this, LoginWithMiTVUserActivity.class);
	
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, this.getClass().getName());
	
				startActivity(intent);
	
				break;
			}
	
			case R.id.block_feed_no_likes_btn: 
			{
				Intent checkPopular = new Intent(FeedActivity.this, PopularPageActivity.class);
	
				startActivity(checkPopular);
	
				break;
			}
	
			default: 
			{
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
			
			boolean pastTotalCount = (firstVisibleItem + visibleItemCount >= totalItemCount);
			
			if (pastTotalCount && !noMoreItems && noTask) 
			{
				showScrollSpinner(true);

				if(noTask) 
				{
					ContentManager.sharedInstance().fetchFromServiceMoreActivityData(this);
				}
				
				noTask = false;
			} 
			else 
			{
				showScrollSpinner(false);
			}
		} 
		else 
		{
			showScrollSpinner(false);
		}
	}
}