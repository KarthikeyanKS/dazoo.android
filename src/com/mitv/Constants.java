
package com.mitv;

import com.mitv.activities.FeedActivity;



/**
 * Class for constant values declaration for the application
 * 
 */
public abstract class Constants 
{
	/* HTTP and HTTPS schemas */
	public static final String HTTP_SCHEME = "http://";
	public static final String HTTPS_SCHEME	= "https://";
	public static final String FORWARD_SLASH = "/";
	
	/* BACKEND API BASE URLs */
	public static final String BACKEND_TEST_ENVIRONMENT = "gitrgitr.com" + FORWARD_SLASH;
	public static final String BACKEND_PRODUCTION_ENVIRONMENT = "mi.tv" + FORWARD_SLASH;
	
	/* FRONTEND API BASE URLs */
	public static final String FRONTEND_TEST_ENVIRONMENT = "gitrgitr.com";
	public static final String FRONTEND_PRODUCTION_ENVIRONMENT = "mi.tv";
	
		
	/* CONFIGURATIONS FOR RELEASE */
	public static final String HTTP_SCHEME_USED	= HTTP_SCHEME;
	public static final String BACKEND_ENVIRONMENT_USED = BACKEND_TEST_ENVIRONMENT;
//	public static final String BACKEND_ENVIRONMENT_USED = BACKEND_PRODUCTION_ENVIRONMENT;
	public static final String FRONTEND_ENVIRONMENT_USED = FRONTEND_TEST_ENVIRONMENT;
//	public static final String FRONTEND_ENVIRONMENT_USED = FRONTEND_PRODUCTION_ENVIRONMENT;
	public static final String SUPPORTED_API_VERSION = "1.0.0";
	public static final String APP_WAS_PREINSTALLED_FILE_NAME = "59b039d2c0c0a7fbe163";
	public static final boolean FORCE_SPANISH_LOCALE = true;
	public static final boolean IS_PREINSTALLED_VERSION = false;
	public static final String CACHE_DATABASE_NAME = "com.mitv.cache.db";
	public static final int CACHE_DATABASE_VERSION = 1;
	public static final boolean FORCE_CACHE_DATABASE_FLUSH = false;
	public static final boolean FORCE_DEFAULT_GOOGLE_TRACKING_ID = true;
	public static final boolean FORCE_ENABLE_DISQUS_COMMENTS = false;
	public static final boolean USE_HOCKEY_APP_CRASH_REPORTS = false;
	public static final boolean USE_HOCKEY_APP_UPDATE_NOTIFICATIONS = false;
	public static final boolean ENABLE_STRICT_MODE = false;
	public static final boolean IGNORE_INVALID_SSL_CERTIFICATES = false;
	public static final boolean ENABLE_FIRST_TIME_TUTORIAL_VIEW = true;
	public static final boolean ENABLE_AMAZON_INSIGHTS = true;
	
	
	/* AMAZON INSIGHTS DEMO */
	public static final String AMAZON_INSIGHTS_IDENTIFIER = "mi.tv.example";
	public static final String AMAZON_INSIGHTS_PUBLIC_KEY = "f2a04686bcc24285a46a7823300fcf1b";
	public static final String AMAZON_INSIGHTS_PRIVATE_KEY = "7GCv830tpJAbqSC5BsXYwJ2S1JGto2Ej29wD99BQK6M";
	
	/* HockeyApp Settings */
	public static final String TESTFLIGHT_TOKEN = "343b5e95-cc27-4e8e-8a0d-ff5f7a181c5c";
	public static final String HOCKEY_APP_TOKEN = "c90b5331b5a7086d88d98021508f2c16";

	/* Request configurations */
	public static final String JSON_MIME_TYPE = "application/json";
	public static final String HTTP_CORE_DEAFULT_ENCODING = "UTF-8";
	public static final int HTTP_CORE_DEFAULT_HTTP_STATUS_RESULT = 1000;
	
	/* Misc configurations */
	public static final String ELLIPSIS_STRING = "...";
		
	/* "Static", don't need to change those */
	public static final String URL_BACKEND_BASE_API					= "api.";
	public static final String URL_BACKEND_BASE_INTERNAL_TRACKING	= "tracking.";
	public static final String BASE_API_URL_USED 					= URL_BACKEND_BASE_API + BACKEND_ENVIRONMENT_USED;
	public static final String URL_SERVER							= HTTP_SCHEME_USED + BASE_API_URL_USED;
	public static final String URL_INTERNAL_TRACKING_SUFFIX			= "track/unique";
	public static final String URL_INTERNAL_TRACKING				= HTTP_SCHEME_USED + URL_BACKEND_BASE_INTERNAL_TRACKING + BACKEND_ENVIRONMENT_USED + URL_INTERNAL_TRACKING_SUFFIX;
	public static final String URL_FRONTEND_ENVIRONMENT 			= HTTP_SCHEME_USED + FRONTEND_ENVIRONMENT_USED;
	
