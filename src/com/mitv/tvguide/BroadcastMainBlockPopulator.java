
package com.mitv.tvguide;



import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import com.millicom.mitv.utilities.DialogHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.ProgressBarUtils;
import com.mitv.utilities.ShareUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BroadcastMainBlockPopulator 
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

	
	
	public void createBlock(final TVBroadcastWithChannelInfo broadcast) 
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
		reminderView.setBroadcast(broadcast);

		likeIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_like_button_iv);
		ImageView mShareIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_share_button_iv);

		RelativeLayout likeContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_like_button_container);
		RelativeLayout shareContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_share_button_container);

		ProgressBar progressBar = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		TextView progressTxt = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);

		TVProgram program = broadcast.getProgram();
		
		ProgramTypeEnum programType = program.getProgramType();

		switch (programType) 
		{
			case TV_EPISODE: 
			{
				programId = broadcast.getProgram().getSeries().getSeriesId();
				contentTitle = broadcast.getProgram().getSeries().getName();
				
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
				break;
			}
			case SPORT: 
			{
				programId = broadcast.getProgram().getSportType().getSportTypeId();
				
				contentTitle = broadcast.getProgram().getSportType().getName();
				
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
				break;
			}
			default: 
			{
				contentTitle = broadcast.getProgram().getTitle();
				programId = broadcast.getProgram().getProgramId();
				titleTv.setText(program.getTitle());
				break;
			}
		}
		
		//TOOD use similar method to LikeService.getLikeType, but using new enums ProgramTypeEnum
		String mLikeType = null;//LikeService.getLikeType(programType);

		if (program.getImages().getPortrait().getLarge() != null && TextUtils.isEmpty(program.getImages().getPortrait().getLarge()) != true)
		{
			ImageAware imageAware = new ImageViewAware(posterIv, false);
			ImageLoader.getInstance().displayImage(program.getImages().getPortrait().getLarge(), imageAware);
		}

		if (broadcast.getChannel() != null) 
		{
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			ImageLoader.getInstance().displayImage(broadcast.getChannel().getImageUrl(), imageAware);
		}

		if (broadcast.isBroadcastCurrentlyAiring())   // broadcast is currently on air: show progress
		{
			ProgressBarUtils.setupProgressBar(activity, broadcast, progressBar, progressTxt);
			timeTv.setVisibility(View.GONE);
		}
		else  // broadcast is in the future: show time
		{
			timeTv.setText(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString() + " - " + broadcast.getEndTimeHourAndMinuteLocalAsString());
		}

		String synopsis = program.getSynopsisShort();
		
		if (TextUtils.isEmpty(synopsis) == false) 
		{
			synopsisTv.setText(program.getSynopsisShort());
			synopsisTv.setVisibility(View.VISIBLE);
		}

		int duration = broadcast.getBroadcastDurationInMinutes();
		
		String extras = "";
		
		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType))
		{
			extras = activity.getResources().getString(R.string.tv_series) + "  "
					+ ((program.getYear() == 0) ? "" : String.valueOf(program.getYear()) + "  ")
					+ ((duration == 0) ? "" : duration + " " + activity.getResources().getString(R.string.minutes) + "  ")
					+ ((program.getGenre() == null) ? "" : (program.getGenre()));
		} 
		else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) 
		{
			extras = activity.getResources().getString(R.string.movie) + "  " 
					+ ((program.getYear() == 0) ? "" : String.valueOf(program.getYear()) + "  ")
					+ ((duration == 0) ? "" : duration + " " + activity.getResources().getString(R.string.minutes) + "  ")
					+ ((program.getGenre() == null) ? "" : (program.getGenre()));
		} 
		else if (Consts.PROGRAM_TYPE_OTHER.equals(programType))
		{
			extras = program.getCategory() + "  "
					+ ((duration == 0) ? "" : duration + " " + activity.getResources().getString(R.string.minutes));
		} 
		else if (Consts.PROGRAM_TYPE_SPORT.equals(programType))
		{
			extras = activity.getResources().getString(R.string.sport) + "  "
					+ ((duration == 0) ? "" : duration + " " + activity.getResources().getString(R.string.minutes) + "  ")
					+ program.getSportType().getName();
		}
		
		extraTv.setText(extras);
		extraTv.setVisibility(View.VISIBLE);

		final boolean mIsLiked;
		
		if (ContentManager.sharedInstance().isLoggedIn()) 
		{
			// mIsLiked = LikeService.isLiked(mToken, broadcast.getProgram().getProgramId());
			mIsLiked = MiTVStore.getInstance().isInTheLikesList(programId);
		}
		else
		{
			mIsLiked = false;
		}

		if (mIsLiked) 
		{
			likeIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like_selected));
		}
		else
		{
			likeIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like_default));
		}

		likeContainer.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
				
				if(isLoggedIn) 
				{
					if (mIsLiked == false)
					{
						//TODO add like using new async task, THROUGH ContentManager
//						if (LikeService.addLike(mProgramId, mLikeType)) {
//							BroadcastPageActivity.toast = LikeService.showSetLikeToast(mActivity, mContentTitle);
//
//							MiTVStore.getInstance().addLikeIdToList(mProgramId);
//
//							mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
//
//							AnimationUtilities.animationSet(mLikeIv);
//
//							mIsLiked = true;
//						} else {
//							Log.d(TAG, "!!! Adding a like faced an error !!!");
//						}

					} 
					else 
					{
						//TODO remove like using new async task, THROUGH ContentManager
//						boolean removeSucceded = LikeService.removeLike(mLikeType, mProgramId);
//
//						if(removeSucceded)
//						{
//							MiTVStore.getInstance().deleteLikeIdFromList(mProgramId);
//	
//							mIsLiked = false;
//							mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
//						}
//						else
//						{
//							Log.w(TAG, "An error occured while removing a like from the webservice");
//						}
					}
				} 
				else 
				{
					if (BroadcastPageActivity.toast != null) 
					{
						BroadcastPageActivity.toast.cancel();
					}

					DialogHelper.showPromptSignInDialog(activity, yesLoginProc(), noLoginProc());
				}

			}
		});

		shareContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				ShareUtils.startShareActivity(activity, activity.getResources().getString(R.string.app_name), broadcast.getShareUrl(), activity.getResources().getString(R.string.share_action_title));
			}
		});

		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.setMargins(20, 20, 20, 10);
		
		containerView.addView(topContentView, layoutParams);
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