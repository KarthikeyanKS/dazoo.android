
package com.mitv.listadapters;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVProgram;
import com.mitv.models.sql.NotificationDataSource;
import com.mitv.models.sql.NotificationSQLElement;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;



public class RemindersListAdapter 
	extends BaseAdapter
{
	private static final String TAG = RemindersListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	private int	currentPosition;

	
	
	public RemindersListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> mBroadcasts) 
	{
		this.broadcasts = mBroadcasts;
		this.activity = activity;		
		this.currentPosition = -1;
	}

	
	
	@Override
	public int getCount() 
	{
		if (broadcasts != null) 
		{
			return broadcasts.size();
		} 
		else 
		{
			return 0;
		}
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
			viewHolder.mReminderIconIv = (FontTextView) rowView.findViewById(R.id.row_reminders_notification_iv);
			viewHolder.mReminderIconIv.setTag(Integer.valueOf(position));

			viewHolder.mDividerView = (View) rowView.findViewById(R.id.row_reminders_header_divider);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);
		
		if (broadcastWithChannelInfo != null)
		{
			// If first or the previous broadcast is not the same date, then show the header.
			holder.mHeaderContainer.setVisibility(View.GONE);
			
			holder.mDividerView.setVisibility(View.VISIBLE);

			int prevPos = Math.max(position - 1, 0);
			
			TVBroadcastWithChannelInfo broadcastPreviousPosition = getItem(prevPos);
			
			int nextPos = Math.min(position + 1, (broadcasts.size() - 1));
			
			TVBroadcastWithChannelInfo broadcastNextPosition = getItem(nextPos);

			String stringCurrent = broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString();
			
			String stringPrevious = broadcastPreviousPosition.getBeginTimeDayAndMonthAsString();
			
			String stringNext = broadcastNextPosition.getBeginTimeDayAndMonthAsString();

			if ((position == 0) ||
				stringCurrent.equals(stringPrevious) == false) 
			{
				StringBuilder headerSB = new StringBuilder();
				headerSB.append(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekAsString());
				headerSB.append(" ");
				headerSB.append(broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				
				holder.mHeaderTv.setText(headerSB.toString());
				
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}

			if (position != (getCount() - 1) && 
				stringCurrent.equals(stringNext) == false) 
			{
				holder.mDividerView.setVisibility(View.GONE);
			}

			TVProgram tvProgram = broadcastWithChannelInfo.getProgram();
			
			if (tvProgram != null)
			{
				holder.mBroadcastTitleTv.setText(tvProgram.getTitle());
				
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				ProgramTypeEnum programType = tvProgram.getProgramType();
				
				switch(programType)
				{
					case TV_EPISODE:
					{
						StringBuilder seasonEpisodeSB = new StringBuilder();
						
						int season = broadcastWithChannelInfo.getProgram().getSeason().getNumber().intValue();
						
						if (season > 0) 
						{
							seasonEpisodeSB.append(activity.getResources().getString(R.string.season));
							seasonEpisodeSB.append(" ");
							seasonEpisodeSB.append(season);
							seasonEpisodeSB.append(" ");
						}
						
						int episode = broadcastWithChannelInfo.getProgram().getEpisodeNumber();
						
						if (episode > 0) 
						{
							seasonEpisodeSB.append(activity.getResources().getString(R.string.episode));
							seasonEpisodeSB.append(" ");
							seasonEpisodeSB.append(episode);
							seasonEpisodeSB.append(" ");
						}
						
						holder.mBroadcastDetailsTv.setText(seasonEpisodeSB.toString());
						holder.mBroadcastTitleTv.setText(tvProgram.getSeries().getName());
						break;
					}
					
					case MOVIE:
					{
						holder.mBroadcastDetailsTv.setText(tvProgram.getGenre() + " " + tvProgram.getYear());
						break;
					}
					
					case SPORT:
					{
						if (tvProgram.getTournament() != null) 
						{
							holder.mBroadcastDetailsTv.setText(tvProgram.getTournament());
						}
						else 
						{
							holder.mBroadcastDetailsTv.setText(tvProgram.getSportType().getName());
						}
						break;
					}
					
					case OTHER:
					{
						String category = tvProgram.getCategory();
						
						holder.mBroadcastDetailsTv.setText(category);
						break;
					}
					
					case UNKNOWN:
					default:
					{
						Log.w(TAG, "Unhandled program type.");
						break;
					}
				}
			}
			
			final TVChannel tvChannel = broadcastWithChannelInfo.getChannel();

			holder.mChannelTv.setText(tvChannel.getName());

			holder.mBroadcastTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			
			holder.mReminderIconIv.setTextColor(activity.getResources().getColor(R.color.blue1));
			
			holder.mInformationContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					
					activity.startActivity(intent);
				}
			});

			holder.mReminderIconIv.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					currentPosition = (Integer) v.getTag();

					NotificationDataSource notificationDataSource = new NotificationDataSource(activity);
	
					NotificationSQLElement notificationDbItem = notificationDataSource.getNotification(tvChannel.getChannelId(), broadcastWithChannelInfo.getBeginTime());
					
					if (notificationDbItem != null) 
					{
						int notificationId = notificationDbItem.getNotificationId();
						
						DialogHelper.showRemoveNotificationDialog(activity, broadcastWithChannelInfo, notificationId, confirmRemoval(), null);
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
		public FontTextView mReminderIconIv;
		public View	mDividerView;
	}
	
	

	public Runnable confirmRemoval() 
	{
		return new Runnable() 
		{
			public void run() 
			{
				if(currentPosition >= 0 && 
				   currentPosition < broadcasts.size()) 
				{
					broadcasts.remove(currentPosition);
					
					notifyDataSetChanged();
				}
				else
				{
					Log.e(TAG, "Current position is out of bounds.");
				}
			}
		};
	}
}
