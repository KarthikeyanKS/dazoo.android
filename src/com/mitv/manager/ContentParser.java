package com.mitv.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.Consts;
import com.mitv.model.AdzerkAd;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.Credit;
import com.mitv.model.DazooLike;
import com.mitv.model.DazooLikeEntity;
import com.mitv.model.FeedItem;
import com.mitv.model.Guide;
import com.mitv.model.Program;
import com.mitv.model.ProgramType;
import com.mitv.model.SearchResult;
import com.mitv.model.Season;
import com.mitv.model.Series;
import com.mitv.model.SportType;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;

public class ContentParser {

	private static final String	TAG	= "ContentParser";

	public ArrayList<Guide> parseGuide(JSONArray mainArray) throws Exception {

		ArrayList<Guide> guides = new ArrayList<Guide>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonGuide = mainArray.getJSONObject(i);
			Guide guide = new Guide(jsonGuide);
			guides.add(guide);
		}
		return guides;
	}
	
	public SearchResult parseSearchResult(JSONObject jsonObject) throws Exception {
		SearchResult searchResult = new SearchResult(jsonObject);
		return searchResult;
	}
	
	public AdzerkAd parseAd(String divId, JSONObject jsonAd) throws Exception {
		AdzerkAd ad = new AdzerkAd(divId, jsonAd);
		
		return ad;
	}

	public ProgramType parseProgramType(JSONObject jsonProgramType) throws Exception {
		ProgramType programType = new ProgramType();
		programType.setId(jsonProgramType.optString(Consts.DAZOO_TAG_ID));
		programType.setName(jsonProgramType.optString(Consts.DAZOO_TAG_NAME));
		programType.setAlias(jsonProgramType.optString(Consts.DAZOO_TAG_ALIAS));
		return programType;
	}

	public ArrayList<ProgramType> parseProgramTypes(JSONArray mainArray) throws Exception {
		ArrayList<ProgramType> programTypes = new ArrayList<ProgramType>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonProgramType = mainArray.getJSONObject(i);
			if (jsonProgramType != null) {
				programTypes.add(parseProgramType(jsonProgramType));
			}
		}
		return programTypes;
	}

	public Tag parseTag(JSONObject jsonTag) throws Exception {
		Tag tag = new Tag();
		tag.setId(jsonTag.optString(Consts.DAZOO_TAG_ID));
		tag.setName(jsonTag.optString(Consts.DAZOO_TAG_NAME));
		return tag;
	}

	public ArrayList<Tag> parseTags(JSONArray mainArray) throws Exception {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		Log.d(TAG, "TAGS PAGE SIZE:" + mainArray.length());

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonProgramType = mainArray.getJSONObject(i);
			if (jsonProgramType != null) {
				tags.add(parseTag(jsonProgramType));
			}
		}
		return tags;
	}

	public ArrayList<TvDate> parseDates(JSONArray mainArray) throws Exception {
		ArrayList<TvDate> tvDates = new ArrayList<TvDate>();

		Log.d(TAG, "DATES PAGE SIZE:" + mainArray.length());

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonTvDate = mainArray.getJSONObject(i);

			TvDate tvDate = new TvDate();
			tvDate.setId(jsonTvDate.optString(Consts.DAZOO_DATE_ID));
			tvDate.setName(jsonTvDate.optString(Consts.DAZOO_DATE_NAME));
			tvDate.setDate(jsonTvDate.optString(Consts.DAZOO_DATE_DATE));

			tvDates.add(tvDate);
		}
		return tvDates;
	}

	public ArrayList<Channel> parseChannels(JSONArray mainArray) throws Exception {

		Log.d(TAG, "CHANNELS PAGE SIZE:" + mainArray.length());

		ArrayList<Channel> channels = new ArrayList<Channel>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonChannel = mainArray.getJSONObject(i);
			if (jsonChannel != null) {
				channels.add(parseChannel(jsonChannel));
			}
		}

		return channels;
	}

	public static Channel parseChannel(JSONObject jsonChannel) throws Exception {
		Channel channel = new Channel(jsonChannel);

		return channel;
	}

	public static Broadcast parseBroadcast(JSONObject jsonBroadcast) throws Exception {
		Broadcast broadcast = new Broadcast(jsonBroadcast);

		return broadcast;
	}

	public static Program parseProgram(JSONObject jsonProgram) throws Exception {
		Program program = new Program();

		return program;
	}

	public static SportType parseSportType(JSONObject jsonSportType) throws Exception {
		SportType sportType = new SportType(jsonSportType);

		return sportType;
	}

	public static Season parseSeason(JSONObject jsonSeason) throws Exception {
		Season season = new Season(jsonSeason);
		return season;
	}

	public TvDate parseTvDate(JSONObject jsonTvDate) throws Exception {
		TvDate tvDate = new TvDate();
		tvDate.setId(jsonTvDate.optString(Consts.DAZOO_DATE_ID));
		tvDate.setName(jsonTvDate.optString(Consts.DAZOO_DATE_NAME));
		tvDate.setDate(jsonTvDate.optString(Consts.DAZOO_DATE_DATE));
		return tvDate;
	}

	public ArrayList<TvDate> parseTvDates(JSONArray mainArray) throws Exception {
		ArrayList<TvDate> tvDates = new ArrayList<TvDate>();
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
					channelIds.add(jsonObject.optString(Consts.DAZOO_CHANNEL_CHANNEL_ID));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return channelIds;
		} else return null;
	}

	public static Credit parseCredit(JSONObject jsonObject) {
		Credit credit = new Credit(jsonObject);
		return credit;
	}

	public static Series parseSeries(JSONObject jsonObject) {
		Series series = new Series(jsonObject);
		return series;
	}

	public static DazooLike parseDazooLike(JSONObject jsonObject) {
		DazooLike dazooLike = new DazooLike();
		String likeType = jsonObject.optString(Consts.DAZOO_LIKE_LIKETYPE);
		dazooLike.setLikeType(likeType);

		DazooLikeEntity dazooLikeEntity = new DazooLikeEntity();
		if (Consts.DAZOO_LIKE_TYPE_SERIES.equals(likeType)) {
			dazooLikeEntity.setTitle(jsonObject.optString(Consts.DAZOO_LIKE_SERIES_TITLE));
			dazooLikeEntity.setSeriesId(jsonObject.optString(Consts.DAZOO_LIKE_SERIES_SERIES_ID));
		} else if (Consts.DAZOO_LIKE_TYPE_PROGRAM.equals(likeType)) {
			dazooLikeEntity.setProgramId(jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAMID));
			String programType = jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAMTYPE);
			if (Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER.equals(programType)) {
				dazooLikeEntity.setTitle(jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER_TITLE));
				dazooLikeEntity.setCategory(jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER_CATEGORY));
			} else if (Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE.equals(programType)) {
				dazooLikeEntity.setTitle(jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_TITLE));
				dazooLikeEntity.setGenre(jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_GENRE));
				dazooLikeEntity.setYear(jsonObject.optInt(Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE_YEAR));
			}
			dazooLikeEntity.setProgramType(programType);
		} else if (Consts.DAZOO_LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
			dazooLikeEntity.setSportTypeId(jsonObject.optString(Consts.DAZOO_LIKE_SPORT_TYPE_SPORTTYPEID));
			dazooLikeEntity.setTitle(jsonObject.optString(Consts.DAZOO_LIKE_SPORT_TYPE_TITLE));
		}
		dazooLike.setEntity(dazooLikeEntity);
		JSONObject nextBroadcast = jsonObject.optJSONObject(Consts.DAZOO_LIKE_NEXT_BROADCAST);
		if (nextBroadcast != null) {
			dazooLike.setNextBroadcastChannelId(nextBroadcast.optString(Consts.DAZOO_LIKE_NEXT_BROADCAST_CHANNELID));
			Log.d(TAG, "Channelid " + nextBroadcast.optString(Consts.DAZOO_LIKE_NEXT_BROADCAST_CHANNELID));
			dazooLike.setNextBroadcastBegintimeMillis(nextBroadcast.optLong(Consts.DAZOO_LIKE_NEXT_BROADCAST_BEGINTIMEMILLIS));
		}

		return dazooLike;
	}

	public static String parseDazooLikeIds(JSONObject jsonObject) {
		String likeType = jsonObject.optString(Consts.DAZOO_LIKE_LIKETYPE);
		if (Consts.DAZOO_LIKE_TYPE_SERIES.equals(likeType)) {
			return jsonObject.optString(Consts.DAZOO_LIKE_SERIES_SERIES_ID);
		} else if (Consts.DAZOO_LIKE_TYPE_PROGRAM.equals(likeType)) {
			return jsonObject.optString(Consts.DAZOO_LIKE_PROGRAM_PROGRAMID);
		} else if (Consts.DAZOO_LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
			return jsonObject.optString(Consts.DAZOO_LIKE_SPORT_TYPE_SPORTTYPEID);
		}
		return null;
	}

	public static FeedItem parseFeedItem(JSONObject jsonObject) {
		FeedItem feedItem = new FeedItem();
		String itemType = jsonObject.optString(Consts.DAZOO_FEED_ITEM_ITEM_TYPE);
		feedItem.setItemType(itemType);

		if (Consts.DAZOO_FEED_ITEM_TYPE_BROADCAST.equals(itemType) || Consts.DAZOO_FEED_ITEM_POPULAR_BROADCAST.equals(itemType) || Consts.DAZOO_FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(itemType)
				|| Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_TWITTER.equals(itemType)) {
			feedItem.setTitle(jsonObject.optString(Consts.DAZOO_FEED_ITEM_TITLE));
			try {
				feedItem.setBroadcast(parseBroadcast(jsonObject.optJSONObject(Consts.DAZOO_FEED_ITEM_BROADCAST)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(itemType)) {
			feedItem.setTitle(jsonObject.optString(Consts.DAZOO_FEED_ITEM_TITLE));
			JSONArray broadcastsJSONArray = jsonObject.optJSONArray(Consts.DAZOO_FEED_ITEM_BROADCASTS);
			int size = broadcastsJSONArray.length();
			ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
			for (int i = 0; i < size; i++) {
				try {
					Broadcast broadcast = parseBroadcast(broadcastsJSONArray.optJSONObject(i));
					broadcasts.add(broadcast);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			feedItem.setBroadcasts(broadcasts);
		}
		return feedItem;
	}

	public static ArrayList<Broadcast> parseBroadcasts(JSONArray jsonArray) {
		ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
		int size = jsonArray.length();
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject;
			try {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject != null) {
					Broadcast broadcast = parseBroadcast(jsonObject);
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