	public static final String URL_GUIDE 							= URL_SERVER + "epg/guide";
	public static final String URL_DATES 							= URL_SERVER + "epg/dates";
	public static final String URL_CHANNELS_ALL 					= URL_SERVER + "epg/channels";
	public static final String URL_CHANNELS_DEFAULT 				= URL_SERVER + "epg/channels/default";
	public static final String URL_FACEBOOK_TOKEN 					= URL_SERVER + "auth/login/facebook";
	public static final String URL_LOGIN_WITH_PLAINTEXT_PASSWORD 	= URL_SERVER + "auth/login/dazoo";
	public static final String URL_LOGIN_WITH_HASHED_PASSWORD 		= URL_SERVER + "auth/login/dazoo/hash";
	public static final String URL_REGISTER_WITH_PLAINTEXT_PASSWORD = URL_SERVER + "auth/login/dazoo/register";
	public static final String URL_REGISTER_WITH_HASHED_PASSWORD 	= URL_SERVER + "auth/login/dazoo/register/hash";
	public static final String URL_RESET_PASSWORD_SEND_EMAIL		= URL_SERVER + "auth/login/dazoo/sendResetPasswordEmail";
	public static final String URL_RESET_AND_CONFIRM_PASSWORD 		= URL_SERVER + "auth/login/dazoo/resetPassword";
	public static final String URL_TAGS_PAGE 						= URL_SERVER + "epg/tags/visible";
	public static final String URL_MY_CHANNEL_IDS 					= URL_SERVER + "my/channels";
	public static final String URL_LIKES 							= URL_SERVER + "my/likes";
	public static final String URL_LIKES_WITH_UPCOMING 				= URL_SERVER + "my/likes/broadcasts";
	public static final String URL_ACTIVITY_FEED 					= URL_SERVER + "my/feed";
	public static final String URL_PROGRAMS 						= URL_SERVER + "epg/programs/";
	public static final String URL_SERIES 							= URL_SERVER + "epg/series/";
	public static final String URL_POPULAR 							= URL_SERVER + "epg/broadcasts/popular/";
	public static final String URL_CONFIGURATION 					= URL_SERVER + "configuration";
	public static final String URL_SEARCH		 					= URL_SERVER + "search";
	public static final String URL_SEARCH_OLD		 				= URL_SERVER + "search?query=%s*";
	public static final String URL_API_VERSION						= URL_SERVER + "versions";
	public static final String URL_NOTIFY_BROADCAST_PREFIX 			= URL_SERVER + "epg/channels/";
	public static final String URL_AUTH_TOKENS 					 	= URL_SERVER + "auth/tokens/";
	
	/* API request strings */
	public static final String	API_CHANNEL_ID_WITH_EQUALS_SIGN		= "channelId=";
	public static final String	API_CHANNEL_ID						= "channelId";
	public static final String	API_FACEBOOK_TOKEN					= "facebookToken";
	public static final String	API_TOKEN							= "token";
	public static final String	API_USER_ID							= "userId";
	public static final String	API_EMAIL							= "email";
	public static final String	API_PASSWORD						= "password";
	public static final String	API_FIRSTNAME						= "firstName";
	public static final String	API_LASTNAME						= "lastName";
	public static final String	API_CREATED							= "created";
	public static final String	API_PROFILEIMAGE					= "profileImage";
	public static final String	API_URL								= "url";
	public static final String	API_USER							= "user";
	public static final String	REQUEST_QUERY_AND					= "&";
	public static final String	REQUEST_QUERY_SEPARATOR				= "/";
	public static final String	REQUEST_PARAMETER_SEPARATOR			= "?";
	public static final String	API_ENTITY_ID						= "entityId";
	public static final String	API_LIKETYPE						= "likeType";
	public static final String	API_UPCOMING						= "upcoming";
	public static final String	API_BROADCASTS						= "/broadcasts";
	public static final String	API_UPCOMING_BROADCASTS				= "/broadcasts/upcoming";
	public static final String	API_POPULAR_COUNT					= "count";
	
	public static final String	API_SKIP							= "skip";
	public static final String	API_LIMIT							= "limit";

	public static final String	EMPTY_STRING						= "";
	public static final String	ERROR_STRING						= "error";

	/* restrictions */
	public static final int TVGUIDE_NUMBER_OF_ITEMS_PER_CHANNEL		= 3;
	public static final int TVGUIDE_NUMBER_OF_CHANNELS_PER_PAGE		= 10;
	public static final int PASSWORD_LENGTH_MIN						= 6;
	public static final int PASSWORD_LENGTH_MAX						= 20;
	public static final int USER_FIRSTNAME_LENGTH_MIN				= 1;
	public static final int USER_LASTNAME_LENGTH_MIN				= 1;
	public static final int API_POPULAR_COUNT_DEFAULT				= 3;

	public static final String	ISO_8601_DATE_FORMAT								= "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String  CALENDAR_TO_STRING_FOR_DEBUG						= "yyyy-MM-dd HH:mm:ss";
	public static final String	DATE_FORMAT_DATE									= "yyyy-MM-dd";
	public static final String	DATE_FORMAT_HOUR_AND_MINUTE							= "HH:mm";
	public static final String	DATE_FORMAT_HOUR_AND_MINUTE_WITH_AM_PM				= "HH:mm a";
	public static final String	DATE_FORMAT_DAY_AND_MONTH							= "dd/MM";
	
	/* Data extra intents */
	public static final String	INTENT_EXTRA_CHANNEL_ID								= "com.mitv.intent.extra.channel.id";
	public static final String	INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS			= "com.mitv.intent.extra.begintimeinmillis";
	public static final String	INTENT_EXTRA_LOG_IN_ACTION							= "com.mitv.intent.extra.log.in.action";

