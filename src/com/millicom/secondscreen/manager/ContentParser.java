package com.millicom.secondscreen.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Credit;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.DazooLikeEntity;
import com.millicom.secondscreen.content.model.Season;
import com.millicom.secondscreen.content.model.Series;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.ProgramType;

public class ContentParser {

	private static final String	TAG	= "ContentParser";

	public ArrayList<Guide> parseGuide(JSONArray mainArray) throws Exception {

		ArrayList<Guide> guides = new ArrayList<Guide>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonGuide = mainArray.getJSONObject(i);

			Guide guide = new Guide();
			guide.setId(jsonGuide.optString(Consts.DAZOO_GUIDE_CHANNEL_ID));
			guide.setName(jsonGuide.optString(Consts.DAZOO_GUIDE_CHANNEL_NAME));

			JSONObject logosJson = jsonGuide.optJSONObject(Consts.DAZOO_GUIDE_LOGO);
			if (logosJson != null) {
				guide.setLogoSHref(logosJson.optString(Consts.DAZOO_IMAGE_SMALL));
				guide.setLogoMHref(logosJson.optString(Consts.DAZOO_IMAGE_MEDIUM));
				guide.setLogoLHref(logosJson.optString(Consts.DAZOO_IMAGE_LARGE));
			}

			JSONArray broadcastsJson = jsonGuide.optJSONArray(Consts.DAZOO_GUIDE_BROADCASTS);

			if (broadcastsJson != null) {
				ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
				for (int j = 0; j < broadcastsJson.length(); j++) {
					JSONObject jsonBroadcast = broadcastsJson.optJSONObject(j);
					if (jsonBroadcast != null) {
						broadcasts.add(parseBroadcast(jsonBroadcast));
					}
				}
				guide.setBroadcasts(broadcasts);
			}
			guides.add(guide);
		}
		return guides;
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

		Log.d(TAG,"TAGS PAGE SIZE:" + mainArray.length());
		
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

		Log.d(TAG,"DATES PAGE SIZE:" + mainArray.length());
		
		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonTvDate = mainArray.getJSONObject(i);

			TvDate tvDate = new TvDate();
			tvDate.setId(jsonTvDate.optString(Consts.DAZOO_DATE_ID));
			tvDate.setName(jsonTvDate.optString(Consts.DAZOO_DATE_NAME));
			tvDate.setAlias(jsonTvDate.optString(Consts.DAZOO_DATE_ALIAS));
			tvDate.setDate(jsonTvDate.optString(Consts.DAZOO_DATE_DATE));

