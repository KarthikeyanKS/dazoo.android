package com.mitv;

import java.nio.charset.Charset;

/**
 * Class for constant values declaration for the application
 * 
 */
public abstract class Consts {

	public static final String	TESTFLIGHT_TOKEN					= "343b5e95-cc27-4e8e-8a0d-ff5f7a181c5c";
	public static final String	HOCKEY_APP_TOKEN					= "c90b5331b5a7086d88d98021508f2c16";
	public static final String 	API_VERSION							= "1.0.0";

	public static final Charset	UTF8_CHARSET						= Charset.forName("UTF-8");

	public static final String MITV_SESSION							= "com.mitv.session";
	
	/* Shared preferences */
	public static final String SHARED_PREFS_MAIN_NAME				= "com.mitv.shared.prefs";
	public static final String USER_ACCOUNT_ACCESS_TOKEN			= "com.mitv.user.account.access.token";
	public static final String USER_ACCOUNT_USER_ID					= "com.mitv.user.account.user.id";
	public static final String USER_ACCOUNT_FIRST_NAME				= "com.mitv.user.first.name";
	public static final String USER_ACCOUNT_LAST_NAME				= "com.mitv.user.last.name";
	public static final String USER_ACCOUNT_EXISTING_FLAG			= "com.mitv.user.existing.flag";
	public static final String USER_ACCOUNT_EMAIL					= "com.mitv.user.account.email";
	public static final String USER_ACCOUNT_PASSWORD				= "com.mitv.user.account.password";
	public static final String USER_ACCOUNT_AVATAR_URL				= "com.mitv.user.account.avatar.url";
	public static final String USER_ACCOUNT_MY_CHANNELS_IDS_JSON	= "com.mitv.user.account.my.channels.ids.json";
	public static final String TV_GUIDE_HOUR						= "com.mitv.tv.guide.hour";
	public static final String HOMEPAGE_AGAIN						= "com.mitv.homepage.again";
	public static final String API_VERSION_SHARED_PREF				= "com.mitv.api.version";
	public static final String APP_WAS_PREINSTALLED					= "com.mitv.app.preinstalled";
	public static final String APP_WAS_PREINSTALLED_FILE_NAME		= "59b039d2c0c0a7fbe163";

	/* API URLs */
	public static final String HTTP_SCHEME							= "http://";
	public static final String HTTPS_SCHEME							= "https://";
	public static final String JSON_MIME_TYPE 						= "application/json";
	
	public static final String URL_MITV_BASE						= "android.api.mi.tv/";
	public static final String URL_GITR_BASE						= "android.api.gitrgitr.com/";
	public static final String BASE_API_URL_USED 					= URL_GITR_BASE;
	public static final String URL_SERVER_SECURE 					= HTTPS_SCHEME + BASE_API_URL_USED;
	public static final String URL_SERVER							= HTTP_SCHEME + BASE_API_URL_USED;
//	public static String 	   URL_INTERNAL_TRACKING 				= HTTP_SCHEME + "tracking.gitrgitr.com/track/unique?verb=views&key=program_id&value=%s&uid=%s";
	public static String 	   URL_INTERNAL_TRACKING 				= HTTP_SCHEME + "tracking.mi.tv/track/unique?verb=views&key=program_id&value=%s&uid=%s";

	public static final String URL_GUIDE 							= URL_SERVER + "epg/guide";
	public static final String URL_DATES 							= URL_SERVER + "epg/dates";
	public static final String URL_CHANNELS_ALL 					= URL_SERVER + "epg/channels";
	public static final String URL_CHANNELS_DEFAULT 				= URL_SERVER + "epg/channels/default";
	public static final String URL_FACEBOOK_TOKEN 					= URL_SERVER + "auth/login/facebook";
	public static final String URL_LOGIN 							= URL_SERVER + "auth/login/dazoo";
	public static final String URL_REGISTER 						= URL_SERVER + "auth/login/dazoo/register";
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
	public static final String URL_SEARCH		 					= URL_SERVER + "search?query=%s*";
	public static final String URL_API_VERSION						= URL_SERVER + "versions";
	public static final String URL_NOTIFY_BROADCAST_PREFIX 			= URL_SERVER + "epg/channels/";
	public static final String URL_AUTH_TOKENS 					 	= URL_SERVER + "auth/tokens/";
	
	/* Used as meta data in all HTTP requests */
	public static final String HTTP_REQUEST_DATA_LOCALE 			= "lang";
	public static final String HTTP_REQUEST_DATA_TIME_ZONE_OFFSET 	= "timeZoneOffset";

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
	public static final int API_POPULAR_COUNT_DEFAULT				= 3;

