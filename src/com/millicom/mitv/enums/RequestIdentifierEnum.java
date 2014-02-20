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
	BROADCASTS_FROM_PROGRAMS(23),
	BROADCASTS_FROM_SERIES_UPCOMING(24),
	SEARCH(25),
	INTERNAL_TRACKING(26),
	USER_ACTIVITY_FEED_ITEM_MORE(27);
	
	
	private final int id;
	
	RequestIdentifierEnum(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
