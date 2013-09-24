package com.millicom.secondscreen;

/**
 * Class for constant values declaration for the application
 * 
 */
public abstract class Consts {

	// Shared preferences
	public static final String	SHARED_PREFS_MAIN_NAME										= "com.millicom.secondscreen.shared.prefs";

	// api urls
	public static final String	MILLICOM_SECONDSCREEN_GUIDE_PAGE_API						= "http://api.gitrgitr.com/guide";
	public static final String	MILLICOM_SECONDSCREEN_PROGRAM_TYPES_PAGE_API				= "http://api.gitrgitr.com/programTypes";
	public static final String	MILLICOM_SECONDSCREEN_DATES_PAGE_API						= "http://api.gitrgitr.com/dates";
	public static final String	MILLICOM_SECONDSCREEN_CHANNELS_PAGE_API						= "http://api.gitrgitr.com/channels";

	// api request strings
	public static final String	MILLICOM_SECONDSCREEN_API_CHANNEL_ID						= "channelId=";
	public static final String	REQUEST_QUERY_AND											= "&";
	public static final String	REQUEST_QUERY_SEPARATOR										= "/";
	public static final String	REQUEST_PARAMETER_SEPARATOR									= "?";

	public static final int		MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_ITEMS_PER_CHANNEL	= 3;
	public static final int		MILLICOM_SECONDSCREEN_TVGUIDE_NUMBER_OF_CHANNELS_PER_PAGE	= 10;

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
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING		= "com.millicom.secondscreen.intent.extra.tvguide.sorting";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_VALUE	= "com.millicom.secondscreen.intent.extra.tvguide.sorting.value";
	public static final String	INTENT_EXTRA_TVGUIDE_SORTING_TYPE	= "com.millicom.secondscreen.intent.extra,tvguide.sorting.type";

	// custom data types
	public static final String	VALUE_TYPE_PROGRAMTYPE				= "com.millicom.secondscreen.value.type.programtype";
	public static final String	VALUE_TYPE_TVDATE					= "com.millicom.seconscreen.value.type.tvdate";

	// TVGuide
	public static final int		TV_GUIDE_NEXT_PROGRAMS_NUMBER		= 3;

	public static final String	IMAGE_MACHINE_SECURITY_KEY			= "24567hright";

	// Parcelable bundles
	public static final String	PARCELABLE_CHANNELS_LIST			= "com.parcelable.channels.list";
	public static final String	PARCELABLE_TV_DATES_LIST			= "com.parcelable.dates.list";
	public static final String	PARCELABLE_PROGRAM_TYPES_LIST		= "com.parcelable.categories.list";

	// activity's requests to update calling fragment
	public static final int		INFO_UPDATE_REMINDERS				= 1;
	public static final int		INFO_UPDATE_LIKES					= 11;
	public static final int		INFO_NO_UPDATE_REMINDERS			= 0;
	public static final int		INFO_NO_UPDATE_LIKES				= 10;

}
