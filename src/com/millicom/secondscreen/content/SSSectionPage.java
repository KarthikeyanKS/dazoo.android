package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.millicom.secondscreen.content.model.Category;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.SSSection;

import android.util.Log;

public class SSSectionPage extends SSPage{

	private static final String TAG = "SSSectionPage";
	
	private SSSection	mSection;

	// Public constructor always needs to be created with a section
	public SSSectionPage(SSSection section) {
		this.mSection = section;
	}

	public Link getLink() {
		return mSection.getLink();
	}

	public String getId() {
		return mSection.getId();
	}

	protected void parseGetPageResult(JSONArray aJsonArray, SSPageGetResult aSSPageGetResult){
	
		Log.d(TAG, "parseGetPageResult");

		try {
			// TODO : Parse the section data like categories, blocks etc
		
			// The resulting page is this
			aSSPageGetResult.setPage(this);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
