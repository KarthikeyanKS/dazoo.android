
package com.mitv.activities;



import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.authentication.LoginWithFacebookActivity;
import com.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.mitv.activities.authentication.SignUpWithEmailActivity;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.FeedListAdapter;
import com.mitv.enums.ActivityHeaderStatusEnum;
import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.ActivityHeaderStatus;
import com.mitv.models.objects.mitvapi.TVFeedItem;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.HyperLinkUtils;
import com.mitv.utilities.NetworkUtils;



public class FeedActivity 
	extends BaseContentActivity 
	implements OnClickListener, OnScrollListener 
{
	private static final String TAG = FeedActivity.class.getName();

	
	private RelativeLayout facebookContainer;
	private RelativeLayout signUpContainer;
//	private Button checkPopularButton;
	private RelativeLayout loginButton;
	private FontTextView termsOfService;
	private ListView listView;
	private FeedListAdapter listAdapter;
	private RelativeLayout listFooterView;
	private LinearLayout listHeaderView;
	private TextView listHeaderButton;
//	private TextView greetingTv;
	private boolean reachedEnd;
	private boolean isEndReachedNoConnectionToastShowing;
	private int lastVisibleBottomRowIndex = 1;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
				
		registerAsListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_INITIAL_DATA);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_LOGIN_WITH_FACEBOOK_TOKEN);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_LOGIN);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_SIGN_UP);
		registerAsListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_ADD_LIKE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_REMOVE_LIKE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_LOGOUT);
	}

	@Override
	protected void onResume()
	{
		setupViews();
		
		/* Since the FeedActivity view alternates between two views, the super.onResumne() must be called only after setting the correct content view */
		super.onResume();
	}
	
	
	private void setupViews() 
	{
		boolean isLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();
		
		if (isLoggedIn) 
		{
			setContentView(R.layout.layout_feed_activity_logged_in);
			initLoggedInViews();
		} 
		else 
		{
			setContentView(R.layout.layout_feed_activity_not_logged_in);
			initNotLoggedInViews();
		}
		
		initStandardViews();

		setTabViews();
	}
	
	
	private void initStandardViews() 
	{	
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getString(R.string.activity_title));
	}
	
	
	private void initLoggedInViews() 
	{
		listView = (ListView) findViewById(R.id.activity_listview);

		LayoutInflater inflater = getLayoutInflater();
		listFooterView = (RelativeLayout) inflater.inflate(R.layout.loading_footer_feed_activity, null);
		listView.addFooterView(listFooterView);
		
		if (shouldShowActivityHeader())
		{
			listHeaderView = (LinearLayout) inflater.inflate(R.layout.block_feed_header, null);
			listView.addHeaderView(listHeaderView);
			listHeaderButton = (TextView) listHeaderView.findViewById(R.id.feed_top_info_button);
			listHeaderButton.setOnClickListener(this);
		}
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
		
		HyperLinkUtils.stripUnderlines(termsOfService);
		
		setEmptyLayoutDetailsMessage("");
	}
	
	

	private void setListAdapter() 
	{
		ArrayList<TVFeedItem> activityFeed = getFromCacheFeedItems();
		
		/* View saying  */
		if(activityFeed != null && !activityFeed.isEmpty())
		{
			FeedItemTypeEnum itemType = activityFeed.get(0).getItemType();
			
			switch (itemType) 
			{
				case POPULAR_BROADCASTS:
				{
//					View header = getLayoutInflater().inflate(R.layout.block_feed_no_likes, null);
//					
//					listView.addHeaderView(header);
//	
//					greetingTv = (TextView) findViewById(R.id.block_feed_no_likes_greeting_tv);
//					
//					checkPopularButton = (Button) findViewById(R.id.block_feed_no_likes_btn);
//					
//					checkPopularButton.setOnClickListener(this);
//	
//					StringBuilder sb = new StringBuilder();
//					sb.append(getString(R.string.hello));
//					sb.append(" ");
//					sb.append(ContentManager.sharedInstance().getFromCacheUserFirstname());
//					sb.append(" ");
//					sb.append(ContentManager.sharedInstance().getFromCacheUserLastname());
//					sb.append(",");
//					
//					greetingTv.setText(sb.toString());
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
	
	private void updateListAdapter() 
	{
		ArrayList<TVFeedItem> activityFeed = getFromCacheFeedItems();
		listAdapter.setFeedItems(activityFeed);
		listAdapter.notifyDataSetChanged();
	}
	
	
	private ArrayList<TVFeedItem> getFromCacheFeedItems() 
	{		
		ArrayList<TVFeedItem> activityFeed = ContentManager.sharedInstance().getCacheManager().getActivityFeedData();
		return activityFeed;
	}
	
	
	
	@Override
	protected void loadData() 
	{		
		if(ContentManager.sharedInstance().getCacheManager().isLoggedIn())
		{
			updateUI(UIStatusEnum.LOADING);
			String loadingString = getString(R.string.loading_message_feed_initial);
			setLoadingLayoutDetailsMessage(loadingString);
			ContentManager.sharedInstance().getElseFetchFromServiceActivityFeedData(this, false);
		}
		else
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
		}
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean isLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();
		
		if(isLoggedIn)
		{
			boolean hasEnoughDataToShowContent = ContentManager.sharedInstance().getCacheManager().containsActivityFeed() && 
												 ContentManager.sharedInstance().getCacheManager().containsUserLikes();
			
			return hasEnoughDataToShowContent;
		}
		else
		{
			return true;
		}
	}

	
	
	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch (requestIdentifier) 
		{
			case USER_LOGOUT: 
			{
				setupViews();
				break;
			}
			
			case TV_GUIDE_INITIAL_CALL:
			case TV_GUIDE_STANDALONE:
			case USER_LOGIN_WITH_FACEBOOK_TOKEN:
			case USER_LOGIN:
			case USER_SIGN_UP: 
			{
				setupViews();
				
				if(fetchRequestResult.wasSuccessful()) 
				{
					loadData();
				}
				else
				{
					boolean isLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();
					
					if (isLoggedIn) 
					{
						updateUI(UIStatusEnum.FAILED);
					}
				}
				
				break;
			}
			
			case USER_ACTIVITY_FEED_INITIAL_DATA:
			case USER_ACTIVITY_FEED_ITEM:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					if(fetchRequestResult == FetchRequestResultEnum.SUCCESS) 
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					} 
					else 
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
					}
				}
				else
				{
					boolean isLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();
					
					if (isLoggedIn) 
					{
						updateUI(UIStatusEnum.FAILED);
					}
				}
				break;
			}
			
			case USER_ACTIVITY_FEED_ITEM_MORE:
			{
				if(fetchRequestResult.wasSuccessful())
				{
					if(fetchRequestResult == FetchRequestResultEnum.SUCCESS) 
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					} 
					else 
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
					}
				}
				break;
			}
			
			case USER_ADD_LIKE:
			case USER_REMOVE_LIKE:
			{
				if(fetchRequestResult.wasSuccessful() == false)
				{
					// TODO - ?
				}
				break;
			}
						
			default:
			{
				Log.w(TAG, "Unknown request identifier");
			}
		}
	}
	
	
	private void reachedEndOfFeedItems()
	{
		reachedEnd = true;
		
		if(listFooterView != null) 
		{
			listFooterView.setVisibility(View.GONE);
		}
		
		setEmptyLayoutDetailsMessage("");
	}
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		showScrollSpinner(false);
		
		switch (status) 
		{
			case USER_TOKEN_EXPIRED:
			{
				setupViews();
				break;
			}
			
			case SUCCESS_WITH_CONTENT:
			{
				switch (latestRequest) 
				{
					case USER_ACTIVITY_FEED_INITIAL_DATA:
					case USER_ACTIVITY_FEED_ITEM:
					{
						setListAdapter();
						break;
					}
					
					case USER_ACTIVITY_FEED_ITEM_MORE:
					{
						updateListAdapter();
						break;
					}
								
					default:
					{
						Log.w(TAG, "Unknown request identifier: " + latestRequest);
						break;
					}
				}
				
				break;
			}
			
			case SUCCESS_WITH_NO_CONTENT: 
			{
				if(ContentManager.sharedInstance().getCacheManager().isLoggedIn()) 
				{
					reachedEndOfFeedItems();
				}
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
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
			
			case R.id.feed_top_info_button:
			{
				ActivityHeaderStatusEnum activityHeaderStatusEnum = ContentManager.sharedInstance().getCacheManager().getActivityHeaderStatus().getActivityHeaderStatus();
				
				switch (activityHeaderStatusEnum) {
				case SHOW_HEADER:
					ContentManager.sharedInstance().getCacheManager().setActivityHeaderShowIfNoLikes(1);
					break;

				case SHOW_IF_NO_LIKES:
					ContentManager.sharedInstance().getCacheManager().setActivityHeaderShowIfNoLikes(2);
					break;
					
				case SHOW_IF_NO_LIKES_SECOND_TIME:
					ContentManager.sharedInstance().getCacheManager().setActivityHeaderNeverShow();
					break;
					
				default:
					break;
				}
				
				ContentManager.sharedInstance().getCacheManager().setActivityHeaderDateUserLastClickedButton();
				
				listView.removeHeaderView(listHeaderView);
				
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
	public void onScrollStateChanged(AbsListView view, int scrollState) {/* Do nothing */
		
	}

	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
	{
		showScrollSpinner(false);
		
		if (totalItemCount > 0) 
		{
			// If scrolling past bottom and there is a next page of products to fetch
			int visibleBottomRowIndex = firstVisibleItem + visibleItemCount;
			boolean pastTotalCount = (visibleBottomRowIndex >= totalItemCount);

			if(visibleBottomRowIndex != lastVisibleBottomRowIndex) {
				boolean scrollingDown = (visibleBottomRowIndex > lastVisibleBottomRowIndex);
				TrackingGAManager.sharedInstance().sendUserFeedListScrolledToItemAtIndexEvent(visibleBottomRowIndex, scrollingDown);
			}
				
			/* Store the latest row index  */
			lastVisibleBottomRowIndex = visibleBottomRowIndex;
			
			
			if (pastTotalCount && !reachedEnd)
			{
				boolean isConnected = NetworkUtils.isConnected();

				if(isConnected)
				{
					showScrollSpinner(true);

					ContentManager.sharedInstance().fetchFromServiceMoreActivityData(this, totalItemCount);
				}
				else
				{
					if(isEndReachedNoConnectionToastShowing == false)
					{
						isEndReachedNoConnectionToastShowing = true;
						
						ToastHelper.createAndShowNoInternetConnectionToast();
						
						final Handler handler = new Handler();
						
						Runnable runnable = new Runnable()
						{
						    public void run() 
						    {
						    	isEndReachedNoConnectionToastShowing = false;
						    }
						};
						
						handler.postDelayed(runnable, 1000);
					}
				}
			}
		}
	}
	
	
	
	public boolean shouldShowActivityHeader()
	{
		boolean showActivityHeader = false;

		ActivityHeaderStatus activityHeaderStatus = ContentManager.sharedInstance().getCacheManager().getActivityHeaderStatus();

		ActivityHeaderStatusEnum status = activityHeaderStatus.getActivityHeaderStatus();

		switch (status) 
		{
		case SHOW_HEADER:

			showActivityHeader = true; 

			break;

		case SHOW_IF_NO_LIKES:
		case SHOW_IF_NO_LIKES_SECOND_TIME:

			String date = ContentManager.sharedInstance().getCacheManager().getActivityHeaderStatus().getDateUserLastClickedButton();

			if (date != null) 
			{
				Calendar now = DateUtils.getNowWithGMTTimeZone();
				
				Calendar lastTime = DateUtils.convertISO8601StringToCalendar(date);
	
				if (lastTime.before(now))
				{
					int a = now.get(Calendar.DAY_OF_YEAR);
					int b = lastTime.get(Calendar.DAY_OF_YEAR);
	
					int difference = a - b;
	
					if (difference > 2) 
					{
						boolean hasLikes = ContentManager.sharedInstance().getCacheManager().containsUserLikes();
						
						if (hasLikes == false)
						{
							showActivityHeader = true;
						}
					}
				}
			}

			break;

		case NEVER_SHOW_AGAIN:

			break;

		default:

			break;
		}

		return showActivityHeader;
	}
	
}