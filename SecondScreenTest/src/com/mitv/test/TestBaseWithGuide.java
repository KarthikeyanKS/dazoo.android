package com.mitv.test;

import java.util.List;

import android.util.Log;

import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.models.gson.UserLoginData;

/**
 * 
 * @author atsampikakis
 *
 */
public class TestBaseWithGuide extends TestCore {
	
	
	private static final String	TAG	= "TestBaseWithGuide";
	
	
	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		
		getTVChannelGuides();
		
		UserLoginData data = PerformUserLoginTest.login();
			
		if(data != null)
		{
			userToken = data.getToken();
		}
		else
		{
			userToken = "";
			
			Log.w(TAG, "Login has failed.");
		}
	}

	
	protected static List<TVChannelGuide> tvChannelGuides;
	protected static List<TVDate> tvDates;
	protected static List<TVChannelId> tvChannelIdsUser;
	protected static String userToken;
	
	public static List<TVChannelGuide> getTVChannelGuides() {
		if(tvChannelGuides == null) {
			tvChannelGuides = TVChannelGuidesGSONTest.testFetchTVChannelGuides();
		}
		return tvChannelGuides;
	}
	
	public static List<TVChannelId> getTVChannelIdsUser() {
		if(tvChannelIdsUser == null) {
			tvChannelIdsUser = GetUserTVChannelIdsTest.getUserTVChannelIds();
		}
		return tvChannelIdsUser;
	}
	
	public static List<TVDate> getTVDates() {
		if(tvDates == null) {
			tvDates =TVDatesGSONTest.testFetchTVDates();
		}
		return tvDates;
	}
}