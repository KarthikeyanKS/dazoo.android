
package com.mitv.populators;



import android.app.Activity;
import android.content.res.Resources;
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

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVProgram;
import com.mitv.ui.elements.LikeView;
import com.mitv.ui.elements.ReminderView;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BroadcastMainBlockPopulator 
	implements OnClickListener
{
	private static final String	TAG	= BroadcastMainBlockPopulator.class.getName();


	private Activity activity;


	
	public BroadcastMainBlockPopulator(Activity activity)
	{
		this.activity = activity;
	}

	
	
	public void createBlock(ScrollView scrollView, final TVBroadcastWithChannelInfo broadcastWithChannelInfo) 
	{
		LinearLayout containerView = (LinearLayout) scrollView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(activity).inflate(R.layout.block_broadcastpage_main_content, null);
		ImageView posterIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_poster_iv);
		TextView contentTitleTextView = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		TextView seasonTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		TextView episodeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		TextView episodeNameTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_name_tv);
		TextView timeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		ImageView channelIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_channel_iv);
		TextView synopsisTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_synopsis_tv);
		TextView extraTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_extra_tv);

		ReminderView reminderView = (ReminderView) topContentView.findViewById(R.id.element_social_buttons_reminder);

		LikeView likeView = (LikeView) topContentView.findViewById(R.id.element_social_buttons_like_view);

		RelativeLayout shareContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_share_button_container);

		ProgressBar progressBar = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		TextView progressTxt = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);

		TVProgram program = broadcastWithChannelInfo.getProgram();

		ProgramTypeEnum programType = program.getProgramType();

		int duration = broadcastWithChannelInfo.getBroadcastDurationInMinutes();

		Resources res = activity.getResources();
		StringBuilder extrasStringBuilder = new StringBuilder();

		String durationString = String.valueOf((duration == 0) ? "" : duration);
		String minutesString = res.getString(R.string.minutes);

		String contentTitle;
		String programId;
		
		switch (programType) 
		{
			case TV_EPISODE: 
			{
				contentTitle = program.getSeries().getName();
				programId = broadcastWithChannelInfo.getProgram().getSeries().getSeriesId();
				
				contentTitleTextView.setText(contentTitle);
	
				if (!program.getSeason().getNumber().equals("0")) 
				{
					seasonTv.setText(activity.getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
					seasonTv.setVisibility(View.VISIBLE);
				}
				
				if (program.getEpisodeNumber() > 0) 
				{
					episodeTv.setText(activity.getResources().getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
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
			
			case MOVIE:
			{
				contentTitle = program.getTitle();
	
				programId = broadcastWithChannelInfo.getProgram().getProgramId();
				
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
			
			case SPORT:
			{
				contentTitle = broadcastWithChannelInfo.getProgram().getTitle();
				programId = broadcastWithChannelInfo.getProgram().getSportType().getSportTypeId();
	
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
			
			case OTHER: 
			{
				contentTitle = broadcastWithChannelInfo.getProgram().getTitle();
				programId = broadcastWithChannelInfo.getProgram().getProgramId();
	
				extrasStringBuilder.append(program.getCategory())
				.append(" ")
				.append(durationString)
				.append(" ")
				.append(minutesString);
				
				break;
			}
			
			default: 
			{
				contentTitle = "";
				programId = "";
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
			
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(program.getImages().getLandscape().getLarge(), imageAware);
		}

		if (broadcastWithChannelInfo.getChannel() != null) 
		{
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getChannel().getImageUrl(), imageAware);
		}

		if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring())   /* Broadcast is currently on air: show progress */
		{
			LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, progressBar, progressTxt);
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
	
	
	
	private String getYearString(TVProgram program) 
	{
		String yearString = "";
		
		if(program != null && program.getYear() != null) 
		{
			yearString = (program.getYear() == 0) ? "" : String.valueOf(program.getYear());
		}
		
		return yearString;
	}

	
	
	private String getGenreString(TVProgram program) 
	{
		String genreString = (program.getGenre() == null) ? "" : program.getGenre();
		
		return genreString;
	}
	

	
	@Override
	public void onClick(View v) 
	{
		int viewId = v.getId();
		
		switch (viewId)
		{
			case R.id.element_social_buttons_share_button_container: 
			{
				TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) v.getTag();
				
				GenericUtils.startShareActivity(activity, activity.getResources().getString(R.string.app_name), broadcastWithChannelInfo.getShareUrl(), activity.getResources().getString(R.string.share_action_title));
				
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled on click action.");
				break;
			}
		}
	}
}