
package com.mitv.activities;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.http.URLParameters;
import com.mitv.managers.ContentManager;
import com.mitv.managers.FontManager;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.populators.BroadcastRepetitionsBlockPopulator;
import com.mitv.populators.BroadcastUpcomingBlockPopulator;
import com.mitv.populators.BroadcastNowAiringBlockPopulator;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.elements.LikeView;
import com.mitv.ui.elements.ReminderView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.HyperLinkUtils;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BroadcastPageActivity 
	extends BaseContentActivity 
	implements OnClickListener 
{
	private static final String TAG = BroadcastPageActivity.class.getName();

	
	private TVChannelId channelId;
	private long beginTimeInMillis;
	boolean isLiked = false;
	private TVBroadcastWithChannelInfo broadcastWithChannelInfo;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> similarBroadcastsAiringNow;
	
	private ImageView posterIv;
	private TextView seasonTv;
	private TextView episodeTv;
	private TextView timeTv;
	private TextView extraTv;
	private ReminderView reminderView;
	private LikeView likeView;
	private RelativeLayout shareContainer;
	private ProgressBar progressBar;
	private TextView progressTxt;
	private TextView contentTitleTextView;
	private TextView episodeNameTv;
	private ImageView channelIv;
	private TextView synopsisTv;
	
	private RelativeLayout disqusCommentsLayout;
	private RelativeLayout disqusLoginToCommentButtonContainer;
	private WebView webViewDisqusComments;
	private String webViewDisqusURL;
	
	private FontTextView disqusLoginToCommentButton;
	private FontTextView disqusCommentsHeader;
	
	private RelativeLayout upcomingContainer;
	private RelativeLayout repetitionsContainer;
	private RelativeLayout nowAiringContainer;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_broadcastpage_activity);

		initViews();
		
		boolean areDisqusCommentsEnabled = ContentManager.sharedInstance().getFromCacheAppConfiguration().areDisqusCommentsEnabled();
		
		if(areDisqusCommentsEnabled == false)
		{
			hideDisqusCommentsWebview();
		}
	}

	
	
	@Override
	protected void onResume() 
	{	
		Intent intent = getIntent();

		boolean needToDownloadBroadcastWithChannelInfo = intent.getBooleanExtra(Constants.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, false);

		/*
		 * Used for when starting this activity from notification center in device or if you click on it from reminder
		 * list
		 */
		if (needToDownloadBroadcastWithChannelInfo) 
		{
			beginTimeInMillis = intent.getLongExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);

			String channelIdAsString = intent.getStringExtra(Constants.INTENT_EXTRA_CHANNEL_ID);

			channelId = new TVChannelId(channelIdAsString);
			broadcastWithChannelInfo = null;
			
			Log.d(TAG, String.format("needToDownloadBroadcastWithChannelInfo: channelId: %s, beginTimeMillis: %d", channelIdAsString, beginTimeInMillis));
		} 
		else 
		{
			broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		}

		updateStatusOfLikeView();
				
		super.onResume();
	}
			

	
	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		
		setIntent(intent);
	}

	
	
	private void updateStatusOfLikeView() 
	{
		if (likeView != null) 
		{
			likeView.updateImage();
		}
	}

	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		String loadingMessage = getString(R.string.loading_message_broadcastpage_program_info);
		setLoadingLayoutDetailsMessage(loadingMessage);
		ContentManager.sharedInstance().getElseFetchFromServiceBroadcastPageData(this, false, broadcastWithChannelInfo, channelId, beginTimeInMillis);
	}

	
	
	@Override
	protected boolean hasEnoughDataToShowContent() 
	{
		boolean hasEnoughDataToShowContent = ContentManager.sharedInstance().getFromCacheHasBroadcastPageData();

		return hasEnoughDataToShowContent;
	}

	
	
	private ArrayList<TVBroadcastWithChannelInfo> filterOutEpisodesWithBadData() 
	{
		/* Remove upcoming broadcasts with season 0 and episode 0 */
		LinkedList<TVBroadcast> upcomingBroadcastsToRemove = new LinkedList<TVBroadcast>();
		
		ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
		
		if (programType == ProgramTypeEnum.TV_EPISODE) 
		{
			for (TVBroadcast upcomingBroadcast : upcomingBroadcasts) 
			{
				TVProgram programFromUpcomingBroadcast = upcomingBroadcast.getProgram();

				if (programFromUpcomingBroadcast != null &&
				    isProgramIrrelevantAndShouldBeDeleted(programFromUpcomingBroadcast)) 
				{
					upcomingBroadcastsToRemove.add(upcomingBroadcast);
				}
			}
		}

		upcomingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(upcomingBroadcasts);

		if (upcomingBroadcastsToRemove != null && !upcomingBroadcastsToRemove.isEmpty()) 
		{
			upcomingBroadcasts.removeAll(upcomingBroadcastsToRemove);
		}

		return upcomingBroadcasts;
	}

	
	
	private void handleInitialDataAvailable() 
	{
		broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();

		repeatingBroadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(broadcastWithChannelInfo);
		
		if (repeatingBroadcasts != null) 
		{
			repeatingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(repeatingBroadcasts);

			for (TVBroadcastWithChannelInfo broadcastWithoutProgramInfo : repeatingBroadcasts) 
			{
				broadcastWithoutProgramInfo.setProgram(broadcastWithChannelInfo.getProgram());
			}
		}

		upcomingBroadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(broadcastWithChannelInfo);
		
		if (upcomingBroadcasts != null) 
		{
			upcomingBroadcasts = filterOutEpisodesWithBadData();
		}
		
		similarBroadcastsAiringNow = ContentManager.sharedInstance().getFromCacheBroadcastsAiringNowOnDifferentChannels(broadcastWithChannelInfo, true);
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			switch (requestIdentifier) 
			{
				case DISQUS_THREAD_DETAILS:
				{
					int totalDisqusPosts = ContentManager.sharedInstance().getDisqusTotalPostsForLatestBroadcast();
					
					showAndReloadDisqusCommentsWebview(totalDisqusPosts);
					
					break;
				}
			
				case BROADCAST_PAGE_DATA: 
				{
					handleInitialDataAvailable();
					
					boolean areDisqusCommentsEnabled = ContentManager.sharedInstance().getFromCacheAppConfiguration().areDisqusCommentsEnabled();
					
					if(areDisqusCommentsEnabled && broadcastWithChannelInfo != null)
					{
						String contentID = broadcastWithChannelInfo.getShareUrl();
						
						buildDisqusCommentsWebViewURL(broadcastWithChannelInfo);
						
						ContentManager.sharedInstance().fetchFromServiceDisqusThreadDetails(this, contentID);
					}
					
					updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					break;
				}
				
				case USER_ADD_LIKE: 
				{
					updateStatusOfLikeView();
					break;
				}
	
				default:
				{
					Log.d(TAG, "other request");
					/* do nothing */break;
				}
			}
		} 
		else 
		{
			switch (requestIdentifier) 
			{
				case DISQUS_THREAD_DETAILS:
				{
					showAndReloadDisqusCommentsWebview(0);
					break;
				}
				
				default: 
				{
					updateUI(UIStatusEnum.FAILED);
				}
			}
		}
	}

	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case USER_TOKEN_EXPIRED:
			{
				/* If the sessions has expired, finish this activity and resume the previous one */
				finish();
				break;
			}
		
			case SUCCESS_WITH_CONTENT: 
			{
				populateBlocks();
				break;
			}
	
			default: 
			{
				hideDisqusCommentsWebview();
				Log.w(TAG, "updateUI - case not handled");
				break;
			}
		}
	}
	
	

	private void initViews() 
	{
		actionBar.setTitle(getResources().getString(R.string.broadcast_info));
		actionBar.setDisplayHomeAsUpEnabled(true);

		posterIv = (ImageView) findViewById(R.id.block_broadcastpage_poster_iv);
		contentTitleTextView = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		seasonTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		episodeTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		episodeNameTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_episode_name_tv);
		timeTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		channelIv = (ImageView) findViewById(R.id.block_broadcastpage_broadcast_channel_iv);
		synopsisTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_synopsis_tv);
		extraTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_extra_tv);

		reminderView = (ReminderView) findViewById(R.id.element_social_buttons_reminder);

		likeView = (LikeView) findViewById(R.id.element_social_buttons_like_view);

		shareContainer = (RelativeLayout) findViewById(R.id.element_social_buttons_share_button_container);

		progressBar = (ProgressBar) findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		progressTxt = (TextView) findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);
		
		upcomingContainer = (RelativeLayout) findViewById(R.id.broacastpage_upcoming);
		repetitionsContainer = (RelativeLayout) findViewById(R.id.broacastpage_repetitions);
		nowAiringContainer = (RelativeLayout) findViewById(R.id.broacastpage_similar_airing_now);
		
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
	}
	
	
	
	private boolean isProgramIrrelevantAndShouldBeDeleted(TVProgram program) 
	{
		boolean isProgramIrrelevantAndShouldBeDeleted = (program.getSeason().getNumber() == 0 && program.getEpisodeNumber() == 0);

		return isProgramIrrelevantAndShouldBeDeleted;
	}

	
	
	private void populateBlocks()
	{
		populateMainView();

		/* Repetitions */
		if (repeatingBroadcasts != null && !repeatingBroadcasts.isEmpty()) 
		{
			BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(this, repetitionsContainer, broadcastWithChannelInfo);
			repeatitionsBlock.createBlock(repeatingBroadcasts);
			repetitionsContainer.setVisibility(View.VISIBLE);
		}
		else 
		{
			repetitionsContainer.setVisibility(View.GONE);
		}

		/* upcoming episodes */
		if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) 
		{
			BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, upcomingContainer, true, broadcastWithChannelInfo);
			upcomingBlock.createBlock(upcomingBroadcasts);
			upcomingContainer.setVisibility(View.VISIBLE);
		} 
		else 
		{
			upcomingContainer.setVisibility(View.GONE);
		}
		
		if (similarBroadcastsAiringNow != null && !similarBroadcastsAiringNow.isEmpty()) 
		{
			BroadcastNowAiringBlockPopulator similarBroadcastsAiringNowBlock = new BroadcastNowAiringBlockPopulator(this, nowAiringContainer, broadcastWithChannelInfo);
			similarBroadcastsAiringNowBlock.createBlock(similarBroadcastsAiringNow);
			nowAiringContainer.setVisibility(View.VISIBLE);
		}
		else
		{
			nowAiringContainer.setVisibility(View.GONE);
		}
	}


	
	private void populateMainView()
	{
		TVProgram program = broadcastWithChannelInfo.getProgram();

		ProgramTypeEnum programType = program.getProgramType();

		int duration = broadcastWithChannelInfo.getBroadcastDurationInMinutes();

		StringBuilder extrasStringBuilder = new StringBuilder();

		String durationString = String.valueOf((duration == 0) ? "" : duration);
		
		String minutesString = getString(R.string.minutes);

		String contentTitle = null;
		
		switch (programType) 
		{
			case TV_EPISODE:
			{
				contentTitle = program.getSeries().getName();
	
				contentTitleTextView.setText(contentTitle);
	
				if (program.getSeason().getNumber() > 0) 
				{
					seasonTv.setText(getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
					seasonTv.setVisibility(View.VISIBLE);
				}
				
				if (program.getEpisodeNumber() > 0) 
				{
					episodeTv.setText(getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
					episodeTv.setVisibility(View.VISIBLE);
				}
				
				if (program.getSeason().getNumber() == 0 && program.getEpisodeNumber() == 0) 
				{
					Typeface bold = FontManager.getFontBold(getApplicationContext());
					episodeNameTv.setTypeface(bold);
					episodeNameTv.setTextSize(16);
				}
	
				episodeNameTv.setText(program.getTitle());
	
				String episodeName = program.getTitle();
				
				if (episodeName.length() > 0) 
				{
					episodeNameTv.setText(episodeName);
					episodeNameTv.setVisibility(View.VISIBLE);
				}
	
				String yearAsString = program.getYearAsString();
				String genreAsString = program.getGenreAsString();
				
				extrasStringBuilder.append(getString(R.string.tv_series))
						.append(" ")
						.append(yearAsString)
						.append(" ")
						.append(durationString)
						.append(minutesString)
						.append(" ")
						.append(genreAsString);
	
				break;
			}
			
			case MOVIE: 
			{
				contentTitle = program.getTitle();
				contentTitleTextView.setText(contentTitle);
	
				String yearAsString = program.getYearAsString();
				String genreAsString = program.getGenreAsString();
				
				extrasStringBuilder.append(getString(R.string.movie))
					.append(" ")
					.append(yearAsString)
					.append(" ")
					.append(durationString)
					.append(minutesString)
					.append(" ")
					.append(genreAsString);
				
				break;
			}
			
			case SPORT: 
			{
				contentTitle = broadcastWithChannelInfo.getProgram().getTitle();
	
				contentTitleTextView.setText(contentTitle);
				episodeNameTv.setText(program.getTitle());
	
				if (program.getTournament() != null) 
				{
					episodeNameTv.setText(program.getTournament());
					episodeNameTv.setVisibility(View.VISIBLE);
				} 
				else 
				{
					episodeNameTv.setText(program.getSportType().getName());
					episodeNameTv.setVisibility(View.VISIBLE);
				}
	
				extrasStringBuilder.append(getString(R.string.sport)).append(" ").append(durationString).append(minutesString).append(" ")
						.append(program.getSportType().getName());
				
				break;
			}
			
			case OTHER: 
			{
				contentTitle = broadcastWithChannelInfo.getProgram().getTitle();
	
				extrasStringBuilder.append(program.getCategory()).append(" ").append(durationString).append(minutesString);
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unhandled program type");
				break;
			}
		}

		contentTitleTextView.setText(contentTitle);

		String extras = extrasStringBuilder.toString();
		
		extraTv.setText(extras);
		extraTv.setVisibility(View.VISIBLE);

		if (program.getImages().getPortrait().getLarge() != null && TextUtils.isEmpty(program.getImages().getPortrait().getLarge()) != true) 
		{
			ImageAware imageAware = new ImageViewAware(posterIv, false);
			ImageLoader.getInstance().displayImage(program.getImages().getLandscape().getLarge(), imageAware);
		}

		if (broadcastWithChannelInfo.getChannel() != null) 
		{
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getChannel().getImageUrl(), imageAware);
		}

		if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) /* Broadcast is currently on air: show progress */
		{
			LanguageUtils.setupProgressBar(this, broadcastWithChannelInfo, progressBar, progressTxt);
			timeTv.setVisibility(View.GONE);
		} 
		else /* Broadcast is in the future: show time */
		{
			timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString() + " - "
					+ broadcastWithChannelInfo.getEndTimeHourAndMinuteLocalAsString());
		}

		String synopsis = program.getSynopsisShort();

		if (TextUtils.isEmpty(synopsis) == false) 
		{
			synopsisTv.setText(program.getSynopsisShort());
			synopsisTv.setVisibility(View.VISIBLE);
		}

		/*
		 * Set tag with broadcast object so that we can get that object from the view in onClickListener and perform add
		 * or remove reminder for broadcast
		 */
		reminderView.setBroadcast(broadcastWithChannelInfo);

		/*
		 * Set tag with broadcast object so that we can get that object from the view in onClickListener and perform add
		 * or remove like for broadcast
		 */
		likeView.setBroadcast(broadcastWithChannelInfo);

		/*
		 * Set tag with broadcast object so that we can get that object from the view in onClickListener and perform
		 * share for broadcast
		 */
		shareContainer.setTag(broadcastWithChannelInfo);

		shareContainer.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) 
	{
		/* Important to call super, else tabs wont work */
		super.onClick(v);

		int viewId = v.getId();

		TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) v.getTag();

		switch (viewId) 
		{
			case R.id.element_social_buttons_share_button_container: 
			{
				GenericUtils.startShareActivity(this, broadcastWithChannelInfo);
				break;
			}
			
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

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}
	
	
	
	private void hideDisqusCommentsWebview()
	{
		disqusCommentsLayout.setVisibility(View.GONE);
	}
	
	
	
	private void showAndReloadDisqusCommentsWebview(final int totalComments)
	{		
		disqusCommentsLayout.setVisibility(View.VISIBLE);

		disqusLoginToCommentButtonContainer.setVisibility(View.GONE);

		StringBuilder sb = new StringBuilder();

		sb.append(getString(R.string.disqus_comments_header_title));

		Boolean isUserLoggedIn = ContentManager.sharedInstance().isLoggedIn();

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
	
	
	
	private void buildDisqusCommentsWebViewURL(final TVBroadcastWithChannelInfo tvBroadcast)
	{
		if(tvBroadcast != null)
		{
			Locale locale = LanguageUtils.getCurrentLocale();
			
			String title = tvBroadcast.getTitle();
			String contentID = tvBroadcast.getShareUrl();
			String url = tvBroadcast.getShareUrl();
			
			URLParameters urlParameters = new URLParameters();
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_LANGUAGE, locale.toString());
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_TITLE, title);
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_IDENTIFIER, contentID);
			urlParameters.add(Constants.DISQUS_COMMENTS_PARAMETER_CONTENT_URL, url);
			
			boolean isUserLoggedIn = ContentManager.sharedInstance().isLoggedIn();
			
			if(isUserLoggedIn)
			{
				String userID = ContentManager.sharedInstance().getFromCacheUserId();
				String username = ContentManager.sharedInstance().getFromCacheUserFirstname();
				String userEmail = ContentManager.sharedInstance().getFromCacheUserEmail();
				String userImage = ContentManager.sharedInstance().getFromCacheUserProfileImage();
				
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
	
	
	
	private Runnable getConfirmProcedure(final Activity activity)
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
}