	/* Alarm extras */
	public static final String	INTENT_NOTIFICATION									= "NOTIFICATION"; //WARNING do NOT change this without changing in the Android Manifest> <action android:name="NOTIFICATION" />
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS		= "com.mitv.intent.alarm.extra.broadcast.begintimemillis";
	public static final String	INTENT_ALARM_EXTRA_CHANNELID						= "com.mitv.intent.alarm.extra.channelid";
	public static final String	INTENT_ALARM_EXTRA_NOTIFICIATION_ID					= "com.mitv.intent.alarm.extra.notification.id";
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_NAME					= "com.mitv.intent.alarm.extra.broadcast.name";
	public static final String	INTENT_ALARM_EXTRA_CHANNEL_NAME						= "com.mitv.intent.alarm.extra.channel.name";
	public static final String	INTENT_ALARM_EXTRA_CHANNEL_LOGO_URL					= "com.mitv.intent.alarm.extra.channel.logo.url";
	public static final String	INTENT_ALARM_EXTRA_DATE_DATE						= "com.mitv.intent.alarm.extra.date.date";
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_HOUR_AND_MINUTE_TIME	= "com.mitv.intent.alarm.extra.broadcast.time";

	public static final String	INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO	= "com.mitv.intent.extra.need.to.download.broadcast.with.channel.info";
	
	public static final String	INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN			= "com.mitv.intent.extra.activity.user.login.success";
	public static final String	INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT			= "com.mitv.intent.extra.activity.user.logout.success";
	
	/* Profile extras */
	public static final String	INTENT_EXTRA_FROM_PROFILE							= "com.mitv.intent.extra.from.profile";
	public static final String	INTENT_EXTRA_SEARCHSTRING							= "com.mitv.intent.extra.searchstring";

	/* Fragments extra arguments */
	public static final String	FRAGMENT_EXTRA_TAG_DISPLAY_NAME						= "com.mitv.fragment.extra.tag.displayname";
	public static final String	FRAGMENT_EXTRA_TAG_ID								= "com.mitv.fragment.extra.tag.id";

	/* TVGuide */
	public static final int		TV_GUIDE_NEXT_PROGRAMS_NUMBER						= 3;
	
	public static final int		FEED_ACTIVITY_FEED_ITEM_INITIAL_COUNT				= 10;
	public static final int		FEED_ACTIVITY_FEED_ITEM_MORE_COUNT					= 5;


	/* Notifications database */
	public static final String	NOTIFICATION_DATABASE_NAME							= "notifications.db";
	/* The database version must be updated after a change in the database schema */
	public static final int		NOTIFICATION_DATABASE_VERSION						 = 4;
	public static final String	NOTIFICATION_DB_TABLE_NOTIFICATIONS					 = "notifications";
	public static final String	NOTIFICATION_DB_COLUMN_NOTIFICATION_ID				 = "notification_id";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME_IN_MILISECONDS = "begin_time_miliseconds";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_BEGIN_TIME			 = "begin_time";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_END_TIME			 = "end_time";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_TYPE				 = "broadcast_type";
	public static final String	NOTIFICATION_DB_COLUMN_SHARE_URL					 = "share_url";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_ID					 = "channel_id";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_NAME					 = "channel_name";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_SMALL			 = "channel_logo_small";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_MEDIUM			 = "channel_logo_medium";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_LARGE			 = "channel_logo_large";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_ID					 = "program_id";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_TITLE				 = "program_title";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_TYPE					 = "program_type";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_SHORT		 = "program_synopsis_short";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_SYNOPSIS_LONG		 = "program_synopsis_long";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_TAGS					 = "program_tags";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_CREDITS				 = "program_credits";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_SEASON 				 = "program_season";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE 				 = "program_episode";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_YEAR 				 = "program_year";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_GENRE 				 = "program_genre";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_CATEGORY 			 = "program_category";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_LOGO_PORTRAIT_SMALL 	 = "program_logo_portrait_small";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_LOGO_PORTRAIT_MEDIUM  = "program_logo_portrait_medium";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_LOGO_PORTRAIT_LARGE 	 = "program_logo_portrait_large";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_LOGO_LANDSCAPE_SMALL  = "program_logo_landscape_small";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_LOGO_LANDSCAPE_MEDIUM = "program_logo_landscape_medium";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_LOGO_LANDSCAPE_LARGE  = "program_logo_landscape_large";
	
	public static final String	NOTIFICATION_DB_COLUMN_SERIES_ID					= "series_id";
	public static final String	NOTIFICATION_DB_COLUMN_SERIES_NAME					= "series_name";
	public static final String	NOTIFICATION_DB_COLUMN_SPORT_TYPE_ID				= "sport_type_id";
	public static final String	NOTIFICATION_DB_COLUMN_SPORT_TYPE_NAME				= "sport_type_name";
	


	// =========================== CONTENT ================================

	/* PROGRAM fields */
	public static final String	PROGRAM_ID									= "programId";
	public static final String	PROGRAM_TYPE									= "programType";
	public static final String	PROGRAM_TITLE									= "title";
	public static final String	PROGRAM_SYNOPSIS_SHORT						= "synopsisShort";
	public static final String	PROGRAM_SYNOPSISS_LONG						= "synopsisLong";

