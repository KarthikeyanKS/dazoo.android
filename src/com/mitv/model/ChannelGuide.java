package com.mitv.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.Consts;
import com.mitv.utilities.DateUtilities;

public class ChannelGuide extends ThreeImageResolutions implements Parcelable {

	private String id;
	private String name;
	
	private ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();

	/* Used for caching broadcast indexes */
	private HashMap<Long, Integer> broadcastIndexCache = new HashMap<Long, Integer>();

	public ChannelGuide() {
	}

	public ChannelGuide(JSONObject jsonGuide) {
		this.setId(jsonGuide.optString(Consts.GUIDE_CHANNEL_ID));
		this.setName(jsonGuide.optString(Consts.GUIDE_CHANNEL_NAME));

		JSONObject logosJson = jsonGuide.optJSONObject(Consts.GUIDE_LOGO);
		if (logosJson != null) {
			this.setImageUrlPortraitOrSquareLow(logosJson.optString(Consts.IMAGE_SMALL));
			this.setImageUrlPortraitOrSquareMedium(logosJson.optString(Consts.IMAGE_MEDIUM));
			this.setImageUrlPortraitOrSquareHigh(logosJson.optString(Consts.IMAGE_LARGE));
		}

		JSONArray broadcastsJson = jsonGuide.optJSONArray(Consts.GUIDE_BROADCASTS);

		if (broadcastsJson != null) {
			ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
			for (int j = 0; j < broadcastsJson.length(); j++) {
				JSONObject jsonBroadcast = broadcastsJson.optJSONObject(j);
				if (jsonBroadcast != null) {
					Broadcast broadcast = new Broadcast(jsonBroadcast);
					broadcasts.add(broadcast);
				}
			}
			this.setBroadcasts(broadcasts);
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setBroadcasts(ArrayList<Broadcast> broadcasts) {
		this.broadcasts = broadcasts;
	}

	public ArrayList<Broadcast> getBroadcasts() {
		return this.broadcasts;
	}

	public ChannelGuide(Parcel in) {
		id = in.readString();
		name = in.readString();
		
		String urlLowRes = in.readString();
		String urlMediumRes = in.readString();
		String urlHighRes = in.readString();
		
		setImageUrlPortraitOrSquareLow(urlLowRes);
		setImageUrlPortraitOrSquareMedium(urlMediumRes);
		setImageUrlPortraitOrSquareHigh(urlHighRes);
		
		in.readTypedList(broadcasts, Broadcast.CREATOR);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(imageUrlLowRes);
		dest.writeString(imageUrlMediumRes);
		dest.writeString(imageUrlHighRes);
		dest.writeTypedList(broadcasts);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ChannelGuide) {
			ChannelGuide other = (ChannelGuide) o;
			if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}

	public static final Parcelable.Creator<ChannelGuide> CREATOR = new Parcelable.Creator<ChannelGuide>() {
		public ChannelGuide createFromParcel(Parcel in) {
			return new ChannelGuide(in);
		}

		public ChannelGuide[] newArray(int size) {
			return new ChannelGuide[size];
		}
	};

	public int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TvDate date) {
		int nearestIndex = 0;

		long timeNow = DateUtilities.timeAsLongFromTvDateAndHour(date, hour);

		nearestIndex = getBroadcastIndex(broadcastList, timeNow);

		return nearestIndex;
	}

	public int getClosestBroadcastIndex(ArrayList<Broadcast> broadcastList) {
		int nearestIndex = 0;

		// get the time now
		Date currentDate = new Date();
		long timeNow = currentDate.getTime();

		nearestIndex = getBroadcastIndex(broadcastList, timeNow);

		return nearestIndex;
	}

	public int getBroadcastIndex(ArrayList<Broadcast> broadcastList, long timeNow) {
		int nearestIndex = 0;
		Integer nearestIndexObj = null;
		Long timeLongObject = Long.valueOf(timeNow);
		if (broadcastIndexCache.containsKey(timeLongObject)) {
			nearestIndexObj = broadcastIndexCache.get(timeLongObject);
			if (nearestIndexObj != null) {
				/* Cache hit! */
				nearestIndex = nearestIndexObj.intValue();
			}
		}

		if (nearestIndexObj == null) {
			/* Cache miss */
			nearestIndex = Broadcast.getClosestBroadcastIndexUsingTime(broadcastList, timeNow);

			broadcastIndexCache.put(timeLongObject, Integer.valueOf(nearestIndex));
		}

		return nearestIndex;
	}
}
