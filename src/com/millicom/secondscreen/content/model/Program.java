package com.millicom.secondscreen.content.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Program implements Parcelable {

	private String programId;
	private String programType;
	private String title;
	private String synopsisShort;
	private String synopsisLong;
	private String posterSUrl;
	private String posterMUrl;
	private String posterLUrl;
	private ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<Credit> credits = new ArrayList<Credit>(); 	
	// specific for programType = "TV_EPISODE"
	private int episodeNumber;
	private Season season;
	private Series series;
	// specific for programType = "MOVIE"
	private int year;
	private String genre;
	// specific for programType = "SPORT"
	private String sportType;
	private String tournament;
	
	
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
	
	public void setSeries(Series series){
		this.series = series;
	}
	
	public Series getSeries(){
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
	
	public void setSportType(String sportType){
		this.sportType = sportType;
	}
	
	public String getSportType(){
		return this.sportType;
	}
	
	public void setTournament(String tournament){
		this.tournament = tournament;
	}
	
	public String getTournament(){
		return this.tournament;
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
		dest.writeString(synopsisShort);
		dest.writeString(synopsisLong);
		dest.writeString(posterSUrl);
		dest.writeString(posterMUrl);
		dest.writeString(posterLUrl);
		dest.writeSerializable(tags);
		dest.writeTypedList(credits);
		dest.writeInt(episodeNumber);
		dest.writeParcelable(season, flags);
		dest.writeParcelable(series, flags);
		dest.writeInt(year);
		dest.writeString(genre);
		dest.writeString(sportType);
		dest.writeString(tournament);
	}
	
	public Program(Parcel in){
		programId = in.readString();
		programType = in.readString();
		title = in.readString();
		synopsisShort = in.readString();
		synopsisLong = in.readString();
		posterSUrl = in.readString();
		posterMUrl = in.readString();
		posterLUrl = in.readString();
		tags = (ArrayList<String>) in.readSerializable();
		credits = in.readArrayList(Credit.class.getClassLoader());
		episodeNumber = in.readInt();
		season = in.readParcelable(Season.class.getClassLoader());
		series = in.readParcelable(Series.class.getClassLoader());
		year = in.readInt();
		genre = in.readString();
		sportType = in.readString();
		tournament = in.readString();
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
	    return "Id: " + programId + "\n programType: " + programType + "\n title: " + title +  "\n posterSUrl:" + posterSUrl + "\n posterMUrl" + posterMUrl 
	    		+ "\n posterLUrl" + posterLUrl + "\n year: " + year +  "\n season: " + season + 
	    		"\n synopsisShort" + synopsisShort + "\n synopsisLong" + synopsisLong;  
	}
}