	public static final String	PROGRAM_IMAGES								= "images";
	public static final String	PROGRAM_TAGS									= "tags";
	public static final String	PROGRAM_CREDITS								= "credits";
	public static final String	PROGRAM_EPISODE								= "episodeNumber";
	public static final String	PROGRAM_SEASON								= "season";
	public static final String	PROGRAM_SERIES								= "series";
	public static final String	PROGRAM_YEAR									= "year";
	public static final String	PROGRAM_GENRE									= "genre";
	public static final String	PROGRAM_SPORTTYPE								= "sportType";
	public static final String	PROGRAM_TOURNAMENT							= "tournament";
	public static final String	PROGRAM_CATEGORY								= "category";
	public static final String	PROGRAM_TYPE_TV_EPISODE						= "TV_EPISODE";
	public static final String	PROGRAM_TYPE_MOVIE							= "MOVIE";
	public static final String	PROGRAM_TYPE_OTHER							= "OTHER";
	public static final String	PROGRAM_TYPE_SPORT							= "SPORT";
	public static final String	PROGRAM_TYPE_SPORTTYPE						= "sportType";

	public static final String	LIKE_ENTITY_TYPE_PROGRAM						= "PROGRAM";
	public static final String	LIKE_ENTITY_TYPE_SERIES						= "SERIES";
	public static final String 	SERIES_DISPLAY_STRING							= "Series";
	public static final String 	CHANNEL_DISPLAY_STRING						= "Channel";
	public static final String 	PROGRAM_DISPLAY_STRING_TV_EPISODE				= "TV Episode";
	public static final String 	PROGRAM_DISPLAY_STRING_MOVIE					= "Movie";
	public static final String 	PROGRAM_DISPLAY_STRING_OTHER					= "Other";
	public static final String 	PROGRAM_DISPLAY_STRING_SPORT					= "Sport";

	/* BROADCAST fields */
	public static final String	BROADCAST_PROGRAM								= "program";
	public static final String	BROADCAST_CHANNEL								= "channel";
	public static final String	BROADCAST_BROADCAST_TYPE						= "broadcastType";
	public static final String	BROADCAST_BEGIN_TIME							= "beginTime";
	public static final String	BROADCAST_END_TIME							= "endTime";
	public static final String	BROADCAST_BEGIN_TIME_MILLIS					= "beginTimeMillis";
	public static final String	BROADCAST_SHARE_URL							= "shareUrl";

	/* CHANNEL fields */
	public static final String	CHANNEL_CHANNEL_ID							= "channelId";
	public static final String	CHANNEL_NAME									= "name";
	public static final String	CHANNEL_LOGO									= "logo";

	/* SEASON fields */
	public static final String	SEASON_NUMBER									= "number";

	/* IMAGE fields */
	public static final String	IMAGE_TYPE_LANDSCAPE							= "landscape";
	public static final String	IMAGE_TYPE_PORTRAIT							= "portrait";
	public static final String	IMAGE_SMALL									= "small";
	public static final String	IMAGE_MEDIUM									= "medium";
	public static final String	IMAGE_LARGE									= "large";

	/* TAG fields */
	public static final String	TAG_ID										= "id";
	public static final String	TAG_NAME										= "displayName";
	public static final String	TAG_ALIAS										= "alias";

	/* DATE fields */
	public static final String	DATE_ID										= "id";
	public static final String	DATE_NAME										= "displayName";
	public static final String	DATE_DATE										= "date";

	/* GUIDE fields */
	public static final String	GUIDE_CHANNEL_ID								= "channelId";
	public static final String	GUIDE_LOGO									= "logo";
	public static final String	GUIDE_BROADCASTS								= "broadcasts";

	/* LIKE fields */
	public static final String	LIKE_ENTITYID									= "entityId";
	public static final String	LIKE_LIKETYPE									= "likeType";
	public static final String	LIKE_TYPE_SERIES								= "SERIES";
	public static final String	LIKE_TYPE_PROGRAM								= "PROGRAM";
	public static final String	LIKE_TYPE_SPORT_TYPE							= "SPORT_TYPE";
	public static final String	LIKE_SERIES_SERIES_ID							= "seriesId";
	public static final String	LIKE_SERIES_TITLE								= "title";
	public static final String	LIKE_PROGRAM_PROGRAMID						= "programId";
	public static final String	LIKE_PROGRAM_PROGRAMTYPE						= "programType";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_OTHER				= "OTHER";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_MOVIE				= "MOVIE";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_OTHER_TITLE			= "title";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_OTHER_CATEGORY		= "category";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_TITLE			= "title";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_GENRE			= "genre";
	public static final String	LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_YEAR			= "year";
	public static final String	LIKE_SPORT_TYPE_SPORTTYPEID					= "sportTypeId";
	public static final String	LIKE_SPORT_TYPE_TITLE							= "title";
	
	public static final String 	LIKE_NEXT_BROADCAST 							= "nextBroadcast";
	public static final String 	LIKE_NEXT_BROADCAST_COUNT 						= "broadcastCount";
	public static final String	LIKE_NEXT_BROADCAST_CHANNELID					= "channelId";
	public static final String	LIKE_NEXT_BROADCAST_BEGINTIMEMILLIS			= "beginTimeMillis";

