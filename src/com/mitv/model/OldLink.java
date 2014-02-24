package com.mitv.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class OldLink implements Parcelable {

	private static final String TAG = "Link";
	public String		url;
	
	public OldLink() {
	}

	public void setUrl(String URL){
		url = URL;
	}
	
	public String getUrl(){
		return url;
	}
	
	public OldLink(JSONObject aJsonObject) throws Exception {
		setUrl(aJsonObject.getString("href"));
	}

	public OldLink(Parcel in) {
		url = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<OldLink>	CREATOR	= new Parcelable.Creator<OldLink>() {
		@Override
		public OldLink createFromParcel(Parcel in) {
			return new OldLink(in);
		}

		@Override
		public OldLink[] newArray(int size) {
			return new OldLink[size];
		}
	};
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);		
	}
}
