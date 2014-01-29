package com.mitv.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.DateUtilities;

public class Broadcast implements Parcelable {

	private static final String TAG = "Broadcast";

	private String broadcastType;

	private Channel channel;
	private Program program;
	private String channelUrl;
	private String shareUrl;

	private long beginTimeMillisGmt;
	private long endTimeMillisGmt;
	private long beginTimeMillisLocal;
	private long endTimeMillisLocal;

	private String beginTimeStringGmt;
	private String endTimeStringGmt;

	private String beginTimeStringLocal;
	private String endTimeStringLocal;

	private String beginTimeStringLocalHourAndMinute;
	private String beginTimeStringLocalDayMonth;

	private long timeToBegin;
	private long timeToEnd;

	private String dayOfWeekString;
	private String dayOfWeekWithTimeString;
	private String tvDateString; // yyyy-mm-dd

	public Broadcast() {
	}

	public String getBeginTimeStringLocal() {
		return beginTimeStringLocal;
	}

	public void setBeginTimeStringLocal(String beginTimeStringLocal) {
		this.beginTimeStringLocal = beginTimeStringLocal;
	}

	public String getEndTimeStringLocal() {
		return endTimeStringLocal;
	}


	public void setEndTimeStringLocal(String endTimeStringLocal) {
		this.endTimeStringLocal = endTimeStringLocal;
	}

	public String getTvDateString() {
		return tvDateString;
	}

	public void setTvDateString(String tvDateString) {
		this.tvDateString = tvDateString;
	}

	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getBroadcastType() {
		return this.broadcastType;
	}

	public long getEndTimeMillisGmt() {
		return this.endTimeMillisGmt;
	}

	public void setEndTimeMillisGmt(long endTimeMillisGmt) {
		this.endTimeMillisGmt = endTimeMillisGmt;
	}

	public long getBeginTimeMillisLocal() {
		return beginTimeMillisLocal;
	}

	public void setBeginTimeMillisLocal(long beginTimeMillisLocal) {
		this.beginTimeMillisLocal = beginTimeMillisLocal;
	}

	public long getEndTimeMillisLocal() {
		return endTimeMillisLocal;
	}

	public void setEndTimeMillisLocal(long endTimeMillisLocal) {
		this.endTimeMillisLocal = endTimeMillisLocal;
	}

	public String getBeginTimeStringLocalDayMonth() {
		return beginTimeStringLocalDayMonth;
	}

	public void setBeginTimeStringLocalDayMonth(String beginTimeStringLocalDayMonth) {
		this.beginTimeStringLocalDayMonth = beginTimeStringLocalDayMonth;
	}

	public String getBeginTimeStringGmt() {
		return this.beginTimeStringGmt;
	}

	public long getTimeToBegin() {
		return this.timeToBegin;
	}

	public void setTimeToBegin(long timeToBegin) {
		this.timeToBegin = timeToBegin;
	}

	public long getTimeToEnd() {
		return this.timeToEnd;
	}

	public void setTimeToEnd(long timeToEnd) {
		this.timeToEnd = timeToEnd;
	}

	public void setBeginTimeStringGmt(String beginTimeStringGmt) {
		this.beginTimeStringGmt = beginTimeStringGmt;
	}

	public String getBeginTimeStringLocalHourAndMinute() {		
		return this.beginTimeStringLocalHourAndMinute;
	}

	public void setBeginTimeStringLocalHourAndMinute(String beginTimeStringLocalHourAndMinute) {
		this.beginTimeStringLocalHourAndMinute = beginTimeStringLocalHourAndMinute;
	}

	public void setEndTimeStringGmt(String endTimeStringGmt) {
		this.endTimeStringGmt = endTimeStringGmt;
	}

