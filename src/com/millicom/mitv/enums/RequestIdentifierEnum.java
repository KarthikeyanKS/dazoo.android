package com.millicom.mitv.enums;

public enum RequestIdentifierEnum {
	TV_DATE(0),
	TV_TAG(1),
	TV_CHANNEL(2),
	TV_GUIDE(3),
	APP_CONFIGURATION(4),
	APP_VERSION(5),
	ADS(6),
	USER_LOGIN(7),
	USER_SIGN_UP(8),
	USER_FB_TOKEN(9), /* This is used when fetching a MiTV token from BE, using a FB token */
	USER_LIKES(10),
	TV_CHANNEL_IDS_USER(11),
	USER_SET_CHANNELS(12),
	USER_ADD_LIKE(13),
	USER_REMOVE_LIKE(14),
	USER_RESET_PASSWORD(15),
	USER_ACTIVITY_FEED_ITEM(16),
	POPULAR_ITEMS(17),
	TV_CHANNEL_IDS_DEFAULT(18),
	USER_LOGOUT(19);
	
	private final int id;
	
	RequestIdentifierEnum(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
