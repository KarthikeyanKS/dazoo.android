package com.millicom.secondscreen;

/**
 * Class for constant values declaration for the application
 * 
 */
public abstract class Consts {

	public static final String	TESTFLIGHT_TOKEN											= "343b5e95-cc27-4e8e-8a0d-ff5f7a181c5c";
	public static final String	HOCKEY_APP_TOKEN											= "c90b5331b5a7086d88d98021508f2c16";

	// Shared preferences
	public static final String	SHARED_PREFS_MAIN_NAME										= "com.millicom.secondscreen.shared.prefs";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_ACCESS_TOKEN				= "com.millicom.secondscreen.user.account.access.token";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_USER_ID					= "com.millicom.secondscreen.user.account.user.id";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_FIRST_NAME				= "com.millicom.secondscreen.user.first.name";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_LAST_NAME				= "com.millicom.secondscreen.user.last.name";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_EXISTING_FLAG			= "com.millicom.secondscreen.user.existing.flag";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_EMAIL					= "com.millicom.secondscreen.user.account.email";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_PASSWORD					= "com.millicom.secondscreen.user.account.password";
	public static final String	MILLICOM_SECONDSCREEN_USER_ACCOUNT_MY_CHANNELS_IDS_JSON		= "com.millicom.secondscreen.user.account.my.channels.ids.json";

	// api urls
	public static final String	MILLICOM_SECONDSCREEN_GUIDE_PAGE_URL						= "http://www.gitrgitr.com/api/epg/guide";
	public static final String	MILLICOM_SECONDSCREEN_DATES_PAGE_URL						= "http://www.gitrgitr.com/api/epg/dates";
	public static final String	MILLICOM_SECONDSCREEN_CHANNELS_ALL_PAGE_URL					= "http://www.gitrgitr.com/api/epg/channels";
	public static final String	MILLICOM_SECONDSCREEN_CHANNELS_DEFAULT_PAGE_URL				= "http://www.gitrgitr.com/api/epg/channels/default";
	public static final String	MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL					= "http://www.gitrgitr.com/api/auth/login/facebook";
	public static final String	MILLICOM_SECONDSCREEN_DAZOO_LOGIN_URL						= "http://www.gitrgitr.com/api/auth/login/dazoo";
	public static final String	MILLICOM_SECONDSCREEN_DAZOO_REGISTER_URL					= "http://www.gitrgitr.com/api/auth/login/dazoo/register";
	public static final String	MILLICOM_SECONDSCREEN_RESET_PASSWORD_URL					= "http://www.gitrgitr.com/api/auth/login/dazoo/sendResetPasswordEmail";
	public static final String	MILLICOM_SECONDSCREEN_TAGS_PAGE_URL							= "http://www.gitrgitr.com/api/epg/tags/visible";
	public static final String	MILLICOM_SECONDSCREEN_MY_CHANNELS_URL						= "http://www.gitrgitr.com/api/my/channels";
	public static final String	MILLICOM_SECONDSCREEN_LIKES_URL								= "http://www.gitrgitr.com/api/my/likes";
	public static final String	MILLICOM_SECONDSCREEN_ACTIVITY_FEED_URL						= "http://www.gitrgitr.com/api/my/feed";
	public static final String	MILLICOM_SECONDSCREEN_PROGRAMS								= "http://www.gitrgitr.com/api/epg/programs/";
	public static final String	MILLICOM_SECONDSCREEN_SERIES								= "http://www.gitrgitr.com/api/epg/series/";
	public static final String	MILLICOM_SECONDSCREEN_POPULAR								= "http://api.gitrgitr.com/epg/broadcasts/popular/";

	public static final String	MILLICON_SECONDSCREEN_HTTP_SCHEME							= "https";

