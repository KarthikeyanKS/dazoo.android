
package com.mitv.tvguide;



import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.activities.SignUpSelectionActivity;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.utilities.DialogHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.LikeView;
import com.mitv.customviews.ReminderView;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.ProgressBarUtils;
import com.mitv.utilities.ShareUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BroadcastMainBlockPopulator implements OnClickListener
{
	@SuppressWarnings("unused")
	private static final String	TAG	= BroadcastMainBlockPopulator.class.getName();


	private Activity activity;
	private ScrollView scrollView;
	private ImageView likeIv;

	private String programId;
	private String contentTitle;



	public BroadcastMainBlockPopulator(Activity activity, ScrollView scrollView)
	{
		this.activity = activity;
		this.scrollView = scrollView;
	}

	private String getYearString(TVProgram program) {
		String yearString = (program.getYear() == 0) ? "" : String.valueOf(program.getYear());
		return yearString;
	}

	private String getGenreString(TVProgram program) {
		String genreString = (program.getGenre() == null) ? "" : program.getGenre();
		return genreString;
	}

	public void createBlock(final TVBroadcastWithChannelInfo broadcastWithChannelInfo) 
	{
		LinearLayout containerView = (LinearLayout) scrollView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(activity).inflate(R.layout.block_broadcastpage_main_content, null);
		ImageView posterIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_poster_iv);
		ProgressBar posterPb = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_poster_progressbar);
		TextView titleTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		TextView seasonTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		TextView episodeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		TextView episodeNameTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_name_tv);
		TextView timeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		ImageView channelIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_channel_iv);
		TextView synopsisTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_synopsis_tv);
		TextView extraTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_extra_tv);
		TextView tagsTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_tags_tv);

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

		switch (programType) 
		{
		case TV_EPISODE: {
			contentTitle = broadcastWithChannelInfo.getProgram().getSeries().getName();
			programId = broadcastWithChannelInfo.getProgram().getSeries().getSeriesId();

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

			titleTv.setText(program.getSeries().getName());

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
			contentTitle = broadcastWithChannelInfo.getProgram().getTitle();
			programId = broadcastWithChannelInfo.getProgram().getProgramId();

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
			contentTitle = broadcastWithChannelInfo.getProgram().getSportType().getName();
			programId = broadcastWithChannelInfo.getProgram().getSportType().getSportTypeId();

			titleTv.setText(program.getTitle());

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
			programId = broadcastWithChannelInfo.getProgram().getProgramId();

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

		titleTv.setText(program.getTitle());

		String extras = extrasStringBuilder.toString();
		extraTv.setText(extras);
		extraTv.setVisibility(View.VISIBLE);


		if (program.getImages().getPortrait().getLarge() != null && TextUtils.isEmpty(program.getImages().getPortrait().getLarge()) != true)
		{
			ImageAware imageAware = new ImageViewAware(posterIv, false);
			ImageLoader.getInstance().displayImage(program.getImages().getPortrait().getLarge(), imageAware);
		}

		if (broadcastWithChannelInfo.getChannel() != null) 
		{
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getChannel().getImageUrl(), imageAware);
		}

		if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring())   /* Broadcast is currently on air: show progress */
		{
			ProgressBarUtils.setupProgressBar(activity, broadcastWithChannelInfo, progressBar, progressTxt);
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
	public void onClick(View v) {
		int viewId = v.getId();
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) v.getTag();

		switch (viewId) {
		case R.id.element_social_buttons_share_button_container: {
			ShareUtils.startShareActivity(activity, activity.getResources().getString(R.string.app_name), broadcastWithChannelInfo.getShareUrl(), activity.getResources().getString(R.string.share_action_title));
			break;

		}
		}

	}



	public Runnable yesLoginProc() 
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



	public Runnable noLoginProc() 
	{
		return new Runnable()
		{
			public void run() 
			{
			}
		};
	}



	public Runnable yesLikeProc() 
	{
		return new Runnable() 
		{
			public void run()
			{
				likeIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like_default));
				//				mIsLiked = false;
			}
		};
	}



	public Runnable noLikeProc()
	{
		return new Runnable() 
		{
			public void run()
			{}
		};
	}
}