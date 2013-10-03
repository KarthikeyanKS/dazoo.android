package com.millicom.secondscreen.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Season;
import com.millicom.secondscreen.content.model.Series;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.ProgramType;

public class ContentParser {

	private static final String	TAG	= "ContentParser";

	public ArrayList<Guide> parseGuide(JSONArray mainArray, String programTypeKey) throws Exception {
		ArrayList<Guide> guides = new ArrayList<Guide>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonGuide = mainArray.getJSONObject(i);

			// get the overall channel info
			Guide guide = new Guide();
			guide.setHref(jsonGuide.optString(Consts.DAZOO_GUIDE_HREF));
			guide.setId(jsonGuide.optString(Consts.DAZOO_GUIDE_CHANNEL_ID));
			guide.setName(jsonGuide.optString(Consts.DAZOO_GUIDE_NAME));

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
						if ("ALL_CATEGORIES".equals(programTypeKey) == false) {
							// if (programTypeKey.equals(jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM).optString(Consts.DAZOO_PROGRAM_ID)) == true) {
							// broadcasts.add(parseBroadcastProgramKey(jsonBroadcast, programTypeKey));
							// }

							JSONArray jsonTags = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM).optJSONArray(Consts.DAZOO_PROGRAM_TAGS);
							ArrayList<String> tags = new ArrayList<String>();
							if (jsonTags != null) {
								for (int k = 0; k < jsonTags.length(); k++) {
									tags.add(jsonTags.getString(k));
								}
							}

							if (tags.contains(programTypeKey)) {
								//broadcasts.add(parseBroadcastProgramKey(jsonBroadcast, programTypeKey));
								broadcasts.add(parseBroadcast(jsonBroadcast));
							}

						} else {
							//broadcasts.add(parseBroadcastAll(jsonBroadcast));
							broadcasts.add(parseBroadcast(jsonBroadcast));
						}
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
		channel.setId(jsonChannel.optString(Consts.DAZOO_CHANNEL_ID));
		channel.setName(jsonChannel.optString(Consts.DAZOO_CHANNEL_NAME));

