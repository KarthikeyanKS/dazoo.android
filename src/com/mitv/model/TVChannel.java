package com.mitv.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.Consts;

public class TVChannel extends ThreeImageResolutions implements Parcelable {

	private String channelId = "";
	private String name = "";
	
	public TVChannel(){		
	}
	
	public void setChannelId(String channelId){
		this.channelId = channelId;
	}
	
	public String getChannelId(){
		return this.channelId;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
		
	public void setChannelPageUrl(String channelPageUrl){
		this.channelId = channelId;
	}
	
	public String getChannelPageUrl(){
		return this.channelId;
	}
	
	public TVChannel(Parcel in){
		channelId = in.readString();
		name = in.readString();
		String urlLowRes = in.readString();
		String urlMediumRes = in.readString();
		String urlHighRes = in.readString();
				
		setImageUrlPortraitOrSquareLow(urlLowRes);
		setImageUrlPortraitOrSquareMedium(urlMediumRes);
		setImageUrlPortraitOrSquareHigh(urlHighRes);
	}
	
	public TVChannel(JSONObject jsonChannel) {
		this.setChannelId(jsonChannel.optString(Consts.CHANNEL_CHANNEL_ID));
		this.setName(jsonChannel.optString(Consts.CHANNEL_NAME));

		try {
			JSONObject jsonPoster = jsonChannel.getJSONObject(Consts.CHANNEL_LOGO);
			if (jsonPoster != null) {
				this.setImageUrlPortraitOrSquareLow(jsonPoster.optString(Consts.IMAGE_SMALL));
				this.setImageUrlPortraitOrSquareMedium(jsonPoster.optString(Consts.IMAGE_MEDIUM));
				this.setImageUrlPortraitOrSquareHigh(jsonPoster.optString(Consts.IMAGE_LARGE));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(channelId);
		dest.writeString(name);
		dest.writeString(imageUrlLowRes);
		dest.writeString(imageUrlMediumRes);
		dest.writeString(imageUrlHighRes);
		}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TVChannel) {
			TVChannel other = (TVChannel) o;
			if (getChannelId() != null && other.getChannelId() != null && getChannelId().equals(other.getChannelId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<TVChannel>	CREATOR	= new Parcelable.Creator<TVChannel>() {
		public TVChannel createFromParcel(Parcel in) {
			return new TVChannel(in);
		}

		public TVChannel[] newArray(int size) {
			return new TVChannel[size];
		}
	};

	@Override
	public String toString() {
	    return "Id: " + channelId + "\n name: " + name + "\n logoSUrl: " + imageUrlLowRes + "\n logoMUrl: " + imageUrlMediumRes + "\n logoLUrl: " + imageUrlHighRes; 
	}
}
