
package com.mitv.adapters;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.utilities.DialogHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.interfaces.RemindersCountInterface;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationSQLElement;



public class RemindersListAdapter 
	extends BaseAdapter
{
	@SuppressWarnings("unused")
	private static final String TAG = RemindersListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	private RemindersCountInterface	remindersCountInterface;
	
	private int	notificationId;
	private int	currentPosition;

	
	
	public RemindersListAdapter(Activity mActivity, ArrayList<TVBroadcastWithChannelInfo> mBroadcasts, RemindersCountInterface remindersInterface) 
	{
		this.broadcasts = mBroadcasts;
		this.activity = mActivity;
		this.remindersCountInterface = remindersInterface;
		
		this.currentPosition = -1;
	}

	
	
	@Override
	public int getCount() 
	{
		if (broadcasts != null) 
		{
			return broadcasts.size();
		} 
		else return 0;
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		if (broadcasts != null) 
		{
			return broadcasts.get(position);
		} 
		else 
		{
			return null;	
		}
	}

	
	
	@Override
	public long getItemId(int arg0) 
	{
		return -1;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = layoutInflater.inflate(R.layout.row_reminders, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_reminders_header_container);
			viewHolder.mInformationContainer = (RelativeLayout) rowView.findViewById(R.id.row_reminders_text_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.row_reminders_header_textview);
			viewHolder.mBroadcastTitleTv = (TextView) rowView.findViewById(R.id.row_reminders_text_title_tv);
			viewHolder.mBroadcastDetailsTv = (TextView) rowView.findViewById(R.id.row_reminders_text_details_tv);
			viewHolder.mBroadcastTimeTv = (TextView) rowView.findViewById(R.id.row_reminders_text_time_tv);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.row_reminders_text_channel_tv);
			viewHolder.mReminderIconIv = (ImageView) rowView.findViewById(R.id.row_reminders_notification_iv);
			viewHolder.mReminderIconIv.setTag(Integer.valueOf(position));

			viewHolder.mDividerView = (View) rowView.findViewById(R.id.row_reminders_header_divider);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final TVBroadcastWithChannelInfo broadcast = getItem(position);
		
		if (broadcast != null) 
		{
			final TVChannel channel = broadcast.getChannel();
			
			TVProgram program = broadcast.getProgram();

			// If first or the previous broadcast is not the same date, show header.
			holder.mHeaderContainer.setVisibility(View.GONE);
			
			holder.mDividerView.setVisibility(View.VISIBLE);

			int prevPos = Math.max(position - 1, 0);

			int nextPos = Math.min(position + 1, (broadcasts.size() - 1));
			
			TVBroadcastWithChannelInfo broadcastPreviousPosition = getItem(prevPos);
			
			TVBroadcastWithChannelInfo broadcastNextPosition = getItem(nextPos);

			String stringCurrent = broadcast.getBeginTimeDayAndMonthAsString();
			
			String stringPrevious = broadcastPreviousPosition.getBeginTimeDayAndMonthAsString();
			
			String stringNext = broadcastNextPosition.getBeginTimeDayAndMonthAsString();

			if ((position == 0) || !stringCurrent.equals(stringPrevious)) 
			{
				holder.mHeaderTv.setText(broadcast.getBeginTimeDayOfTheWeekAsString() + " " + broadcast.getBeginTimeDayAndMonthAsString());
				
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}

			if (position != (getCount() - 1) && !stringCurrent.equals(stringNext)) 
			{
				holder.mDividerView.setVisibility(View.GONE);
			}

			if (program != null)
			{
				holder.mBroadcastTitleTv.setText(program.getTitle());
				
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				ProgramTypeEnum programType = program.getProgramType();
				
				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType))
				{
					String season = broadcast.getProgram().getSeason().getNumber().toString();
					
					int seasonNumber = Integer.parseInt(season);
					
					int episode = broadcast.getProgram().getEpisodeNumber();
					
					String seasonEpisode = "";
					
					if (seasonNumber > 0) 
					{
						seasonEpisode += activity.getResources().getString(R.string.season) + " " + season + " ";
					}
					
					if (episode > 0) 
					{
						seasonEpisode += activity.getResources().getString(R.string.episode) + " " + episode;
					}
					
					holder.mBroadcastDetailsTv.setText(seasonEpisode);
				} 
				else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) 
				{
					holder.mBroadcastDetailsTv.setText(program.getGenre() + " " + program.getYear());
				} 
				else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) 
				{
					String category = program.getCategory();
					holder.mBroadcastDetailsTv.setText(category);
				} 
				else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) 
				{
					if (program.getTournament() != null) 
					{
						holder.mBroadcastDetailsTv.setText(program.getTournament());
					}
					else 
					{
						holder.mBroadcastDetailsTv.setText(program.getSportType().getName());
					}
				}
			}

			if (channel != null) 
			{
				holder.mChannelTv.setText(channel.getName());
			}

			holder.mBroadcastTimeTv.setText(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			
			holder.mInformationContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					//TODO NewArc dont we have TVBroadcastWithChannelInfo here?
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, true);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_PROFILE, true);
					activity.startActivity(intent);
				}
			});

			holder.mReminderIconIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			
			holder.mReminderIconIv.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					currentPosition = (Integer) v.getTag();

					NotificationDataSource notificationDataSource = new NotificationDataSource(activity);

					NotificationSQLElement notificationDbItem = notificationDataSource.getNotification(channel.getChannelId().getChannelId(), broadcast.getBeginTime());
					
					if (notificationDbItem != null) 
					{
						notificationId = notificationDbItem.getNotificationId();
						
						DialogHelper.showRemoveNotificationDialog(activity, broadcast, notificationId, confirmRemoval(), cancelRemoval());
					}
				}
			});
		}
		return rowView;
	}

	

	private static class ViewHolder 
	{
		public RelativeLayout mHeaderContainer;
		public RelativeLayout mInformationContainer;
		public TextView	mHeaderTv;
		public TextView	mBroadcastTitleTv;
		public TextView	mBroadcastDetailsTv;
		public TextView	mBroadcastTimeTv;
		public TextView	mChannelTv;
		public ImageView mReminderIconIv;
		public View	mDividerView;
	}
	
	

	public Runnable confirmRemoval() 
	{
		return new Runnable() 
		{
			public void run() 
			{
				if(currentPosition >= 0 && currentPosition < broadcasts.size()) 
				{
					broadcasts.remove(currentPosition);
					
					remindersCountInterface.setValues(broadcasts.size());
					
					notifyDataSetChanged();
				}
			}
		};
	}
	
	

	public Runnable cancelRemoval() 
	{
		return new Runnable() 
		{
			public void run(){}
		};
	}
}
