package com.millicom.secondscreen.content.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Program implements Parcelable {

	private String programId;
	private String programType;
	private String title;
	private String subtitle;
	private String posterSUrl;
	private String posterMUrl;
	private String posterLUrl;
	private String cast; 			// TODO
	private String year;
	private String runtime;
	private ArrayList<String> tags = new ArrayList<String>();
	private Season season;
	private String episode;
	private String description;
	private String synopsisShort;
	private String synopsisLong;
	private Series series;
	
	public Program(){
	}
	
	public void setProgramId(String programId){
		this.programId = programId;
	}
	
	public String getProgramId(){
		return this.programId;
	}
	
	public void setProgramType(String programType){
		this.programType = programType;
	}
	
	public String getProgramType(){
		return this.programType;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setSubtitle(String subtitle){
		this.subtitle = subtitle;
	}
	
	public String getSubtitle(){
		return this.subtitle;
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
	
	public void setCast(String cast){
		this.cast = cast;
	}
	
	public String getCast(){
		return this.cast;
	}
	
	public void setYear(String year){
		this.year = year;
	}
	
	public String getYear(){
		return this.year;
	}
	
	public void setRuntime(String runtime){
		this.runtime = runtime;
	}
	
	public String getRuntime(){
		return runtime;
	}
	
	public void setTags(ArrayList<String> tags){
		this.tags = tags;
	}
	
	public ArrayList<String> getTags(){
		return this.tags;
	}
	
	public void setSeason(Season season){
		this.season = season;
	}
	
	public Season getSeason(){
		return this.season;
	}
	
	public void setEpisode(String episode){
		this.episode = episode;
	}
	
	public String getEpisode(){
		return this.episode;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
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
	
	public void setSeries(Series series){
		this.series = series;
	}
	
	public Series getSeries(){
		return this.series;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(programId);
		dest.writeString(programType);
		dest.writeString(title);
		dest.writeString(subtitle);
		dest.writeString(posterSUrl);
		dest.writeString(posterMUrl);
		dest.writeString(posterLUrl);
		dest.writeString(cast);
		dest.writeString(year);	
		dest.writeString(runtime);
		dest.writeParcelable(season, flags);
		dest.writeString(episode);
		dest.writeString(description);
		dest.writeSerializable(tags);
		dest.writeString(synopsisShort);
		dest.writeString(synopsisLong);
		//dest.writeParcelable(series, flags);
	}
	
	public Program(Parcel in){
		programId = in.readString();
		programType = in.readString();
		title = in.readString();
		subtitle = in.readString();
		posterSUrl = in.readString();
		posterMUrl = in.readString();
		posterLUrl = in.readString();
		cast = in.readString();
		year = in.readString();
		runtime = in.readString();
		season = (Season) in.readParcelable(Season.class.getClassLoader());
		episode = in.readString();
		description = in.readString();
		tags = (ArrayList<String>) in.readSerializable();
		synopsisShort = in.readString();
		synopsisLong = in.readString();
		//series = (Series) in.readParcelable(Series.class.getClassLoader());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Program) {
			Program other = (Program) o;
			if (getProgramId() != null && other.getProgramId() != null && getProgramId().equals(other.getProgramId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Program>	CREATOR	= new Parcelable.Creator<Program>() {
		public Program createFromParcel(Parcel in) {
			return new Program(in);
		}

		public Program[] newArray(int size) {
			return new Program[size];
		}
	};
	
	@Override
	public String toString() {
	    return "Id: " + programId + "\n programType: " + programType + "\n title: " + title + "\n subtitle: " + subtitle + "\n posterSUrl:" + posterSUrl + "\n posterMUrl" + posterMUrl 
	    		+ "\n posterLUrl" + posterLUrl + "\n cast" + cast + "\n year: " + year + "\n runtime: " + runtime + "\n season: " + season + "\n episode:" + episode + 
	    		"\n synopsisShort" + synopsisShort + "\n synopsisLong" + synopsisLong;  
	}
}
