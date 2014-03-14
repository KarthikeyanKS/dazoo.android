
package com.mitv.activities;



import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVProgram;
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
	extends BaseContentActivity
	implements OnClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = BroadcastPageActivity.class.getName();

	private TVChannelId channelId;
	private long beginTimeInMillis;
	private boolean hasPopulatedViews = false;
	private TVBroadcastWithChannelInfo broadcastWithChannelInfo;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;
	private ScrollView scrollView;
	public static Toast toast;
	boolean isLiked = false;
	private LayoutInflater inflater;
	private LinearLayout containerView;
	private View topContentView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_broadcastpage_activity);

		initViews();
	}
	
	@Override
	protected void onResume() {
		
		Intent intent = getIntent();

		boolean needToDownloadBroadcastWithChannelInfo = intent.getBooleanExtra(Constants.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, false);
		
		/* Used for when starting this activity from notification center in device or if you click on it from reminder list */
		if (needToDownloadBroadcastWithChannelInfo)
		{
			beginTimeInMillis = intent.getLongExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);
			
			String channelIdAsString = intent.getStringExtra(Constants.INTENT_EXTRA_CHANNEL_ID);
			
			channelId = new TVChannelId(channelIdAsString);
		} else {
			broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		}

		updateStatusOfLikeView();
		
		super.onResume();
	}
	
	private void updateStatusOfLikeView() {
		if(likeView != null) {
			likeView.updateImage();
		}
	}
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceBroadcastPageData(this, false, broadcastWithChannelInfo, channelId, beginTimeInMillis);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasEnoughDataToShowContent = false;
		if(broadcastWithChannelInfo != null) {
			hasEnoughDataToShowContent = true;
		}
		return hasEnoughDataToShowContent;
	}
	
	

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			if (requestIdentifier == RequestIdentifierEnum.BROADCAST_PAGE_DATA) 
			{
				broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
				
				repeatingBroadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(broadcastWithChannelInfo);
				
				if(repeatingBroadcasts != null) {
					for(TVBroadcastWithChannelInfo broadcastWithoutProgramInfo : repeatingBroadcasts) 
					{
						broadcastWithoutProgramInfo.setProgram(broadcastWithChannelInfo.getProgram());
					}
				}
				
				upcomingBroadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(broadcastWithChannelInfo);

				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			} else {
				Log.d(TAG, "other request");
			}
		} 
		else 
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
		
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCESS_WITH_CONTENT:
			{				
				if(!hasPopulatedViews) 
				{
					populateBlocks();
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

	
	private void initViews() {
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		actionBar.setTitle(getResources().getString(R.string.broadcast_info));
		actionBar.setDisplayHomeAsUpEnabled(true);
		scrollView = (ScrollView) findViewById(R.id.broadcast_scroll);

		containerView = (LinearLayout) scrollView.findViewById(R.id.broacastpage_block_container_layout);

		topContentView = inflater.inflate(R.layout.block_broadcastpage_main_content, null);
		posterIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_poster_iv);
		contentTitleTextView = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		seasonTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		episodeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		episodeNameTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_name_tv);
		timeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		channelIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_channel_iv);
		synopsisTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_synopsis_tv);
		extraTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_extra_tv);

		reminderView = (ReminderView) topContentView.findViewById(R.id.element_social_buttons_reminder);

		likeView = (LikeView) topContentView.findViewById(R.id.element_social_buttons_like_view);

		shareContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_share_button_container);

		progressBar = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		progressTxt = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);
	}

	private boolean isProgramIrrelevantAndShouldBeDeleted(TVProgram program) {
		boolean isProgramIrrelevantAndShouldBeDeleted = (program.getSeason().getNumber() == 0 && program.getEpisodeNumber() == 0);
		
		return isProgramIrrelevantAndShouldBeDeleted;
	}

	
	private void populateBlocks()
	{
		hasPopulatedViews = true;
		populateMainView();

		 //TODO NewArc should we remove those irrelevant broadcasts in the AsynkTask (GetTVBroadcastsFromSeries) instead?
		/* Remove upcoming broadcasts with season 0 and episode 0 */
		LinkedList<TVBroadcast> upcomingBroadcastsToRemove = new LinkedList<TVBroadcast>();
		
		ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
		switch (programType) {
			case TV_EPISODE:{
				TVProgram program = broadcastWithChannelInfo.getProgram();
				
				if (isProgramIrrelevantAndShouldBeDeleted(program)) 
				{
					for (TVBroadcast upcomingBroadcast : upcomingBroadcasts) {
						TVProgram programFromUpcomingBroadcast = upcomingBroadcast.getProgram();
						
						if (isProgramIrrelevantAndShouldBeDeleted(programFromUpcomingBroadcast))
						{
							upcomingBroadcastsToRemove.add(upcomingBroadcast);
						}
					}
				}
				break;
			}
			default: {/* Do nothing if it is not a TV Episode */break;}
		}
		
		for (TVBroadcast upcomingBroadcastToRemove : upcomingBroadcastsToRemove) 
		{
			upcomingBroadcasts.remove(upcomingBroadcastToRemove);
		}

		
		 /* Repetitions */
		 if (repeatingBroadcasts != null && !repeatingBroadcasts.isEmpty()) {
			 BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(this, scrollView, broadcastWithChannelInfo);
			 repeatitionsBlock.createBlock(repeatingBroadcasts);
		 }
		
		 /* upcoming episodes */
		 if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) {
			 BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, scrollView, true, broadcastWithChannelInfo);
			 upcomingBlock.createBlock(upcomingBroadcasts);
		 }
	}
	
	private String getYearString(TVProgram program) {
		String yearString = "";
		if(program != null && program.getYear() != null) {
			yearString = (program.getYear() == 0) ? "" : String.valueOf(program.getYear());
		}
		return yearString;
	}

	private String getGenreString(TVProgram program) {
		String genreString = (program.getGenre() == null) ? "" : program.getGenre();
		return genreString;
	}

	private void populateMainView() 
	{
		TVProgram program = broadcastWithChannelInfo.getProgram();

		ProgramTypeEnum programType = program.getProgramType();

		int duration = broadcastWithChannelInfo.getBroadcastDurationInMinutes();

		Resources res = getResources();
		StringBuilder extrasStringBuilder = new StringBuilder();

		String durationString = String.valueOf((duration == 0) ? "" : duration);
		String minutesString = res.getString(R.string.minutes);

		String contentTitle = null;
		switch (programType) 
		{
		case TV_EPISODE: 
		{
			contentTitle = program.getSeries().getName();
			
			contentTitleTextView.setText(contentTitle);

			if (!program.getSeason().getNumber().equals("0")) 
			{
				seasonTv.setText(res.getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
				seasonTv.setVisibility(View.VISIBLE);
			}
			if (program.getEpisodeNumber() > 0) 
			{
				episodeTv.setText(res.getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
				episodeTv.setVisibility(View.VISIBLE);
			}
			if (program.getSeason().getNumber().equals("0") && program.getEpisodeNumber() == 0) 
			{
				episodeNameTv.setTextSize(18);
			}

			episodeNameTv.setText(program.getTitle());

			String episodeName = program.getTitle();
			if (episodeName.length() > 0) 
			{
				episodeNameTv.setText(episodeName);
				episodeNameTv.setVisibility(View.VISIBLE);
			}

			extrasStringBuilder.append(res.getString(R.string.tv_series))
			.append(" ")
			.append(getYearString(program))
			.append(" ")
			.append(durationString)
			.append(" ")
			.append(minutesString)
			.append(" ")
			.append(getGenreString(program));

			break;
		}
		case MOVIE: {
			contentTitle = program.getTitle();			
			contentTitleTextView.setText(contentTitle);

			extrasStringBuilder.append(res.getString(R.string.movie))
			.append(" ")
			.append(getYearString(program))
			.append(" ")
			.append(durationString)
			.append(" ")
			.append(minutesString)
			.append(" ")
			.append(getGenreString(program));
			break;
		}
		case SPORT: {
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

			extrasStringBuilder.append(res.getString(R.string.sport))
			.append(" ")
			.append(durationString)
			.append(" ")
			.append(minutesString)
			.append(" ")
			.append(program.getSportType().getName());
			break;
		}
		case OTHER: {
			contentTitle = broadcastWithChannelInfo.getProgram().getTitle();

			extrasStringBuilder.append(program.getCategory())
			.append(" ")
			.append(durationString)
			.append(" ")
			.append(minutesString);
			break;
		}
		default: {
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

		if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring())   /* Broadcast is currently on air: show progress */
		{
			LanguageUtils.setupProgressBar(this, broadcastWithChannelInfo, progressBar, progressTxt);
			timeTv.setVisibility(View.GONE);
		}
		else  /* Broadcast is in the future: show time */
		{
			timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString() + " - " + broadcastWithChannelInfo.getEndTimeHourAndMinuteLocalAsString());
		}

		String synopsis = program.getSynopsisShort();

		if (TextUtils.isEmpty(synopsis) == false) 
		{
			synopsisTv.setText(program.getSynopsisShort());
			synopsisTv.setVisibility(View.VISIBLE);
		}
		
		/* Set tag with broadcast object so that we can get that object from the view in onClickListener and perform add or remove reminder for broadcast */
		reminderView.setBroadcast(broadcastWithChannelInfo);
		
		/* Set tag with broadcast object so that we can get that object from the view in onClickListener and perform add or remove like for broadcast */
		likeView.setBroadcast(broadcastWithChannelInfo);
		
		/* Set tag with broadcast object so that we can get that object from the view in onClickListener and perform share for broadcast */
		shareContainer.setTag(broadcastWithChannelInfo);

		shareContainer.setOnClickListener(this);

		topContentView.setVisibility(View.VISIBLE);

		containerView.addView(topContentView);
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
				GenericUtils.startShareActivity(this, getString(R.string.app_name), broadcastWithChannelInfo.getShareUrl(), getString(R.string.share_action_title));
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
}