	// api request strings
	public static final String	MILLICOM_SECONDSCREEN_API_CHANNEL_ID_WITH_EQUALS_SIGN		= "channelId=";
	public static final String	MILLICOM_SECONDSCREEN_API_CHANNEL_ID						= "channelId";
	public static final String	MILLICOM_SECONDSCREEN_API_FACEBOOK_TOKEN					= "facebookToken";
	public static final String	MILLICOM_SECONDSCREEN_API_TOKEN								= "token";
	public static final String	MILLICOM_SECONDSCREEN_API_USER_ID							= "userId";
	public static final String	MILLICOM_SECONDSCREEN_API_EMAIL								= "email";
	public static final String	MILLICOM_SECONDSCREEN_API_PASSWORD							= "password";
	public static final String	MILLICOM_SECONDSCREEN_API_FIRSTNAME							= "firstName";
	public static final String	MILLICOM_SECONDSCREEN_API_LASTNAME							= "lastName";
	public static final String	MILLICOM_SECONDSCREEN_API_CREATED							= "created";
	public static final String	MILLICOM_SECONDSCREEN_API_USER								= "user";
	public static final String	REQUEST_QUERY_AND											= "&";
	public static final String	REQUEST_QUERY_SEPARATOR										= "/";
	public static final String	REQUEST_PARAMETER_SEPARATOR									= "?";
	public static final String	MILLICOM_SECONDSCREEN_API_ENTITY_ID							= "entityId";
	public static final String	MILLICOM_SECONDSCREEN_API_LIKETYPE							= "likeType";
	public static final String	MILLICOM_SECONDSCREEN_API_UPCOMING							= "upcoming";
	public static final String	MILLICOM_SECONDSCREEN_API_BROADCASTS						= "/broadcasts";
	public static final String	MILLICOM_SECONDSCREEN_API_UPCOMING_BROADCASTS				= "/broadcasts/upcoming";
	public static final String	MILLICOM_SECONDSCREEN_API_POPULAR_COUNT						= "count";
	public static final String	MILLICOM_SECONDSCREEN_API_SKIP								= "skip";
	public static final String	MILLICOM_SECONDSCREEN_API_LIMIT								= "limit";

	public static final String	EMPTY_STRING												= "";
	public static final String	ERROR_STRING												= "error";

	// restrictions
	public static final int		MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_ITEMS_PER_CHANNEL	= 3;
	public static final int		MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_CHANNELS_PER_PAGE	= 10;
	public static final int		MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN					= 6;
	public static final int		MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX					= 20;
	public static final int		MILLICOM_SECONDSCREEN_API_POPULAR_COUNT_DEFAULT				= 3;

	public static enum REQUEST_STATUS {
		LOADING, FAILED, EMPTY_RESPONSE, SUCCESSFUL
	};

	public static String		DAZOO_BACK_STACK									= "com.millicom.secondscreen.dazoo.back.stack";

	public static final String	ISO_DATE_FORMAT										= "yyyy-MM-dd'T'HH:mm:ss'Z'";

	// Activity page content block types
	public static final String	BLOCK_TYPE_PRODUCT_TV								= "com.millicom.secondscreen.content.product.tv";
	public static final String	BLOCK_TYPE_PRODUCT_MOVIE							= "com.millicom.secondscreen.content.product.movie";
	public static final String	BLOCK_TYPE_PRODUCT_SPORT							= "com.millicom.secondscreen.content.product.sport";
	public static final String	BLOCK_TYPE_PRODUCT_KIDS								= "com.millicom.secondscreen.content.product.kids";
	public static final String	BLOCK_TYPE_PRODUCT_RECOMMENDED_LIST					= "com.millicom.secondscreen.content.product.list.recommended";

	// Broadcast intents
	public static final String	BROADCAST_SORTING_SELECTED							= "com.broadcast.sorting.selected";
	public static final String	BROADCAST_FORCE_RELOAD								= "com.broadcast.only.downloads";
	public static final String	BROADCAST_HOMEPAGE									= "com.broadcast.homepage";

	// Section Ids
	public static final String	SECTION_ID_TVGUIDE									= "secondscreen.section.tvguide";
	public static final String	SECTION_ID_ACTIVITY									= "secondscreen.section.activity";
	public static final String	SECTION_ID_ME										= "secondscreen.section.me";

