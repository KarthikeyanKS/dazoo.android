package com.mitv.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Link implements Parcelable {

	private static final String TAG = "Link";
	public String		url;
	
	public Link() {
	}

	public void setUrl(String URL){
		url = URL;
	}
	
	public String getUrl(){
		return url;
	}
	
	public Link(JSONObject aJsonObject) throws Exception {
		setUrl(aJsonObject.getString("href"));
	}

	public Link(Parcel in) {
		url = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Link>	CREATOR	= new Parcelable.Creator<Link>() {
		@Override
		public Link createFromParcel(Parcel in) {
			return new Link(in);
		}

		@Override
		public Link[] newArray(int size) {
			return new Link[size];
		}
	};
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);		
	}
}
