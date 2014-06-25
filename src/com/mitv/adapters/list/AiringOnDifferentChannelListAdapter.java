
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

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class AiringOnDifferentChannelListAdapter 
	extends BaseAdapter
{
	@SuppressWarnings("unused")
	private static final String TAG = AiringOnDifferentChannelListAdapter.class.getName();

	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private final ArrayList<TVBroadcastWithChannelInfo> broadcasts;

	
	
	public AiringOnDifferentChannelListAdapter(
			final Activity activity, 
			final ArrayList<TVBroadcastWithChannelInfo> broadcasts) 
	{
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.broadcasts = new ArrayList<TVBroadcastWithChannelInfo>(broadcasts);
		
		this.activity = activity;
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = (TVBroadcastWithChannelInfo) getItem(position);

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
			TVProgram program = broadcastWithChannelInfo.getProgram();
			
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
			
			ImageSetOrientation imageSetOrientation = program.getImages();
			
			boolean containsPortraitOrientation = imageSetOrientation.containsPortraitImageSet();
			
			if(containsPortraitOrientation)
			{
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(imageSetOrientation.getPortrait().getImageURLForDeviceDensityDPI(), imageAware);
			}
			
			holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			holder.mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			StringBuilder titleSB = new StringBuilder();
			
			StringBuilder descriptionSB = new StringBuilder();
			
			if(program.isPopular())
			{
				String stringIconTrending = activity.getString(R.string.icon_trending);
					
				titleSB.append(stringIconTrending)
				.append(" ");
			}
			
			ProgramTypeEnum programType = program.getProgramType();

			switch (programType) 
			{
				case MOVIE: 
				{
					titleSB.append(activity.getString(R.string.icon_movie))
					.append(" ");
					
					descriptionSB.append(program.getGenre())
					.append(" ")
					.append(program.getYear());
					
					break;
				}
				
				case TV_EPISODE: 
				{
					String seasonAndEpisodeString = broadcastWithChannelInfo.buildSeasonAndEpisodeString();
					
					descriptionSB.append(seasonAndEpisodeString);
					
					break;
				}
				
				case SPORT: 
				{
					if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ");
					}
	
					descriptionSB.append(program.getSportType().getName())
					.append(": ")
					.append(program.getTournament());
					
					break;
				}
				
				case OTHER: 
				{
					if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ");
					}
					
					descriptionSB.append(program.getCategory());
					
					break;
				}
				
				default: 
				{
					// Do nothing
					break;
				}
			}
			
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
				
				ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				activity.startActivity(intent);
			}
		});
		
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
