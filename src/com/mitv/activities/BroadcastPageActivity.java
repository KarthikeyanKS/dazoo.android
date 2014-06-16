
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
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.http.URLParameters;
import com.mitv.managers.ContentManager;
import com.mitv.managers.FontManager;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVCredit;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.populators.BroadcastAiringOnDifferentChannelBlockPopulator;
import com.mitv.populators.BroadcastRepetitionsBlockPopulator;
import com.mitv.populators.BroadcastUpcomingBlockPopulator;
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

	
	private boolean requiresDataReload;
	private TVChannelId channelId;
	private long beginTimeInMillis;
	
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> broadcastsAiringOnOtherChannels;
	
	
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
	private TextView castInfo;
	
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

		if (!getIntent().getBooleanExtra(Constants.INTENT_NOTIFICATION_EXTRA_IS_FROM_NOTIFICATION, false) && isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_broadcastpage_activity);

		initViews();
		
		boolean areDisqusCommentsEnabled = false;
		if (ContentManager.sharedInstance().getFromCacheAppConfiguration() != null) {
			areDisqusCommentsEnabled = ContentManager.sharedInstance().getFromCacheAppConfiguration().areDisqusCommentsEnabled();
		}
		
		if(areDisqusCommentsEnabled == false)
		{
			hideDisqusCommentsWebview();
		}
	}

	
	
	@Override
	protected void onResume() 
	{	
		Intent intent = getIntent();

		// Defaulting the fetching of data to true
		requiresDataReload = intent.getBooleanExtra(Constants.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, true);

		beginTimeInMillis = 0;
		channelId = null;
		
		if(requiresDataReload)
		{
			long beginTime = intent.getLongExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);

			String channelIdAsString = intent.getStringExtra(Constants.INTENT_EXTRA_CHANNEL_ID);

			if(beginTime != 0 && channelIdAsString != null)
			{
				beginTimeInMillis = beginTime;
				
				channelId = new TVChannelId(channelIdAsString);
			}
			else
			{
				TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
				
				if(broadcast != null)
				{
					beginTimeInMillis = broadcast.getBeginTimeMillis();
					
					channelId = broadcast.getChannel().getChannelId();
				}
			}
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
		
		ContentManager.sharedInstance().getElseFetchFromServiceBroadcastPageData(this, requiresDataReload, channelId, beginTimeInMillis);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
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
		
		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
		
		ProgramTypeEnum programType = broadcast.getProgram().getProgramType();
		
		if (programType == ProgramTypeEnum.TV_EPISODE) 
		{
			for (TVBroadcast upcomingBroadcast : upcomingBroadcasts) 
			{
				TVProgram programFromUpcomingBroadcast = upcomingBroadcast.getProgram();

				if (programFromUpcomingBroadcast != null && 
					programFromUpcomingBroadcast.hasZeroValueForSeasonOrEpisodeNumber()) 
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
		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
		
		repeatingBroadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(broadcast);
		
		if (repeatingBroadcasts != null) 
		{
			repeatingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(repeatingBroadcasts);

			for (TVBroadcastWithChannelInfo broadcastWithoutProgramInfo : repeatingBroadcasts) 
			{
				broadcastWithoutProgramInfo.setProgram(broadcast.getProgram());
			}
		}

		upcomingBroadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(broadcast);
		
		if (upcomingBroadcasts != null) 
		{
			upcomingBroadcasts = filterOutEpisodesWithBadData();
		}
		
		broadcastsAiringOnOtherChannels = ContentManager.sharedInstance().getFromCacheBroadcastsAiringOnDifferentChannels(broadcast, true);
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
					
					TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
					
					boolean areDisqusCommentsEnabled = ContentManager.sharedInstance().getFromCacheAppConfiguration().areDisqusCommentsEnabled();
					
					if(areDisqusCommentsEnabled && broadcast != null)
					{
						String contentID = broadcast.getShareUrl();
						
						buildDisqusCommentsWebViewURL(broadcast);
						
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
					Log.d(TAG, "Other request");
					break;
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
		castInfo = (TextView) findViewById(R.id.block_broadcastpage_broadcast_cast_info);
		extraTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_extra_tv);

		reminderView = (ReminderView) findViewById(R.id.element_social_buttons_reminder);

		likeView = (LikeView) findViewById(R.id.element_social_buttons_like_view);

		shareContainer = (RelativeLayout) findViewById(R.id.element_social_buttons_share_button_container);

		progressTxt = (TextView) findViewById(R.id.block_broadcastpage_broadcast_progressbar_text);
		progressBar = (ProgressBar) findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		
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
	
	
	
	private void populateBlocks()
	{
		populateMainView();

		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
		
		/* Repetitions */
		if (repeatingBroadcasts != null && !repeatingBroadcasts.isEmpty()) 
		{
			BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(this, repetitionsContainer, broadcast);
			repeatitionsBlock.createBlock(repeatingBroadcasts);
			repetitionsContainer.setVisibility(View.VISIBLE);
		}
		else 
		{
			repetitionsContainer.setVisibility(View.GONE);
		}

		/* Upcoming episodes */
		if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) 
		{
			BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, upcomingContainer, true, broadcast);
			
			upcomingBlock.createBlock(upcomingBroadcasts);
			
			upcomingContainer.setVisibility(View.VISIBLE);
		} 
		else 
		{
			upcomingContainer.setVisibility(View.GONE);
		}
		
		/* Playing at the same time on other channels */
		if(Constants.ENABLE_BROADCASTS_PLAYING_AT_THE_SAME_TIME_ON_OTHER_CHANNELS)
		{
			if (broadcastsAiringOnOtherChannels != null && !broadcastsAiringOnOtherChannels.isEmpty()) 
			{
				BroadcastAiringOnDifferentChannelBlockPopulator similarBroadcastsAiringNowBlock = new BroadcastAiringOnDifferentChannelBlockPopulator(this, nowAiringContainer, broadcast);
				
				similarBroadcastsAiringNowBlock.createBlock(broadcastsAiringOnOtherChannels);
				
				nowAiringContainer.setVisibility(View.VISIBLE);
			}
			else
			{
				nowAiringContainer.setVisibility(View.GONE);
			}
		}
		else
		{
			nowAiringContainer.setVisibility(View.GONE);
		}
	}


	
	private void populateMainView()
	{
		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
		
		TVProgram program = broadcast.getProgram();

		ProgramTypeEnum programType = program.getProgramType();

		int duration = broadcast.getBroadcastDurationInMinutes();

		StringBuilder extrasStringBuilder = new StringBuilder();

		String durationString = String.valueOf((duration == 0) ? "" : duration);
		
		String minutesString = getString(R.string.minutes);

		StringBuilder titleSB = new StringBuilder();

		if(Constants.ENABLE_POPULAR_BROADCAST_PROCESSING)
		{
			if(broadcast.isPopular())
			{
				titleSB.append(getString(R.string.icon_trending))
					.append(" ");
			}
		}
		
		titleSB.append(broadcast.getTitle());
		
		contentTitleTextView.setText(titleSB);
		
		switch (programType) 
		{
			case TV_EPISODE:
			{
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
				
				/* TV Credits */
				String castTitle = getResources().getString(R.string.cast_and_crew);
				setTVCreditInfo(program, castTitle);
	
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
				
				/* TV Credits */
				String castTitle = getResources().getString(R.string.cast_and_crew);
				setTVCreditInfo(program, castTitle);
				
				break;
			}
			
			case SPORT: 
			{
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
	
				extrasStringBuilder.append(getString(R.string.sport))
					.append(" ")
					.append(durationString)
					.append(minutesString)
					.append(" ")
					.append(program.getSportType().getName());
				
				/* TV Credits */
				String castTitle = getResources().getString(R.string.cast_info_sport);
				setTVCreditInfo(program, castTitle);
				
				break;
			}
			
			case OTHER: 
			{
				extrasStringBuilder
					.append(program.getCategory())
					.append(" ")
					.append(durationString)
					.append(minutesString);
				
				/* TV Credits */
				String castTitle = getResources().getString(R.string.cast_info_other);
				setTVCreditInfo(program, castTitle);
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unhandled program type");
				break;
			}
		}

		String extras = extrasStringBuilder.toString();
		
		extraTv.setText(extras);
		extraTv.setVisibility(View.VISIBLE);

		ImageSetOrientation imageSetOrientation = program.getImages();
		
		boolean containsLandscapeOrientation = imageSetOrientation.containsLandscapeImageSet();
		
		if(containsLandscapeOrientation)
		{
			ImageAware imageAware = new ImageViewAware(posterIv, false);
			
			ImageLoader.getInstance().displayImage(program.getImages().getLandscape().getImageURLForDeviceDensityDPI(), imageAware);
		}

		if (broadcast.getChannel() != null) 
		{
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			
			ImageLoader.getInstance().displayImage(broadcast.getChannel().getImageUrl(), imageAware);
		}

		BroadcastTypeEnum broadcastType = broadcast.getBroadcastType();
		
		StringBuilder timeSB = new StringBuilder();
		
		if(broadcast.isBroadcastCurrentlyAiring())
		{
			LanguageUtils.setupProgressBar(this, broadcast, progressBar, progressTxt);

			if(programType == ProgramTypeEnum.SPORT &&
			   broadcastType == BroadcastTypeEnum.LIVE) 
			{
				timeSB.append(getString(R.string.icon_live));
				
				timeTv.setTextColor(getResources().getColor(R.color.red));
				
				timeTv.setText(timeSB.toString());
				
				timeTv.setVisibility(View.VISIBLE);
			}
			else
			{
				timeTv.setVisibility(View.GONE);
			}
		} 
		else /* Broadcast is in the future: only show time */
		{
			if(programType == ProgramTypeEnum.SPORT &&
			   broadcastType == BroadcastTypeEnum.LIVE)
			{
				timeSB.append(getString(R.string.icon_live))
				.append(" ");
			}
			
			timeSB.append(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString())
			.append(" - ")
			.append(broadcast.getEndTimeHourAndMinuteLocalAsString());
			
			timeTv.setText(timeSB.toString());
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
		reminderView.setBroadcast(broadcast);

		/*
		 * Set tag with broadcast object so that we can get that object from the view in onClickListener and perform add
		 * or remove like for broadcast
		 */
		likeView.setUserLike(program);

		/*
		 * Set tag with broadcast object so that we can get that object from the view in onClickListener and perform
		 * share for broadcast
		 */
		shareContainer.setTag(broadcast);

		shareContainer.setOnClickListener(this);
	}
	
	
	
	/**
	 * Appends cast info to a broadcast page with program details.
	 * 
	 * @param program
	 */
	private void setTVCreditInfo(TVProgram program, String title) {
		StringBuilder extrasStringBuilder = new StringBuilder();
		int howManyActorsInCast = 0;
		
		ArrayList<TVCredit> tvCredit = program.getCredits();
		
		extrasStringBuilder.append(title)
		.append(": ");
		
		for (int i = 0; i < tvCredit.size(); i++) {
			
			String type = tvCredit.get(i).getType();
			
			if (type.equals(Constants.PROGRAM_CAST_ACTORS)) {
				extrasStringBuilder.append(tvCredit.get(i).getName());
				howManyActorsInCast++;
				
				if (tvCredit.size()-1 > i) {
					extrasStringBuilder.append(", ");
				}
			}
		}
		
		if (tvCredit != null && tvCredit.size() > 0 && howManyActorsInCast != 0) {
			castInfo.setText(extrasStringBuilder.toString());
			castInfo.setVisibility(View.VISIBLE);
			
		} else {
			castInfo.setVisibility(View.GONE);
		}
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
		ContentManager.sharedInstance().popFromSelectedBroadcastWithChannelInfo();
		
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
