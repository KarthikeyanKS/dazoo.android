package com.mitv.test;

import java.util.List;

import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;

/**
 * 
 * @author atsampikakis
 *
 */
public class TestBaseWithGuide extends TestCore {
	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		getTVChannelGuides();
	}

	
	protected static List<TVChannelGuide> tvChannelGuides;
	protected static List<TVDate> tvDates;
	protected static List<TVChannelId> tvChannelIdsUser;

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
