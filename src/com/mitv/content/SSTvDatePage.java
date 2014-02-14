package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.OldTVDate;

public class SSTvDatePage extends SSPage {

	public static final String	TAG	= "SSTvDatePage";

	private static SSTvDatePage	sInstance;

	public static SSTvDatePage getInstance() {
		if (sInstance == null) {
			sInstance = new SSTvDatePage();
		}
		return sInstance;
	}

	public SSTvDatePage() {
		super(Consts.URL_DATES);
	}
	
	public ArrayList<OldTVDate> getTvDates() {
		Log.d(TAG, "get Tv Dates");
		return super.getTvDates();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
		Log.d(TAG, "parseGetPageResult");
		try {
			super.parseTvDates(jsonArray);

			// The resulting page is this
			pageGetResult.setPage(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}

}
