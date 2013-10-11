package com.millicom.secondscreen;

/**
 * Class for constant values declaration for the application
 * 
 */
public abstract class Consts {

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
	public static final String	MILLICOM_SECONDSCREEN_GUIDE_PAGE_URL						= "http://api.gitrgitr.com/epg/guide";
	public static final String	MILLICOM_SECONDSCREEN_PROGRAM_TYPES_PAGE_URL				= "http://api.gitrgitr.com/epg/programTypes";
	public static final String	MILLICOM_SECONDSCREEN_DATES_PAGE_URL						= "http://api.gitrgitr.com/epg/dates";
	public static final String	MILLICOM_SECONDSCREEN_CHANNELS_PAGE_URL						= "http://api.gitrgitr.com/epg/channels";
	public static final String	MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL					= "http://api.gitrgitr.com/auth/login/facebook";
	public static final String	MILLICOM_SECONDSCREEN_DAZOO_LOGIN_URL						= "http://api.gitrgitr.com/auth/login/dazoo";
	public static final String	MILLICOM_SECONDSCREEN_DAZOO_REGISTER_URL					= "http://api.gitrgitr.com/auth/login/dazoo/register";
	public static final String	MILLICOM_SECONDSCREEN_RESET_PASSWORD_URL					= "http://api.gitrgitr.com/auth/login/dazoo/sendResetPasswordEmail";
	public static final String	MILLICOM_SECONDSCREEN_TAGS_PAGE_URL							= "http://api.gitrgitr.com/epg/tags/visible";
	public static final String	MILLICOM_SECONDSCREEN_MY_CHANNELS_URL						= "http://api.gitrgitr.com/mychannels/";
	public static final String	MILLICOM_SECONDSCREEN_USERS_URL								= "http://api.gitrgitr.com/auth/users";

	public static final String	MILLICON_SECONDSCREEN_HTTP_SCHEME							= "http";

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

	public static final String	EMPTY_STRING												= "";
	public static final String	ERROR_STRING												= "error";

	// restrictions
	public static final int		MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_ITEMS_PER_CHANNEL	= 3;
	public static final int		MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_CHANNELS_PER_PAGE	= 10;
	public static final int		MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN					= 6;
	public static final int		MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX					= 20;

	public static enum REQUEST_STATUS {
		LOADING, FAILED, EMPTY_RESPONSE, SUCCESSFUL
	};

	// Date iso format
	public static final String	ISO_DATE_FORMAT						= "yyyy-MM-dd'T'HH:mm:ssZZZ";

	// Activity page content block types
	public static final String	BLOCK_TYPE_PRODUCT_TV				= "com.millicom.secondscreen.content.product.tv";
	public static final String	BLOCK_TYPE_PRODUCT_MOVIE			= "com.millicom.secondscreen.content.product.movie";
	public static final String	BLOCK_TYPE_PRODUCT_SPORT			= "com.millicom.secondscreen.content.product.sport";
	public static final String	BLOCK_TYPE_PRODUCT_KIDS				= "com.millicom.secondscreen.content.product.kids";
	public static final String	BLOCK_TYPE_PRODUCT_RECOMMENDED_LIST	= "com.millicom.secondscreen.content.product.list.recommended";

	// Broadcast intents
	public static final String	BROADCAST_SORTING_SELECTED			= "com.broadcast.sorting.selected";
	public static final String	BROADCAST_FORCE_RELOAD				= "com.broadcast.only.downloads";

	// Section Ids
	public static final String	SECTION_ID_TVGUIDE					= "secondscreen.section.tvguide";
	public static final String	SECTION_ID_ACTIVITY					= "secondscreen.section.activity";
	public static final String	SECTION_ID_ME						= "secondscreen.section.me";

	// data extra intents
	public static final String	INTENT_EXTRA_SECTION				= "com.millicom.secondscreen.intent.extra.section";
	public static final String	INTENT_EXTRA_GUIDE					= "com.millicom.secondscreen.intent.extra.guide";
	public static final String	INTENT_EXTRA_CHANNEL				= "com.millicom.secondscreen.intent.extra.channel";
	public static final String	INTENT_EXTRA_CHANNEL_PAGE_LINK		= "com.millicom.secondscreen.intent.extra.channel.page.link";
	public static final String	INTENT_EXTRA_CHANNEL_GUIDE			= "com.millicom.secondscreen.intent.extra.channel.guide";
	public static final String	INTENT_EXTRA_CHANNEL_BROADCAST		= "com.millicom.secondscreen.intent.extra.channel.broadcast";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING		= "com.millicom.secondscreen.intent.extra.tvguide.sorting";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_VALUE	= "com.millicom.secondscreen.intent.extra.tvguide.sorting.value";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_TYPE	= "com.millicom.secondscreen.intent.extra,tvguide.sorting.type";
	public static final String	INTENT_EXTRA_TAG					= "com.millicom.secondscreen.intent.tag";
	public static final String	INTENT_EXTRA_TVGUIDE_PAGE_URL		= "com.millicom.secondscreen.intent.extra.tvguide.page.url";
	public static final String	INTENT_EXTRA_TVGUIDE_TVDATE			= "com.millicom.secondscreen.intent.extra.tvguide.tvdate";

