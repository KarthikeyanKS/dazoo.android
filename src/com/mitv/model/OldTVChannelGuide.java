package com.mitv.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.Consts;
import com.mitv.utilities.OldDateUtilities;

public class OldTVChannelGuide extends OldThreeImageResolutions implements Parcelable {

	private String id;
	
	private ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();

	/* Used for caching broadcast indexes */
	private HashMap<Long, Integer> broadcastIndexCache = new HashMap<Long, Integer>();

	public OldTVChannelGuide() {
	}

	public OldTVChannelGuide(JSONObject jsonGuide) {
		this.setId(jsonGuide.optString(Consts.GUIDE_CHANNEL_ID));

		JSONObject logosJson = jsonGuide.optJSONObject(Consts.GUIDE_LOGO);
		if (logosJson != null) {
			this.setImageUrlPortraitOrSquareLow(logosJson.optString(Consts.IMAGE_SMALL));
			this.setImageUrlPortraitOrSquareMedium(logosJson.optString(Consts.IMAGE_MEDIUM));
			this.setImageUrlPortraitOrSquareHigh(logosJson.optString(Consts.IMAGE_LARGE));
		}

		JSONArray broadcastsJson = jsonGuide.optJSONArray(Consts.GUIDE_BROADCASTS);

		if (broadcastsJson != null) {
			ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();
			for (int j = 0; j < broadcastsJson.length(); j++) {
				JSONObject jsonBroadcast = broadcastsJson.optJSONObject(j);
				if (jsonBroadcast != null) {
					OldBroadcast broadcast = new OldBroadcast(jsonBroadcast);
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

	public void setBroadcasts(ArrayList<OldBroadcast> broadcasts) {
		this.broadcasts = broadcasts;
	}

	public ArrayList<OldBroadcast> getBroadcasts() {
		return this.broadcasts;
	}

	public OldTVChannelGuide(Parcel in) 
	{
		id = in.readString();
		
		String urlLowRes = in.readString();
		String urlMediumRes = in.readString();
		String urlHighRes = in.readString();
		
		setImageUrlPortraitOrSquareLow(urlLowRes);
		setImageUrlPortraitOrSquareMedium(urlMediumRes);
		setImageUrlPortraitOrSquareHigh(urlHighRes);
		
		in.readTypedList(broadcasts, OldBroadcast.CREATOR);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(imageUrlLowRes);
		dest.writeString(imageUrlMediumRes);
		dest.writeString(imageUrlHighRes);
		dest.writeTypedList(broadcasts);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OldTVChannelGuide) {
			OldTVChannelGuide other = (OldTVChannelGuide) o;
			if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}

	public static final Parcelable.Creator<OldTVChannelGuide> CREATOR = new Parcelable.Creator<OldTVChannelGuide>() {
		public OldTVChannelGuide createFromParcel(Parcel in) {
			return new OldTVChannelGuide(in);
		}

		public OldTVChannelGuide[] newArray(int size) {
			return new OldTVChannelGuide[size];
		}
	};

	public int getClosestBroadcastIndexFromTime(ArrayList<OldBroadcast> broadcastList, int hour, OldTVDate date) {
		int nearestIndex = 0;

		long timeNow = OldDateUtilities.timeAsLongFromTvDateAndHour(date, hour);

		nearestIndex = getBroadcastIndex(broadcastList, timeNow);

		return nearestIndex;
	}

	public int getClosestBroadcastIndex(ArrayList<OldBroadcast> broadcastList) {
		int nearestIndex = 0;

		// get the time now
		Date currentDate = new Date();
		long timeNow = currentDate.getTime();

		nearestIndex = getBroadcastIndex(broadcastList, timeNow);

		return nearestIndex;
	}

	public int getBroadcastIndex(ArrayList<OldBroadcast> broadcastList, long timeNow) {
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
			nearestIndex = OldBroadcast.getClosestBroadcastIndexUsingTime(broadcastList, timeNow);

			broadcastIndexCache.put(timeLongObject, Integer.valueOf(nearestIndex));
		}

		return nearestIndex;
	}
}
