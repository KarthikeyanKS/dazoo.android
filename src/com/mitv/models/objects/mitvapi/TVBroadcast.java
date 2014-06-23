
package com.mitv.models.objects.mitvapi;



import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVBroadcastJSON;
import com.mitv.models.orm.TVBroadcastORM;
import com.mitv.models.orm.TVProgramORM;
import com.mitv.utilities.DateUtils;



/**
 * All variables in this class needs to be either transient or use the
 * @Expose(deserialize=false) annotation, else the JSON parsing using GSON will
 * fail.
 * 
 * @author consultant_hdme
 * 
 */
public class TVBroadcast 
	extends TVBroadcastJSON
	implements GSONDataFieldValidation 
{
	private static final String TAG = TVBroadcast.class.getName();

	
	private static final int NO_INT_VALUE_SET = -1;
	
	protected Calendar beginTimeCalendarLocal;
	protected Calendar endTimeCalendarLocal;
	private Integer durationInMinutes = NO_INT_VALUE_SET;
	
	/* IMPORTANT TO SET STRING TO NULL AND NOT EMPTY STRING */
	private String title = null;
	
	
	
	public TVBroadcast(){}
	
	
	
	public TVBroadcast(TVBroadcastORM ormData)
	{
		this.program = TVProgramORM.getTVProgramByID(ormData.getProgram().getProgramId());
		
		this.beginTimeMillis = ormData.getBeginTimeMillis();
		this.beginTime = ormData.getBeginTime();
		this.endTime = ormData.getEndTime();
		this.broadcastType = ormData.getBroadcastType();
		this.shareUrl = ormData.getShareUrl();
	}
	
	
	
	public String getTitle() 
	{
		if (title == null) 
		{
			if (program != null) 
			{
				ProgramTypeEnum programType = program.getProgramType();

				switch (programType) 
				{
					case TV_EPISODE: 
					{
						TVSeries series = program.getSeries();
						
						if (series != null) 
						{
							title = series.getName();
						}
						break;
					}
					
					case MOVIE:
					case SPORT:
					case OTHER:
					default:
					{
						title = program.getTitle();
						break;
					}
				}
			}
		}
		return title;
	}
	
	
	
	
	public String getProgramDetailsAsString()
	{
		StringBuilder detailsSB = new StringBuilder(); 
		
		TVProgram program = getProgram();
		
		ProgramTypeEnum programType = program.getProgramType();

		switch(programType)
		{
			case TV_EPISODE:
			{
				String seasonAndEpisodeString = buildSeasonAndEpisodeString();

				detailsSB.append(seasonAndEpisodeString);

				break;
			}

			case MOVIE:
			{
				detailsSB.append(program.getGenre())
				.append(" ")
				.append(program.getYear());

				break;
			}

			case SPORT:
			{
				if (program.getTournament() != null) 
				{
					detailsSB.append(program.getTournament());
				}
				else 
				{
					detailsSB.append(program.getSportType().getName());
				}
				break;
			}

			case OTHER:
			{
				String category = program.getCategory();

				detailsSB.append(category);

				break;
			}

			case UNKNOWN:
			default:
			{
				Log.w(TAG, "Unhandled program type.");
				break;
			}
		}
		
		return detailsSB.toString();
	}
	
	
	
	public String buildSeasonAndEpisodeString()
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
	
		StringBuilder seasonEpisodeSB = new StringBuilder();
		
		ProgramTypeEnum programType = program.getProgramType();
		
		if(programType == ProgramTypeEnum.TV_EPISODE)
		{
			int season = program.getSeason().getNumber().intValue();
			
			int episode = program.getEpisodeNumber();
	
			if (season > 0) 
			{
				seasonEpisodeSB.append(context.getString(R.string.season))
				.append(" ")
				.append(season)
				.append(" ");
			}
			
			if (episode > 0) 
			{
				seasonEpisodeSB.append(context.getString(R.string.episode))
				.append(" ")
				.append(episode);
			}
		}
		
		return seasonEpisodeSB.toString();
	}
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(beginTime);
		
		return beginTimeCalendarGMT;
	}

	
	
	/**
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarGMT()
	{
		Calendar endTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(endTime);
		
		return endTimeCalendarGMT;
	}

	
	
	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarLocal() 
	{
		if(beginTimeCalendarLocal == null) 
		{	
			beginTimeCalendarLocal = getBeginTimeCalendarGMT();
			
			beginTimeCalendarLocal = DateUtils.setTimeZoneAndOffsetToLocal(beginTimeCalendarLocal);
		}
		
		return beginTimeCalendarLocal;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarLocal()
	{
		if(endTimeCalendarLocal == null)
		{	
			endTimeCalendarLocal = getEndTimeCalendarGMT();
			
			endTimeCalendarLocal = DateUtils.setTimeZoneAndOffsetToLocal(endTimeCalendarLocal);
		}
		
		return endTimeCalendarLocal;
	}
	
	
	
	/* Used when creating new TVBroadcast objects at tag generation */
	public Calendar getBeginTimeCalendarLocalForTagGeneration() 
	{
		return beginTimeCalendarLocal;
	}
	
	
	
	/* Used when creating new TVBroadcast objects at tag generation */
	public Calendar getEndTimeCalendarLocalForTagGeneration() 
	{
		return endTimeCalendarLocal;
	}
	
	
	
	public boolean isBroadcastCurrentlyAiring() 
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean isRunning = getBeginTimeCalendarGMT().before(now) && getEndTimeCalendarGMT().after(now);

		return isRunning;
	}

	
	
	public Integer getBroadcastDurationInMinutes() 
	{
		if(durationInMinutes == NO_INT_VALUE_SET)
		{		    
		    durationInMinutes = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarGMT(), getEndTimeCalendarGMT(), Calendar.MINUTE, false, 0);
		}

	    return durationInMinutes;
	}
	
	
	
	public Integer getElapsedMinutesSinceBroadcastStarted() 
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		Integer elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarGMT(), now, Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	public Integer getRemainingMinutesUntilBroadcastEnds() 
	{	    
	    Calendar now = DateUtils.getNowWithGMTTimeZone();
		
	    Integer elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(now, getEndTimeCalendarGMT(), Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	public boolean isEventAiringInLessThan(int minutes) 
	{
		Calendar eventBroadcastBeginTimeGMT = getBeginTimeCalendarGMT();
		
		Calendar eventBroadcastBeginTimeGMTWithNegativeIncrement = (Calendar) (eventBroadcastBeginTimeGMT.clone());

		eventBroadcastBeginTimeGMTWithNegativeIncrement.add(Calendar.MINUTE, -minutes);
		
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean isNowAfter = now.after(eventBroadcastBeginTimeGMTWithNegativeIncrement);
	    boolean isNowBefore = now.before(eventBroadcastBeginTimeGMT);
		
	    boolean isEventAiringInPeriod = (isNowAfter && isNowBefore);
	    
	    return isEventAiringInPeriod;
	}
	
	
	
	public boolean isEndTimeAfter(Calendar inputCalendar)
	{
		boolean isEndTimeAfterInputCalendar = getEndTimeCalendarLocal().after(inputCalendar);

		return isEndTimeAfterInputCalendar;
	}
	
	
	
	public boolean isAiring() 
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();

		boolean isAiring = getBeginTimeCalendarGMT().before(now) && getEndTimeCalendarGMT().after(now);

		return isAiring;
	}

	
	
	public boolean hasEnded()
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean hasEnded = now.after(getEndTimeCalendarGMT());
		
		return hasEnded;
	}
	
	
	
	public boolean hasNotAiredYet()
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean hasNotAiredYet = now.before(getBeginTimeCalendarGMT());
		
		return hasNotAiredYet;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "yyyy-MM-dd"
	 */
	public String getBeginTimeDateRepresentationFromLocal() 
	{
		String beginTimeDateRepresentation = DateUtils.buildDateCompositionAsString(getBeginTimeCalendarLocal());
		
		return beginTimeDateRepresentation;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "dd/MM"
	 */
	public String getBeginTimeDayAndMonthAsString() 
	{
		String beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getBeginTimeCalendarLocal(), false);
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		String beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendarLocal());
		
		return beginTimeHourAndMinuteRepresentation;
	}
	

	public String getEndTimeHourAndMinuteLocalAsString() 
	{
		String endTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getEndTimeCalendarLocal());
		
		return endTimeHourAndMinuteRepresentation;
	}
	
	
	
	public boolean isBeginTimeTodayOrTomorrow()
	{
		Calendar now = DateUtils.getNowWithLocalTimezone();
		
		Calendar beginTime = this.getBeginTimeCalendarLocal();
		
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
	
	
	
	public boolean isTheSameDayAs(TVBroadcast other)
	{
		Calendar beginTime1 = this.getBeginTimeCalendarLocal();
		Calendar beginTime2 = other.getBeginTimeCalendarLocal();
		
		return DateUtils.areCalendarsTheSameTVAiringDay(beginTime1, beginTime2);
	}
	
		
	
	/*
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getBeginTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendarLocal(), true);
	}

	
	
	/*
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getBeginTimeDayOfTheWeekWithHourAndMinuteAsString()
	{	
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		StringBuilder sb = new StringBuilder();
		
		String dayOfTheWeekAsString = DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendarLocal(), true);
		
		String timeOfDayAsString = getBeginTimeHourAndMinuteLocalAsString();
		
		sb.append(dayOfTheWeekAsString);
		
		boolean isToday = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.today));
		boolean isTomorrow = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.tomorrow));
		boolean isTonight = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.tonight));
		boolean isYesterday = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.yesterday));
		boolean isTomorrowNight = dayOfTheWeekAsString.equalsIgnoreCase(context.getString(R.string.tomorrow_night));
		
		if (isToday == false && isTomorrow == false && isTonight == false && isYesterday == false && isTomorrowNight == false) 
		{
			String dayAndMonthString = getBeginTimeDayAndMonthAsString();
			sb.append(" ");
			sb.append(dayAndMonthString);
		}

		sb.append(", ");
		sb.append(timeOfDayAsString);
		
		return sb.toString();
	}
	
	
	
	/*
	 * Returns a string representation of the total amount of time remaining for the broadcast to begin.
	 * The representation can contain a formated number of days, hours, minutes, or if the broadcast is already finished.
	 * 
	 */
	public String getStartingTimeAsString()
	{
		Resources res = SecondScreenApplication.sharedInstance().getApplicationContext().getResources();
		
		StringBuilder sb = new StringBuilder();
		
		Calendar now = DateUtils.getNowWithGMTTimeZone();

		int daysLeft = DateUtils.calculateDifferenceBetween(now, getBeginTimeCalendarGMT(), Calendar.DAY_OF_MONTH, false, 0);

		if(daysLeft > 0)
		{
			sb.append(res.getString(R.string.search_starts_in));
			sb.append(" ");
			sb.append(daysLeft);
			sb.append(" ");
			sb.append(res.getQuantityString(R.plurals.day, daysLeft));
		} 
		else 
		{
			int hoursLeft = DateUtils.calculateDifferenceBetween(now, getBeginTimeCalendarGMT(), Calendar.HOUR_OF_DAY, false, 0);

			if(hoursLeft > 0) 
			{
				sb.append(res.getString(R.string.search_starts_in));
				sb.append(" ");
				sb.append(hoursLeft);
				sb.append(" ");
				sb.append(res.getQuantityString(R.plurals.hour, hoursLeft));
			} 
			else 
			{
				int minutesLeft = DateUtils.calculateDifferenceBetween(now, getBeginTimeCalendarGMT(), Calendar.MINUTE, false, 0);

				if(minutesLeft > 0) 
				{
					sb.append(res.getString(R.string.search_starts_in));
					sb.append(" ");
					sb.append(minutesLeft);
					sb.append(" ");
					sb.append(res.getString(R.string.minutes));
				} 
				else 
				{
					sb.append(res.getString(R.string.search_result_broadcast_has_finished));
				}
			}
		}
		
		return sb.toString();
	}


	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((beginTimeMillis == null) ? 0 : beginTimeMillis.hashCode());
		
		return result;
	}

	
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		TVBroadcastJSON other = (TVBroadcastJSON) obj;
		
		if (beginTimeMillis == null)
		{
			if (other.getBeginTimeMillis() != null)
				return false;
		} 
		else if (!beginTimeMillis.equals(other.getBeginTimeMillis()))
			return false;
		
		return true;
	}
	
	
	@Override
	public boolean areDataFieldsValid() {
		final int yearOf2000 = 2000;
		boolean tvProgramOk = true;
		if(getProgram() != null) {
			tvProgramOk = getProgram().areDataFieldsValid();
		}
		
		
		boolean broadcastFields = (tvProgramOk && (getBeginTimeMillis() != null) && 
				!TextUtils.isEmpty(getBeginTime()) && !TextUtils.isEmpty(getEndTime()) &&
				(getBroadcastType() != BroadcastTypeEnum.UNKNOWN) && !TextUtils.isEmpty(getShareUrl())
				);
		
		boolean additionalFieldsOk = (
						getBeginTimeCalendarGMT() != null && (getBeginTimeCalendarGMT().get(Calendar.YEAR) > yearOf2000)  && getEndTimeCalendarGMT() != null &&
						(getEndTimeCalendarGMT().get(Calendar.YEAR) > yearOf2000)  &&
						getBroadcastDurationInMinutes() != null &&
						!TextUtils.isEmpty(getBeginTimeDateRepresentationFromLocal()) && !TextUtils.isEmpty(getBeginTimeDayAndMonthAsString()) &&
						!TextUtils.isEmpty(getBeginTimeHourAndMinuteLocalAsString()) && !TextUtils.isEmpty(getEndTimeHourAndMinuteLocalAsString())
						);
		
		boolean areDataFieldsValid = broadcastFields && additionalFieldsOk;
		
		return areDataFieldsValid;
	}
}
