package com.millicom.secondscreen.content.model;

import android.os.Parcel;
import android.os.Parcelable;

// domain model for cast and crew to the movie or series
public class Cast implements Parcelable{

	String actorName;
	String characterName;
	String posterUrl;
	
	public Cast(){
	}
	
	public void setActorName(String actorName){
		this.actorName = actorName;
	}
	
	public String getActorName(){
		return actorName;
	}
	
	public void setCharacterName(String characterName){
		this.characterName = characterName;
	}
	
	public String getCharacterName(){
		return this.characterName;
	}
	
	public void setPosterUrl(String posterUrl){
		this.posterUrl = posterUrl;
	}
	
	public String getPosterUrl(){
		return this.posterUrl;
	}
	
	public Cast(Parcel in){
		actorName = in.readString();
		characterName = in.readString();
		posterUrl = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(actorName);
		dest.writeString(characterName);
		dest.writeString(posterUrl);
	}

	public static final Parcelable.Creator<Cast>	CREATOR	= new Parcelable.Creator<Cast>() {
		public Cast createFromParcel(Parcel in) {
			return new Cast(in);
		}

		public Cast[] newArray(int size) {
			return new Cast[size];
		}
	};
}