		JSONObject jsonPoster = jsonChannel.getJSONObject(Consts.DAZOO_CHANNEL_LOGO);
		if (jsonPoster != null) {
			channel.setLogoSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
			channel.setLogoMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
			channel.setLogoLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
		}
		return channel;
	}

	/*
	public Broadcast parseBroadcastAll(JSONObject jsonBroadcast) throws Exception {
		Broadcast broadcast = new Broadcast();
		// broadcast.setBroadcastId(jsonBroadcast.optString("broadcastId"));
		broadcast.setBeginTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BEGIN_TIME));
		broadcast.setEndTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_END_TIME));
		broadcast.setDurationInMinutes(jsonBroadcast.optInt(Consts.DAZOO_BROADCAST_DURATION_IN_MINUTES));
		broadcast.setBeginTimeMillis(jsonBroadcast.optLong(Consts.DAZOO_BROADCAST_BEGIN_TIME_MILLIS));

		JSONObject jsonChannel = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_CHANNEL);
		if (jsonChannel != null) {

			// broadcast.setChannel(parseChannel(jsonChannel));
			broadcast.setChannelUrl(jsonChannel.optString(Consts.DAZOO_CHANNEL_HREF));
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM);
		if (jsonProgram != null) {
			broadcast.setProgram(parseProgramAll(jsonProgram));
		}
		return broadcast;
	}

	public Broadcast parseBroadcastProgramKey(JSONObject jsonBroadcast, String programTypeKey) throws Exception {
		Broadcast broadcast = new Broadcast();
		// broadcast.setBroadcastId(jsonBroadcast.optString("id"));
		broadcast.setBeginTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BEGIN_TIME));
		broadcast.setEndTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_END_TIME));
		broadcast.setDurationInMinutes(jsonBroadcast.optInt(Consts.DAZOO_BROADCAST_DURATION_IN_MINUTES));
		broadcast.setBeginTimeMillis(jsonBroadcast.optLong(Consts.DAZOO_BROADCAST_BEGIN_TIME_MILLIS));

		JSONObject jsonChannel = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_CHANNEL);
		if (jsonChannel != null) {

			// broadcast.setChannel(parseChannel(jsonChannel));
			broadcast.setChannelUrl(jsonChannel.optString(Consts.DAZOO_CHANNEL_HREF));
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM);
		if (jsonProgram != null) {
			broadcast.setProgram(parseProgramKey(jsonProgram, programTypeKey));
		}
		return broadcast;
	}

	public Program parseProgramAll(JSONObject jsonProgram) throws Exception {
		Program program = new Program();
		program.setProgramId(jsonProgram.optString(Consts.DAZOO_PROGRAM_ID));
		program.setProgramTypeId(jsonProgram.optString(Consts.DAZOO_PROGRAM_TYPE_ID));
		program.setTitle(jsonProgram.optString(Consts.DAZOO_PROGRAM_TITLE));
		program.setSubtitle(jsonProgram.optString(Consts.DAZOO_PROGRAM_SUBTITLE));

		JSONObject jsonPoster = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_POSTER);
		if (jsonPoster != null) {
			program.setPosterSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
			program.setPosterMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
			program.setPosterLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
		}

		program.setCast(jsonProgram.optString(Consts.DAZOO_PROGRAM_CAST));
		program.setYear(jsonProgram.optString(Consts.DAZOO_PROGRAM_YEAR));
		program.setRuntime(jsonProgram.optString(Consts.DAZOO_PROGRAM_RUNTIME));

		JSONArray jsonTags = jsonProgram.optJSONArray(Consts.DAZOO_PROGRAM_TAGS);
		if (jsonTags != null) {
			ArrayList<String> tags = new ArrayList<String>();
			for (int k = 0; k < jsonTags.length(); k++) {
				tags.add(jsonTags.getString(k));
			}
			program.setTags(tags);
		}
		
		 program.setSeason(jsonProgram.optString("season"));
        program.setEpisode(jsonProgram.optString("episode"));
        program.setDescription(jsonProgram.optString("description"));
        program.setSynopsisShort(jsonProgram.optString("synopsisShort"));
        program.setSynopsisLong(jsonProgram.optString("synopsisLong"));
        return program;
        }
		
		public Program parseProgramKey(JSONObject jsonProgram, String programTypeKey) throws Exception {
		Program program = new Program();
		program.setProgramId(jsonProgram.optString(Consts.DAZOO_PROGRAM_ID));
		program.setProgramTypeId(jsonProgram.optString(Consts.DAZOO_PROGRAM_TYPE_ID));

		String temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_TITLE);
		if (temp.length() > 0) {
			program.setTitle(temp);
		} else {
			program.setTitle("");
		}

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SUBTITLE);
		if (temp.length() > 0) {
			program.setSubtitle(temp);
		} else {
			program.setSubtitle("");
		}

		JSONObject jsonPoster = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_POSTER);
		if (jsonPoster != null) {
			program.setPosterSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
			program.setPosterMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
			program.setPosterLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
		}

		program.setCast(jsonProgram.optString(Consts.DAZOO_PROGRAM_CAST));
		program.setYear(jsonProgram.optString(Consts.DAZOO_PROGRAM_YEAR));
		program.setRuntime(jsonProgram.optString(Consts.DAZOO_PROGRAM_RUNTIME));

		JSONArray jsonTags = jsonProgram.optJSONArray(Consts.DAZOO_PROGRAM_TAGS);
		if (jsonTags != null) {
			ArrayList<String> tags = new ArrayList<String>();
			for (int k = 0; k < jsonTags.length(); k++) {
				tags.add(jsonTags.getString(k));
			}
			program.setTags(tags);
		}

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SEASON);
		if (temp.length() > 0) program.setSeason(temp);
		else program.setSeason("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_EPISODE);
		if (temp.length() > 0) program.setEpisode(temp);
		else program.setEpisode("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_DESCRIPTION);
		if (temp.length() > 0) program.setDescription(temp);
		else program.setDescription("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SYNOPSIS_SHORT);
		if (temp.length() > 0) program.setSynopsisShort(temp);
		else program.setSynopsisShort("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SYNOPSISS_LONG);
		if (temp.length() > 0) program.setSynopsisLong(temp);
		else program.setSynopsisShort("");

		return program;
	}	
		*/
	
	public Broadcast parseBroadcast(JSONObject jsonBroadcast) throws Exception {
		Broadcast broadcast = new Broadcast();
		// broadcast.setBroadcastId(jsonBroadcast.optString("broadcastId"));
		broadcast.setBeginTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BEGIN_TIME));
		broadcast.setEndTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_END_TIME));
		//broadcast.setDurationInMinutes(jsonBroadcast.optInt(Consts.DAZOO_BROADCAST_DURATION_IN_MINUTES));
		broadcast.setBeginTimeMillis(jsonBroadcast.optLong(Consts.DAZOO_BROADCAST_BEGIN_TIME_MILLIS));

		JSONObject jsonChannel = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_CHANNEL);
		if (jsonChannel != null) {

			// broadcast.setChannel(parseChannel(jsonChannel));
			broadcast.setChannelUrl(jsonChannel.optString(Consts.DAZOO_CHANNEL_HREF));
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM);
		if (jsonProgram != null) {
			broadcast.setProgram(parseProgram(jsonProgram));
		}
		return broadcast;
	}

	public Program parseProgram(JSONObject jsonProgram) throws Exception {
		Program program = new Program();
		program.setProgramId(jsonProgram.optString(Consts.DAZOO_PROGRAM_ID));
		program.setProgramTypeId(jsonProgram.optString(Consts.DAZOO_PROGRAM_TYPE_ID));

		String temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_TITLE);
		if (temp.length() > 0) {
			program.setTitle(temp);
		} else {
			program.setTitle("");
		}

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SUBTITLE);
		if (temp.length() > 0) {
			program.setSubtitle(temp);
		} else {
			program.setSubtitle("");
		}

		JSONObject jsonPoster = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_POSTER);
		if (jsonPoster != null) {
			program.setPosterSUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_SMALL));
			program.setPosterMUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_MEDIUM));
			program.setPosterLUrl(jsonPoster.optString(Consts.DAZOO_IMAGE_LARGE));
		}

		program.setCast(jsonProgram.optString(Consts.DAZOO_PROGRAM_CAST));
		program.setYear(jsonProgram.optString(Consts.DAZOO_PROGRAM_YEAR));
		//program.setRuntime(jsonProgram.optString(Consts.DAZOO_PROGRAM_RUNTIME));

		JSONArray jsonTags = jsonProgram.optJSONArray(Consts.DAZOO_PROGRAM_TAGS);
		if (jsonTags != null) {
			ArrayList<String> tags = new ArrayList<String>();
			for (int k = 0; k < jsonTags.length(); k++) {
				tags.add(jsonTags.getString(k));
			}
			program.setTags(tags);
		}

		JSONObject seasonJSON = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_SEASON);
		program.setSeason(parseSeason(seasonJSON));
		//if (temp.length() > 0) program.setSeason(temp);
		//else program.setSeason("");

		JSONObject seriesJSON = jsonProgram.optJSONObject(Consts.DAZOO_PROGRAM_SERIES);
		program.setSeries(parseSeries(seriesJSON));
		
		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_EPISODE);
		if (temp.length() > 0) program.setEpisode(temp);
		else program.setEpisode("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_DESCRIPTION);
		if (temp.length() > 0) program.setDescription(temp);
		else program.setDescription("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SYNOPSIS_SHORT);
		if (temp.length() > 0) program.setSynopsisShort(temp);
		else program.setSynopsisShort("");

		temp = jsonProgram.optString(Consts.DAZOO_PROGRAM_SYNOPSISS_LONG);
		if (temp.length() > 0) program.setSynopsisLong(temp);
		else program.setSynopsisShort("");

		return program;
	}
	
	public Series parseSeries(JSONObject jsonSeries) throws Exception{
		Series series = new Series();
		return series;
	}

	public Season parseSeason(JSONObject jsonSeason) throws Exception{
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
}
