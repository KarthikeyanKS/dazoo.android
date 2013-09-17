package com.millicom.secondscreen.content.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class SSSection implements Parcelable{

	private String	id			= "";
	private String	type		= "";
	private Link	link;
	
	private boolean	mIsSelected	= false;

	public SSSection(Link link) {
		this.link = link;
	}

	public SSSection(JSONObject aJsonObject) throws Exception {
		link = new Link(aJsonObject);
		setId(aJsonObject.optString("id"));
		setType(aJsonObject.optString("type"));
	}

	public SSSection(Parcel in) {
		id = (in.readString());
		type = (in.readString());
		link = (in.readParcelable(Link.class.getClassLoader()));
		mIsSelected = (in.readByte() == 1);
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public Link getLink() {
		return link;
	}

	public boolean isSelected() {
		return mIsSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.mIsSelected = isSelected;
	}

	public static final Parcelable.Creator<SSSection>	CREATOR	= new Parcelable.Creator<SSSection>() {
		@Override
		public SSSection createFromParcel(Parcel in) {
			return new SSSection(in);
		}

		@Override
		public SSSection[] newArray(int size) {
			return new SSSection[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(type);
		dest.writeParcelable(link, flags);
		dest.writeByte((byte) (mIsSelected ? 1 : 0));
	}
}
