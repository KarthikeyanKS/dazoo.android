
package com.mitv.activities;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Intent;
import android.content.res.Resources;
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
import com.mitv.ContentManager;
import com.mitv.FontManager;
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



public class BroadcastPageActivity extends BaseContentActivity implements OnClickListener {
	private static final String TAG = BroadcastPageActivity.class.getName();

	private TVChannelId channelId;
	private long beginTimeInMillis;
	boolean isLiked = false;
	private TVBroadcastWithChannelInfo broadcastWithChannelInfo;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;

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
	private WebView webDisqus;

	private RelativeLayout upcomingContainer;
	private RelativeLayout repetitionsContainer;
	
	private static final String DISQUS_COMMENTS_URL = "http://gitrgitr.com/test";
	private static final String DISQUS_COMMENTS_PAGE_URL = DISQUS_COMMENTS_URL + "/index.htm";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_broadcastpage_activity);

		initViews();
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
		if (needToDownloadBroadcastWithChannelInfo) {
			beginTimeInMillis = intent.getLongExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);

			String channelIdAsString = intent.getStringExtra(Constants.INTENT_EXTRA_CHANNEL_ID);

			channelId = new TVChannelId(channelIdAsString);
			broadcastWithChannelInfo = null;
			
			Log.d(TAG, String.format("needToDownloadBroadcastWithChannelInfo: channelId: %s, beginTimeMillis: %d", channelIdAsString, beginTimeInMillis));
		} else {
			broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		}

		String contentID;
		try {
			contentID = URLEncoder.encode(broadcastWithChannelInfo.getShareUrl(), "UTF-8");
			
			contentID = contentID.replace("+", "%20");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			contentID = "";
		}
		
		String title;
		try {
			title = URLEncoder.encode(broadcastWithChannelInfo.getTitle(), "UTF-8");
			
			title = title.replace("+", "%20");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			title = "";
		}
		
		String url;
		try {
			url = URLEncoder.encode(broadcastWithChannelInfo.getShareUrl(), "UTF-8");
			
			url = url.replace("+", "%20");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			url = "";
		}
		
		boolean isUserLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		String userID;
		String username;
		String userEmail;
		String userImage;
		
		if(isUserLoggedIn)
		{
			try 
			{
				userID = ContentManager.sharedInstance().getFromCacheUserId();
				
				userID = URLEncoder.encode(userID, "UTF-8");
				
				userID = userID.replace("+", "%20");
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				userID = "";
			}
			
			try 
			{
				username = ContentManager.sharedInstance().getFromCacheUserFirstname();
						
				username = URLEncoder.encode(username, "UTF-8");
				
				username = username.replace("+", "%20");
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				username = "";
			}
			
			try 
			{
				userEmail = ContentManager.sharedInstance().getFromCacheUserEmail();
				
				userEmail = URLEncoder.encode(userEmail, "UTF-8");
				
				userEmail = userEmail.replace("+", "%20");
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				userEmail = "";
			}
			
			try 
			{
				userImage = ContentManager.sharedInstance().getFromCacheUserProfileImage();
				
				userImage = URLEncoder.encode(userImage, "UTF-8");
				
				userImage = userImage.replace("+", "%20");
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				userImage = "";
			}
		}
		else
		{
			userID = null;
			username = null;
			userEmail = null;
			userImage = null;
		}
		
		StringBuilder urlSB = new StringBuilder();
		
		urlSB.append(DISQUS_COMMENTS_PAGE_URL);
		urlSB.append("?title=");
		urlSB.append(title);
		urlSB.append("&identifier=");
		urlSB.append(contentID);
		urlSB.append("&url=");
		urlSB.append(url);
		
		if(isUserLoggedIn)
		{
			urlSB.append("&id=");
			urlSB.append(userID);
			
			urlSB.append("&username=");
			urlSB.append(username);
			
			urlSB.append("&email=");
			urlSB.append(userEmail);
			
			urlSB.append("&avatar=");
			urlSB.append(userImage);
		}
		
		webDisqus.loadUrl(urlSB.toString());
		
		updateStatusOfLikeView();

		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	private void updateStatusOfLikeView() {
		if (likeView != null) {
			likeView.updateImage();
		}
	}

	@Override
	protected void loadData() {
		updateUI(UIStatusEnum.LOADING);
		String loadingMessage = getString(R.string.loading_message_broadcastpage_program_info);
		setLoadingLayoutDetailsMessage(loadingMessage);
		ContentManager.sharedInstance().getElseFetchFromServiceBroadcastPageData(this, false, broadcastWithChannelInfo, channelId, beginTimeInMillis);
	}

	@Override
	protected boolean hasEnoughDataToShowContent() {
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

		if (upcomingBroadcastsToRemove != null && !upcomingBroadcastsToRemove.isEmpty()) {
			upcomingBroadcasts.removeAll(upcomingBroadcastsToRemove);
		}

		return upcomingBroadcasts;
	}

	private void handleInitialDataAvailable() {
		broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();

		repeatingBroadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(broadcastWithChannelInfo);
		if (repeatingBroadcasts != null) {
			repeatingBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>(repeatingBroadcasts);

			for (TVBroadcastWithChannelInfo broadcastWithoutProgramInfo : repeatingBroadcasts) {
				broadcastWithoutProgramInfo.setProgram(broadcastWithChannelInfo.getProgram());
			}
		}

		upcomingBroadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(broadcastWithChannelInfo);
		if (upcomingBroadcasts != null) {
			upcomingBroadcasts = filterOutEpisodesWithBadData();
		}

	}

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		if (fetchRequestResult.wasSuccessful()) {
			switch (requestIdentifier) {
			case BROADCAST_PAGE_DATA: {
				handleInitialDataAvailable();
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				break;

			}
			case USER_ADD_LIKE: {
				updateStatusOfLikeView();
				break;

			}

			default: {
				Log.d(TAG, "other request");
				/* do nothing */break;
			}
			}
		} else {
			updateUI(UIStatusEnum.FAILED);
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
				Log.w(TAG, "updateUI - case not handled");
				break;
			}
		}
	}

	private void initViews() {

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
		
		webDisqus = (WebView) findViewById(R.id.disqus);
		
		WebSettings webSettings = webDisqus.getSettings();
		
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webDisqus.requestFocusFromTouch();
		
		webDisqus.setWebViewClient(new WebViewClient()
		{
			@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
		        if (checkMatchedLoadedURL(url))
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
		
		webDisqus.setWebChromeClient(new WebChromeClient() 
		{
			  public void onConsoleMessage(String message, int lineNumber, String sourceID) 
			  {
			    Log.d("MyApplication", message + " -- From line "
			                         + lineNumber + " of "
			                         + sourceID);
			  }
			});
	}
	
	

	/* used to check if the loaded url matches the base url loaded by the fragment(mUrl)
	 * @param loadedUrl
	 * @return true if matches | false if doesn't or either url is null
	 */
	private boolean checkMatchedLoadedURL(String loadedUrl)
	{
		boolean matchesLoadedURL = false;
		
		if (loadedUrl != null && 
			loadedUrl.isEmpty() == false)
		{
			int lastCharacterPosition = loadedUrl.length()-1;
			
			char buff = loadedUrl.charAt(lastCharacterPosition);
			
			if (buff == '/')
			{
				loadedUrl = loadedUrl.substring(0, lastCharacterPosition);
			}

			matchesLoadedURL = DISQUS_COMMENTS_URL.equalsIgnoreCase(loadedUrl);
		}
		
		return matchesLoadedURL;
	}
	  

	private boolean isProgramIrrelevantAndShouldBeDeleted(TVProgram program) 
	{
		boolean isProgramIrrelevantAndShouldBeDeleted = (program.getSeason().getNumber() == 0 && program.getEpisodeNumber() == 0);

		return isProgramIrrelevantAndShouldBeDeleted;
	}

	private void populateBlocks() {
		populateMainView();

		/* Repetitions */
		if (repeatingBroadcasts != null && !repeatingBroadcasts.isEmpty()) {
			BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(this, repetitionsContainer, broadcastWithChannelInfo);
			repeatitionsBlock.createBlock(repeatingBroadcasts);
			repetitionsContainer.setVisibility(View.VISIBLE);
		}
		else {
			repetitionsContainer.setVisibility(View.GONE);
		}

		/* upcoming episodes */
		if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) {
			BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, upcomingContainer, true, broadcastWithChannelInfo);
			upcomingBlock.createBlock(upcomingBroadcasts);
			upcomingContainer.setVisibility(View.VISIBLE);
		} else {
			upcomingContainer.setVisibility(View.GONE);
		}
	}

	private String getYearString(TVProgram program) {
		String yearString = "";

		if (program != null && program.getYear() != null) {
			yearString = (program.getYear() == 0) ? "" : String.valueOf(program.getYear());
		}

		return yearString;
	}

	private String getGenreString(TVProgram program) {
		String genreString = (program.getGenre() == null) ? "" : program.getGenre();

		return genreString;
	}

	private void populateMainView() {
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

			if (program.getSeason().getNumber() > 0) {
				seasonTv.setText(res.getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
				seasonTv.setVisibility(View.VISIBLE);
			}
			if (program.getEpisodeNumber() > 0) {
				episodeTv.setText(res.getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
				episodeTv.setVisibility(View.VISIBLE);
			}
			if (program.getSeason().getNumber() == 0 && program.getEpisodeNumber() == 0) {
				Typeface bold = FontManager.getFontBold(getApplicationContext());
				episodeNameTv.setTypeface(bold);
				episodeNameTv.setTextSize(16);
			}

			episodeNameTv.setText(program.getTitle());

			String episodeName = program.getTitle();
			if (episodeName.length() > 0) {
				episodeNameTv.setText(episodeName);
				episodeNameTv.setVisibility(View.VISIBLE);
			}

			extrasStringBuilder.append(res.getString(R.string.tv_series)).append(" ").append(getYearString(program)).append(" ").append(durationString)
					.append(minutesString).append(" ").append(getGenreString(program));

			break;
		}
		case MOVIE: {
			contentTitle = program.getTitle();
			contentTitleTextView.setText(contentTitle);

			extrasStringBuilder.append(res.getString(R.string.movie)).append(" ").append(getYearString(program)).append(" ").append(durationString)
					.append(minutesString).append(" ").append(getGenreString(program));
			break;
		}
		case SPORT: {
			contentTitle = broadcastWithChannelInfo.getProgram().getTitle();

			contentTitleTextView.setText(contentTitle);
			episodeNameTv.setText(program.getTitle());

			if (program.getTournament() != null) {
				episodeNameTv.setText(program.getTournament());
				episodeNameTv.setVisibility(View.VISIBLE);
			} else {
				episodeNameTv.setText(program.getSportType().getName());
				episodeNameTv.setVisibility(View.VISIBLE);
			}

			extrasStringBuilder.append(res.getString(R.string.sport)).append(" ").append(durationString).append(minutesString).append(" ")
					.append(program.getSportType().getName());
			break;
		}
		case OTHER: {
			contentTitle = broadcastWithChannelInfo.getProgram().getTitle();

			extrasStringBuilder.append(program.getCategory()).append(" ").append(durationString).append(minutesString);
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

		if (program.getImages().getPortrait().getLarge() != null && TextUtils.isEmpty(program.getImages().getPortrait().getLarge()) != true) {
			ImageAware imageAware = new ImageViewAware(posterIv, false);
			ImageLoader.getInstance().displayImage(program.getImages().getLandscape().getLarge(), imageAware);
		}

		if (broadcastWithChannelInfo.getChannel() != null) {
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getChannel().getImageUrl(), imageAware);
		}

		if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) /* Broadcast is currently on air: show progress */
		{
			LanguageUtils.setupProgressBar(this, broadcastWithChannelInfo, progressBar, progressTxt);
			timeTv.setVisibility(View.GONE);
		} else /* Broadcast is in the future: show time */
		{
			timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString() + " - "
					+ broadcastWithChannelInfo.getEndTimeHourAndMinuteLocalAsString());
		}

		String synopsis = program.getSynopsisShort();

		if (TextUtils.isEmpty(synopsis) == false) {
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
	public void onClick(View v) {
		/* Important to call super, else tabs wont work */
		super.onClick(v);

		int viewId = v.getId();

		TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) v.getTag();

		switch (viewId) {
		case R.id.element_social_buttons_share_button_container: {
			GenericUtils.startShareActivity(this, broadcastWithChannelInfo);
			break;

		}

		default: {
			Log.w(TAG, "Unhandled onClick action");
			break;
		}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		finish();
	}
}