	public static enum REQUEST_STATUS {
		LOADING, FAILED, EMPTY_RESPONSE, SUCCESSFUL, BAD_REQUEST
	};
	
	public static String		BACK_STACK											= "com.mitv.back.stack";

	public static final String	ISO_DATE_FORMAT										= "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String	TVDATE_DATE_FORMAT									= "yyyy-MM-dd";

	/* Activity page content block types */
	public static final String	BLOCK_TYPE_PRODUCT_TV								= "com.mitv.content.product.tv";
	public static final String	BLOCK_TYPE_PRODUCT_MOVIE							= "com.mitv.content.product.movie";
	public static final String	BLOCK_TYPE_PRODUCT_SPORT							= "com.mitv.content.product.sport";
	public static final String	BLOCK_TYPE_PRODUCT_KIDS								= "com.mitv.content.product.kids";
	public static final String	BLOCK_TYPE_PRODUCT_RECOMMENDED_LIST					= "com.mitv.content.product.list.recommended";

	/* Broadcast intents */
	public static final String	BROADCAST_SORTING_SELECTED							= "com.broadcast.sorting.selected";
	public static final String	BROADCAST_FORCE_RELOAD								= "com.broadcast.only.downloads";
	public static final String	BROADCAST_HOMEPAGE									= "com.broadcast.homepage";

	/* Section Ids */
	public static final String	SECTION_ID_TVGUIDE									= "secondscreen.section.tvguide";
	public static final String	SECTION_ID_ACTIVITY									= "secondscreen.section.activity";
	public static final String	SECTION_ID_ME										= "secondscreen.section.me";

	/* Data extra intents */
	public static final String	INTENT_EXTRA_SECTION								= "com.mitv.intent.extra.section";
	public static final String	INTENT_EXTRA_GUIDE									= "com.mitv.intent.extra.guide";
	public static final String	INTENT_EXTRA_CHANNEL								= "com.mitv.intent.extra.channel";
	public static final String	INTENT_EXTRA_CHANNEL_PAGE_LINK						= "com.mitv.intent.extra.channel.page.link";
	public static final String	INTENT_EXTRA_CHANNEL_ID								= "com.mitv.intent.extra.channel.id";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE							= "com.mitv.intent.extra.channel.guide";
	public static final String	INTENT_EXTRA_CHANNEL_SORTING						= "com.mitv.intent.extra.channel.sorting";
	public static final String	INTENT_EXTRA_CHANNEL_SORTING_VALUE					= "com.mitv.intent.extra.channel.sorting.value";
	public static final String	INTENT_EXTRA_CHANNEL_CHOSEN_DATE					= "com.mitv.intent.extra.channel.chosen.date";
	public static final String	INTENT_EXTRA_CHANNEL_LOGO_URL						= "com.mitv.intent.extra.channel.logo.url";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING						= "com.mitv.intent.extra.tvguide.sorting";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_VALUE					= "com.mitv.intent.extra.tvguide.sorting.value";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_TYPE					= "com.mitv.intent.extra,tvguide.sorting.type";
	public static final String	INTENT_EXTRA_TVGUIDE_PAGE_URL						= "com.mitv.intent.extra.tvguide.page.url";
	public static final String	INTENT_EXTRA_TVGUIDE_TVDATE							= "com.mitv.intent.extra.tvguide.tvdate";
	public static final String	INTENT_EXTRA_CHOSEN_DATE_TVGUIDE					= "com.mitv.intent.extra.chosen.date.tvguide";
	public static final String	INTENT_EXTRA_BROADCAST_OBJECT						= "com.mitv.intent.extra.broadcast.object";
	public static final String	INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS			= "com.mitv.intent.extra.begintimeinmillis";
	public static final String	INTENT_EXTRA_BROADCAST_URL							= "com.mitv.intent.extra.broadcast.url";
	public static final String	INTENT_EXTRA_NOTIFICATION_TO_SET					= "com.mitv.intent.extra.notification.to.set";
	public static final String	INTENT_EXTRA_NOTIFICATION_ID						= "com.mitv.intent.extra.notification.id";
	public static final String	INTENT_EXTRA_CORE									= "com.mitv.intent.extra.core";
	public static final String	INTENT_EXTRA_GUIDE_AVAILABLE						= "com.mitv.intent.extra.guide.available";
	public static final String	INTENT_EXTRA_GUIDE_AVAILABLE_VALUE					= "com.mitv.intent.extra.guide.available.value";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION			= "com.mitv.intent.extra.tvguide.sorting.value.position";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE				= "com.mitv.intent.extra.channel.guide.available";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE			= "com.mitv.intent.extra.channel.guide.available.value";
	public static final String	INTENT_EXTRA_TAG_GUIDE_AVAILABLE					= "com.mitv.intent.extra.tag.guide.available";
	public static final String	INTENT_EXTRA_TAG_GUIDE_AVAILABLE_VALUE				= "com.mitv.intent.extra.tag.guide.available.value";
	public static final String	INTENT_EXTRA_DATE_DATE								= "com.mitv.intent.extra.date.date";
	public static final String	INTENT_EXTRA_MY_CHANNELS_CHANGED					= "com.mitv.intent.extra.my.channels.changed";
	public static final String	INTENT_EXTRA_LOG_OUT_ACTION							= "com.mitv.intent.extra.log.out.action";
	public static final String	INTENT_EXTRA_LOG_IN_ACTION							= "com.mitv.intent.extra.log.in.action";
	public static final String	INTENT_EXTRA_SIGN_UP_ACTION							= "com.mitv.intent.extra.sign.up.action";
	public static final String	INTENT_EXTRA_UPCOMING_BROADCASTS					= "com.mitv.intent.extra.upcoming.broadcasts";
	public static final String	INTENT_EXTRA_REPEATING_BROADCASTS					= "com.mitv.intent.extra.repeating.broadcasts";
	public static final String	INTENT_EXTRA_REPEATING_PROGRAM						= "com.mitv.intent.extra.repeating.program";
	public static final String	INTENT_EXTRA_RUNNING_BROADCAST						= "com.mitv.intent.extra.repeating.runningBroadcast";
	public static final String	INTENT_EXTRA_CLOCK_SELECTION						= "com.mitv.intent.extra.clock.selection";
	public static final String	INTENT_EXTRA_CLOCK_SELECTION_VALUE					= "com.mitv.intent.extra.clock.selection.value";
	public static final String	INTENT_EXTRA_TV_GUIDE_HOUR							= "com.mitv.intent.extra.tvguide.hour";
	public static final String	INTENT_EXTRA_BAD_REQUEST							= "com.mitv.intent.extra.bad.request";