	/* LIKE ENTITY fields */
	public static final String	LIKE_ENTITY_ENTITY_ID						= "entity_id";
	public static final String	LIKE_ENTITY_ENTITY_TYPE						= "entityType";
	public static final String	LIKE_ENTITY_TITLE							= "title";
	public static final String	LIKE_ENTITY_SYNOPSIS_SHORT					= "synopsisShort";
	public static final String	LIKE_ENTITY_SYNOPSIS_LONG					= "synopsisLong";
	public static final String	LIKE_ENTITY_POSTER							= "poster";
	public static final String	LIKE_ENTITY_TAGS							= "tags";
	public static final String	LIKE_ENTITY_CREDITS							= "credits";
	public static final String	LIKE_ENTITY_EPISODE_NUMBER					= "episodeNumber";
	public static final String	LIKE_ENTITY_SEASON							= "season";
	public static final String	LIKE_ENTITY_SERIES							= "series";
	public static final String	LIKE_ENTITY_YEAR							= "year";
	public static final String	LIKE_ENTITY_GENRE							= "genre";

	/* CREDIT fields */
	public static final String	CREDIT_NAME									= "name";
	public static final String	CREDIT_TYPE									= "type";

	/* SERIES fields */
	public static final String	SERIES_NAME									= "name";
	public static final String	SERIES_SERIES_ID							= "seriesId";

	/* SPORT TYPE fields */
	public static final String	SPORTTYPE_SPORTTYPEID						= "sportTypeId";
	public static final String	SPORTTYPE_NAME								= "name";

	/* FEED fields */
	public static final String	FEED_ITEM_TYPE_BROADCAST						= "BROADCAST";
	public static final String	FEED_ITEM_TYPE_RECOMMENDED_BROADCAST			= "RECOMMENDED_BROADCAST";
	public static final String	FEED_ITEM_TYPE_POPULAR_BROADCASTS				= "POPULAR_BROADCASTS";
	public static final String	FEED_ITEM_TYPE_POPULAR_BROADCAST					= "POPULAR_BROADCAST";
	public static final String	FEED_ITEM_TYPE_POPULAR_TWITTER				= "POPULAR_TWITTER";
	public static final String	FEED_ITEM_ITEM_TYPE							= "itemType";
	public static final String	FEED_ITEM_TITLE								= "title";
	public static final String	FEED_ITEM_BROADCAST							= "broadcast";
	public static final String	FEED_ITEM_BROADCASTS							= "broadcasts";
	
	/* JSON KEYS FOR CONFIGURATION */
	public static final String JSON_KEY_CONFIGURATION_FIRST_HOUR_OF_TV_DAY			= "firstHourOfDay";
	public static final String JSON_KEY_CONFIGURATION_ADZERK_NETWORK_ID				= "adzerkNetworkId";
	public static final String JSON_KEY_CONFIGURATION_ADZERK_SITE_ID				= "adzerkSiteId";
	public static final String JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_ENABLED		= "android.googleAnalyticsEnabled";
	public static final String JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_TRACKING_ID	= "android.googleAnalyticsTrackingId";
	public static final String JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_SAMPLE_RATE	= "android.googleAnalyticsSampleRate";
	public static final String JSON_KEY_CONFIGURATION_ADS_ENABLED					= "android.adsEnabled";
	public static final String JSON_KEY_CONFIGURATION_CELLS_BETWEEN_AD_CELLS_BASE	= "android.%s.cellCountBetweenAdCells";
	public static final String JSON_KEY_CONFIGURATION_ADZERK_AD_FORMATS_BASE		= "android.%s.adzerkFormats";
	public static final String JSON_KEY_CONFIGURATION_WELCOME_TOAST					= "android.welcometoast";
	
	/* JSON KEYS FOR API VERSION*/
	public static final String JSON_KEY_API											= "api";
	public static final String JSON_KEY_API_VERSION_NAME							= "name";
	public static final String JSON_KEY_API_VERSION_VALUE 							= "value";
	
	/* This CANNOT be changed since they are used as part of JSON_KEY, should be: guide, activity */
	public static final String JSON_AND_FRAGMENT_KEY_ACTIVITY 						= FeedActivity.class.getName();
	
	/* PREFERENCES KEYS */
	public static final String PREFS_KEY_APP_WAS_PREINSTALLED						= "APP_WAS_PREINSTALLED";
	public static final String PREFS_KEY_APP_WAS_NOT_PREINSTALLED					= "APP_WAS_NOT_PREINSTALLED";
	
	/* GOOGLE ANALYTICS KEYS */
	/* Fields */
	public static final String GA_FIELD_USER_ID										= "&uid";
	