	public String getEndTimeStringGmt() {
		return this.endTimeStringGmt;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Program getProgram() {
		return this.program;
	}

	public void setChannelUrl(String channelUrl) {
		this.channelUrl = channelUrl;
	}

	public String getChannelUrl() {
		return this.channelUrl;
	}

	public void setBeginTimeMillisGmt(long beginTimeMillisGmt) {
		if(beginTimeMillisGmt == 0) {
			Log.e("Broadcast", "Setting zero as timestamp");
		}
		this.beginTimeMillisGmt = beginTimeMillisGmt;
	}

	public long getBeginTimeMillisGmt() {
		return this.beginTimeMillisGmt;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public String getDayOfWeekString() {
		return dayOfWeekString;
	}

	public void setDayOfWeekString(String dayOfWeekString) {
		this.dayOfWeekString = dayOfWeekString;
	}



	public String getDayOfWeekWithTimeString() {
		return dayOfWeekWithTimeString;
	}

	public void setDayOfWeekWithTimeString(String dayOfWeekWithTimeString) {
		this.dayOfWeekWithTimeString = dayOfWeekWithTimeString;
	}

	public String getShareUrl() {
		return this.shareUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Broadcast) {
			Broadcast other = (Broadcast) o;
			if (getBeginTimeMillisGmt() != 0 && other.getBeginTimeMillisGmt() != 0 && getBeginTimeMillisGmt() == other.getBeginTimeMillisGmt()
					&& getChannel().getChannelId() != null && other.getChannel().getChannelId() != null
					&& (getChannel().getChannelId()).equals(other.getChannel().getChannelId())) {
				return true;
			}
		}
		return false;
	}		

	public Broadcast(Parcel in) {
		channel = (Channel) in.readParcelable(Channel.class.getClassLoader());
		program = (Program) in.readParcelable(Program.class.getClassLoader());
		channelUrl = in.readString();
		shareUrl = in.readString();	
		beginTimeStringGmt = in.readString();
		endTimeStringGmt = in.readString();
		beginTimeMillisGmt = in.readLong();
		endTimeMillisGmt = in.readLong();
		beginTimeMillisLocal = in.readLong();
		endTimeMillisLocal = in.readLong();

		this.calculateAndSetTimeData();

		beginTimeStringLocal = in.readString();
		endTimeStringLocal = in.readString();
		beginTimeStringLocalHourAndMinute = in.readString();
		beginTimeStringLocalDayMonth = in.readString();	
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(channel, flags);
		dest.writeParcelable(program, flags);
		dest.writeString(channelUrl);
		dest.writeString(shareUrl);
		dest.writeString(beginTimeStringGmt);
		dest.writeString(endTimeStringGmt);
		dest.writeLong(beginTimeMillisGmt);
		dest.writeLong(endTimeMillisGmt);
		dest.writeLong(beginTimeMillisLocal);
		dest.writeLong(endTimeMillisLocal);
		dest.writeString(beginTimeStringLocal);
		dest.writeString(endTimeStringLocal);
		dest.writeString(beginTimeStringLocalHourAndMinute);
		dest.writeString(beginTimeStringLocalDayMonth);
	}

	public Broadcast(NotificationDbItem item) {
		String beginTimeStringLocal = item.getBroadcastBeginTimeStringLocal();
		this.setBeginTimeStringGmt(beginTimeStringLocal);
		String millisGmtString = item.getBroadcastBeginTimeInMillisGmtAsString();
		long millisGmt = Long.parseLong(millisGmtString);
		this.setBeginTimeMillisGmt(millisGmt);

		Program program = new Program();
		program.setTitle(item.getProgramTitle());
		String programType = item.getProgramType();
		program.setProgramType(programType);

		long millisLocal = DateUtilities.convertTimeStampToLocalTime(millisGmt);
			
		String beginTimeStringLocalHourAndMinute = DateUtilities.getTimeOfDayFormatted(millisLocal);
		this.setBeginTimeStringLocalHourAndMinute(beginTimeStringLocalHourAndMinute);

		try {
			String beginTimeStringLocalDayMonth = DateUtilities.tvDateStringToDatePickerString(millisLocal);
			this.setBeginTimeStringLocalDayMonth(beginTimeStringLocalDayMonth);

			String dayOfWeekString = DateUtilities.isoStringToDayOfWeek(millisLocal);
			dayOfWeekString = Character.toUpperCase(dayOfWeekString.charAt(0)) + dayOfWeekString.substring(1);
			this.setDayOfWeekString(dayOfWeekString);

			String dayOfWeekAndTimeString = new StringBuilder().append(dayOfWeekString).append(", ").append(beginTimeStringLocalHourAndMinute).toString();
			this.setDayOfWeekWithTimeString(dayOfWeekAndTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
			program.setEpisodeNumber(item.getProgramEpisodeNumber());
			Season season = new Season();
			season.setNumber(item.getProgramSeason());
			program.setSeason(season);
		} else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
			program.setYear(item.getProgramYear());
			program.setGenre(item.getProgramGenre());
		}
		else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
			program.setCategory(item.getProgramCategory());
		}
		else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
			SportType sportType = new SportType();
			sportType.setName(item.getProgramCategory()); //Use category for sport type name 
			program.setSportType(sportType);
			program.setTournament(item.getProgramGenre()); //And genre for tournament name
		}

