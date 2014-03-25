
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

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class BlockPopularListViewAdapter 
	extends BaseAdapter 
{
	private static final String	TAG	= BlockPopularListViewAdapter.class.getName();

	
	private LayoutInflater mLayoutInflater;
	private Activity mActivity;
	private ArrayList<TVBroadcastWithChannelInfo> mPopularBroadcasts;

	
	public BlockPopularListViewAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts)
	{
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
	}

	
	@Override
	public int getCount() 
	{
		if (mPopularBroadcasts != null) 
		{
			return mPopularBroadcasts.size();
		} 
		else
		{	
			return 0;
		}
	}

	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position)
	{
		if (mPopularBroadcasts != null) 
		{
			return mPopularBroadcasts.get(position);
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
		
		Log.d(TAG,"BROADCAST NAME: " + broadcastWithChannelInfo.getProgram().getTitle());

		if (rowView == null) 
		{
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = mLayoutInflater.inflate(R.layout.block_feed_popular_listitem, null);
			
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
			// different details about the broadcast program depending on the type
			ImageAware imageAware = new ImageViewAware(holder.mPoster, false);
			
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);
			
			//TODO NewArc verify that getBeginTimeDayOfTheWeekWithHourAndMinuteAsString is what we want here
			holder.mTime.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			holder.mChannelName.setText(broadcastWithChannelInfo.getChannel().getName());
			
			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
			
			if (programType == ProgramTypeEnum.TV_EPISODE) 
			{
				holder.mTitle.setText(broadcastWithChannelInfo.getProgram().getSeries().getName());
			} 
			else 
			{
				holder.mTitle.setText(broadcastWithChannelInfo.getProgram().getTitle());
			}
			
			
			switch (programType) 
			{
				case MOVIE: 
				{
					holder.mDetails.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
					break;
				}
				
				case TV_EPISODE: 
				{
					holder.mDetails.setText(mActivity.getString(R.string.season) + " " + broadcastWithChannelInfo.getProgram().getSeason().getNumber() + " "
							+ mActivity.getString(R.string.episode) + " " + broadcastWithChannelInfo.getProgram().getEpisodeNumber());
					break;
				}
				
				case SPORT:
				{
					holder.mDetails.setText(broadcastWithChannelInfo.getProgram().getSportType().getName() + " " + broadcastWithChannelInfo.getProgram().getTournament());
					break;
				}
				
				case OTHER:
				{
					holder.mDetails.setText(broadcastWithChannelInfo.getProgram().getCategory());
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
				Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
				
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				mActivity.startActivity(intent);
			}
		});

		return rowView;
	}

	
	
	private static class ViewHolder 
	{
		LinearLayout	mContainer;
		ImageView		mPoster;
		TextView		mTitle;
		TextView		mTime;
		TextView		mChannelName;
		TextView		mDetails;
	}
}