	/* Alarm extras */
	public static final String	INTENT_NOTIFICATION									= "NOTIFICATION"; //WARNING do NOT change this without changing in the Android Manifest> <action android:name="NOTIFICATION" />
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS		= "com.mitv.intent.alarm.extra.broadcast.begintimemillis";
	public static final String	INTENT_ALARM_EXTRA_CHANNELID						= "com.mitv.intent.alarm.extra.channelid";
	public static final String	INTENT_ALARM_EXTRA_NOTIFICIATION_ID					= "com.mitv.intent.alarm.extra.notification.id";
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_NAME					= "com.mitv.intent.alarm.extra.broadcast.name";
	public static final String	INTENT_ALARM_EXTRA_CHANNEL_NAME						= "com.mitv.intent.alarm.extra.channel.name";
	public static final String	INTENT_ALARM_EXTRA_CHANNEL_LOGO_URL					= "com.mitv.intent.alarm.extra.channel.logo.url";
	public static final String	INTENT_ALARM_EXTRA_DATE_DATE						= "com.mitv.intent.alarm.extra.date.date";
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_TIME					= "com.mitv.intent.alarm.extra.broadcast.time";

	public static final String	INTENT_EXTRA_FROM_NOTIFICATION						= "com.mitv.intent.extra.from.notification";

	/* Activity feed extras */
	public static final String	INTENT_EXTRA_FROM_ACTIVITY							= "com.mitv.intent.extra.from.activity";
	public static final String	INTENT_EXTRA_ACTIVITY_CARD_NUMBER					= "com.mitv.intent.extra.activity.card.number";

	/* Profile extras */
	public static final String	INTENT_EXTRA_FROM_PROFILE							= "com.mitv.intent.extra.from.profile";

	/* Fragments extra arguments */
	public static final String	FRAGMENT_EXTRA_TAG									= "com.mitv.fragment.extra.tag";
	public static final String	FRAGMENT_EXTRA_TVDATE								= "com.mitv.fragment.extra.tvdate";
	public static final String	FRAGMENT_EXTRA_TVDATE_POSITION						= "com.mitv.fragment.extra.tvdate.position";

	/* Custom data types */
	public static final String	VALUE_TYPE_PROGRAMTYPE								= "com.mitv.value.type.programtype";
	public static final String	VALUE_TYPE_TVDATE									= "com.mitv.value.type.tvdate";
	public static final String	VALUE_TYPE_TAG										= "com.mitv.value.type.tag";