	// custom data types
	public static final String	VALUE_TYPE_PROGRAMTYPE				= "com.millicom.secondscreen.value.type.programtype";
	public static final String	VALUE_TYPE_TVDATE					= "com.millicom.secondscreen.value.type.tvdate";
	public static final String	VALUE_TYPE_TAG						= "com.millicom.secondscreen.value.type.tag";

	// TVGuide
	public static final int		TV_GUIDE_NEXT_PROGRAMS_NUMBER		= 3;
	public static final String	TV_GUIDE_PAGE_NUMBER				= "com.millicom.secondscreen.tvguide.page.number";

	public static final String	IMAGE_MACHINE_SECURITY_KEY			= "24567hright";

	// Parcelable bundles
	public static final String	PARCELABLE_CHANNELS_LIST			= "com.parcelable.channels.list";
	public static final String	PARCELABLE_TV_DATES_LIST			= "com.parcelable.dates.list";
	public static final String	PARCELABLE_PROGRAM_TYPES_LIST		= "com.parcelable.categories.list";
	public static final String	PARCELABLE_TAGS_LIST				= "com.parcelable.tags.list";

	// activity's requests to update calling fragment
	public static final int		INFO_UPDATE_REMINDERS				= 1;
	public static final int		INFO_UPDATE_LIKES					= 11;
	public static final int		INFO_NO_UPDATE_REMINDERS			= 0;
	public static final int		INFO_NO_UPDATE_LIKES				= 10;

	// Response codes
	public static final int		GOOD_RESPONSE						= 200;
	public static final int		GOOD_RESPONSE_CHANNELS_ARE_ADDED	= 204;

	public static final int		BAD_RESPONSE						= 400;
	public static final int		BAD_RESPONSE_MISSING_TOKEN			= 401;
	public static final int		BAD_RESPONSE_INVALID_TOKEN			= 403;

	// =========================== CONTENT ================================

	// PROGRAM fields:
	public static final String	DAZOO_PROGRAM_HREF					= "href";
	public static final String	DAZOO_PROGRAM_ID					= "_id";
	public static final String	DAZOO_PROGRAM_TITLE					= "title";
	public static final String	DAZOO_PROGRAM_TYPE_ID				= "programTypeId";
	public static final String	DAZOO_PROGRAM_TAGS					= "tags";
	public static final String	DAZOO_PROGRAM_SYNOPSIS_SHORT		= "synopsisShort";
	public static final String	DAZOO_PROGRAM_SYNOPSISS_LONG		= "synopsisLong";
	public static final String	DAZOO_PROGRAM_POSTER				= "poster";
	public static final String	DAZOO_PROGRAM_EPISODE				= "episodeNumber";
	public static final String	DAZOO_PROGRAM_SEASON				= "season";
	public static final String	DAZOO_PROGRAM_DESCRIPTION			= "description";
	public static final String	DAZOO_PROGRAM_CAST					= "cast";
	// public static final String DAZOO_PROGRAM_RUNTIME = "runtime";
	public static final String	DAZOO_PROGRAM_SUBTITLE				= "subtitle";
	public static final String	DAZOO_PROGRAM_YEAR					= "year";
	public static final String	DAZOO_PROGRAM_SERIES				= "series";

	// BROADCAST fields
	public static final String	DAZOO_BROADCAST_HREF				= "href";
	public static final String	DAZOO_BROADCAST_PROGRAM				= "program";
	public static final String	DAZOO_BROADCAST_CHANNEL				= "channel";
	public static final String	DAZOO_BROADCAST_BEGIN_TIME			= "beginTime";
	public static final String	DAZOO_BROADCAST_END_TIME			= "endTime";
	// public static final String DAZOO_BROADCAST_DURATION_IN_MINUTES = "durationInMinutes";
	public static final String	DAZOO_BROADCAST_BEGIN_TIME_MILLIS	= "beginTimeMillis";

	// CHANNEL fields
	public static final String	DAZOO_CHANNEL_HREF					= "href";
	public static final String	DAZOO_CHANNEL_CHANNEL_ID			= "channelId";
	public static final String	DAZOO_CHANNEL_ID					= "_id";
	public static final String	DAZOO_CHANNEL_NAME					= "name";
	public static final String	DAZOO_CHANNEL_LOGO					= "logo";

	// SEASON fields
	public static final String	DAZOO_SEASON_NUMBER					= "number";

	// IMAGE fields
	public static final String	DAZOO_IMAGE_SMALL					= "small";
	public static final String	DAZOO_IMAGE_MEDIUM					= "medium";
	public static final String	DAZOO_IMAGE_LARGE					= "large";

	// TAG fields
	public static final String	DAZOO_TAG_ID						= "_id";
	public static final String	DAZOO_TAG_NAME						= "name";
	public static final String	DAZOO_TAG_ALIAS						= "alias";

	// DATE fields
	public static final String	DAZOO_DATE_ID						= "_id";
	public static final String	DAZOO_DATE_NAME						= "name";
	public static final String	DAZOO_DATE_ALIAS					= "alias";
	public static final String	DAZOO_DATE_DATE						= "date";

	// GUIDE fields
	public static final String	DAZOO_GUIDE_HREF					= "href";
	public static final String	DAZOO_GUIDE_CHANNEL_ID				= "channelId";
	public static final String	DAZOO_GUIDE_NAME					= "name";
	public static final String	DAZOO_GUIDE_LOGO					= "logo";
	public static final String	DAZOO_GUIDE_BROADCASTS				= "broadcasts";
}