	// data extra intents
	public static final String	INTENT_EXTRA_SECTION								= "com.millicom.secondscreen.intent.extra.section";
	public static final String	INTENT_EXTRA_GUIDE									= "com.millicom.secondscreen.intent.extra.guide";
	public static final String	INTENT_EXTRA_CHANNEL								= "com.millicom.secondscreen.intent.extra.channel";
	public static final String	INTENT_EXTRA_CHANNEL_PAGE_LINK						= "com.millicom.secondscreen.intent.extra.channel.page.link";
	public static final String	INTENT_EXTRA_CHANNEL_ID								= "com.millicom.secondscreen.intent.extra.channel.id";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE							= "com.millicom.secondscreen.intent.extra.channel.guide";
	public static final String	INTENT_EXTRA_CHANNEL_SORTING						= "com.millicom.secondscreen.intent.extra.channel.sorting";
	public static final String	INTENT_EXTRA_CHANNEL_SORTING_VALUE					= "com.millicom.secondscreen.intent.extra.channel.sorting";
	public static final String	INTENT_EXTRA_CHANNEL_CHOSEN_DATE					= "com.millicom.secondscreen.intent.extra.channel.chosen.date";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING						= "com.millicom.secondscreen.intent.extra.tvguide.sorting";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_VALUE					= "com.millicom.secondscreen.intent.extra.tvguide.sorting.value";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_TYPE					= "com.millicom.secondscreen.intent.extra,tvguide.sorting.type";
	public static final String	INTENT_EXTRA_TVGUIDE_PAGE_URL						= "com.millicom.secondscreen.intent.extra.tvguide.page.url";
	public static final String	INTENT_EXTRA_TVGUIDE_TVDATE							= "com.millicom.secondscreen.intent.extra.tvguide.tvdate";
	public static final String	INTENT_EXTRA_CHOSEN_DATE_TVGUIDE					= "com.millicom.secondscreen.intent.extra.chosen.date.tvguide";
	public static final String	INTENT_EXTRA_BROADCAST								= "com.millicom.secondscreen.intent.extra.broadcast";
	public static final String	INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS			= "com.millicom.secondscreen.intent.extra.begintimeinmillis";
	public static final String	INTENT_EXTRA_BROADCAST_URL							= "com.millicom.secondscren.intent,extra.broadcast.url";
	public static final String	INTENT_EXTRA_NOTIFICATION_TO_SET					= "com.millicom.secondscreen.intent.extra.notification.to.set";
	public static final String	INTENT_EXTRA_NOTIFICATION_ID						= "com.millicom.secondscreen.intent.extra.notification.id";
	public static final String	INTENT_EXTRA_DAZOO_CORE								= "com.millicom.secondscreen.intent.extra.dazoo.core";
	public static final String	INTENT_EXTRA_GUIDE_AVAILABLE						= "com.millicom.secondscreen.intent.extra.guide.available";
	public static final String	INTENT_EXTRA_GUIDE_AVAILABLE_VALUE					= "com.millicom.secondscreen.intent.extra.guide.available.value";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION			= "com.millicom.secondscreen.intent.extra.tvguide.sorting.value.position";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE				= "com.millicom.secondscreen.intent.extra.channel.guide.available";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE			= "com.millicom.secondscreen.intent.extra.channel.guide.available.value";
	public static final String	INTENT_EXTRA_TAG_GUIDE_AVAILABLE					= "com.millicom.secondscreen.intent.extra.tag.guide.available";
	public static final String	INTENT_EXTRA_TAG_GUIDE_AVAILABLE_VALUE				= "com.millicom.secondscreen.intent.extra.tag.guide.available.value";
	public static final String	INTENT_EXTRA_DATE_DATE								= "com.millicom.secondscreen.intent.extra.date.date";
	public static final String	INTENT_EXTRA_MY_CHANNELS_CHANGED					= "com.millicom.secondscreen.intent.extra.my.channels.changed";
	public static final String	INTENT_EXTRA_LOG_OUT_ACTION							= "com.millicom.secondscreen.intent.extra.log.out.action";
	public static final String	INTENT_EXTRA_LOG_IN_ACTION							= "com.millicom.secondscreen.intent.extra.log.in.action";
	public static final String	INTENT_EXTRA_DATA_FETCHING							= "com.millicom.secondscreen.intent.extra.log.in.action";
	public static final String	INTENT_EXTRA_UPCOMING_BROADCASTS					= "com.millicom.secondscreen.intent.extra.upcoming.broadcasts";
	public static final String	INTENT_EXTRA_REPEATING_BROADCASTS					= "com.millicom.secondscreen.intent.extra.repeating.broadcasts";
	public static final String	INTENT_EXTRA_CLOCK_SELECTION						= "com.millicom.secondscreen.intent.extra.clock.selection";
	public static final String	INTENT_EXTRA_CLOCK_SELECTION_VALUE					= "com.millicom.secondscreen.intent.extra.clock.selection.value";

