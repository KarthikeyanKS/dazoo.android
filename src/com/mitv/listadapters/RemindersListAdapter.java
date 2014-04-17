
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

import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVProgram;
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
			holder.mHeaderContainer.setVisibility(View.GONE);
			
			holder.mDividerView.setVisibility(View.VISIBLE);
			
			boolean isFirstposition = (position == 0);
			
			boolean isLastPosition = (position == (getCount() - 1));

			int previousBroadcastPosition = Math.max(position - 1, 0);
			
			TVBroadcastWithChannelInfo previousBroadcastInList = getItem(previousBroadcastPosition);
			
			boolean isCurrentBroadcastBeginTimeEqualToPreviousBroadcastBeginTime = broadcastWithChannelInfo.isTheSameDayAs(previousBroadcastInList);

			int nextBroadcastPosition = Math.min(position + 1, (broadcasts.size() - 1));
			
			if (isFirstposition || isCurrentBroadcastBeginTimeEqualToPreviousBroadcastBeginTime == false) 
			{
				StringBuilder headerSB = new StringBuilder();
				headerSB.append(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekAsString());
				headerSB.append(" ");
				headerSB.append(broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				
				holder.mHeaderTv.setText(headerSB.toString());
				
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}

			TVBroadcastWithChannelInfo nextBroacastPosition = getItem(nextBroadcastPosition);
			
			boolean isCurrentBroadcastBeginTimeEqualToNextBroadcastBeginTime = broadcastWithChannelInfo.isTheSameDayAs(nextBroacastPosition);
			
			if (isLastPosition && isCurrentBroadcastBeginTimeEqualToNextBroadcastBeginTime == false) 
			{
				holder.mDividerView.setVisibility(View.GONE);
			}

			TVProgram tvProgram = broadcastWithChannelInfo.getProgram();
			
			if (tvProgram != null)
			{
				holder.mBroadcastTitleTv.setText(broadcastWithChannelInfo.getTitle());
				
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				ProgramTypeEnum programType = tvProgram.getProgramType();
				
				switch(programType)
				{
					case TV_EPISODE:
					{
						String seasonAndEpisodeString = broadcastWithChannelInfo.buildSeasonAndEpisodeString();
						
						holder.mBroadcastDetailsTv.setText(seasonAndEpisodeString);
						
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
					ContentManager.sharedInstance().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
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
		private RelativeLayout mHeaderContainer;
		private RelativeLayout mInformationContainer;
		private TextView mHeaderTv;
		private TextView mBroadcastTitleTv;
		private TextView mBroadcastDetailsTv;
		private TextView mBroadcastTimeTv;
		private TextView mChannelTv;
		private FontTextView mReminderIconIv;
		private View mDividerView;
	}
	
	

	private Runnable confirmRemoval() 
	{
		return new Runnable() 
		{
			public void run() 
			{
				if(currentPosition >= 0 && 
				   currentPosition < broadcasts.size()) 
				{
					TVBroadcastWithChannelInfo broadcastForReminderToDelete = broadcasts.get(currentPosition);
					broadcasts.remove(currentPosition);
					
					TrackingGAManager.sharedInstance().sendUserReminderEvent(activity, broadcastForReminderToDelete, true);
					
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
