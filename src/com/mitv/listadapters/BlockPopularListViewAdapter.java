
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BlockPopularListViewAdapter 
	extends BaseAdapter 
{
	private static final String	TAG	= BlockPopularListViewAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;

	
	
	public BlockPopularListViewAdapter(
			final Activity activity, 
			final ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts)
	{
		this.activity = activity;
		this.popularBroadcasts = popularBroadcasts;
	}

	
	
	@Override
	public int getCount() 
	{
		if (popularBroadcasts != null) 
		{
			return popularBroadcasts.size();
		} 
		else
		{	
			return 0;
		}
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position)
	{
		if (popularBroadcasts != null) 
		{
			return popularBroadcasts.get(position);
		} 
		else 
		{
			return null;
		}
	}

	
	
	@Override
	public long getItemId(int position) 
	{
		return -1;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);
		
		Log.d(TAG, "BROADCAST NAME: " + broadcastWithChannelInfo.getProgram().getTitle());

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = layoutInflater.inflate(R.layout.block_feed_popular_listitem, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.block_popular_feed_container);
			viewHolder.mPoster = (ImageView) rowView.findViewById(R.id.block_feed_popular_listitem_iv);
			viewHolder.mTitle = (TextView) rowView.findViewById(R.id.block_popular_feed_details_title_tv);
			viewHolder.mTime = (TextView) rowView.findViewById(R.id.block_popular_feed_details_time_tv);
			viewHolder.mChannelName = (TextView) rowView.findViewById(R.id.block_popular_feed_details_channel_tv);
			viewHolder.mDetails = (TextView) rowView.findViewById(R.id.block_popular_feed_details_extra_tv);
			
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null) 
		{
			// Different details about the broadcast program depending on the type
			ImageAware imageAware = new ImageViewAware(holder.mPoster, false);
			
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);
			
			holder.mTime.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			
			holder.mChannelName.setText(broadcastWithChannelInfo.getChannel().getName());
			
			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
			
			String title;
			
			if (programType == ProgramTypeEnum.TV_EPISODE) 
			{
				title = broadcastWithChannelInfo.getProgram().getSeries().getName();
				
				holder.mTitle.setText(title);
			} 
			else 
			{
				title = broadcastWithChannelInfo.getProgram().getTitle();
						
				holder.mTitle.setText(title);
			}
			
			StringBuilder detailsSB = new StringBuilder();
			
			switch (programType) 
			{
				case MOVIE: 
				{
					detailsSB.append(broadcastWithChannelInfo.getProgram().getGenre());
					detailsSB.append(" ");
					detailsSB.append(broadcastWithChannelInfo.getProgram().getYear());
					
					holder.mDetails.setText(detailsSB.toString());
					break;
				}
				
				case TV_EPISODE: 
				{
					detailsSB.append(activity.getString(R.string.season));
					detailsSB.append(" ");
					detailsSB.append(broadcastWithChannelInfo.getProgram().getSeason().getNumber());
					detailsSB.append(" ");
					detailsSB.append(activity.getString(R.string.episode));
					detailsSB.append(" ");
					detailsSB.append(broadcastWithChannelInfo.getProgram().getEpisodeNumber());
					
					holder.mDetails.setText(detailsSB.toString());
					break;
				}
				
				case SPORT:
				{
					detailsSB.append(broadcastWithChannelInfo.getProgram().getSportType().getName());
					detailsSB.append(" ");
					detailsSB.append(broadcastWithChannelInfo.getProgram().getTournament());
					
					holder.mDetails.setText(detailsSB.toString());
					break;
				}
				
				case OTHER:
				{
					detailsSB.append(broadcastWithChannelInfo.getProgram().getCategory());
					
					holder.mDetails.setText(detailsSB.toString());
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unhandled program type.");
					break;
				}
			}
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(activity, BroadcastPageActivity.class);
				
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				activity.startActivity(intent);
			}
		});

		return rowView;
	}

	
	
	private static class ViewHolder 
	{
		private LinearLayout mContainer;
		private ImageView mPoster;
		private TextView mTitle;
		private TextView mTime;
		private TextView mChannelName;
		private TextView mDetails;
	}
}
