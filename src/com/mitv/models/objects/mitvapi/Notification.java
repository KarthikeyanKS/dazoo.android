

package com.mitv.models.objects.mitvapi;



import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.NotificationTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.orm.NotificationORM;
import com.mitv.utilities.DateUtils;



public class Notification 
{
	private static final String TAG = Notification.class.getName();
	
	
	/*
	 * The notification ID is only set after the notification is scheduled
	 */
	private Integer notificationId;
	
	private NotificationTypeEnum notificationType;
	
	private String programId;
	private String channelId;
	
	private Long broadcastBeginTimeInMilliseconds;
	private String broadcastTitle;
	private String broadcastChannelName;
	private String broadcastChannelLogo;
	private String broadcastProgramDetails;
	private ProgramTypeEnum broadcastProgramType;
	
	
	/* Only used if the notificationType is COMPETITION_EVENT */
	private Long competitionId;
	private Long eventId;
	
	
	
	protected Calendar beginTimeCalendarGMT;
	protected Calendar beginTimeCalendarLocal;
	
	
	
	public Notification(){}
	
	
	
	public Notification(final TVBroadcastWithChannelInfo broadcast)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.TV_BROADCAST;
		
		this.programId = broadcast.getProgram().getProgramId();
		this.channelId = broadcast.getChannel().getChannelId().getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		this.broadcastTitle = broadcast.getTitle();
		this.broadcastChannelName = broadcast.getChannel().getName();
		this.broadcastChannelLogo = broadcast.getChannel().getImageUrl();
		
		TVProgram program = broadcast.getProgram();
		
		this.broadcastProgramType = program.getProgramType();
		this.broadcastProgramDetails = broadcast.getProgramDetailsAsString();
				
