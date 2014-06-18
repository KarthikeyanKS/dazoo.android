
package com.mitv.models.objects.mitvapi.competitions;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventMatchStatusEnum;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastJSON;
import com.mitv.models.gson.mitvapi.competitions.EventJSON;
import com.mitv.utilities.DateUtils;



public class Event
	extends EventJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Event.class.getName();
	
	
	private Calendar eventCalendar;
	
	
	public Event(){}
	
	
	
	public String getTitle()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getHomeTeam())
		.append(" - ")
		.append(getAwayTeam());
		
		return sb.toString();
	}
	
	
	
	public EventMatchStatusEnum getMatchStatus()
	{
		return EventMatchStatusEnum.getTypeEnumFromCode(getMatchStatusId());
	}
	
	
	
	public List<EventBroadcast> getEventBroadcasts()
	{
		List<EventBroadcast> list = new ArrayList<EventBroadcast>();
		
		for (EventBroadcastJSON ev : getBroadcasts()) 
		{
			EventBroadcast element = new EventBroadcast(ev);
			list.add(element);
		}
		
		return list;
	}
	
	
	
	public EventBroadcast getEventBroadcastMatchingBeginTimeMillis(long beginTimeMillis)
	{
		EventBroadcast data = null;
		
		for (EventBroadcastJSON ev : getBroadcasts()) 
		{
			boolean matchesBeginTimeMilliseconds = (ev.getBeginTimeMillis() == beginTimeMillis);
			
			if(matchesBeginTimeMilliseconds)
			{
				data = new EventBroadcast(ev);
				break;
			}
		}
		
		return data;
	}
	
	
	
	public String getStadiumImageURL()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(Constants.EVENT_STADIUM_IMAGE_PATH);
		sb.append(Constants.FORWARD_SLASH);
		sb.append(getStadiumId());
		sb.append(Constants.EVENT_STADIUM_IMAGE_SIZE_LARGE);
		sb.append(Constants.EVENT_STADIUM_IMAGE_EXTENSION);
		
		return sb.toString();
	}
	
	
	
	public String getGameTimeAndStatusAsString(boolean includeIcon)
	{
		StringBuilder sb = new StringBuilder();

		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

		EventMatchStatusEnum matchStatus = getMatchStatus();
		
		switch(matchStatus) 
		{
			default:
			case FINISHED:
			{
				sb.append(context.getResources().getString(R.string.event_page_completed));
				
				break;
			}
		
			case IN_PROGRESS:
			{
				if(getCurrentMinute() == Constants.EVENT_CURRENT_MINUTE_UNAVAILABLE)
				{
					sb.append(context.getString(R.string.event_page_current_minute_unavailable));
				}
				else if(getCurrentMinute() == Constants.EVENT_CURRENT_MINUTE_IN_PENALTIES)
				{
					sb.append(context.getString(R.string.event_page_current_minute_penalties));
				}
				else
				{
					if(includeIcon)
					{	
						sb.append(context.getResources().getString(R.string.icon_time_is_ongoing))
						.append(" ");
					}
					
					sb.append(getCurrentMinute());
				}

				break;
			}
			
			case INTERVAL:
			{
				sb.append(context.getResources().getString(R.string.event_page_halftime));
				
				break;
			}

			case ABANDONED:
			{
				sb.append(context.getResources().getString(R.string.event_page_abandoned));
				
				break;
			}
			
			case SUSPENDED:
			{
				sb.append(context.getResources().getString(R.string.event_page_suspended));
				
				break;
			}
			
			case POSTPONED:
			{
				sb.append(context.getResources().getString(R.string.event_page_postponed));
				
				break;
			}
			
			case DELAYED:
			{
				sb.append(context.getResources().getString(R.string.event_page_delayed));
				
				break;
			}
		}
				
		return sb.toString();
	}
	
	
	
	public boolean containsBroadcastDetails()
	{
		return (getBroadcasts().isEmpty() == false);
	}
	
	
	
	public boolean containsTeamInfo()
	{
		if(getHomeTeamId() == 0 || getAwayTeamId() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	
	
	public String getScoreAsString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getHomeGoals());
		sb.append(" - ");
		sb.append(getAwayGoals());
		
		return sb.toString();
	}
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEventDateCalendarGMT()
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(getEventDate());
		
		return beginTimeCalendarGMT;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The start time of the competition, if available. Otherwise, the current time
	 */
	public Calendar getEventDateCalendarLocal() 
	{
		if(eventCalendar == null)
		{	
			eventCalendar = getEventDateCalendarGMT();
			
			eventCalendar = DateUtils.setTimeZoneAndOffsetToLocal(eventCalendar);
		}
		
		return eventCalendar;
	}
	
	
	
	public boolean isTheSameDayAs(Event other)
	{
		Calendar beginTime1 = this.getEventDateCalendarLocal();
		Calendar beginTime2 = other.getEventDateCalendarLocal();
		
		return DateUtils.areCalendarsTheSameTVAiringDay(beginTime1, beginTime2);
	}
	
	
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		String beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getEventDateCalendarLocal());
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	
	
	public boolean isEventTimeTodayOrTomorrow()
	{
		Calendar now = DateUtils.getNowWithLocalTimezone();
		
		Calendar beginTime = this.getEventDateCalendarLocal();
		
    	boolean isCorrectYear = (now.get(Calendar.YEAR) - beginTime.get(Calendar.YEAR)) == 0;
    	boolean isCorrectMonth = (now.get(Calendar.MONTH) - beginTime.get(Calendar.MONTH)) == 0;
    	boolean isSameDay = DateUtils.areCalendarsTheSameTVAiringDay(beginTime, now);
    	
		boolean isAiringToday = isCorrectYear && isCorrectMonth && isSameDay;
		boolean isAiringTomorrow = false;
		
		if (isAiringToday == false)
		{
			Calendar tomorrow = (Calendar) now.clone();
	 		
			tomorrow.add(Calendar.DAY_OF_MONTH, 1);

			isSameDay = DateUtils.areCalendarsTheSameTVAiringDay(beginTime, tomorrow);
	 		
			isAiringTomorrow = isCorrectYear && isCorrectMonth && isSameDay;
		}
		
		boolean isBeginTimeTodayOrTomorrow = (isAiringToday || isAiringTomorrow);
		
		return isBeginTimeTodayOrTomorrow;
	}
	
	
	
	public boolean isEventAiringToday()
	{
		Calendar now = DateUtils.getNowWithLocalTimezone();
		
		Calendar beginTime = this.getEventDateCalendarLocal();
		
    	boolean isCorrectYear = (now.get(Calendar.YEAR) - beginTime.get(Calendar.YEAR)) == 0;
    	boolean isCorrectMonth = (now.get(Calendar.MONTH) - beginTime.get(Calendar.MONTH)) == 0;
    	boolean isSameDay = DateUtils.areCalendarsTheSameTVAiringDay(beginTime, now);
    	
    	boolean isEventAiringToday = isSameDay && isCorrectMonth && isCorrectYear;
    	
		return isEventAiringToday;
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getEventTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getEventDateCalendarLocal());
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar in the format "dd/MM"
	 */
	public String getEventTimeDayAndMonthAsString() 
	{
		String beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getEventDateCalendarLocal(), false);
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + (int) getEventId();
		
		return result;
	}

	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		Event other = (Event) obj;
		
		if (getEventId() != other.getEventId()) 
		{
			return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * Compares previous or next team with current team to check if they belongs to the same phase or not.
	 * 
	 * @param other
	 * @return
	 */
	public boolean isSamePhase(Event other) 
	{
		boolean isSamePhase = false;
		
		if (getPhaseId() == other.getPhaseId()) 
		{
			isSamePhase = true;
		}
		
		return isSamePhase;
	}
	
	
	
	public long getBeginTimeLocalInMillis() 
	{
		long beginTimeMillis = this.getEventDateCalendarLocal().getTimeInMillis();
		
		return beginTimeMillis;
	}	
	
	
	

	public String getShareUrl() 
	{
		StringBuilder sb = new StringBuilder();

		sb.append(Constants.HTTP_SCHEME_USED)
		.append(Constants.BACKEND_DEPLOYMENT_DOMAIN_URL)
		.append(Constants.URL_SHARE_SPORT_SPANISH)
		.append(Constants.URL_EVENTS_SPANISH)
		.append(Constants.FORWARD_SLASH)
		.append(this.getEventId());
		
		String url = sb.toString();
		
		return url;
	}
}