	/* TVGuide */
	public static final int		TV_GUIDE_NEXT_PROGRAMS_NUMBER						= 3;
	public static final String	TV_GUIDE_PAGE_NUMBER								= "com.mitv.tvguide.page.number";

	public static final String	IMAGE_MACHINE_SECURITY_KEY							= "24567hright";

	/* Parcelable bundles */
	public static final String	PARCELABLE_CHANNELS_LIST							= "com.parcelable.channels.list";
	public static final String	PARCELABLE_TV_DATES_LIST							= "com.parcelable.dates.list";
	public static final String	PARCELABLE_PROGRAM_TYPES_LIST						= "com.parcelable.categories.list";
	public static final String	PARCELABLE_TAGS_LIST								= "com.parcelable.tags.list";

	/* Activity's requests to update calling fragment */
	public static final int		INFO_UPDATE_REMINDERS								= 1;
	public static final int		INFO_UPDATE_LIKES									= 11;
	public static final int		INFO_NO_UPDATE_REMINDERS							= 0;
	public static final int		INFO_NO_UPDATE_LIKES								= 10;
	public static final int		INFO_UPDATE_LOGOUT									= 111;
	public static final int		INFO_UPDATE_MYCHANNELS								= 1111;
	public static final String	INFO_UPDATE_LIKES_NUMBER							= "com.mitv.info.update.likes.number";
	public static final String	INFO_UPDATE_REMINDERS_NUMBER						= "com.mitv.info.update.reminders.number";
	public static final String	INFO_UPDATE_MYCHANNELS_NUMBER						= "com.mitv.info.update.mychannels.number";

	/* Response codes */
	public static final int		GOOD_RESPONSE										= 200;
	public static final int		GOOD_RESPONSE_RESET_PASSWORD						= 204;
	public static final int		GOOD_RESPONSE_CHANNELS_ARE_ADDED					= 204;
	public static final int		GOOD_RESPONSE_LIKE_IS_DELETED						= 204;

	public static final int		BAD_RESPONSE										= 400;
	public static final int		BAD_RESPONSE_MISSING_TOKEN							= 401;
	public static final int		BAD_RESPONSE_INVALID_TOKEN							= 403;
	public static final int		BAD_RESPONSE_PROGRAM_SERIES_NOT_FOUND				= 400;

	public static final int		BAD_RESPONSE_TIMEOUT								= 500;
	
	/* Response strings*/
	public static final String 	BAD_RESPONSE_STRING_EMAIL_ALREADY_TAKEN				= "Email already taken";
	public static final String 	BAD_RESPONSE_STRING_NOT_REAL_EMAIL					= "Not a real email";
	public static final String 	BAD_RESPONSE_STRING_PASSWORD_TOO_SHORT				= "Password not secure";
	public static final String 	BAD_RESPONSE_STRING_FIRSTNAME_NOT_SUPPLIED			= "First name not supplied";
	public static final String 	BAD_RESPONSE_STRING_EMAIL_NOT_FOUND					= "Email not found";

	/* Notifications */
	public static final int		NOTIFY_MINUTES_BEFORE_THE_BROADCAST					= -15;
	public static final String	NOTIFY_NUMBER										= "com.mitv.notify.number";
	public static final String	NOTIFY_BROADCAST_URL_MIDDLE							= "/broadcasts/";

	/* Notifications database */
	public static final String	NOTIFICATION_DATABASE_NAME							= "notifications.db";
	public static final int		NOTIFICATION_DATABASE_VERSION						= 1;
	public static final String	NOTIFICATION_DB_TABLE_NOTIFICATIONS					= "notifications";
	public static final String	NOTIFICATION_DB_COLUMN_NOTIFICATION_ID				= "notification_id";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_URL				= "broadcast_url";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_ID					= "program_id";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_TITLE				= "program_title";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_TYPE					= "program_type";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_SEASON				= "program_season";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_EPISODE				= "program_episode";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_YEAR					= "program_year";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_TAG					= "program_tag";
	public static final String	NOTIFICATION_DB_COLUMN_PROGRAM_GENRE				= "program_genre";
	public static final String 	NOTIFICATION_DB_COLUMN_PROGRAM_CATEGORY				= "program_category";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_ID					= "channel_id";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_NAME					= "channel_name";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_LOGO_URL				= "channel_logo_url";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIME			= "broadcast_begintime";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS	= "begin_time_millis";

	// =========================== CONTENT ================================

