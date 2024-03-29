
package com.mitv.activities;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.base.BaseCommentsActivity;
import com.mitv.asynctasks.other.RemoveAlreadyEndedBroadcastsTask;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.FontManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVCredit;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.populators.BroadcastAiringOnDifferentChannelBlockPopulator;
import com.mitv.populators.BroadcastRepetitionsBlockPopulator;
import com.mitv.populators.BroadcastUpcomingBlockPopulator;
import com.mitv.ui.elements.LikeView;
import com.mitv.ui.elements.ReminderView;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BroadcastPageActivity
	extends BaseCommentsActivity 
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
	
	private RelativeLayout upcomingContainer;
	private RelativeLayout repetitionsContainer;
	private RelativeLayout nowAiringContainer;
	
	private boolean notificationEventSent = false;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		boolean isFromNotification = getIntent().getBooleanExtra(Constants.INTENT_NOTIFICATION_EXTRA_IS_FROM_NOTIFICATION, false);
		
		Log.d(TAG, "Event sent: Is from notification: " + isFromNotification);
		if (isFromNotification == false && isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_broadcastpage_activity);

		initViews();
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
				TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
				
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
		boolean hasEnoughDataToShowContent = ContentManager.sharedInstance().getCacheManager().containsBroadcastPageData();

		return hasEnoughDataToShowContent;
	}

	
	
	private ArrayList<TVBroadcastWithChannelInfo> filterOutEpisodesWithBadData() 
	{
		/* Remove upcoming broadcasts with season 0 and episode 0 */
		LinkedList<TVBroadcast> upcomingBroadcastsToRemove = new LinkedList<TVBroadcast>();
		
		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
		
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
		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
		
		repeatingBroadcasts = ContentManager.sharedInstance().getCacheManager().getRepeatingBroadcastsVerifyCorrect(broadcast);
		
		if (repeatingBroadcasts != null) 
		{
			repeatingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(repeatingBroadcasts);

			for (TVBroadcastWithChannelInfo broadcastWithoutProgramInfo : repeatingBroadcasts) 
			{
				broadcastWithoutProgramInfo.setProgram(broadcast.getProgram());
			}
		}

		upcomingBroadcasts = ContentManager.sharedInstance().getCacheManager().getUpcomingBroadcastsVerifyCorrect(broadcast);
		
		if (upcomingBroadcasts != null) 
		{
			upcomingBroadcasts = filterOutEpisodesWithBadData();
		}
		
		broadcastsAiringOnOtherChannels = ContentManager.sharedInstance().getCacheManager().getBroadcastsAiringOnDifferentChannels(broadcast, true);
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
					int totalDisqusPosts = ContentManager.sharedInstance().getCacheManager().getDisqusTotalPostsForLatestBroadcast();
					
					showAndReloadDisqusCommentsWebview(totalDisqusPosts);
					
					break;
				}
			
				case BROADCAST_PAGE_DATA: 
				{
					handleInitialDataAvailable();
					
					loadDisqusForBroadcast();
					
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
		
		initDisqus();
	}
	
	
	
	private void populateBlocks()
	{
		populateMainView();

		final TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
		
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
			final Activity activity = this;
			
			RemoveAlreadyEndedBroadcastsTask removeAlreadyEndedBroadcastsTask = new RemoveAlreadyEndedBroadcastsTask(broadcastsAiringOnOtherChannels, 0) {
				
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);			
					if (broadcastsAiringOnOtherChannels != null && !broadcastsAiringOnOtherChannels.isEmpty()) 
					{
						BroadcastAiringOnDifferentChannelBlockPopulator similarBroadcastsAiringNowBlock = new BroadcastAiringOnDifferentChannelBlockPopulator(activity, nowAiringContainer, broadcast);
						
						similarBroadcastsAiringNowBlock.createBlock(broadcastsAiringOnOtherChannels);
						
						nowAiringContainer.setVisibility(View.VISIBLE);
					}
					else
					{
						nowAiringContainer.setVisibility(View.GONE);
					}
				}
			};
			
			removeAlreadyEndedBroadcastsTask.execute();
		}
		else
		{
			nowAiringContainer.setVisibility(View.GONE);
		}
		
		// Analytics for event was put here, because its only here the broadcast data is available.
		if (getIntent().getBooleanExtra(Constants.INTENT_NOTIFICATION_EXTRA_IS_FROM_NOTIFICATION, false) && notificationEventSent == false) 
		{
			TrackingGAManager.sharedInstance().sendUserOpenedBroadcastpageFromReminder(broadcast);
			notificationEventSent = true;
		}
	}


	
	private void populateMainView()
	{
		TVBroadcastWithChannelInfo broadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
		
		TVProgram program = broadcast.getProgram();

		ProgramTypeEnum programType = program.getProgramType();

		int duration = broadcast.getBroadcastDurationInMinutes();

		StringBuilder extrasStringBuilder = new StringBuilder();

		String durationString = String.valueOf((duration == 0) ? "" : duration);
		
		String minutesString = getString(R.string.minutes);

		StringBuilder titleSB = new StringBuilder();

		if(program.isPopular())
		{
			titleSB.append(getString(R.string.icon_trending))
			.append(" ");
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
		
		List<TVCredit> tvCredit = program.getCredits();
		
		extrasStringBuilder.append(title)
		.append(": ");
		
		for (int i = 0; i < tvCredit.size(); i++) {
			
			String type = tvCredit.get(i).getType();
			
			if (type.equals(Constants.PROGRAM_CAST_ACTORS)) 
			{
				extrasStringBuilder.append(tvCredit.get(i).getName());
				
				howManyActorsInCast++;
				
				if (tvCredit.size()-1 > i)
				{
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
		ContentManager.sharedInstance().getCacheManager().popFromSelectedBroadcastWithChannelInfo();
		
		super.onBackPressed();

		finish();
	}

}
