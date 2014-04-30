
package com.mitv.listadapters;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionTagListAdapter extends AdListAdapter<TVBroadcastWithChannelInfo> 
{
	private static final String TAG = CompetitionTagListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private int currentPosition;

	
	
	public CompetitionTagListAdapter(Activity activity, String fragmentName, ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts, int currentPosition)
	{
		super(fragmentName, activity, taggedBroadcasts, Constants.AD_UNIT_ID_GUIDE_ACTIVITY);
		this.taggedBroadcasts = taggedBroadcasts;
		this.activity = activity;
		this.currentPosition = currentPosition;
	}

	
	
	@Override
	public int getViewTypeCount() 
	{
		return super.getViewTypeCount() + 1;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Log.d(TAG, "Fetching index " + position);
		
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if (rowView == null)
		{
			rowView = getViewForCompetitionCell(position, convertView, parent);
		}

		return rowView;
	}

	
	
	public View getViewForCompetitionCell(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;

		// Get the item with the displacement depending on the scheduled time on air
		int indexForBroadcast = currentPosition + position;
		
		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(indexForBroadcast);

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = layoutInflater.inflate(R.layout.row_competition_list, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.competition_item_container);
			viewHolder.startTime = (TextView) rowView.findViewById(R.id.row_competition_page_begin_time_broadcast);
			viewHolder.channelInfo = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			viewHolder.teamOneFlag = (ImageView) rowView.findViewById(R.id.row_competition_team_one_flag);
			viewHolder.teamTwoFlag = (ImageView) rowView.findViewById(R.id.row_competition_team_two_flag);
			viewHolder.teamOneName = (TextView) rowView.findViewById(R.id.row_competition_team_one_name);
			viewHolder.teamTwoName = (TextView) rowView.findViewById(R.id.row_competition_team_two_name);
			
			
			// old
			// save progressbar and timeleftduration..
			
//			viewHolder.mImageIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
//			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
//			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
//			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
//			viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
//			viewHolder.mTimeLeftTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
//			viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null) 
		{
			if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) 
			{
//				LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, holder.mDurationPb, holder.mTimeLeftTv);
			} 
			else 
			{
//				holder.mDurationPb.setVisibility(View.GONE);
//				holder.mTimeLeftTv.setVisibility(View.GONE);
			}

			ImageAware imageAwareOne = new ImageViewAware(holder.teamOneFlag, false);
			ImageAware imageAwareTwo = new ImageViewAware(holder.teamTwoFlag, false);
			
			
//			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);

//			holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
//			holder.mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			StringBuilder titleSB = new StringBuilder();
			
			StringBuilder descriptionSB = new StringBuilder();
			
			if(broadcastWithChannelInfo.isPopular())
			{
//				String stringIconTrending = activity.getString(R.string.icon_trending);
//				
//				titleSB.append(stringIconTrending)
//				.append(" ");
			}
			
			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
			
			if (programType.equals(ProgramTypeEnum.SPORT)) {
				
				if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
				{
					titleSB.append(activity.getString(R.string.icon_live))
					.append(" ");
				}

				descriptionSB.append(broadcastWithChannelInfo.getProgram().getSportType().getName())
				.append(": ")
				.append(broadcastWithChannelInfo.getProgram().getTournament());
			}

			
			titleSB.append(broadcastWithChannelInfo.getTitle());
			
//			holder.mTitleTv.setText(titleSB.toString());
//			holder.mDescTv.setText(descriptionSB.toString());
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				/* Items in list will not be clickable at frist stage */
				
//				Intent intent = new Intent(activity, BroadcastPageActivity.class);
//				
//				ContentManager.sharedInstance().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
//				
//				activity.startActivity(intent);
			}
		});
		
		return rowView;
	}

	
	
	static class ViewHolder 
	{
		private RelativeLayout mContainer;
		private TextView startTime;
		private TextView channelInfo;
		private ImageView teamOneFlag;
		private ImageView teamTwoFlag;
		private TextView teamOneName;
		private TextView teamTwoName;
	}

	
	
	@Override
	public int getCount() 
	{
		if (taggedBroadcasts != null) 
		{
			return taggedBroadcasts.size() - currentPosition;
		} 
		else 
		{
			return 0;
		}
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		if (taggedBroadcasts != null) 
		{
			return taggedBroadcasts.get(position);
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
}
