package com.mitv.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.OldAdzerkAd;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldTVChannel;
import com.mitv.model.OldCredit;
import com.mitv.model.OldTVLike;
import com.mitv.model.OldMiTVLikeEntity;
import com.mitv.model.OldTVFeedItem;
import com.mitv.model.OldTVChannelGuide;
import com.mitv.model.OldProgram;
import com.mitv.model.OldProgramType;
import com.mitv.model.OldSearchResult;
import com.mitv.model.OldSeason;
import com.mitv.model.OldSeries;
import com.mitv.model.OldSportType;
import com.mitv.model.OldTVTag;
import com.mitv.model.OldTVDate;

public class ContentParser {

	private static final String	TAG	= "ContentParser";

	public ArrayList<OldTVChannelGuide> parseGuide(JSONArray mainArray) throws Exception {

		ArrayList<OldTVChannelGuide> guides = new ArrayList<OldTVChannelGuide>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonGuide = mainArray.getJSONObject(i);
			OldTVChannelGuide guide = new OldTVChannelGuide(jsonGuide);
			guides.add(guide);
		}
		return guides;
	}
	
	public OldSearchResult parseSearchResult(JSONObject jsonObject) throws Exception {
		OldSearchResult searchResult = new OldSearchResult(jsonObject);
		return searchResult;
	}
	
	public OldAdzerkAd parseAd(String divId, JSONObject jsonAd) throws Exception {
		OldAdzerkAd ad = new OldAdzerkAd(divId, jsonAd);
		
		return ad;
	}

	public OldProgramType parseProgramType(JSONObject jsonProgramType) throws Exception {
		OldProgramType programType = new OldProgramType();
		programType.setId(jsonProgramType.optString(Consts.TAG_ID));
		programType.setName(jsonProgramType.optString(Consts.TAG_NAME));
		programType.setAlias(jsonProgramType.optString(Consts.TAG_ALIAS));
		return programType;
	}

	public ArrayList<OldProgramType> parseProgramTypes(JSONArray mainArray) throws Exception {
		ArrayList<OldProgramType> programTypes = new ArrayList<OldProgramType>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonProgramType = mainArray.getJSONObject(i);
			if (jsonProgramType != null) {
				programTypes.add(parseProgramType(jsonProgramType));
			}
		}
		return programTypes;
	}

	public OldTVTag parseTag(JSONObject jsonTag) throws Exception {
		OldTVTag tag = new OldTVTag();
		tag.setId(jsonTag.optString(Consts.TAG_ID));
		tag.setName(jsonTag.optString(Consts.TAG_NAME));
		return tag;
	}

	public ArrayList<OldTVTag> parseTags(JSONArray mainArray) throws Exception {
		ArrayList<OldTVTag> tags = new ArrayList<OldTVTag>();

		Log.d(TAG, "TAGS PAGE SIZE:" + mainArray.length());

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonProgramType = mainArray.getJSONObject(i);
			if (jsonProgramType != null) {
				tags.add(parseTag(jsonProgramType));
			}
		}
		return tags;
	}

	public ArrayList<OldTVDate> parseDates(JSONArray mainArray) throws Exception {
		ArrayList<OldTVDate> tvDates = new ArrayList<OldTVDate>();

		Log.d(TAG, "DATES PAGE SIZE:" + mainArray.length());

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonTvDate = mainArray.getJSONObject(i);

			OldTVDate tvDate = new OldTVDate();
			tvDate.setId(jsonTvDate.optString(Consts.DATE_ID));
			tvDate.setName(jsonTvDate.optString(Consts.DATE_NAME));
			tvDate.setDate(jsonTvDate.optString(Consts.DATE_DATE));

			tvDates.add(tvDate);
		}
		return tvDates;
	}

	public ArrayList<OldTVChannel> parseChannels(JSONArray mainArray) throws Exception {

		Log.d(TAG, "CHANNELS PAGE SIZE:" + mainArray.length());

		ArrayList<OldTVChannel> channels = new ArrayList<OldTVChannel>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonChannel = mainArray.getJSONObject(i);
			if (jsonChannel != null) {
				channels.add(parseChannel(jsonChannel));
			}
		}

		return channels;
	}

	public static OldTVChannel parseChannel(JSONObject jsonChannel) throws Exception {
		OldTVChannel channel = new OldTVChannel(jsonChannel);

		return channel;
	}

	public static OldBroadcast parseBroadcast(JSONObject jsonBroadcast) throws Exception {
		OldBroadcast broadcast = new OldBroadcast(jsonBroadcast);

		return broadcast;
	}

	public static OldProgram parseProgram(JSONObject jsonProgram) throws Exception {
		OldProgram program = new OldProgram();

		return program;
	}

	public static OldSportType parseSportType(JSONObject jsonSportType) throws Exception {
		OldSportType sportType = new OldSportType(jsonSportType);

		return sportType;
	}

	public static OldSeason parseSeason(JSONObject jsonSeason) throws Exception {
		OldSeason season = new OldSeason(jsonSeason);
		return season;
	}

	public OldTVDate parseTvDate(JSONObject jsonTvDate) throws Exception {
		OldTVDate tvDate = new OldTVDate();
		tvDate.setId(jsonTvDate.optString(Consts.DATE_ID));
		tvDate.setName(jsonTvDate.optString(Consts.DATE_NAME));
		tvDate.setDate(jsonTvDate.optString(Consts.DATE_DATE));
		return tvDate;
	}

	public ArrayList<OldTVDate> parseTvDates(JSONArray mainArray) throws Exception {
		ArrayList<OldTVDate> tvDates = new ArrayList<OldTVDate>();
		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonTvDate = mainArray.getJSONObject(i);
			if (jsonTvDate != null) {
				tvDates.add(parseTvDate(jsonTvDate));
			}
		}
		return tvDates;
	}

	public static ArrayList<String> parseChannelIds(JSONArray jsonArray) {
		if (jsonArray != null) {
			ArrayList<String> channelIds = new ArrayList<String>();
			int size = jsonArray.length();
			for (int i = 0; i < size; i++) {
				JSONObject jsonObject;
				try {
					jsonObject = jsonArray.getJSONObject(i);
					channelIds.add(jsonObject.optString(Consts.CHANNEL_CHANNEL_ID));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return channelIds;
		} else return null;
	}

	public static OldCredit parseCredit(JSONObject jsonObject) {
		OldCredit credit = new OldCredit(jsonObject);
		return credit;
	}

	public static OldSeries parseSeries(JSONObject jsonObject) {
		OldSeries series = new OldSeries(jsonObject);
		return series;
	}

	public static OldTVLike parseMiTVLike(JSONObject jsonObject) {
		OldTVLike mitvLike = new OldTVLike();
		String likeType = jsonObject.optString(Consts.LIKE_LIKETYPE);
		mitvLike.setLikeType(likeType);

		OldMiTVLikeEntity mitvLikeEntity = new OldMiTVLikeEntity();
		if (Consts.LIKE_TYPE_SERIES.equals(likeType)) {
			mitvLikeEntity.setTitle(jsonObject.optString(Consts.LIKE_SERIES_TITLE));
			mitvLikeEntity.setSeriesId(jsonObject.optString(Consts.LIKE_SERIES_SERIES_ID));
		} else if (Consts.LIKE_TYPE_PROGRAM.equals(likeType)) {
			mitvLikeEntity.setProgramId(jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAMID));
			String programType = jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAMTYPE);
			if (Consts.LIKE_PROGRAM_PROGRAM_TYPE_OTHER.equals(programType)) {
				mitvLikeEntity.setTitle(jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAM_TYPE_OTHER_TITLE));
				mitvLikeEntity.setCategory(jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAM_TYPE_OTHER_CATEGORY));
			} else if (Consts.LIKE_PROGRAM_PROGRAM_TYPE_MOVIE.equals(programType)) {
				mitvLikeEntity.setTitle(jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_TITLE));
				mitvLikeEntity.setGenre(jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_GENRE));
				mitvLikeEntity.setYear(jsonObject.optInt(Consts.LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_YEAR));
			}
			mitvLikeEntity.setProgramType(programType);
		} else if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
			mitvLikeEntity.setSportTypeId(jsonObject.optString(Consts.LIKE_SPORT_TYPE_SPORTTYPEID));
			mitvLikeEntity.setTitle(jsonObject.optString(Consts.LIKE_SPORT_TYPE_TITLE));
		}
		mitvLike.setEntity(mitvLikeEntity);
		JSONObject nextBroadcast = jsonObject.optJSONObject(Consts.LIKE_NEXT_BROADCAST);
		if (nextBroadcast != null) {
			mitvLike.setNextBroadcastChannelId(nextBroadcast.optString(Consts.LIKE_NEXT_BROADCAST_CHANNELID));
			Log.d(TAG, "Channelid " + nextBroadcast.optString(Consts.LIKE_NEXT_BROADCAST_CHANNELID));
			mitvLike.setNextBroadcastBegintimeMillis(nextBroadcast.optLong(Consts.LIKE_NEXT_BROADCAST_BEGINTIMEMILLIS));
		}

		return mitvLike;
	}

	public static String parseMiTVLikeIds(JSONObject jsonObject) {
		String likeType = jsonObject.optString(Consts.LIKE_LIKETYPE);
		if (Consts.LIKE_TYPE_SERIES.equals(likeType)) {
			return jsonObject.optString(Consts.LIKE_SERIES_SERIES_ID);
		} else if (Consts.LIKE_TYPE_PROGRAM.equals(likeType)) {
			return jsonObject.optString(Consts.LIKE_PROGRAM_PROGRAMID);
		} else if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
			return jsonObject.optString(Consts.LIKE_SPORT_TYPE_SPORTTYPEID);
		}
		return null;
	}

	public static OldTVFeedItem parseFeedItem(JSONObject jsonObject) {
		OldTVFeedItem feedItem = new OldTVFeedItem();
		String itemType = jsonObject.optString(Consts.FEED_ITEM_ITEM_TYPE);
		feedItem.setItemType(itemType);

		if (Consts.FEED_ITEM_TYPE_BROADCAST.equals(itemType) || Consts.FEED_ITEM_TYPE_POPULAR_BROADCAST.equals(itemType) || Consts.FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(itemType)
				|| Consts.FEED_ITEM_TYPE_POPULAR_TWITTER.equals(itemType)) {
			feedItem.setTitle(jsonObject.optString(Consts.FEED_ITEM_TITLE));
			try {
				feedItem.setBroadcast(parseBroadcast(jsonObject.optJSONObject(Consts.FEED_ITEM_BROADCAST)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (Consts.FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(itemType)) {
			feedItem.setTitle(jsonObject.optString(Consts.FEED_ITEM_TITLE));
			JSONArray broadcastsJSONArray = jsonObject.optJSONArray(Consts.FEED_ITEM_BROADCASTS);
			int size = broadcastsJSONArray.length();
			ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();
			for (int i = 0; i < size; i++) {
				try {
					OldBroadcast broadcast = parseBroadcast(broadcastsJSONArray.optJSONObject(i));
					broadcasts.add(broadcast);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(broadcasts.size() == 1) {
				feedItem.setItemType(Consts.FEED_ITEM_TYPE_POPULAR_BROADCAST);
				OldBroadcast onlyBroadcast = broadcasts.get(0);
				feedItem.setBroadcast(onlyBroadcast);
			} else {
				feedItem.setBroadcasts(broadcasts);
			}
		}
		return feedItem;
	}

	public static ArrayList<OldBroadcast> parseBroadcasts(JSONArray jsonArray) {
		ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();
		int size = jsonArray.length();
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject;
			try {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject != null) {
					OldBroadcast broadcast = parseBroadcast(jsonObject);
					broadcasts.add(broadcast);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return broadcasts;
	}
	
	// Parse api version
	public static String parseApiVersion(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject != null) {
					String name = jsonObject.getString(Consts.JSON_KEY_API_VERSION_NAME);
					if (name.equals(Consts.JSON_KEY_API)) {
						String version = jsonObject.getString(Consts.JSON_KEY_API_VERSION_VALUE);
						Log.d(TAG, "Parsed api version: " + version);
						return version;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}