		this.competitionId = Long.valueOf(-1);
		this.eventId = Long.valueOf(-1);
	}
	
	
	
	public Notification(
			final Competition competition,
			final Event event,
			final EventBroadcast broadcast,
			final TVChannel channel)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.COMPETITION_EVENT_WITH_LOCAL_CHANNEL;
		
		this.programId = broadcast.getProgramId();
		this.channelId = broadcast.getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		this.broadcastTitle = event.getTitle();
		this.broadcastChannelName = channel.getName();
		this.broadcastChannelLogo = channel.getImageUrl();
		
		this.broadcastProgramType = ProgramTypeEnum.UNKNOWN;
		this.broadcastProgramDetails = competition.getDisplayName();
				
		this.competitionId = event.getCompetitionId();
		this.eventId = event.getEventId();
	}
	
	
	
	public Notification(
			final Competition competition,
			final Event event,
			final EventBroadcast broadcast,
			final String channelName,
			final String channelURL)
	{
		this.notificationId = Integer.valueOf(-1);
		
		this.notificationType = NotificationTypeEnum.COMPETITION_EVENT_WITH_EMBEDED_CHANNEL;
		
		this.programId = broadcast.getProgramId();
		this.channelId = broadcast.getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = broadcast.getBeginTimeMillis();
		this.broadcastTitle = event.getTitle();
		this.broadcastChannelName = channelName;
		this.broadcastChannelLogo = channelURL;
		
		this.broadcastProgramType = ProgramTypeEnum.UNKNOWN;
		this.broadcastProgramDetails = competition.getDisplayName();
				
		this.competitionId = event.getCompetitionId();
		this.eventId = event.getEventId();
	}
	
	
	
	public Notification(final NotificationORM ormData)
	{
		this.notificationId = ormData.getNotificationId();
		this.notificationType = ormData.getNotificationType();
		
		this.programId = ormData.getProgramId();
		this.channelId = ormData.getChannelId();
		
		this.broadcastBeginTimeInMilliseconds = ormData.getBeginTimeInMilliseconds();
		this.broadcastTitle = ormData.getBroadcastTitle();
		this.broadcastChannelName = ormData.getBroadcastChannelName();
		this.broadcastChannelLogo = ormData.getBroadcastChannelLogo();
		this.broadcastProgramType = ormData.getBroadcastProgramType();
		this.broadcastProgramDetails = ormData.getBroadcastProgramDetails();
		
		this.competitionId = ormData.getCompetitionId();
		this.eventId = ormData.getEventId();
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarGMT()
	{
		if(beginTimeCalendarGMT == null)
		{	
			beginTimeCalendarGMT = DateUtils.getNowWithGMTTimeZone();
			
			beginTimeCalendarGMT.setTimeInMillis(broadcastBeginTimeInMilliseconds);
		}
		
		return beginTimeCalendarGMT;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarLocal()
	{
		if(beginTimeCalendarLocal == null)
		{	
			beginTimeCalendarLocal = DateUtils.getNowWithGMTTimeZone();
			
			beginTimeCalendarLocal.setTimeInMillis(broadcastBeginTimeInMilliseconds);
			
			beginTimeCalendarLocal = DateUtils.setTimeZoneAndOffsetToLocal(beginTimeCalendarLocal);
		}
		
		return beginTimeCalendarLocal;
	}
	
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		String beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendarLocal());
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	

	public String getBeginTimeDayAndMonthAsString() 
	{
		String beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getBeginTimeCalendarLocal(), false);
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	
	public boolean isBeginTimeTodayOrTomorrow()
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		Calendar beginTime = this.getBeginTimeCalendarGMT();
		
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

	
	
	
	public boolean isTheSameDayAs(Notification other)
	{
		Calendar beginTime1 = this.getBeginTimeCalendarGMT();
		Calendar beginTime2 = other.getBeginTimeCalendarGMT();
		
		return DateUtils.areCalendarsTheSameTVAiringDay(beginTime1, beginTime2);
	}
	
	
	
	public String getBeginTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendarLocal());
	}
	
	
	
	public String getBeginTimeDayOfTheWeekWithHourAndMinuteAsString()
	{	
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		StringBuilder sb = new StringBuilder();
		
		String dayOfTheWeekAsString = DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendarLocal());
		
		String timeOfDayAsString = getBeginTimeHourAndMinuteLocalAsString();
		
		sb.append(dayOfTheWeekAsString);
		
		boolean isToday = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.today));
		boolean isTomorrow = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.tomorrow));
		
		if (isToday == false && isTomorrow == false) 
		{
			String dayAndMonthString = getBeginTimeDayAndMonthAsString();
			sb.append(" ");
			sb.append(dayAndMonthString);
		}

		sb.append(", ");
		sb.append(timeOfDayAsString);
		
		return sb.toString();
	}



	public Integer getNotificationId() 
	{
		if(notificationId == null)
		{
			notificationId = Integer.valueOf(0);
			
			Log.w(TAG, "notificationId is null");
		}
		
		return notificationId;
	}



	public void setNotificationId(Integer notificationId) 
	{
		if(notificationId == null)
		{
			Log.w(TAG, "Attempting to set a null notificationId. Defaulting to 0");
			
			this.notificationId = Integer.valueOf(0);
		}
		else
		{
			this.notificationId = notificationId;
		}
	}



	public NotificationTypeEnum getNotificationType() 
	{
		return notificationType;
	}



	public Long getCompetitionId() 
	{
		if(competitionId == null)
		{
			competitionId = Long.valueOf(0);
			
			Log.w(TAG, "competitionId is null");
		}
		
		return competitionId;
	}



	public Long getEventId() 
	{
		if(eventId == null)
		{
			eventId = Long.valueOf(0);
			
			Log.w(TAG, "eventId is null");
		}
		
		return eventId;
	}



	public String getProgramId() 
	{
		if(programId == null)
		{
			programId = new String("");
			
			Log.w(TAG, "programId is null");
		}
		
		return programId;
	}



	public Long getBeginTimeInMilliseconds() 
	{
		if(broadcastBeginTimeInMilliseconds == null)
		{
			broadcastBeginTimeInMilliseconds = Long.valueOf(0);
			
			Log.w(TAG, "broadcastBeginTimeInMilliseconds is null");
		}
		
		return broadcastBeginTimeInMilliseconds;
	}



	public String getChannelId() 
	{
		if(channelId == null)
		{
			channelId = new String("");
			
			Log.w(TAG, "channelId is null");
		}
		
		return channelId;
	}


	
	public String getBroadcastTitle() 
	{
		if(broadcastTitle == null)
		{
			broadcastTitle = new String("");
			
			Log.w(TAG, "broadcastTitle is null");
		}
		
		return broadcastTitle;
	}

	

	public String getBroadcastChannelName() 
	{
		if(broadcastChannelName == null)
		{
			broadcastChannelName = new String("");
			
			Log.w(TAG, "broadcastChannelName is null");
		}
		
		return broadcastChannelName;
	}


	
	public ProgramTypeEnum getBroadcastProgramType() 
	{
		return broadcastProgramType;
	}


	
	public String getBroadcastProgramDetails() 
	{
		if(broadcastProgramDetails == null)
		{
			broadcastProgramDetails = new String("");
			
			Log.w(TAG, "broadcastProgramDetails is null");
		}
		
		return broadcastProgramDetails;
	}
	
	
	
	public String getBroadcastChannelLogo() 
	{
		if(broadcastChannelLogo == null)
		{
			broadcastChannelLogo = new String("");
			
			Log.w(TAG, "broadcastChannelLogo is null");
		}
		
		return broadcastChannelLogo;
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
		
		Notification other = (Notification) obj;
		
		boolean areChannelIdsEqual = getChannelId().equals(other.getChannelId());
		boolean areProgramIdEqual = getProgramId().equals(other.getProgramId());
		boolean areBeginTimeMillisEqual = getBeginTimeInMilliseconds().equals(other.getBeginTimeInMilliseconds());
		
		if(areChannelIdsEqual && areProgramIdEqual && areBeginTimeMillisEqual)
		{
			return true;
		}
		
		return false;
	}
}
