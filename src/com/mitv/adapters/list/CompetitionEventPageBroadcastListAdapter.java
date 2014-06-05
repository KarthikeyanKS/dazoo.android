
package com.mitv.adapters.list;



import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.ui.elements.ReminderView;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionEventPageBroadcastListAdapter 
	extends BaseAdapter
{
	@SuppressWarnings("unused")
	private static final String TAG = CompetitionEventsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private long competitionId;
	private long eventId;
	private List<EventBroadcast> broadcastDetails;
	
	
	
	public CompetitionEventPageBroadcastListAdapter(
			final Activity activity,
			final long competitionId,
			final long eventId,
			final List<EventBroadcast> broadcastDetails)
	{
		super();
		
		this.competitionId = competitionId;
		
		this.eventId = eventId;
		
		this.broadcastDetails = broadcastDetails;
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (broadcastDetails != null) 
		{
			count = broadcastDetails.size();
		}
		
		return count;
	}

	
	
	@Override
	public EventBroadcast getItem(int position) 
	{
		EventBroadcast item = null;
		
		if (broadcastDetails != null)
		{
			item = broadcastDetails.get(position);
		}
		
		return item;
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
			rowView = layoutInflater.inflate(R.layout.row_competition_event_broadcast_list_item, null);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.channelLogo = (ImageView) rowView.findViewById(R.id.competition_event_channel_logo);
			viewHolder.beginTime = (TextView) rowView.findViewById(R.id.competition_event_full_date);
			viewHolder.reminderView = (ReminderView) rowView.findViewById(R.id.competition_event_row_reminder_view);
			viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.competition_event_broadcast_progressbar);
			viewHolder.onGoingTimeLeft = (TextView) rowView.findViewById(R.id.competition_event_time_left_ongoing);
			viewHolder.divider = (View) rowView.findViewById(R.id.competition_event_broadcast_divider);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (holder != null)
		{
			final EventBroadcast element = getItem(position);

			boolean isAiring = element.isAiring();
			boolean hasEnded = element.hasEnded();

			ImageAware imageAwareForChannelLogo = new ImageViewAware(holder.channelLogo, false);

			String logoUrl = element.getChannelLogoUrl();

			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(logoUrl, imageAwareForChannelLogo);

			/* Set airing date and time */
			StringBuilder sb = new StringBuilder();

			String startTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(element.getEventBroadcastBeginTimeLocal());

			String endTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(element.getEventBroadcastEndTimeLocal());

			/* Event has ended */
			if(hasEnded) 
			{
				holder.beginTime.setTextColor(activity.getResources().getColor(R.color.grey1));

				sb.append(element.getEventTimeDayOfTheWeekAsString())
				.append(",  ")
				.append(startTimeHourAndMinuteAsString)
				.append(" - ")
				.append(endTimeHourAndMinuteAsString);

				holder.progressBar.setVisibility(View.GONE);
				holder.onGoingTimeLeft.setVisibility(View.GONE);
				holder.reminderView.setVisibility(View.GONE);
				holder.progressBar.setVisibility(View.GONE);
			}
			else
			{
				Calendar now = DateUtils.getNowWithGMTTimeZone();

				int totalMinutes = element.getTotalAiringTimeInMinutes();

				int currentMinutes = DateUtils.calculateDifferenceBetween(element.getEventBroadcastBeginTimeGMT(), now, Calendar.MINUTE, false, 0);

				sb.append(element.getEventTimeDayOfTheWeekAsString())
				.append(",  ")
				.append(startTimeHourAndMinuteAsString)
				.append(" - ")
				.append(endTimeHourAndMinuteAsString);

				if (isAiring)
				{
					int minutesLeft = Math.abs(totalMinutes - currentMinutes);

					StringBuilder sbMinutesLeft = new StringBuilder();
					sbMinutesLeft.append(LanguageUtils.getRemainingTimeAsString(activity, minutesLeft));

					holder.beginTime.setTextColor(activity.getResources().getColor(R.color.red));

					LanguageUtils.setupOnlyProgressBar(activity, currentMinutes, totalMinutes, holder.progressBar);
					holder.divider.setBackgroundColor(activity.getResources().getColor(R.color.grey4));

					holder.progressBar.setVisibility(View.VISIBLE);
					holder.onGoingTimeLeft.setText(sbMinutesLeft.toString());
					holder.onGoingTimeLeft.setVisibility(View.VISIBLE);
					holder.reminderView.setVisibility(View.GONE);
				}
				/* Event has not started yet */
				else
				{
					holder.beginTime.setTextColor(activity.getResources().getColor(R.color.black));

					Event event = ContentManager.sharedInstance().getFromCacheEventByID(competitionId, eventId);
					
					String channelId = element.getChannelId();
					
					TVChannelId tvChannelId = new TVChannelId(channelId);
					
					TVChannel channel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
					
					holder.reminderView.setVisibility(View.VISIBLE);
					holder.reminderView.setCompetitionEventBroadcast(event, element, channel);
					
					boolean iconSizeSmall = true;
					holder.reminderView.setSizeOfIcon(iconSizeSmall);

					holder.progressBar.setVisibility(View.GONE);
					holder.onGoingTimeLeft.setVisibility(View.GONE);
				}
			}

			holder.beginTime.setText(sb.toString());
		}
		
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private ImageView channelLogo;
		private TextView beginTime;
		private ReminderView reminderView;
		private ProgressBar progressBar;
		private TextView onGoingTimeLeft;
		private View divider;
	}
}