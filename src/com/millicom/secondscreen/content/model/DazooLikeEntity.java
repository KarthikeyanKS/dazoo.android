package com.millicom.secondscreen.content.model;


import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DazooLikeEntity implements Parcelable{

	private String entityId;
	private String entityType;
	private String title;
	private String synopsisShort;
	private String synopsisLong;
	private String posterSUrl;
	private String posterMUrl;
	private String posterLUrl;
	private ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<Credit> credits = new ArrayList<Credit>();
	// "TV_EPISODE"-specific fields
	private int episodeNumber;
	private Season season;
	private ArrayList<Series> series = new ArrayList<Series>();
	// "MOVIE"-specific fields
	private int year;
	private String genre;
	
	public void setEntityId(String entityId){
		this.entityId = entityId;
	}
	
	public String getEntityId(){
		return this.entityId;
	}
	
	public void setEntityType(String entityType){
		this.entityType = entityType;
	}
	
	public String getEntityType(){
		return this.entityType;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setSynopsisShort(String synopsisShort){
		this.synopsisShort = synopsisShort;
	}
	
	public String getSynopsisShort(){
		return this.synopsisShort;
	}
	
	public void setSynopsisLong(String synopsisLong){
		this.synopsisLong = synopsisLong;
	}
	
	public String getSynopsisLong(){
		return this.synopsisLong;
	}
	
	public void setPosterSUrl(String posterSUrl){
		this.posterSUrl = posterSUrl;
	}
	
	public String getPosterSUrl(){
		return posterSUrl;
	}
	
	public void setPosterMUrl(String posterMUrl){
		this.posterMUrl = posterMUrl;
	}
	
	public String getPosterMUrl(){
		return this.posterMUrl;
	}
	
	public void setPosterLUrl(String posterLUrl){
		this.posterLUrl = posterLUrl;
	}
	
	public String getPosterLUrl(){
		return this.posterLUrl;
	}
	
	public void setTags(ArrayList<String> tags){
		this.tags = tags;
	}
	
	public ArrayList<String> getTags(){
		return this.tags;
	}
	
	public void setCredits(ArrayList<Credit> credits){
		this.credits = credits;
	}
	
	public ArrayList<Credit> getCredits(){
		return this.credits;
	}
	
	public void setEpisodeNumber(int episodeNumber){
		this.episodeNumber = episodeNumber;
	}
	
	public int getEpisodeNumber(){
		return this.episodeNumber;
	}
	
	public void setSeason(Season season){
		this.season = season;
	}
	
	public Season getSeason(){
		return this.season;
	}
	
	public void setSeries(ArrayList<Series> series){
		this.series = series;
	}
	
	public ArrayList<Series> getSeries(){
		return this.series;
	}
	
	public void setYear(int year){
		this.year = year;
	}
	
	public int getYear(){
		return this.year;
	}
	
	public void setGenre(String genre){
		this.genre = genre;
	}
	
	public String getGenre(){
		return this.genre;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(entityId);
		dest.writeString(entityType);
		dest.writeString(title);
		dest.writeString(synopsisShort);
		dest.writeString(synopsisLong);
		dest.writeString(posterSUrl);
		dest.writeString(posterMUrl);
		dest.writeString(posterLUrl);
		dest.writeSerializable(tags);
		dest.writeTypedList(credits);	
		dest.writeInt(episodeNumber);
		dest.writeParcelable(season, flags);
		dest.writeTypedList(series);
		dest.writeInt(year);
		dest.writeString(genre);
	}
	
	public DazooLikeEntity(Parcel in){
		entityId = in.readString();
		entityType = in.readString();
		title = in.readString();
		synopsisShort = in.readString();
		synopsisLong = in.readString();
		posterSUrl = in.readString();
		posterMUrl = in.readString();
		posterLUrl = in.readString();
		tags =  (ArrayList<String>) in.readSerializable();
		in.readTypedList(credits, Credit.CREATOR); 
		episodeNumber = in.readInt();
		season = (Season) in.readParcelable(Season.class.getClassLoader());
		in.readTypedList(series, Series.CREATOR);
		year = in.readInt();
		genre = in.readString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DazooLikeEntity) {
			DazooLikeEntity other = (DazooLikeEntity) o;
			if (getEntityId() != null && other.getEntityId() != null && getEntityId().equals(other.getEntityId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<DazooLikeEntity>	CREATOR	= new Parcelable.Creator<DazooLikeEntity>() {
		public DazooLikeEntity createFromParcel(Parcel in) {
			return new DazooLikeEntity(in);
		}

		public DazooLikeEntity[] newArray(int size) {
			return new DazooLikeEntity[size];
		}
	};
	
}
