package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.TvDate;

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
		super(Consts.MILLICOM_SECONDSCREEN_DATES_PAGE_URL);
	}
	
	public ArrayList<TvDate> getTvDates() {
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
