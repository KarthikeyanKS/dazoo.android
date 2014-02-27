package com.millicom.mitv.enums;

public enum RequestIdentifierEnum {
	TV_DATE(0),
	TV_TAG(1),
	TV_CHANNEL(2),
	TV_GUIDE(3),
	APP_CONFIGURATION(4),
	APP_VERSION(5),
	ADS_ADZERK_GET(6),
	ADS_ADZERK_SEEN(7),
	USER_LOGIN(8),
	USER_SIGN_UP(9),
	USER_FB_TOKEN(10), /* This is used when fetching a MiTV token from BE, using a FB token */
	USER_LIKES(11),
	TV_CHANNEL_IDS_USER(12),
	USER_SET_CHANNELS(13),
	USER_ADD_LIKE(14),
	USER_REMOVE_LIKE(15),
	USER_RESET_PASSWORD_SEND_EMAIL(16),
	USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD(17),
	USER_ACTIVITY_FEED_ITEM(18),
	POPULAR_ITEMS(19),
	TV_CHANNEL_IDS_DEFAULT(20),
	USER_LOGOUT(21),
	BROADCAST_DETAILS(22),
	REPEATING_BROADCASTS_FOR_PROGRAMS(23),
	BROADCASTS_FROM_SERIES_UPCOMING(24),
	SEARCH(25),
	INTERNAL_TRACKING(26),
	USER_ACTIVITY_FEED_ITEM_MORE(27),
	USER_ACTIVITY_FEED_LIKES(28),
	INTERNET_CONNECTIVITY(29),
	USER_FACEBOOK(30),  /* This is used for facebook login with the facebook api */
	BROADCAST_PAGE_DATA(31), /* This is used with the BroadcastPage only, for notifying that all data has been fetched */
	USER_MY_PROFILE_DATA(32);
	
	
	
	private final int id;
	
	RequestIdentifierEnum(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
