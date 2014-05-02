
package com.mitv.adapters.pager;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.adapters.list.AdListAdapter;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionTabEventsListAdapter
	extends AdListAdapter<Event> 
{
	private static final String TAG = CompetitionTabEventsListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private int currentPosition;
	
	private List<Event> events;
	
	

	
	
	public CompetitionTabEventsListAdapter(
			final Activity activity, 
			final String fragmentName, 
			final List<Event> events, 
			final int currentPosition)
	{
		super(fragmentName, activity, events, Constants.AD_UNIT_ID_GUIDE_ACTIVITY);
		
		this.events = events;
		this.activity = activity;
		this.currentPosition = currentPosition;
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
		
		final Event event = getItem(indexForBroadcast);

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = layoutInflater.inflate(R.layout.row_competition_event_list_item, null);
			
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

		if (event != null) 
		{
			ImageAware imageAwareOne = new ImageViewAware(holder.teamOneFlag, false);
			ImageAware imageAwareTwo = new ImageViewAware(holder.teamTwoFlag, false);
			
			
//			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);

//			holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
//			holder.mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			StringBuilder titleSB = new StringBuilder();
			
			StringBuilder descriptionSB = new StringBuilder();
			
//			holder.mTitleTv.setText(titleSB.toString());
//			holder.mDescTv.setText(descriptionSB.toString());
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// TODO: Unimplemented at this stage
			}
		});
		
		return rowView;
	}

	
	
	@Override
	public int getCount() 
	{
		if (events != null) 
		{
			return events.size();
		} 
		else 
		{
			return 0;
		}
	}

	
	
	@Override
	public Event getItem(int position) 
	{
		if (events != null) 
		{
			return events.get(position);
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
	
	
	
	private static class ViewHolder 
	{
		private RelativeLayout mContainer;
		private TextView startTime;
		private TextView channelInfo;
		private ImageView teamOneFlag;
		private ImageView teamTwoFlag;
		private TextView teamOneName;
		private TextView teamTwoName;
	}
}