	// alarm extras
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS		= "com.millicom.secondscreen.intent.alarm.extra.broadcast.begintimemillis";
	public static final String	INTENT_ALARM_EXTRA_CHANNELID						= "com.millicom.secondscreen.intent.alarm.extra.channelid";
	public static final String	INTENT_ALARM_EXTRA_NOTIFICIATION_ID					= "com.millicom.secondscreen.intent.alarm.extra.notification.id";
	public static final String	INTENT_ALARM_EXTRA_BROADCAST_NAME					= "com.millicom.secondscreen.intent.alarm.extra.broadcast.name";
	public static final String	INTENT_ALARM_EXTRA_CHANNEL_NAME						= "com.millicom.secondscreen.intent.alarm.extra.channel.name";
	public static final String	INTENT_ALARM_EXTRA_DATE_DATE						= "com.millicom.secondscreen.intent.alarm.extra.date.date";

	public static final String	INTENT_EXTRA_FROM_NOTIFICATION						= "com.millicom.secondscreen.intent.extra.from.notification";

	// activity feed extras
	public static final String	INTENT_EXTRA_FROM_ACTIVITY							= "com.millicom.secondscreen.intent.extra.from.activity";

	// fragments extra arguments
	public static final String	FRAGMENT_EXTRA_TAG									= "com.millicom.secondscreen.fragment.extra.tag";
	public static final String	FRAGMENT_EXTRA_TVDATE								= "com.millicom.secondscreen.fragment.extra.tvdate";
	public static final String	FRAGMENT_EXTRA_TVDATE_POSITION						= "com.millicom.secondscreen.fragment.extra.tvdate.position";

	// custom data types
	public static final String	VALUE_TYPE_PROGRAMTYPE								= "com.millicom.secondscreen.value.type.programtype";
	public static final String	VALUE_TYPE_TVDATE									= "com.millicom.secondscreen.value.type.tvdate";
	public static final String	VALUE_TYPE_TAG										= "com.millicom.secondscreen.value.type.tag";

	// TVGuide
	public static final int		TV_GUIDE_NEXT_PROGRAMS_NUMBER						= 3;
	public static final String	TV_GUIDE_PAGE_NUMBER								= "com.millicom.secondscreen.tvguide.page.number";

	public static final String	IMAGE_MACHINE_SECURITY_KEY							= "24567hright";

	// Parcelable bundles
	public static final String	PARCELABLE_CHANNELS_LIST							= "com.parcelable.channels.list";
	public static final String	PARCELABLE_TV_DATES_LIST							= "com.parcelable.dates.list";
	public static final String	PARCELABLE_PROGRAM_TYPES_LIST						= "com.parcelable.categories.list";
	public static final String	PARCELABLE_TAGS_LIST								= "com.parcelable.tags.list";

	// activity's requests to update calling fragment
	public static final int		INFO_UPDATE_REMINDERS								= 1;
	public static final int		INFO_UPDATE_LIKES									= 11;
	public static final int		INFO_NO_UPDATE_REMINDERS							= 0;
	public static final int		INFO_NO_UPDATE_LIKES								= 10;
	public static final int		INFO_UPDATE_LOGOUT									= 111;
	public static final int		INFO_UPDATE_MYCHANNELS								= 1111;
	public static final String	INFO_UPDATE_LIKES_NUMBER							= "com.millicom.secondscreen.info.update.likes.number";
	public static final String	INFO_UPDATE_REMINDERS_NUMBER						= "com.millicom.secondscreen.info.update.reminders.number";
	public static final String	INFO_UPDATE_MYCHANNELS_NUMBER						= "com.millicom.secondscreen.info.update.mychannels.number";

	// Response codes
	public static final int		GOOD_RESPONSE										= 200;
	public static final int		GOOD_RESPONSE_RESET_PASSWORD						= 204;
	public static final int		GOOD_RESPONSE_CHANNELS_ARE_ADDED					= 204;
	public static final int		GOOD_RESPONSE_LIKE_IS_DELETED						= 204;

	public static final int		BAD_RESPONSE										= 400;
	public static final int		BAD_RESPONSE_MISSING_TOKEN							= 401;
	public static final int		BAD_RESPONSE_INVALID_TOKEN							= 403;
	public static final int		BAD_RESPONSE_PROGRAM_SERIES_NOT_FOUND				= 400;

	// notifications
	public static final int		NOTIFY_MINUTES_BEFORE_THE_BROADCAST					= -15;
	public static final String	NOTIFY_NUMBER										= "com.millicom.secondscreen.notify.number";
	public static final String	NOTIFY_BROADCAST_URL_PREFIX							= "http://www.gitrgitr.com/api/epg/channels/";
	public static final String	NOTIFY_BROADCAST_URL_MIDDLE							= "/broadcasts/";