	/* PROGRAM fields */
	public static final String	PROGRAM_ID									= "programId";
	public static final String	PROGRAM_TYPE									= "programType";
	public static final String	PROGRAM_TITLE									= "title";
	public static final String	PROGRAM_SYNOPSIS_SHORT						= "synopsisShort";
	public static final String	PROGRAM_SYNOPSISS_LONG						= "synopsisLong";
	// public static final String PROGRAM_POSTER = "poster";
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

	public static final String	BROADCAST_TYPE_RERUN							= "RERUN";
	public static final String	BROADCAST_TYPE_LIVE							= "LIVE";
	public static final String	BROADCAST_TYPE_OTHER							= "OTHER";
	public static final String	BROADCAST_TYPE_RECORDED						= "RECORDED";

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
	public static final String JSON_AND_FRAGMENT_KEY_GUIDE 							= "guide";
	public static final String JSON_AND_FRAGMENT_KEY_ACTIVITY 						= "activity";
	
	/* PREFERENCES KEYS */
	public static final String PREFS_KEY_APP_WAS_PREINSTALLED						= "APP_WAS_PREINSTALLED";
	public static final String PREFS_KEY_APP_WAS_NOT_PREINSTALLED					= "APP_WAS_NOT_PREINSTALLED";
	
	/* GOOGLE ANALYTICS KEYS */
	public static final String GA_KEY_APP_WAS_PREINSTALLED_SHARED_PREFS				= "APP_WAS_PREINSTALLED_SHARED_PREFS";
	public static final String GA_KEY_APP_WAS_PREINSTALLED_EXTERNAL_STORAGE			= "APP_WAS_PREINSTALLED_EXTERNAL_STORAGE";
	public static final String GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION		= "APP_WAS_PREINSTALLED_SYSTEM_APP_LOCATION";
	public static final String GA_KEY_APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG			= "APP_WAS_PREINSTALLED_SYSTEM_APP_FLAG";
	public static final String GA_EVENT_KEY_SYSTEM_EVENT							= "SystemEvent";

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
	public static final String JSON_KEY_SEARCH_RESULT_SUGGESTION		= "suggestion";
	public static final String JSON_KEY_SEARCH_RESULT_NUMBER_OF_RESULTS	= "numberOfResults";
	public static final String JSON_KEY_SEARCH_RESULT_RESULTS			= "results";
	public static final String JSON_KEY_SEARCH_RESULT_ITEM_ENTITY		= "entity";
	public static final String JSON_KEY_SEARCH_RESULT_ITEM_DISPLAY_TEXT	= "displayText";
	public static final String JSON_KEY_SEARCH_RESULT_ITEM_ENTITY_TYPE	= "entityType";
	public static final String JSON_KEY_SEARCH_ENTITY_BROADCASTS		= "broadcasts";
	public static final String JSON_KEY_SEARCH_ENTITY_NAME				= "name";
	
	
	/* JSON KEYS FOR DESERIALIZATION */
	public static final String JSON_USER_LIKE_SERIES_SERIES_ID = "seriesId";
	public static final String JSON_USER_LIKE_SPORT_TYPE_ID = "sportTypeId";
	public static final String JSON_USER_LIKE_PROGRAM_ID = "programId";
	public static final String JSON_USER_LIKE_PROGRAM_TYPE = "programType";
	public static final String JSON_USER_LIKE_PROGRAM_OTHER_CATEGORY = "category";
	public static final String JSON_USER_LIKE_PROGRAM_MOVIE_GENRE = "genre";
	public static final String JSON_USER_LIKE_PROGRAM_MOVIE_YEAR = "year";
	
	
	/* REFRESH INTERVALS FOR LOCAL DATA */
	public static final int LIKE_IDS_REFRESH_INTERVAL_IN_MINUTES 		= 1;
	public static final int CHANNEL_IDS_REFRESH_INTERVAL_IN_MINUTES 	= 1;
	
	/* HTTP CORE REQUEST TIMEOUT DEFAULT VALUES */
	public static final int HTTP_CORE_CONNECTION_TIMEOUT_IN_MILISECONDS = 2000;
	public static final int HTTP_CORE_SOCKET_TIMEOUT_IN_MILISECONDS = 2000;
	
	/* NETWORK CONNECTIVITY CHECK CONFIGURATIONS */
	public static final String HOST_NAME_FOR_CONNECTIVITY_CHECK = "http://www.google.com";
    public static final int HOST_TIMEOUT_IN_MILISECONDS_FOR_CONNECTIVITY_CHECK = 2000;
    
    /* USER AUTHORIZATION TOKEN */
    public static final String USER_AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String USER_AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer";
}