			tvDates.add(tvDate);
		}
		return tvDates;
	}

	public ArrayList<Channel> parseChannels(JSONArray mainArray) throws Exception {
		
		Log.d(TAG,"CHANNELS PAGE SIZE:" + mainArray.length());
		
		ArrayList<Channel> channels = new ArrayList<Channel>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonChannel = mainArray.getJSONObject(i);
			if (jsonChannel != null) {
				channels.add(parseChannel(jsonChannel));
			}
		}

		return channels;
	}

	public Channel parseChannel(JSONObject jsonChannel) throws Exception {
		Channel channel = new Channel();
		channel.setChannelId(jsonChannel.optString(Consts.DAZOO_CHANNEL_CHANNEL_ID));
		channel.setName(jsonChannel.optString(Consts.DAZOO_CHANNEL_NAME));

		JSONObject jsonPoster = jsonChannel.getJSONObject(Consts.DAZOO_CHANNEL_LOGO);
		if (jsonPoster != null) {
			channel.setLogoSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
			channel.setLogoMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
			channel.setLogoLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
		}
		return channel;
	}

	public Broadcast parseBroadcast(JSONObject jsonBroadcast) throws Exception {
		Broadcast broadcast = new Broadcast();
		broadcast.setBeginTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BEGIN_TIME));
		broadcast.setEndTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_END_TIME));
		broadcast.setBeginTimeMillis(jsonBroadcast.optLong(Consts.DAZOO_BROADCAST_BEGIN_TIME_MILLIS));

		broadcast.setShareUrl(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_SHARE_URL));

		JSONObject jsonChannel = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_CHANNEL);
		if (jsonChannel != null) {
			broadcast.setChannel(parseChannel(jsonChannel));
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM);
		if (jsonProgram != null) {
			broadcast.setProgram(parseProgram(jsonProgram));
		}

		broadcast.setBroadcastType(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BROADCAST_TYPE));

		return broadcast;
	}

	public Program parseProgram(JSONObject jsonProgram) throws Exception {
		Program program = new Program();
		program.setProgramId(jsonProgram.optString(Consts.DAZOO_PROGRAM_ID));

		String programType = jsonProgram.optString(Consts.DAZOO_PROGRAM_TYPE);
		program.setProgramType(programType);

		String temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_TITLE);
		if (temp.length() > 0) {
			program.setTitle(temp);
		} else {
			program.setTitle("");
		}

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SYNOPSIS_SHORT);
		if (temp.length() > 0) program.setSynopsisShort(temp);
		else program.setSynopsisShort("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SYNOPSISS_LONG);
		if (temp.length() > 0) program.setSynopsisLong(temp);
		else program.setSynopsisShort("");

		JSONObject jsonPoster = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_POSTER);
		if (jsonPoster != null) {
			program.setPosterSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
			program.setPosterMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
			program.setPosterLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
		}

		JSONArray jsonTags = jsonProgram.optJSONArray(Consts.DAZOO_PROGRAM_TAGS);
		if (jsonTags != null) {
			ArrayList<String> tags = new ArrayList<String>();
			for (int k = 0; k < jsonTags.length(); k++) {
				tags.add(jsonTags.getString(k));
			}
			program.setTags(tags);
		}

		JSONArray jsonCredits = jsonProgram.optJSONArray(Consts.DAZOO_PROGRAM_CREDITS);
		if (jsonCredits != null) {
			ArrayList<Credit> credits = new ArrayList<Credit>();
			for (int k = 0; k < jsonCredits.length(); k++) {
				credits.add(parseCredit(jsonCredits.getJSONObject(k)));
			}
			program.setCredits(credits);
		}

		if ((Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE).equals(programType)) {
			int tempInt = jsonProgram.optInt(Consts.DAZOO_PROGRAM_EPISODE);
			if (temp.length() > 0) program.setEpisodeNumber(tempInt);
			else program.setEpisodeNumber(-1);

			JSONObject seasonJSON = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_SEASON);
			program.setSeason(parseSeason(seasonJSON));

			JSONObject seriesJSON = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_SERIES);
			program.setSeries(parseSeries(seriesJSON));

		} else if ((Consts.DAZOO_PROGRAM_TYPE_MOVIE).equals(programType)) {
			program.setYear(jsonProgram.optInt(Consts.DAZOO_PROGRAM_YEAR));
			program.setGenre(jsonProgram.optString(Consts.DAZOO_PROGRAM_GENRE));
		} else if ((Consts.DAZOO_PROGRAM_TYPE_SPORT).equals(programType)) {
			program.setSportType(jsonProgram.optString(Consts.DAZOO_PROGRAM_SPORTTYPE));
			program.setTournament(jsonProgram.optString(Consts.DAZOO_PROGRAM_TOURNAMENT));
		}

		return program;
	}

	public static Season parseSeason(JSONObject jsonSeason) throws Exception {
		Season season = new Season();
		season.setNumber(jsonSeason.optString(Consts.DAZOO_SEASON_NUMBER));
		return season;
	}

	public TvDate parseTvDate(JSONObject jsonTvDate) throws Exception {
		TvDate tvDate = new TvDate();
		tvDate.setId(jsonTvDate.optString(Consts.DAZOO_DATE_ID));
		tvDate.setName(jsonTvDate.optString(Consts.DAZOO_DATE_NAME));
		tvDate.setAlias(jsonTvDate.optString(Consts.DAZOO_DATE_ALIAS));
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
					Log.d(TAG, jsonObject.optString(Consts.DAZOO_CHANNEL_CHANNEL_ID));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return channelIds;
		} else return null;
	}

	public static Credit parseCredit(JSONObject jsonObject) {
		Credit credit = new Credit();
		credit.setName(jsonObject.optString(Consts.DAZOO_CREDIT_NAME));
		credit.setType(jsonObject.optString(Consts.DAZOO_CREDIT_TYPE));
		return credit;
	}

	public static Series parseSeries(JSONObject jsonObject) {
		Series series = new Series();
		series.setName(jsonObject.optString(Consts.DAZOO_SERIES_NAME));
		series.setSeriesId(jsonObject.optString(Consts.DAZOO_SERIES_SERIES_ID));
		return series;
	}

	public static DazooLike parseDazooLike(JSONObject jsonObject) {
		DazooLike dazooLike = new DazooLike();
		dazooLike.setEntityId(jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_ID));
		dazooLike.setEntityType(jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_TYPE));

		String entity = jsonObject.optString(Consts.DAZOO_LIKE_ENTITY);
		if (entity != null && TextUtils.isEmpty(entity) != true) {
			JSONObject likeEntity;
			try {
				likeEntity = new JSONObject(entity);
				DazooLikeEntity dazooLikeEntity = ContentParser.parseDazooLikeEntity(likeEntity);
				dazooLike.setEntity(dazooLikeEntity);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dazooLike;
	}

	public static DazooLikeEntity parseDazooLikeEntity(JSONObject jsonObject) {
		if (jsonObject != null) {
			DazooLikeEntity dazooLikeEntity = new DazooLikeEntity();
			dazooLikeEntity.setEntityId(jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_ENTITY_ID));

			String entityType = jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_ENTITY_TYPE);

			dazooLikeEntity.setEntityType(entityType);
			dazooLikeEntity.setTitle(jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_TITLE));
			dazooLikeEntity.setSynopsisShort(jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_SYNOPSIS_SHORT));
			dazooLikeEntity.setSynopsisLong(jsonObject.optString(Consts.DAZOO_LIKE_ENTITY_SYNOPSIS_LONG));

			JSONObject jsonPoster = jsonObject.optJSONObject(Consts.DAZOO_LIKE_ENTITY_POSTER);
			if (jsonPoster != null) {
				dazooLikeEntity.setPosterSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
				dazooLikeEntity.setPosterMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
				dazooLikeEntity.setPosterLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
			}

			JSONArray jsonTags = jsonObject.optJSONArray(Consts.DAZOO_LIKE_ENTITY_TAGS);
			if (jsonTags != null) {
				ArrayList<String> tags = new ArrayList<String>();
				int size = jsonTags.length();
				for (int l = 0; l < size; l++) {
					tags.add(jsonTags.optString(l));
				}
				dazooLikeEntity.setTags(tags);
			}

			JSONArray jsonCreditArray = jsonObject.optJSONArray(Consts.DAZOO_LIKE_ENTITY_CREDITS);
			if (jsonCreditArray != null) {
				ArrayList<Credit> credits = new ArrayList<Credit>();
				int size = jsonCreditArray.length();
				for (int j = 0; j < size; j++) {
					JSONObject jsonCredit;
					try {
						jsonCredit = jsonCreditArray.getJSONObject(j);
						if (jsonCredit != null) {
							credits.add(parseCredit(jsonCredit));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				dazooLikeEntity.setCredits(credits);
			}

			if (entityType != null && TextUtils.isEmpty(entityType) != true) {
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(entityType)) {
					dazooLikeEntity.setEpisodeNumber(jsonObject.optInt(Consts.DAZOO_LIKE_ENTITY_EPISODE_NUMBER));

					JSONObject jsonSeason = jsonObject.optJSONObject(Consts.DAZOO_LIKE_ENTITY_SEASON);
					if (jsonSeason != null) {
						try {
							dazooLikeEntity.setSeason(parseSeason(jsonSeason));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					JSONObject jsonSeries = jsonObject.optJSONObject(Consts.DAZOO_LIKE_ENTITY_SERIES);
					if (jsonSeries != null) {
						dazooLikeEntity.setSeries(parseSeries(jsonSeries));
					}
				}
			}
			return dazooLikeEntity;
		} else return null;
	}
}