	/* Category keys */
	public static final String GA_EVENT_CATEGORY_KEY_SYSTEM_EVENT					= "SystemEvent";
	public static final String GA_EVENT_CATEGORY_KEY_USER_EVENT						= "UserEvent";
	
	
	/* Action keys */
	public static final String GA_KEY_APP_WAS_PREINSTALLED_SHARED_PREFS				= "APP_WAS_PREINSTALLED_SHARED_PREFS";
	public static final String GA_KEY_APP_WAS_PREINSTALLED_EXTERNAL_STORAGE			= "APP_WAS_PREINSTALLED_EXTERNAL_STORAGE";
	public static final String GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION		= "APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION";
	public static final String GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG			= "APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG";
	public static final String GA_EVENT_KEY_SYSTEM_EVENT_DEVICE_TIME_UNSYNCED		= "DEVICE_LOCAL_TIME_UNSYNCED";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_EMAIL		= "SIGN_UP_COMPLETED_EMAIL";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_SIGN_UP_COMPLETED_FACEBOOK 	= "SIGN_UP_COMPLETED_FACEBOOK";
	public static final String GA_EVENT_KEY_USER_EVENT_TAG_SELECTED					= "TAG_SELECTED";
	public static final String GA_EVENT_KEY_USER_EVENT_HOUR_SELECTED				= "HOUR_SELECTED";
	public static final String GA_EVENT_KEY_USER_EVENT_SEARCH_RESULT_PRESS			= "SEARCH_RESULT_CELL_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_SEARCH						= "SEARCH_QUERY";
	public static final String GA_EVENT_KEY_USER_EVENT_MY_CHANNELS_SEARCH			= "MY_CHANNELS_SEARCH_QUERY";
	public static final String GA_EVENT_KEY_USER_EVENT_MY_CHANNELS_SEARCH_RESULT_PRESS	= "MY_CHANNELS_SEARCH_RESULT_CELL_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_HARDWARE_BUTTON_MENU_PRESS	= "HARDWARE_BUTTON_MENU_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_PROFILE_TOP_VIEW_PRESS	= "USER_PROFILE_PAGE_TOP_VIEW_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_ADD_MORE_CHANNELS_CELL_PRESS	= "ADD_MORE_CHANNELS_CELL_IN_HOME_ACTIVITY_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_SEARCH_KEYBOARD_BUTTON_PRESS	= "SEARCH_KEYBOARD_BUTTON_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_LIKE_IN_LIKES_ACTIVITY_PRESS	= "LIKE_CELL_IN_LIKES_ACTIVITY_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_CHANNEL_IN_MY_CHANNELS_PRESS	= "CHANNEL_CELL_IN_MY_CHANNELS_ACTIVITY_PRESSED";
	public static final String GA_EVENT_KEY_USER_EVENT_DAY_SELECTED					= "DAY_SELECTED";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_SHARE					= "SHARE";
	public static final String GA_EVENT_KEY_USER_EVENT_FEED_ITEM_POPULAR_SINGLE		= "FEED_ITEM_SELECTED_POPULAR_SINGLE";
	public static final String GA_EVENT_KEY_USER_EVENT_FEED_ITEM_POPULAR_MULTIPLE	= "FEED_ITEM_SELECTED_POPULAR_MULTIPLE";
	public static final String GA_EVENT_KEY_USER_EVENT_FEED_ITEM_TWITTER			= "FEED_ITEM_SELECTED_TWITTER";
	public static final String GA_EVENT_KEY_USER_EVENT_FEED_ITEM_RECOMMENDED		= "FEED_ITEM_SELECTED_RECOMMENDED";
	public static final String GA_EVENT_KEY_USER_EVENT_FEED_ITEM_YOU_LIKE			= "FEED_ITEM_SELECTED_YOU_LIKE";
	public static final String GA_EVENT_KEY_USER_EVENT_FEED_LIST_SCROLLED			= "FEED_LIST_SCROLLED_TO_ITEM_AT_INDEX";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_LIKE					= "LIKE";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_REMINDER				= "REMINDER";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_SIGN_IN					= "USER_SIGNED_IN";
	public static final String GA_EVENT_KEY_USER_EVENT_USER_SIGN_OUT				= "USER_SIGNED_OUT";
	public static final String GA_EVENT_KEY_ACTION_FIRST_BOOT						= "OnBoot";

	public static final String GA_KEY_APP_VERSION									= "APP_VERSION";
	public static final String GA_KEY_DEVICE_ID										= "ANDROID_DEVICE_ID";
	public static final String GA_KEY_DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT		= "DEVICE_WITH_PREINSTALLED_APP_FIRST_BOOT";
	
	/* GOOGLE ANALYTICS VALUES */
	public static final String GA_APP_VERSION_NOT_SET								= "ERROR_APP_VERSION_NOT_SET";
	
	/* ADZERK URL */
	public static final String ADS_POST_URL							= HTTP_SCHEME + "engine.adzerk.net/api/v2";
	
	/* JSON KEYS FOR ADZERK ADS */
	public static final String JSON_KEY_ADS_OBJECT_KEY_DECISIONS	= "decisions";
	public static final String JSON_KEY_ADS_OBJECT_KEY_USER 		= "user";
	public static final String JSON_KEY_ADS_SUB_OBJECT_KEY_CONTENTS = "contents";
	public static final String JSON_KEY_ADS_SUB_OBJECT_KEY_DATA	 	= "data";
	
	public static final String JSON_KEY_ADS_DIV_NAME				= "divName";	
	public static final String JSON_KEY_ADS_NETWORK_ID				= "networkId";
	public static final String JSON_KEY_ADS_SITE_ID					= "siteId";
	public static final String JSON_KEY_ADS_AD_TYPES 				= "adTypes";
	public static final String JSON_KEY_ADS_PLACEMENTS 				= "placements";
	public static final String JSON_KEY_ADS_IS_MOBILE 				= "isMobile";
				
