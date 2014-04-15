package com.mitv.populators;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.activities.broadcast_list_more.NowAiringListMoreActivity;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BroadcastNowAiringBlockPopulator 
	implements OnClickListener 
{
	private static String TAG = BroadcastNowAiringBlockPopulator.class.getName();

	
	private static final int TOTAL_SHOWS_IN_INITIAL_LIST = 3;
	
	private static final int POSITION_ONE = 0;
	private static final int POSITION_TWO = 1;
	private static final int POSITION_THREE = 2;
	
	
	private Activity activity;
	private TVBroadcastWithChannelInfo runningBroadcast;
	
	private RelativeLayout containerView;
	private View dividerView;

	
	
	public BroadcastNowAiringBlockPopulator(
			final Activity activity, 
			final RelativeLayout containerView,
			final TVBroadcastWithChannelInfo runningBroadcast)
	{
		this.activity = activity;
		this.containerView = containerView;
		this.runningBroadcast = runningBroadcast;
	}
	
	
	
	@Override
	public void onClick(View view) 
	{
		int viewId = view.getId();
		
		switch (viewId) 
		{
			case R.id.block_broadcast_airing_now_one:
			case R.id.block_broadcast_airing_now_two:
			case R.id.block_broadcast_airing_now_three:
			{
				TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) view.getTag();
				
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				Intent intent = new Intent(activity, BroadcastPageActivity.class);
				
				activity.startActivity(intent);
				
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unhandled on click action.");
				break;
			}
		}
	}

	
	
	public void populatePartOfBlock(
			final int position, 
			final ArrayList<TVBroadcastWithChannelInfo> broadcastList) 
	{
		if (broadcastList.size() > position && broadcastList.get(position) != null)
		{
			TVBroadcastWithChannelInfo broadcastWithChannelInfo = broadcastList.get(position);

			RelativeLayout container = null;
			
			switch(position) 
			{
				case POSITION_ONE: 
				{
					container = (RelativeLayout) containerView.findViewById(R.id.block_broadcast_airing_now_one);
					break;
				}
	
				case POSITION_TWO: 
				{
					container = (RelativeLayout) containerView.findViewById(R.id.block_broadcast_airing_now_two);
	
					dividerView = containerView.findViewById(R.id.block_broadcast_airing_now_one_bottom_divider);
					dividerView.setVisibility(View.VISIBLE);
	
					break;
				}
	
				case POSITION_THREE: 
				{
					container = (RelativeLayout) containerView.findViewById(R.id.block_broadcast_airing_now_three);
	
					dividerView = containerView.findViewById(R.id.block_broadcast_airing_now_two_bottom_divider);
					dividerView.setVisibility(View.VISIBLE);
					
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unhandled position.");
					break;
				}
			}
						
			container.setVisibility(View.VISIBLE);
			
			ImageView imageIv = (ImageView) container.findViewById(R.id.element_poster_broadcast_image_iv);
			TextView titleTv = (TextView) container.findViewById(R.id.element_poster_broadcast_title_tv);
			TextView timeTv = (TextView) container.findViewById(R.id.element_poster_broadcast_time_tv);
			TextView channelTv = (TextView) container.findViewById(R.id.element_poster_broadcast_channel_tv);
			TextView descriptionTv = (TextView) container.findViewById(R.id.element_poster_broadcast_type_tv);
			TextView timeLeftTv = (TextView) container.findViewById(R.id.element_poster_broadcast_timeleft_tv);
			ProgressBar durationPb = (ProgressBar) container.findViewById(R.id.element_poster_broadcast_progressbar);
			
			if (broadcastWithChannelInfo != null) 
			{
				LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, durationPb, timeLeftTv);

				ImageAware imageAware = new ImageViewAware(imageIv, false);
				
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);

				timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
				channelTv.setText(broadcastWithChannelInfo.getChannel().getName());

				ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();

				switch (programType) 
				{
					case MOVIE: 
					{
						StringBuilder titleSB = new StringBuilder();
						
						titleSB.append(activity.getString(R.string.icon_movie))
						.append(" ")
						.append(broadcastWithChannelInfo.getTitle());
						
						titleTv.setText(titleSB.toString());
						
						descriptionTv.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
						
						break;
					}
					
					case TV_EPISODE: 
					{
						titleTv.setText(broadcastWithChannelInfo.getTitle());
						
						String seasonAndEpisodeString = broadcastWithChannelInfo.buildSeasonAndEpisodeString();
						descriptionTv.setText(seasonAndEpisodeString);
						break;
					}
					
					case SPORT: 
					{
						if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
						{
							StringBuilder titleSB = new StringBuilder();
							
							titleSB.append(activity.getString(R.string.icon_live))
							.append(" ")
							.append(broadcastWithChannelInfo.getTitle());
							
							titleTv.setText(titleSB.toString());
						} 
						else 
						{
							titleTv.setText(broadcastWithChannelInfo.getTitle());
						}
		
						StringBuilder descriptionSB = new StringBuilder();
						
						descriptionSB.append(broadcastWithChannelInfo.getProgram().getSportType().getName())
						.append(": ")
						.append(broadcastWithChannelInfo.getProgram().getTournament());
						
						descriptionTv.setText(descriptionSB.toString());
						break;
					}
					
					case OTHER: 
					{
						if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
						{
							StringBuilder titleSB = new StringBuilder();
							
							titleSB.append(activity.getString(R.string.icon_live))
							.append(" ")
							.append(broadcastWithChannelInfo.getTitle());
							
							titleTv.setText(titleSB.toString());
						} 
						else 
						{
							titleTv.setText(broadcastWithChannelInfo.getTitle());
						}
						
						descriptionTv.setText(broadcastWithChannelInfo.getProgram().getCategory());
						break;
					}
					
					default: 
					{
						titleTv.setText("");
						descriptionTv.setText("");
						break;
					}
				}
			}

			container.setOnClickListener(this);
			container.setTag(broadcastWithChannelInfo);
		}
	}
	
	
	
	public void createBlock(final ArrayList<TVBroadcastWithChannelInfo> similarBroadcastsAiringNow) 
	{
		TextView title = (TextView) containerView.findViewById(R.id.block_broadcast_airing_now_title_textview);

		String titleString = activity.getString(R.string.similar_broadcasts_airing_now);

		title.setText(titleString);
		
		populatePartOfBlock(POSITION_ONE, similarBroadcastsAiringNow);
		populatePartOfBlock(POSITION_TWO, similarBroadcastsAiringNow);
		populatePartOfBlock(POSITION_THREE, similarBroadcastsAiringNow);

		if(similarBroadcastsAiringNow.size() > TOTAL_SHOWS_IN_INITIAL_LIST) 
		{
			View divider = (View) containerView.findViewById(R.id.block_broadcast_airing_now_three_bottom_divider);

			divider.setVisibility(View.VISIBLE);

			TextView showMoreTxt = (TextView) containerView.findViewById(R.id.block_broadcast_airing_now_show_more_textview);

			StringBuilder showMoreSB = new StringBuilder();
			
			showMoreSB.append(activity.getString(R.string.more_similar_broadcasts_airing_now))
					.append(" (")
					.append(similarBroadcastsAiringNow.size())
					.append(")");
			
			showMoreTxt.setText(showMoreSB.toString());
			
			showMoreTxt.setVisibility(View.VISIBLE);

			showMoreTxt.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Runnable procedure = getProcedure(similarBroadcastsAiringNow);
					procedure.run();
				}
			});
		}

		containerView.setVisibility(View.VISIBLE);
	}
	
	
	
	
	private Runnable getProcedure(final ArrayList<TVBroadcastWithChannelInfo> similarBroadcastsAiringNow) 
	{
		return new Runnable() 
		{
			public void run() 
			{
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(runningBroadcast);
				
				Intent intent = new Intent(activity, NowAiringListMoreActivity.class);

				activity.startActivity(intent);
			}
		};
	}
}
