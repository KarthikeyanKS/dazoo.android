
package com.mitv.adapters.list;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class TVGuideCompetitionTagListAdapter 
	extends BaseAdapter
{
	@SuppressWarnings("unused")
	private static final String TAG = TVGuideCompetitionTagListAdapter.class.getName();

	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	
	
	public TVGuideCompetitionTagListAdapter(
			final Activity activity, 
			final String fragmentName, 
			final ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts, 
			final int currentPosition) 
	{
		super();
		
		this.taggedBroadcasts = taggedBroadcasts;
		
		this.activity = activity;
		
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (taggedBroadcasts != null) 
		{
			count = taggedBroadcasts.size();
		}
		
		return count;
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = null;
		
		if (taggedBroadcasts != null)
		{
			broadcastWithChannelInfo = taggedBroadcasts.get(position);
		}
		
		return broadcastWithChannelInfo;
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
		
		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = layoutInflater.inflate(R.layout.element_poster_broadcast, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_container);
			viewHolder.mImageIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
			viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
			viewHolder.mTimeLeftTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
			viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null) 
		{
			if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) 
			{
				LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, holder.mDurationPb, holder.mTimeLeftTv);
			} 
			else 
			{
				holder.mDurationPb.setVisibility(View.GONE);
				holder.mTimeLeftTv.setVisibility(View.GONE);
			}

			ImageAware imageAware = new ImageViewAware(holder.mImageIv, false);
			
			ImageSetOrientation imageSetOrientation = broadcastWithChannelInfo.getProgram().getImages();
			
			boolean containsPortraitOrientation = imageSetOrientation.containsPortraitImageSet();
			
			if(containsPortraitOrientation)
			{
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getImageURLForDeviceDensityDPI(), imageAware);
			}
			
			holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			holder.mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			StringBuilder titleSB = new StringBuilder();
			
			StringBuilder descriptionSB = new StringBuilder();
			
			if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
			{
				titleSB.append(activity.getString(R.string.icon_live))
				.append(" ");
			}
			
			if (broadcastWithChannelInfo.getProgram().getSportType() != null) {
				descriptionSB.append(broadcastWithChannelInfo.getProgram().getSportType().getName())
				.append(": ")
				.append(broadcastWithChannelInfo.getProgram().getTournament());
				
				titleSB.append(broadcastWithChannelInfo.getTitle());
				
				holder.mTitleTv.setText(titleSB.toString());
				holder.mDescTv.setText(descriptionSB.toString());
			}

			holder.mContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					
					ContentManager.sharedInstance().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					ContentManager.sharedInstance().setSelectedTVChannelId(broadcastWithChannelInfo.getChannel().getChannelId());
					
					/* FIFA - Navigation to event page */
					ArrayList<String> tags = broadcastWithChannelInfo.getProgram().getTags();

					if (tags != null && !tags.isEmpty()) 
					{
						for (int i = 0; i < tags.size(); i++) 
						{
							if (tags.get(i).equals(Constants.FIFA_TAG_ID)) 
							{
								long eventId = broadcastWithChannelInfo.getEventId();
								
								if (eventId > 0) {
									/*
									 * WARNING WARNING WARNING
									 * 
									 * Hard coded competition ID used here.
									 * 
									 */
									Competition competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(Constants.FIFA_COMPETITION_ID);

									/* Changing the already existing intent to competition event page */
									intent = new Intent(activity, EventPageActivity.class);

									intent.putExtra(Constants.INTENT_COMPETITION_ID, competition.getCompetitionId());

									intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, eventId);

									intent.putExtra(Constants.INTENT_COMPETITION_NAME, competition.getDisplayName());
								}
							}

						}
					}
					
					activity.startActivity(intent);
				}
			});
		}

		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private RelativeLayout mContainer;
		private ImageView mImageIv;
		private TextView mTitleTv;
		private TextView mTimeTv;
		private TextView mChannelTv;
		private TextView mDescTv;
		private TextView mTimeLeftTv;
		private ProgressBar mDurationPb;
	}
	
}