	public static final String JSON_KEY_ADS_USER_KEY				= "key";
	public static final String JSON_KEY_ADS_AD_ID 					= "adId";
	public static final String JSON_KEY_ADS_CREATIVE_ID 			= "creativeId";
	public static final String JSON_KEY_ADS_FLIGHT_ID 				= "flightId";
	public static final String JSON_KEY_ADS_CAMPAIGN_ID 			= "campaignId";
	
	public static final String JSON_KEY_ADS_CLICK_URL 				= "clickUrl";
	public static final String JSON_KEY_ADS_IMPRESSION_URL 			= "impressionUrl";
	public static final String JSON_KEY_ADS_IMAGE_URL 				= "imageUrl";
		
	public static final String JSON_KEY_ADS_IMAGE_WIDTH 			= "width";
	public static final String JSON_KEY_ADS_IMAGE_HEIGHT 			= "height";

	/* JSON KEYS FOR SEARCH RESULTS */
	public static final String JSON_KEY_SEARCH_RESULT_RESULTS			= "results";
	public static final String JSON_KEY_SEARCH_RESULT_ITEM_ENTITY		= "entity";
	public static final String JSON_KEY_SEARCH_RESULT_ITEM_DISPLAY_TEXT	= "displayText";
	public static final String JSON_KEY_SEARCH_RESULT_ITEM_ENTITY_TYPE	= "entityType";
	public static final String JSON_KEY_SEARCH_ENTITY_BROADCASTS		= "broadcasts";
	public static final String JSON_KEY_SEARCH_ENTITY_SERIES_NAME		= "name";
	public static final String JSON_KEY_SEARCH_ENTITY_SERIES_ID			= "id";
	
	/* QUERYSTRING KEYS USED IN SEARCH */
	public static final String SEARCH_QUERYSTRING_PARAMETER_QUERY_KEY = "query";
	public static final String SEARCH_WILDCARD = "*";
	
	/* QUERYSTRING KEYS USED IN INTERNAL TRACKING */
	public static final String INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_KEY = "verb";
	public static final String INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VERB_VALUE_VIEWS = "views";
	public static final String INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_KEY = "key";
	public static final String INTERNAL_TRACKING_QUERYSTRING_PARAMETER_VALUE_PROGRAM_ID = "program_id";
	public static final String INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_VALUE = "value";
	public static final String INTERNAL_TRACKING_QUERYSTRING_PARAMETER_KEY_UID = "uid";
	
	
	/* JSON KEYS FOR DESERIALIZATION */
	public static final String JSON_USER_LIKE_SERIES_SERIES_ID = "seriesId";
	public static final String JSON_USER_LIKE_SPORT_TYPE_ID = "sportTypeId";
	public static final String JSON_USER_LIKE_PROGRAM_ID = "programId";
	public static final String JSON_USER_LIKE_PROGRAM_TYPE = "programType";
	public static final String JSON_USER_LIKE_PROGRAM_OTHER_CATEGORY = "category";
	public static final String JSON_USER_LIKE_PROGRAM_MOVIE_GENRE = "genre";
	public static final String JSON_USER_LIKE_PROGRAM_MOVIE_YEAR = "year";
	
	public static final String JSON_USER_FEED_ITEM_BROADCAST = "broadcast";
	public static final String JSON_USER_FEED_ITEM_BROADCASTS = "broadcasts";
	
	public static final String JSON_VERSIONS_KEY_NAME = "api";
	public static final String JSON_VERSIONS_KEY_PROMOTION = "promotion";
	public static final String JSON_VERSIONS_KEY_ANDROID = "android";
	
	public static final String SYSTEM_APP_PATH = "/system/app/";
	
	/* HTTP CORE REQUEST TIMEOUT DEFAULT VALUES */
	public static final int HTTP_CORE_CONNECTION_TIMEOUT_IN_MILISECONDS = 10000;
	public static final int HTTP_CORE_SOCKET_TIMEOUT_IN_MILISECONDS = 10000;
	
	
	/* NETWORK CONNECTIVITY CHECK CONFIGURATIONS */
	public static final String HOST_NAME_FOR_CONNECTIVITY_CHECK = "http://www.google.com";
    public static final int HOST_TIMEOUT_IN_MILISECONDS_FOR_CONNECTIVITY_CHECK = 2000;
    
    /* NETWORK CONNECTIVITY CHECK CONFIGURATIONS */
    public static final String HOST_FOR_NTP_CHECK = "se.pool.ntp.org";
    public static final int HOST_TIMEOUT_IN_MILISECONDS_FOR_NTP_CHECK = 4000;
    
    
    /* USER AUTHORIZATION TOKEN FOR HTTP REQUESTS */
    public static final String USER_AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String USER_AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer";
    
    
    /* LOCALE TOKENS FOR HTTP REQUESTS */
	public static final String HTTP_REQUEST_DATA_LOCALE = "Dazoo-Locale";
	public static final String HTTP_REQUEST_DATA_TIME_ZONE_OFFSET = "timeZoneOffset";
	
	
	/* CONFIGURATIONS FOR THE FACEBOOK AUTHENTICATION */
	public static final String APP_FACEBOOK_ID = "265897563561764";
	public static final String APP_FACEBOOK_PERMISSIONS = "basic_info,email,user_photos,user_location,user_likes";
	public static final int APP_FACEBOOK_SSO = 1000;
	public static final String APP_URL_FACEBOOK_GRAPH = "https://graph.facebook.com";
	public static final String APP_URL_FACEBOOK_ME = "/me";
	public static final String APP_URL_FACEBOOK_GRAPH_ME = APP_URL_FACEBOOK_GRAPH + APP_URL_FACEBOOK_ME;
	