		// program.setTags()

		this.setProgram(program);

		Channel channel = new Channel();
		channel.setChannelId(item.getChannelId());
		channel.setName(item.getChannelName());
		channel.setAllImageUrls(item.getChannelLogoUrl());

		this.setChannel(channel);
	}

	public void calculateAndSetTimeData() {
		String beginTimeStringLocalHourAndMinute = DateUtilities.getTimeOfDayFormatted(beginTimeMillisLocal);
		this.setBeginTimeStringLocalHourAndMinute(beginTimeStringLocalHourAndMinute);

		try {
			long endTimeMillisGmt = DateUtilities.isoStringToLong(endTimeStringGmt);
			long endTimeMillisLocal = DateUtilities.convertTimeStampToLocalTime(endTimeMillisGmt);

			this.setEndTimeMillisGmt(endTimeMillisGmt);
			this.setEndTimeMillisLocal(endTimeMillisLocal);


			String beginTimeStringLocalDayMonth = DateUtilities.tvDateStringToDatePickerString(beginTimeMillisLocal);
			this.setBeginTimeStringLocalDayMonth(beginTimeStringLocalDayMonth);

			Date now = new Date();
			Calendar today = Calendar.getInstance();
			today.setTime(now);
			
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.setTime(now);
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(beginTimeMillisLocal);
			
			if (today.get(Calendar.YEAR) == date.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) {
				this.setDayOfWeekString(SecondScreenApplication.getInstance().getResources().getString(R.string.today));
			}
			else if (tomorrow.get(Calendar.YEAR) == date.get(Calendar.YEAR) && tomorrow.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) {
				this.setDayOfWeekString(SecondScreenApplication.getInstance().getResources().getString(R.string.tomorrow));
			}
			else {
				String dayOfWeekString = DateUtilities.isoStringToDayOfWeek(beginTimeMillisLocal);
				dayOfWeekString = Character.toUpperCase(dayOfWeekString.charAt(0)) + dayOfWeekString.substring(1);
				this.setDayOfWeekString(dayOfWeekString);
			}

			String dayOfWeekAndTimeString = new StringBuilder().append(dayOfWeekString).append(", ").append(beginTimeStringLocalHourAndMinute).toString();
			this.setDayOfWeekWithTimeString(dayOfWeekAndTimeString);


			String tvDateString = DateUtilities.isoDateToTvDateString(beginTimeMillisLocal);
			this.setTvDateString(tvDateString);

			String beginTimeStringLocal = DateUtilities.timeToTimeString(beginTimeMillisLocal);
			this.setBeginTimeStringLocal(beginTimeStringLocal);

			String endTimeStringLocal = DateUtilities.timeToTimeString(endTimeMillisLocal);
			this.setEndTimeStringLocal(endTimeStringLocal);

			updateTimeToBeginAndTimeToEnd();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	
	public String getStartsInTimeString() {
		Resources res = SecondScreenApplication.getInstance().getApplicationContext().getResources();
		
		String startsInTimeString = "Not set";
		try {
			int daysLeft = DateUtilities.getDifferenceInDays(this.beginTimeMillisGmt);
			if(daysLeft > 0) {
				startsInTimeString = String.format(Locale.getDefault(), "%s %d %s", res.getString(R.string.search_starts_in), daysLeft, res.getQuantityString(R.plurals.day, daysLeft));
			} else {
				int hoursLeft = DateUtilities.getDifferenceInHours(this.beginTimeMillisGmt);
				if(hoursLeft > 0) {
					startsInTimeString = String.format(Locale.getDefault(), "%s %d %s", res.getString(R.string.search_starts_in), hoursLeft, res.getQuantityString(R.plurals.hour, hoursLeft));
				} else {
					int minutesLeft = DateUtilities.getDifferenceInMinutes(this.beginTimeMillisGmt);
					if(minutesLeft > 0) {
						startsInTimeString = String.format(Locale.getDefault(), "%s %d %s", res.getString(R.string.search_starts_in), minutesLeft, res.getString(R.string.minutes));
					} else {
						startsInTimeString = "Has finished";
					}
				}
			}
		} catch (ParseException e) {
			startsInTimeString = "Parse error";
		}
		
		return startsInTimeString;
	}

	public Broadcast(JSONObject jsonBroadcast) {
		String beginTimeStringGmt = jsonBroadcast.optString(Consts.BROADCAST_BEGIN_TIME);
		String endTimeStringGmt = jsonBroadcast.optString(Consts.BROADCAST_END_TIME);
		long beginTimeMillisGMT = jsonBroadcast.optLong(Consts.BROADCAST_BEGIN_TIME_MILLIS);
		long beginTimeMillisLocal = DateUtilities.convertTimeStampToLocalTime(beginTimeMillisGMT);

		this.setBeginTimeStringGmt(beginTimeStringGmt);
		this.setEndTimeStringGmt(endTimeStringGmt);

		this.setBeginTimeMillisGmt(beginTimeMillisGMT);
		this.setBeginTimeMillisLocal(beginTimeMillisLocal);

		this.setShareUrl(jsonBroadcast.optString(Consts.BROADCAST_SHARE_URL));

		this.calculateAndSetTimeData();

		JSONObject jsonChannel = jsonBroadcast.optJSONObject(Consts.BROADCAST_CHANNEL);
		if (jsonChannel != null) {
			Channel channel = new Channel(jsonChannel);
			if(channel != null) {
				this.setChannel(channel);
			}
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject(Consts.BROADCAST_PROGRAM);
		if (jsonProgram != null) {
			Program program = new Program(jsonProgram);
			if(program != null) {
				this.setProgram(program);
			}
		}

		this.setBroadcastType(jsonBroadcast.optString(Consts.BROADCAST_BROADCAST_TYPE));
	}

	public static class BroadcastComparatorByTime implements Comparator<Broadcast> {

		@Override
		public int compare(Broadcast lhs, Broadcast rhs) {
			long left = lhs.getBeginTimeMillisGmt();
			long right = rhs.getBeginTimeMillisGmt();

			if (left > right) {
				return 1;
			} else if (left < right) {
				return -1;
			} else {
				String leftProgramName = lhs.getProgram().getTitle();
				String rightProgramName = rhs.getProgram().getTitle();
				return leftProgramName.compareTo(rightProgramName);
			}
		}
	}

	public static final Parcelable.Creator<Broadcast> CREATOR = new Parcelable.Creator<Broadcast>() {
		public Broadcast createFromParcel(Parcel in) {
			return new Broadcast(in);
		}

		public Broadcast[] newArray(int size) {
			return new Broadcast[size];
		}
	};

	@Override
	public String toString() {
		return "\n beginTime: " + beginTimeStringGmt + "\n endTime: " + endTimeStringGmt + "\n channel: " + channel + "\n program: " + program + "\n shareUrl" + shareUrl;
	}

	public static int getClosestBroadcastIndexUsingTime(ArrayList<Broadcast> broadcastList, long timeNow) {
		int nearestIndex = -1;

		for (int i = 0; i < broadcastList.size(); i++) {
			Broadcast broadcast = broadcastList.get(i);
			boolean hasNotEnded = broadcast.hasNotEnded(timeNow);

			if(hasNotEnded) {
				nearestIndex = i;
				break;
			}
		}
		return nearestIndex;
	}

	public boolean isRunning(long timeNow) {
		boolean isRunning = false;

		boolean hasStarted = hasStarted(timeNow);
		boolean hasNotEnded = hasNotEnded(timeNow);

		if(hasStarted &&  hasNotEnded) {
			isRunning = true;
		}

		return isRunning;
	}

	public boolean isRunning() {
		long timeNow = new Date().getTime();
		return isRunning(timeNow);
	}

	public boolean hasStarted(long timeNow)	{
		long beginTime = getBeginTimeMillisGmt();
		boolean hasStarted = timeNow >= beginTime;

		return hasStarted;
	}

	public boolean hasStarted() {
		long timeNow = new Date().getTime();
		return hasStarted(timeNow);
	}

	public int minutesSinceStart() {
		int minutesSinceStart = (int) Math.abs(timeToBegin / (1000 * 60));
		if(!hasStarted()) {
			minutesSinceStart *= -1;
		}
		return minutesSinceStart;
	}

	public boolean hasNotEnded(long timeNow)	{
		long endTime = getEndTimeMillisGmt();
		boolean hasNotEnded = (timeNow <= endTime);

		return hasNotEnded;
	}

	public boolean hasNotEnded() {
		long timeNow = new Date().getTime();
		return hasNotEnded(timeNow);
	}

	public static int getClosestBroadcastIndex(ArrayList<Broadcast> broadcastList) {
		int nearestIndex = -1;
		Date currentDate = new Date();
		long timeNow = currentDate.getTime();

		nearestIndex = getClosestBroadcastIndexUsingTime(broadcastList, timeNow);

		return nearestIndex;
	}

	public static int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TvDate date) {
		int nearestIndex = 0;
		long timeNow = DateUtilities.timeAsLongFromTvDateAndHour(date, hour);
		nearestIndex = getClosestBroadcastIndexUsingTime(broadcastList, timeNow);

		return nearestIndex;
	}

	public static ArrayList<Broadcast> getBroadcastsStartingFromPosition(int index, ArrayList<Broadcast> broadcastList, int numberOfClosest) {
		ArrayList<Broadcast> nextBroadcasts = new ArrayList<Broadcast>();

		for (int j = index; j < index + numberOfClosest; j++) {
			if (j < broadcastList.size()) {
				nextBroadcasts.add(broadcastList.get(j));
			}
		}
		return nextBroadcasts;
	}

	public void updateTimeToBeginAndTimeToEnd() {
		try {
			long timeToBegin = DateUtilities.getAbsoluteTimeDifference(beginTimeMillisGmt);
			long timeToEnd = DateUtilities.getAbsoluteTimeDifference(endTimeMillisGmt);

			this.setTimeToBegin(timeToBegin);
			this.setTimeToEnd(timeToEnd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getDurationInMinutes() {
		this.updateTimeToBeginAndTimeToEnd();

		int durationInMinutes = 0;
		durationInMinutes = (int) (timeToBegin - timeToEnd) / (1000 * 60);

		durationInMinutes = Math.abs(durationInMinutes);

		return durationInMinutes;
	}


}
