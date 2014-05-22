package com.mitv.enums;

public enum RequestIdentifierEnum {
	TV_DATE(0, "TV Dates"),
	TV_TAG(1, "TV Tags"),
	TV_CHANNEL(2, "TV Channel objects"),
	TV_GUIDE_INITIAL_CALL(3, "TV Guide for the initial app call"),
	APP_CONFIGURATION(4, "App config"),
	APP_VERSION(5, "App/Api version data"),
	ADS_ADZERK_GET(6, "Ad adzerk get"),
	ADS_ADZERK_SEEN(7, "Ad adzerk seen"),
	USER_LOGIN(8, "User login (MiTV)"),
	USER_SIGN_UP(9, "User sign up"),
	USER_LOGIN_WITH_FACEBOOK_TOKEN(10, "User login using FB token"), /* This is used when fetching a MiTV token from BE, using a FB token */
	USER_LIKES(11, "User likes"),
	TV_CHANNEL_IDS_USER_INITIAL_CALL(12, "TV Channel IDs user inital call only"), /* Only use when fetching data from splash screen */
	USER_SET_CHANNELS(13, "User set channels"),
	USER_ADD_LIKE(14, "User add like"),
	USER_REMOVE_LIKE(15, "User remove like"),
	USER_RESET_PASSWORD_SEND_EMAIL(16, "User reset password send email"),
	USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD(17, "User reset password confirm password"),
	USER_ACTIVITY_FEED_ITEM(18, "User activity feed item"),
	POPULAR_ITEMS_INITIAL_CALL(19, "Popular items"),
	TV_CHANNEL_IDS_DEFAULT(20, "TV channel ids default"),
	USER_LOGOUT(21, "User logout"),
	BROADCAST_DETAILS(22, "Broadcast details (with channel info"),
	REPEATING_BROADCASTS_FOR_PROGRAMS(23, "Repeating broadcasts for program"),
	UPCOMING_BROADCASTS_FOR_SERIES(24, "Upcoming broadcast for series"),
	SEARCH(25, "search"),
	INTERNAL_TRACKING(26, "Internal tracking"),
	USER_ACTIVITY_FEED_ITEM_MORE(27, "User activity feed item more"),
	USER_ACTIVITY_FEED_LIKES(28, "User activity feed likes"),
	INTERNET_CONNECTIVITY(29, "Internet connectivity check"),
	BROADCAST_PAGE_DATA(31, "Broadcast page data"), /* This is used with the BroadcastPage only, for notifying that all data has been fetched */
	TV_BROADCASTS_FOR_TAGS(32, "TV Broadcasts for tags processing"),
	TV_GUIDE_STANDALONE(33, "TV Guide call for standlone calls"),  /* This identifier will be used when refetching the guide due to a channel change, or forcing a refetch */
	TV_CHANNEL_IDS_USER_STANDALONE(34, "TV Channel IDs user for standalone calls"), /* Used when start app as not logged in and then login in, need to update the TV Channels Ids */
	USER_ACTIVITY_FEED_INITIAL_DATA(35, "Feed items and likes for FeedActivity"),
	SNTP_CALL(36, "SNTP call"),
	DISQUS_THREAD_COMMENTS(37, "Disqus thread comments"),
	DISQUS_THREAD_DETAILS(38, "Disqus thread details"),
	POPULAR_ITEMS_STANDALONE(39, "Popular items standalone"),
	TV_BROADCASTS_POUPULAR_PROCESSING(40, "TV Broadcasts popular processing"),
	COMPETITIONS_ALL_INITIAL(41, "All available competitions"),
	COMPETITIONS_ALL_STANDALONE(42, "All available competitions"),
	COMPETITION_INITIAL_DATA(43, "Competition initial data agregation"),
	COMPETITION_BY_ID(44, "Specific Competition by ID"),
	COMPETITION_TEAMS(45, "All teams present in a specific competition"),
	COMPETITION_TEAM_BY_ID(46, "Specific team by ID"),
	COMPETITION_TEAM_DETAILS(47, "Details for a team present in a specific competition"),
	COMPETITION_PHASES(48, "All phases for a specific competition"),
	COMPETITION_PHASE_BY_ID(49, "A phase for a specific competition"),
	COMPETITION_EVENTS(50, "All events for a specific competition"),
	COMPETITION_EVENT_BY_ID(51, "An event for a specific competition"),
	COMPETITION_STANDINGS_BY_PHASE_ID(52, "All standings for a specific phase"),
	COMPETITION_STANDINGS_MULTIPLE_BY_PHASE_ID(53, "All standings for multiple specific phases"),
	COMPETITION_POST_PROCESSING(54, "Competition post processing"),
	COMPETITION_EVENT_HIGHLIGHTS(55, "Competition event highlights"),
	COMPETITION_EVENT_LINEUP(56, "Competition event line");
	
	
	
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
