
package com.mitv.activities;



import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
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

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.authentication.LoginWithFacebookActivity;
import com.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.mitv.activities.authentication.SignUpWithEmailActivity;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.FeedListAdapter;
import com.mitv.models.TVFeedItem;
import com.mitv.ui.elements.FontTextView;



public class FeedActivity 
	extends BaseContentActivity 
	implements OnClickListener, OnScrollListener 
{
	private static final String TAG = FeedActivity.class.getName();

	
	private RelativeLayout facebookContainer;
	private RelativeLayout signUpContainer;
	private Button checkPopularButton;
	private RelativeLayout loginButton;
	private FontTextView termsOfService;
	private ListView listView;
	private FeedListAdapter listAdapter;
	private View listFooterView;
	private TextView greetingTv;
	
	private Boolean noTask = true;
	
	private boolean currentlyShowingLoggedInLayout;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if(isLoggedIn)
		{
			setContentView(R.layout.layout_activity_activity);

			initLogedInViews();
			
			initStandardViews();
			
			currentlyShowingLoggedInLayout = true;
		}
		else
		{
			setContentView(R.layout.layout_activity_not_logged_in_activity);
			
			initNotLoggedInViews();
			
			initStandardViews();
			
			currentlyShowingLoggedInLayout = false;
		}
	}
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if (isLoggedIn != currentlyShowingLoggedInLayout) 
		{
			if(isLoggedIn)
			{
				setContentView(R.layout.layout_activity_activity);

				initLogedInViews();
				
				initStandardViews();
				
				setTabViews();
				
				currentlyShowingLoggedInLayout = true;
			}
			else
			{
				setContentView(R.layout.layout_activity_not_logged_in_activity);
				
				initNotLoggedInViews();
				
				initStandardViews();
				
				setTabViews();
				
				currentlyShowingLoggedInLayout = false;
			}
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

	
	
	private void initLogedInViews() 
	{
		listView = (ListView) findViewById(R.id.activity_listview);
		
		LayoutInflater inflater = getLayoutInflater();
		
		listFooterView = (View) inflater.inflate(R.layout.row_loading_footerview, null);
		
		listView.addFooterView(listFooterView);
		
		listFooterView.setVisibility(View.GONE);
	}

	
	
	private void initNotLoggedInViews() 
	{
		facebookContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_facebook_container);
		facebookContainer.setOnClickListener(this);

		signUpContainer = (RelativeLayout) findViewById(R.id.activity_not_logged_in_signup_email_container);
		signUpContainer.setOnClickListener(this);

		loginButton = (RelativeLayout) findViewById(R.id.activity_not_logged_in_login_btn);
		loginButton.setOnClickListener(this);
		
		termsOfService = (FontTextView) findViewById(R.id.activity_not_logged_in_terms_link);

		String linkText = getString(R.string.sign_up_terms_link);
		
		termsOfService.setText(Html.fromHtml(linkText));
		termsOfService.setMovementMethod(LinkMovementMethod.getInstance());
		
		stripUnderlines(termsOfService);
		
		setEmptyLayoutDetailsMessage("");
	}
	
	

	private void setListAdapter() 
	{
		ArrayList<TVFeedItem> activityFeed = ContentManager.sharedInstance().getFromCacheActivityFeedData();
		
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
					
					checkPopularButton = (Button) findViewById(R.id.block_feed_no_likes_btn);
					
					checkPopularButton.setOnClickListener(this);
	
					StringBuilder sb = new StringBuilder();
					sb.append(getResources().getString(R.string.hello));
					sb.append(" ");
					sb.append(ContentManager.sharedInstance().getFromCacheUserFirstname());
					sb.append(" ");
					sb.append(ContentManager.sharedInstance().getFromCacheUserLastname());
					sb.append(",");
					
					greetingTv.setText(sb.toString());
					break;
				}

				default:
				{
					Log.w(TAG, "Unhandled item type.");
					break;
				}
			}
		}

		listView.setOnScrollListener(this);
		
		listAdapter = new FeedListAdapter(this, activityFeed);
		
		listView.setAdapter(listAdapter);
		
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
			updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
		}
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch (requestIdentifier) 
		{			
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
	
				startActivity(intent);
				
				break;
			}
	
			case R.id.activity_not_logged_in_signup_email_container:
			{
				Intent intent = new Intent(FeedActivity.this, SignUpWithEmailActivity.class);
	
				startActivity(intent);
	
				break;
			}
	
			case R.id.activity_not_logged_in_login_btn: 
			{
				Intent intent = new Intent(FeedActivity.this, LoginWithMiTVUserActivity.class);
	
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
			
			if (pastTotalCount && noTask) 
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
	
	private class URLSpanWithoutUnderline extends URLSpan 
	{
		public URLSpanWithoutUnderline(String url) 
		{
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds)
		{
			super.updateDrawState(ds);
			
			ds.setUnderlineText(false);
		}
	}
	
	// TODO NewArc - Is this really needed?
	private void stripUnderlines(TextView textView) 
	{
		Spannable s = (Spannable) textView.getText();
		
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		
		for (URLSpan span : spans) 
		{
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			
			s.removeSpan(span);
			
			span = new URLSpanWithoutUnderline(span.getURL());
			
			s.setSpan(span, start, end, 0);
		}
		
		textView.setText(s);
	}
}