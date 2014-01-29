package com.mitv.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.Consts;

public class Channel extends ThreeImageResolutions implements Parcelable {

	private String channelId = "";
	private String name = "";
	
	public Channel(){		
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
	
	public Channel(Parcel in){
		channelId = in.readString();
		name = in.readString();
		String urlLowRes = in.readString();
		String urlMediumRes = in.readString();
		String urlHighRes = in.readString();
				
		setImageUrlPortraitOrSquareLow(urlLowRes);
		setImageUrlPortraitOrSquareMedium(urlMediumRes);
		setImageUrlPortraitOrSquareHigh(urlHighRes);
	}
	
	public Channel(JSONObject jsonChannel) {
		this.setChannelId(jsonChannel.optString(Consts.DAZOO_CHANNEL_CHANNEL_ID));
		this.setName(jsonChannel.optString(Consts.DAZOO_CHANNEL_NAME));

		try {
			JSONObject jsonPoster = jsonChannel.getJSONObject(Consts.DAZOO_CHANNEL_LOGO);
			if (jsonPoster != null) {
				this.setImageUrlPortraitOrSquareLow(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
				this.setImageUrlPortraitOrSquareMedium(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
				this.setImageUrlPortraitOrSquareHigh(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
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
		if (o instanceof Channel) {
			Channel other = (Channel) o;
			if (getChannelId() != null && other.getChannelId() != null && getChannelId().equals(other.getChannelId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Channel>	CREATOR	= new Parcelable.Creator<Channel>() {
		public Channel createFromParcel(Parcel in) {
			return new Channel(in);
		}

		public Channel[] newArray(int size) {
			return new Channel[size];
		}
	};

	@Override
	public String toString() {
	    return "Id: " + channelId + "\n name: " + name + "\n logoSUrl: " + imageUrlLowRes + "\n logoMUrl: " + imageUrlMediumRes + "\n logoLUrl: " + imageUrlHighRes; 
	}
}