	// notifications database
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
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_ID					= "channel_id";
	public static final String	NOTIFICATION_DB_COLUMN_CHANNEL_NAME					= "channel_name";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIME			= "broadcast_begintime";
	public static final String	NOTIFICATION_DB_COLUMN_BROADCAST_BEGINTIMEMILLIS	= "begin_time_millis";

	// =========================== CONTENT ================================

	// PROGRAM fields:
	public static final String	DAZOO_PROGRAM_ID									= "programId";
	public static final String	DAZOO_PROGRAM_TYPE									= "programType";
	public static final String	DAZOO_PROGRAM_TITLE									= "title";
	public static final String	DAZOO_PROGRAM_SYNOPSIS_SHORT						= "synopsisShort";
	public static final String	DAZOO_PROGRAM_SYNOPSISS_LONG						= "synopsisLong";
	public static final String	DAZOO_PROGRAM_POSTER								= "poster";
	public static final String	DAZOO_PROGRAM_TAGS									= "tags";
	public static final String	DAZOO_PROGRAM_CREDITS								= "credits";
	public static final String	DAZOO_PROGRAM_EPISODE								= "episodeNumber";
	public static final String	DAZOO_PROGRAM_SEASON								= "season";
	public static final String	DAZOO_PROGRAM_SERIES								= "series";
	public static final String	DAZOO_PROGRAM_YEAR									= "year";
	public static final String	DAZOO_PROGRAM_GENRE									= "genre";
	public static final String	DAZOO_PROGRAM_SPORTTYPE								= "sportType";
	public static final String	DAZOO_PROGRAM_TOURNAMENT							= "tournament";
	public static final String	DAZOO_PROGRAM_CATEGORY								= "category";
	public static final String	DAZOO_PROGRAM_TYPE_TV_EPISODE						= "TV_EPISODE";
	public static final String	DAZOO_PROGRAM_TYPE_MOVIE							= "MOVIE";
	public static final String	DAZOO_PROGRAM_TYPE_OTHER							= "OTHER";
	public static final String	DAZOO_PROGRAM_TYPE_SPORT							= "SPORT";
	public static final String	DAZOO_PROGRAM_TYPE_SPORTTYPE						= "sportType";

	public static final String	DAZOO_LIKE_ENTITY_TYPE_PROGRAM						= "PROGRAM";
	public static final String	DAZOO_LIKE_ENTITY_TYPE_SERIES						= "SERIES";

	// BROADCAST fields
	public static final String	DAZOO_BROADCAST_PROGRAM								= "program";
	public static final String	DAZOO_BROADCAST_CHANNEL								= "channel";
	public static final String	DAZOO_BROADCAST_BROADCAST_TYPE						= "broadcastType";
	public static final String	DAZOO_BROADCAST_BEGIN_TIME							= "beginTime";
	public static final String	DAZOO_BROADCAST_END_TIME							= "endTime";
	public static final String	DAZOO_BROADCAST_BEGIN_TIME_MILLIS					= "beginTimeMillis";
	public static final String	DAZOO_BROADCAST_SHARE_URL							= "shareUrl";

	public static final String	DAZOO_BROADCAST_TYPE_RERUN							= "RERUN";
	public static final String	DAZOO_BROADCAST_TYPE_LIVE							= "LIVE";
	public static final String	DAZOO_BROADCAST_TYPE_OTHER							= "OTHER";
	public static final String	DAZOO_BROADCAST_TYPE_RECORDED						= "RECORDED";

	// CHANNEL fields
	public static final String	DAZOO_CHANNEL_CHANNEL_ID							= "channelId";
	public static final String	DAZOO_CHANNEL_NAME									= "name";
	public static final String	DAZOO_CHANNEL_LOGO									= "logo";

	// SEASON fields
	public static final String	DAZOO_SEASON_NUMBER									= "number";

	// IMAGE fields
	public static final String	DAZOO_IMAGE_SMALL									= "small";
	public static final String	DAZOO_IMAGE_MEDIUM									= "medium";
	public static final String	DAZOO_IMAGE_LARGE									= "large";

	// TAG fields
	public static final String	DAZOO_TAG_ID										= "_id";
	public static final String	DAZOO_TAG_NAME										= "name";
	public static final String	DAZOO_TAG_ALIAS										= "alias";