	/* CONFIGURATIONS FOR SEARCH */
	public static final int SEARCH_QUERY_LENGTH_THRESHOLD = 2;
	public static final int DELAY_IN_MILLIS_UNTIL_SEARCH = 400;
	
    /* CONFIGURATIONS FOR NOTIFICATIONS */
	public static final int	NOTIFY_MINUTES_BEFORE_THE_BROADCAST	= 15;
    
    /* Shared preferences for data storage */
	public static final String SHARED_PREFERENCES_NAME = "com.mitv.shared.preferences";
	public static final String SHARED_PREFERENCES_USER_IMAGE_URL = "com.mitv.shared.preferences.user.image.url";
	public static final String SHARED_PREFERENCES_USER_DATA = "com.mitv.shared.preferences.user.data";
	public static final String SHARED_PREFERENCES_APP_WAS_PREINSTALLED = "com.mitv.app.preinstalled";
	public static final String SHARED_PREFERENCES_APP_INSTALLED_VERSION = "com.mitv.app.installed.version";
	public static final String SHARED_PREFERENCES_APP_IS_RESTARTING = "app_is_restarting";
	
	/* Shared preferences for TUTORIAL */
	public static final String SHARED_PREFERENCES_APP_USER_HAS_SEEN_TUTORIAL = "com.mitv.app.tutorial.has.seen";
	public static final String SHARED_PREFERENCES_APP_TUTORIAL_SHOULD_NEVER_START_AGAIN = "com.mitv.app.tutorial.never.start.again";
	public static String SHARED_PREFERENCES_DATE_LAST_OPEN_APP = "com.mitv.app.tutorial.date";
	public static final String SHARED_PREFERENCES_IS_VIEWING_TUTORIAL = "com.mitv.app.is.viewing.tutorial";
	
	/* CONFIGURATIONS FOR DISQUS COMMENTS WEBVIEW */
	public static final String DISQUS_COMMENTS_DOMAIN = HTTP_SCHEME_USED + BACKEND_ENVIRONMENT_USED;
	public static final String DISQUS_COMMENTS_PAGE_URL = DISQUS_COMMENTS_DOMAIN + "/test/index.htm";
	public static final String DISQUS_COMMENTS_PARAMETER_CONTENT_TITLE = "title";
	public static final String DISQUS_COMMENTS_PARAMETER_CONTENT_IDENTIFIER = "identifier";
	public static final String DISQUS_COMMENTS_PARAMETER_CONTENT_LANGUAGE = "language";
	public static final String DISQUS_COMMENTS_PARAMETER_CONTENT_URL = "url";
	public static final String DISQUS_COMMENTS_PARAMETER_USER_ID = "id";
	public static final String DISQUS_COMMENTS_PARAMETER_USER_NAME = "username";
	public static final String DISQUS_COMMENTS_PARAMETER_USER_EMAIL = "email";
	public static final String DISQUS_COMMENTS_PARAMETER_USER_AVATAR_IMAGE = "avatar";
	
	/* CONFIGURATIONS FOR DISQUS COMMENTS API CALLS */
	public static final String DISQUS_API_URL = "https://disqus.com/api";
	public static final String DISQUS_API_VERSION = "3.0";
	public static final String DISQUS_API_THREAD_POSTS = "/threads/listPosts";
	public static final String DISQUS_API_THREAD_DETAILS = "/threads/details";
	public static final String DISQUS_API_REQUESTS_OUTPUT_TYPE = "json";
	public static final String DISQUS_API_FORUM_PARAMETER = "forum";
	public static final String DISQUS_API_LIMIT_PARAMETER = "limit";
	public static final int DISQUS_API_LIMIT_VALUE = 25;
	public static final String DISQUS_API_THREAD_IDENT_PARAMETER = "thread:ident";
	public static final String DISQUS_API_FORUM_SECRET_KEY_PARAMETER = "api_secret";
	public static final String DISQUS_API_FORUM_NAME = "mitvexample";
	public static final String DISQUS_API_FORUM_SECRET_KEY = "VdUJEM6UxNrvz8tvwbgpm2Q4EiupswNYMs57DoxMuTlzKTKonLnMEZEIdG3UoBL8";
	
	/* CONFIGURATIONS FOR FACEBOOK */
	public static final String FACEBOOK_APP_PACKAGE_NAME = "com.facebook.katana";
	public static final int MINIMUM_REQUIRED_FACEBOOK_APP_VERSION_CODE = 9000;
	
	public static final String ALL_CATEGORIES_TAG_ID = "all_categories";
	
	/* Ad mob stuff */
	public static final String AD_UNIT_ID_FEED_ACTIVITY = "ca-app-pub-3190252107510485/6151464254";
	public static final String AD_UNIT_ID_GUIDE_ACTIVITY = "ca-app-pub-3190252107510485/9244531457";
}