package com.millicom.mitv.enums;

public enum RequestIdentifierEnum {
	TV_DATE(0, "TV Dates"),
	TV_TAG(1, "TV Tags"),
	TV_CHANNEL(2, "TV Channel objects"),
	TV_GUIDE(3, "TV Guide"),
	APP_CONFIGURATION(4, "App config"),
	APP_VERSION(5, "App/Api version data"),
	ADS_ADZERK_GET(6, "Ad adzerk get"),
	ADS_ADZERK_SEEN(7, "Ad adzerk seen"),
	USER_LOGIN(8, "User login (MiTV)"),
	USER_SIGN_UP(9, "User sign up"),
	USER_FB_TOKEN(10, "User token from BE using Facebook token"), /* This is used when fetching a MiTV token from BE, using a FB token */
	USER_LIKES(11, "User likes"),
	TV_CHANNEL_IDS_USER(12, "TV Channel IDs user"),
	USER_SET_CHANNELS(13, "User set channels"),
	USER_ADD_LIKE(14, "User add like"),
	USER_REMOVE_LIKE(15, "User remove like"),
	USER_RESET_PASSWORD_SEND_EMAIL(16, "User reset password send email"),
	USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD(17, "User reset password confirm password"),
	USER_ACTIVITY_FEED_ITEM(18, "User activity feed item"),
	POPULAR_ITEMS(19, "Popular items"),
	TV_CHANNEL_IDS_DEFAULT(20, "TV channel ids default"),
	USER_LOGOUT(21, "User logout"),
	BROADCAST_DETAILS(22, "Broadcast details (with channel info"),
	REPEATING_BROADCASTS_FOR_PROGRAMS(23, "Repeating broadcasts for program"),
	BROADCASTS_FROM_SERIES_UPCOMING(24, "Upcoming broadcast for series"),
	SEARCH(25, "search"),
	INTERNAL_TRACKING(26, "Internal tracking"),
	USER_ACTIVITY_FEED_ITEM_MORE(27, "User activity feed item more"),
	USER_ACTIVITY_FEED_LIKES(28, "User activity feed likes"),
	INTERNET_CONNECTIVITY(29, "Internet connectivity check"),
	USER_FACEBOOK(30, "User login Facebook"),  /* This is used for facebook login with the facebook api */
	BROADCAST_PAGE_DATA(31, "Broadcast page data"); /* This is used with the BroadcastPage only, for notifying that all data has been fetched */
	
	
	
	private final int id;
	private final String description; /* Used for logs */

	RequestIdentifierEnum(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id);
		sb.append(": ");
		sb.append(this.description);

		return sb.toString();
	}

	public int getId() {
		return id;
	}
}
