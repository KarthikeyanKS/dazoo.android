package com.mitv.activities.base;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.SignUpSelectionActivity;
import com.mitv.enums.DisqusTypeEnum;
import com.mitv.http.URLParameters;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.utilities.HyperLinkUtils;
import com.mitv.utilities.LanguageUtils;

/**
 * To enable Disqus comments to a new activity:
 * 
 * 1 Include @layout/block_broadcastpage_disqus_comments to the layout
 * 
 * 2 Extend this activity
 * 
 * 3 If the comments are not on a broadcast, event or team;
 * 	3.1 Add new type to DisqusTypeEnum
 * 	3.2 Add appropriate loadDisqusFor<DataType> method
 * 	3.3 Add new <DataType> handling to loadDisqus()
 * 
 * 4 Add initDisqus() call to initViews()/initLayout()
 * 
 * 5 Add loadDisqusFor<DataType> when page data is ready in onDataAvailable 
 * 
 * 6 Add snipplet as a case in onDataAvailable:		
 *	 case COMPETITION_EVENT_BY_ID:
				{
					if(fetchRequestResult.wasSuccessful())
					{
						loadDisqusForEvent(competitionID, eventID);
						
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					}
					else
					{
						updateUI(UIStatusEnum.FAILED);
					}
					break;
				}
 *
 * 7 All ready to go!
 * 
 */

public abstract class BaseCommentsActivity extends BaseContentActivity {

	private static final String TAG = BaseCommentsActivity.class.getName();

	/* Disqus comments */

	protected RelativeLayout disqusCommentsLayout;
	protected RelativeLayout disqusLoginToCommentButtonContainer;
	protected WebView webViewDisqusComments;
	protected String webViewDisqusURL;
	protected FontTextView disqusLoginToCommentButton;
	protected FontTextView disqusCommentsHeader;
	
	private DisqusTypeEnum disqusTypeEnum;
	private long competitionID;
	private long eventID;
	private long teamID;

	

	protected void initDisqus() {
		disqusCommentsLayout = (RelativeLayout) findViewById(R.id.disqus_comments_layout);
		webViewDisqusComments = (WebView) findViewById(R.id.disqus_comments_webview);

		disqusLoginToCommentButtonContainer = (RelativeLayout) findViewById(R.id.disqus_login_to_comment_button_container);

		disqusLoginToCommentButton = (FontTextView) findViewById(R.id.disqus_login_to_comment_button);
		disqusCommentsHeader = (FontTextView) findViewById(R.id.disqus_comments_header_text);

		WebSettings webSettings = webViewDisqusComments.getSettings();

		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);

		webViewDisqusComments.requestFocusFromTouch();

		webViewDisqusComments.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				if (HyperLinkUtils.checkIfMatchesDisqusURLOrFrontendURL(url))
				{
					return false;
				}
				else
				{
					Uri uri = Uri.parse(url);

					Intent intent = new Intent(Intent.ACTION_VIEW, uri);

					startActivity(intent);
				}