	// DATE fields
	public static final String	DAZOO_DATE_ID										= "_id";
	public static final String	DAZOO_DATE_NAME										= "name";
	public static final String	DAZOO_DATE_ALIAS									= "alias";
	public static final String	DAZOO_DATE_DATE										= "date";

	// GUIDE fields
	public static final String	DAZOO_GUIDE_CHANNEL_ID								= "channelId";
	public static final String	DAZOO_GUIDE_CHANNEL_NAME							= "name";
	public static final String	DAZOO_GUIDE_LOGO									= "logo";
	public static final String	DAZOO_GUIDE_BROADCASTS								= "broadcasts";

	// DAZOO LIKE fields
	public static final String	DAZOO_LIKE_ENTITYID									= "entityId";
	public static final String	DAZOO_LIKE_LIKETYPE									= "likeType";
	public static final String	DAZOO_LIKE_TYPE_SERIES								= "SERIES";
	public static final String	DAZOO_LIKE_TYPE_PROGRAM								= "PROGRAM";
	public static final String	DAZOO_LIKE_TYPE_SPORT_TYPE							= "SPORT_TYPE";
	public static final String	DAZOO_LIKE_SERIES_SERIES_ID							= "seriesId";
	public static final String	DAZOO_LIKE_SERIES_TITLE								= "title";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAMID						= "programId";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAMTYPE						= "programType";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER				= "OTHER";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE				= "MOVIE";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER_TITLE			= "title";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER_CATEGORY		= "category";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_TITLE			= "title";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_GENRE			= "genre";
	public static final String	DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_YEAR			= "year";
	public static final String	DAZOO_LIKE_SPORT_TYPE_SPORTTYPEID					= "sportTypeId";
	public static final String	DAZOO_LIKE_SPORT_TYPE_TITLE							= "title";

	// DAZOO LIKE ENTITY fields
	public static final String	DAZOO_LIKE_ENTITY_ENTITY_ID							= "_id";
	public static final String	DAZOO_LIKE_ENTITY_ENTITY_TYPE						= "entityType";
	public static final String	DAZOO_LIKE_ENTITY_TITLE								= "title";
	public static final String	DAZOO_LIKE_ENTITY_SYNOPSIS_SHORT					= "synopsisShort";
	public static final String	DAZOO_LIKE_ENTITY_SYNOPSIS_LONG						= "synopsisLong";
	public static final String	DAZOO_LIKE_ENTITY_POSTER							= "poster";
	public static final String	DAZOO_LIKE_ENTITY_TAGS								= "tags";
	public static final String	DAZOO_LIKE_ENTITY_CREDITS							= "credits";
	public static final String	DAZOO_LIKE_ENTITY_EPISODE_NUMBER					= "episodeNumber";
	public static final String	DAZOO_LIKE_ENTITY_SEASON							= "season";
	public static final String	DAZOO_LIKE_ENTITY_SERIES							= "series";
	public static final String	DAZOO_LIKE_ENTITY_YEAR								= "year";
	public static final String	DAZOO_LIKE_ENTITY_GENRE								= "genre";

	// CREDIT fields
	public static final String	DAZOO_CREDIT_NAME									= "name";
	public static final String	DAZOO_CREDIT_TYPE									= "type";

	// SERIES fields
	public static final String	DAZOO_SERIES_NAME									= "name";
	public static final String	DAZOO_SERIES_SERIES_ID								= "seriesId";

	// SPORT TYPE fields
	public static final String	DAZOO_SPORTTYPE_SPORTTYPEID							= "sportTypeId";
	public static final String	DAZOO_SPORTTYPE_NAME								= "name";

	// FEED fields
	public static final String	DAZOO_FEED_ITEM_TYPE_BROADCAST						= "BROADCAST";
	public static final String	DAZOO_FEED_ITEM_TYPE_RECOMMENDED_BROADCAST			= "RECOMMENDED_BROADCAST";
	public static final String	DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS				= "POPULAR_BROADCASTS";
	public static final String	DAZOO_FEED_ITEM_TYPE_POPULAR_TWITTER				= "POPULAR_TWITTER";
	public static final String	DAZOO_FEED_ITEM_ITEM_TYPE							= "itemType";
	public static final String	DAZOO_FEED_ITEM_TITLE								= "title";
	public static final String	DAZOO_FEED_ITEM_BROADCAST							= "broadcast";
	public static final String	DAZOO_FEED_ITEM_BROADCASTS							= "broadcasts";

}
