package com.mitv.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DazooLikeEntity implements Parcelable {

	private String	title;
	// likeType = SERIES
	private String	seriesId;
	// likeType = PROGRAM
	private String	programType;
	private String	programId;
	// likeType = PROGRAM : OTHER
	private String	category;
	// likeType = PROGRAM : MOVIE
	private String	genre;
	private int	year;
	//likeTyoe = SPORT_TYPE
	private String sportTypeId;

	public DazooLikeEntity() {
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}
	
	public void setSeriesId(String seriesId){
		this.seriesId = seriesId;
	}
	
	public String getSeriesId(){
		return this.seriesId;
	}
	
	public void setProgramType(String programType){
		this.programType = programType;
	}
	
	public String getProgramType(){
		return this.programType;
	}
	
	public void setProgramId(String programId){
		this.programId = programId;
	}
	
	public String getProgramId(){
		return this.programId;
	}
	
	public void setCategory(String category){
		this.category = category;
	}
	
	public String getCategory(){
		return this.category;
	}
	
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getGenre() {
		return this.genre;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return this.year;
	}
	
	public void setSportTypeId(String sportTypeId){
		this.sportTypeId = sportTypeId;
	}
	
	public String getSportTypeId(){
		return this.sportTypeId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(seriesId);
		dest.writeString(programType);
		dest.writeString(programId);
		dest.writeString(category);
		dest.writeString(genre);
		dest.writeInt(year);
		dest.writeString(sportTypeId);
	}

	public DazooLikeEntity(Parcel in) {
		title = in.readString();
		seriesId = in.readString();
		programType = in.readString();
		programId = in.readString();
		category = in.readString();
		genre = in.readString();
		year = in.readInt();
		sportTypeId = in.readString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DazooLikeEntity) {
			DazooLikeEntity other = (DazooLikeEntity) o;
			if (getTitle() != null && other.getTitle() != null && getTitle().equals(other.getTitle())) {
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
