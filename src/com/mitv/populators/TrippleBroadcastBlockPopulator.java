
package com.mitv.populators;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.activities.broadcast_list_more.RepetitionsListMoreActivity;
import com.mitv.activities.broadcast_list_more.UpcomingListMoreActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.ui.elements.ReminderView;



public class TrippleBroadcastBlockPopulator 
	implements OnClickListener 
{
	private static String TAG = TrippleBroadcastBlockPopulator.class.getName();

	
	private static final int TOTAL_SHOWS_IN_INITIAL_LIST = 3;
	
	private static final int POSITION_ONE = 0;
	private static final int POSITION_TWO = 1;
	private static final int POSITION_THREE = 2;
	
	private Activity activity;
	private TVBroadcastWithChannelInfo runningBroadcast;
	
	private RelativeLayout containerView;
	private ReminderView reminderViewOne;
	private ReminderView reminderViewTwo;
	private ReminderView reminderViewThree;
	private View dividerView;

	/* If this is set to false, then the block populator is used for upcoming episodes */
	private boolean usedForRepetitions;

	
	
	public TrippleBroadcastBlockPopulator(
			final String tag, 
			final boolean usedForRepetitions, 
			final Activity activity, 
			final RelativeLayout containerView,
			final TVBroadcastWithChannelInfo runningBroadcast)
	{
		this.activity = activity;
		this.containerView = containerView;
		this.runningBroadcast = runningBroadcast;
		this.usedForRepetitions = usedForRepetitions;
	}
	
	
	
	@Override
	public void onClick(View view) 
	{
		int viewId = view.getId();
		
		switch (viewId) 
		{
			case R.id.block_broadcast_upcoming_one:
			case R.id.block_broadcast_upcoming_two:
			case R.id.block_broadcast_upcoming_three:
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
			final TVBroadcastWithChannelInfo broadcastWithChannelInfo = broadcastList.get(position);

			LinearLayout container = null;
			
			boolean setSmallIcon = true;

			switch (position) 
			{
				case POSITION_ONE: 
				{
					container = (LinearLayout) containerView.findViewById(R.id.block_broadcast_upcoming_one);
	
					reminderViewOne = (ReminderView) container.findViewById(R.id.block_tripple_broadcast_reminder_view);
					reminderViewOne.setBroadcast(broadcastWithChannelInfo);

					reminderViewOne.setSizeOfIcon(setSmallIcon);
	
					break;
				}
	
				case POSITION_TWO: 
				{
					container = (LinearLayout) containerView.findViewById(R.id.block_broadcast_upcoming_two);
	
					reminderViewTwo = (ReminderView) container.findViewById(R.id.block_tripple_broadcast_reminder_view);
					reminderViewTwo.setBroadcast(broadcastWithChannelInfo);

					reminderViewTwo.setSizeOfIcon(setSmallIcon);
	
					dividerView = containerView.findViewById(R.id.block_tripple_broadcast_one_bottom_divider);
					dividerView.setVisibility(View.VISIBLE);
	
					break;
				}
	
				case POSITION_THREE: 
				{
					container = (LinearLayout) containerView.findViewById(R.id.block_broadcast_upcoming_three);
	
					reminderViewThree = (ReminderView) container.findViewById(R.id.block_tripple_broadcast_reminder_view);
					reminderViewThree.setBroadcast(broadcastWithChannelInfo);

					reminderViewThree.setSizeOfIcon(setSmallIcon);
	
					dividerView = containerView.findViewById(R.id.block_tripple_broadcast_two_bottom_divider);
					dividerView.setVisibility(View.VISIBLE);
					
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unhandled position.");
					break;
				}
			}

			TextView seasonEpisodeTv = (TextView) container.findViewById(R.id.block_tripple_broadcast_season_episode);
			TextView titleTimeTv = (TextView) container.findViewById(R.id.block_tripple_broadcast_title_time);
			TextView channelTv = (TextView) container.findViewById(R.id.block_tripple_broadcast_channel);

			container.setVisibility(View.VISIBLE);

			titleTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			channelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			if(usedForRepetitions == false)
			{
				TVProgram programLocal = broadcastWithChannelInfo.getProgram();

				ProgramTypeEnum programTypeEnum = programLocal.getProgramType();

				switch (programTypeEnum) 
				{
					case TV_EPISODE: 
					{
						int season = programLocal.getSeason().getNumber().intValue();
	
						int episode = programLocal.getEpisodeNumber();
	
						StringBuilder seasonEpisodeSB = new StringBuilder();
	
						if (season > 0) 
						{
							seasonEpisodeSB.append(activity.getString(R.string.season))
							.append(" ")
							.append(season)
							.append(" ");
						}
	
						if (episode > 0)
						{
							seasonEpisodeSB.append(activity.getString(R.string.episode))
							.append(" ")
							.append(episode);
						}
	
						seasonEpisodeTv.setText(seasonEpisodeSB.toString());
						break;
					}
				
					default: 
					{
						seasonEpisodeTv.setText(programLocal.getTitle());
						break;
					}
				}

				seasonEpisodeTv.setVisibility(View.VISIBLE);
			}
			
			container.setOnClickListener(this);
			container.setTag(broadcastWithChannelInfo);
		}
	}
	
	
	
	public void createBlock(final ArrayList<TVBroadcastWithChannelInfo> repeatingOrUpcomingBroadcasts) 
	{
		/* Remove running broadcast */
		repeatingOrUpcomingBroadcasts.remove(runningBroadcast);
	
		TextView title = (TextView) containerView.findViewById(R.id.block_tripple_broadcast_title_textview);

		String titleString;

		String showMoreString;
		
		if (usedForRepetitions) 
		{
			ProgramTypeEnum programType = runningBroadcast.getProgram().getProgramType();
			
			switch (programType) 
			{
				case TV_EPISODE: 
				{
					titleString = activity.getString(R.string.repetitions_episode);
					break;
				}
				
				case MOVIE: 
				{
					titleString = activity.getString(R.string.repetitions_movie);
					break;
				}
				
				case SPORT: 
				{
					titleString = activity.getString(R.string.repetitions_sport_event);
					break;
				}
				
				case OTHER:
				{
					titleString = activity.getString(R.string.repetitions_other);
					break;
				}
				
				default: 
				{
					titleString = "";
					Log.w(TAG, "Unhandled program type.");
					break;
				}
			}

			showMoreString = activity.getString(R.string.repetitions_more);
		}
		else 
		{
			titleString = activity.getString(R.string.upcoming_episodes);

			showMoreString = activity.getString(R.string.upcoming_episodes_more);
		}

		title.setText(titleString);

		populatePartOfBlock(POSITION_ONE, repeatingOrUpcomingBroadcasts);
		populatePartOfBlock(POSITION_TWO, repeatingOrUpcomingBroadcasts);
		populatePartOfBlock(POSITION_THREE, repeatingOrUpcomingBroadcasts);

		if(repeatingOrUpcomingBroadcasts.size() > TOTAL_SHOWS_IN_INITIAL_LIST) 
		{
			View divider = (View) containerView.findViewById(R.id.block_tripple_broadcast_three_bottom_divider);

			divider.setVisibility(View.VISIBLE);

			TextView showMoreTxt = (TextView) containerView.findViewById(R.id.block_tripple_broadcast_more_textview);

			showMoreTxt.setText(showMoreString);
			
			showMoreTxt.setVisibility(View.VISIBLE);

			showMoreTxt.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Runnable procedure = getConfirmRemovalProcedure(repeatingOrUpcomingBroadcasts);
					procedure.run();
				}
			});
		}

		containerView.setVisibility(View.VISIBLE);
	}
		
	
	
	private Runnable getConfirmRemovalProcedure(final ArrayList<TVBroadcastWithChannelInfo> repeatingOrUpcomingBroadcasts) 
	{
		return new Runnable() 
		{
			public void run() 
			{
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(runningBroadcast);

				if (usedForRepetitions) 
				{
					Intent intent = new Intent(activity, RepetitionsListMoreActivity.class);

					ContentManager.sharedInstance().setRepeatingBroadcasts(runningBroadcast, repeatingOrUpcomingBroadcasts);

					activity.startActivity(intent);
				} 
				else
				{
					Intent intent = new Intent(activity, UpcomingListMoreActivity.class);

					ContentManager.sharedInstance().setUpcomingBroadcasts(runningBroadcast, repeatingOrUpcomingBroadcasts);

					activity.startActivity(intent);
				}
			}
		};
	}
}