				return false;
			}
		});

		webViewDisqusComments.setWebChromeClient(new WebChromeClient() 
		{
			public void onConsoleMessage(String message, int lineNumber, String sourceID) 
			{
				Log.d(TAG, message + " -- From line " + lineNumber + " of " + sourceID);
			}
		});
		
		boolean areDisqusCommentsEnabled = false;
		
		if (ContentManager.sharedInstance().getCacheManager().getAppConfiguration() != null) 
		{
			areDisqusCommentsEnabled = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().areDisqusCommentsEnabled();
		}
		
		if(areDisqusCommentsEnabled == false)
		{
			hideDisqusCommentsWebview();
		}
	}
	
	protected void loadDisqusForEvent(long competitionID, long eventID)
	{
		this.disqusTypeEnum = DisqusTypeEnum.EVENT;
		this.competitionID = competitionID;
		this.eventID = eventID;
		loadDisqus();
	}
	
	protected void loadDisqusForTeam(long teamID) 
	{
		this.disqusTypeEnum = DisqusTypeEnum.TEAM;
		this.teamID = teamID;
		loadDisqus();
	}
	
	protected void loadDisqusForBroadcast() 
	{
		this.disqusTypeEnum = DisqusTypeEnum.BROADCAST;
		loadDisqus();
	}

	
	
	private void loadDisqus() {
		boolean areDisqusCommentsEnabled = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().areDisqusCommentsEnabled();
		String contentID = null;
		String title = null;
		String url = null;
		
		switch (disqusTypeEnum) {
		
		case BROADCAST:
			TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
			
			if (broadcast != null) 
			{
				contentID = broadcast.getShareUrl();
				title = broadcast.getTitle();
				url = broadcast.getShareUrl();
			}
			break;
			
		case EVENT:
			Event event = ContentManager.sharedInstance().getCacheManager().getEventById(competitionID, eventID);
			
			if (event != null)
			{
				contentID = event.getShareUrl();
				title = event.getTitle();
				url = event.getShareUrl();
			}
			break;
			
		case TEAM:
			Team team = ContentManager.sharedInstance().getCacheManager().getTeamById(teamID);
			
			if (team != null) 
			{
				contentID = team.getShareUrl();
				title = team.getDisplayName();
				url = team.getShareUrl();
			}
			break;

		default:
			break;
		}
		
		if (areDisqusCommentsEnabled && contentID != null && TextUtils.isEmpty(contentID) == false)
		{
			buildDisqusCommentsWebViewURL(title, contentID, url);
			ContentManager.sharedInstance().fetchFromServiceDisqusThreadDetails(this, contentID);
		}
	}


	protected void hideDisqusCommentsWebview()
	{
		disqusCommentsLayout.setVisibility(View.GONE);
	}



	protected void showAndReloadDisqusCommentsWebview(final int totalComments)
	{		
		disqusCommentsLayout.setVisibility(View.VISIBLE);

		disqusLoginToCommentButtonContainer.setVisibility(View.GONE);

		StringBuilder sb = new StringBuilder();

		sb.append(getString(R.string.disqus_comments_header_title));

		Boolean isUserLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();

		if(isUserLoggedIn)
		{
			disqusLoginToCommentButton.setVisibility(View.GONE);

			disqusCommentsHeader.setText(sb.toString());

			webViewDisqusComments.setVisibility(View.VISIBLE);			
		}
		else
		{
			webViewDisqusComments.setVisibility(View.GONE);

			disqusLoginToCommentButtonContainer.setVisibility(View.VISIBLE);

			disqusLoginToCommentButtonContainer.setOnClickListener(this);

			if(totalComments > 0)
			{
				sb.append(" (");
				sb.append(totalComments);
				sb.append(")");
			}

			disqusCommentsHeader.setText(sb.toString());
		}

		if(webViewDisqusURL != null && webViewDisqusURL.isEmpty() == false)
		{
			webViewDisqusComments.loadUrl(webViewDisqusURL);
		}
		else
		{
			webViewDisqusComments.reload();
		}
	}



	protected void buildDisqusCommentsWebViewURL(final String title, final String contentID, final String url)
	{
		if(title != null && TextUtils.isEmpty(title) == false && contentID != null && TextUtils.isEmpty(contentID) == false && url != null && TextUtils.isEmpty(url) == false)
		{
			Locale locale = LanguageUtils.getCurrentLocale();

			URLParameters urlParameters = new URLParameters();
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_LANGUAGE, locale.toString());
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_TITLE, title);
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_IDENTIFIER, contentID);
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_URL, url);

			boolean isUserLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();

			if(isUserLoggedIn)
			{
				String userID = ContentManager.sharedInstance().getCacheManager().getUserId();
				String username = ContentManager.sharedInstance().getCacheManager().getUserFirstname();
				String userEmail = ContentManager.sharedInstance().getCacheManager().getUserEmail();
				String userImage = ContentManager.sharedInstance().getCacheManager().getUserProfileImage();

				urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_USER_ID, userID);
				urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_USER_NAME, username);
				urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_USER_EMAIL, userEmail);
				urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_USER_AVATAR_IMAGE, userImage);
			}

			StringBuilder urlSB = new StringBuilder();
			urlSB.append(Constants.DISQUS_COMMENTS_PAGE_URL);
			urlSB.append(urlParameters.toString());

			webViewDisqusURL = urlSB.toString();
		}
		else
		{
			Log.w(TAG, "TVBroadcast is null. Disqus URL will not be built");
		}
	}



	protected Runnable getConfirmProcedure(final Activity activity)
	{
		return new Runnable() 
		{
			public void run() 
			{
				Intent intent = new Intent(activity, SignUpSelectionActivity.class);			

				activity.startActivity(intent);
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		int viewId = v.getId();

		switch (viewId) 
		{
			case R.id.disqus_login_to_comment_button_container:
			{
				String title = getString(R.string.disqus_comments_login_to_comment_prompt_title);
				String message = getString(R.string.disqus_comments_login_to_comment_prompt_message);
				String confirmButtonText = getString(R.string.disqus_comments_login_to_comment_prompt_button_confirm);
				String cancelButtonText = getString(R.string.disqus_comments_login_to_comment_prompt_button_cancel);
				
				Runnable confirmProcedure = getConfirmProcedure(this);

				DialogHelper.showDialog(this, title, message, confirmButtonText, cancelButtonText, confirmProcedure, null);
				break;
			}
	
			default: 
			{
				Log.w(TAG, "Unhandled onClick action");
				break;
			}
		}
	}
}
