package com.millicom.secondscreen.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
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

			// get the overall channel info
			Guide guide = new Guide();
			guide.setHref(jsonGuide.optString("href"));
			guide.setId(jsonGuide.optString("channelId"));
			guide.setName(jsonGuide.optString("name"));
			
			JSONObject logosJson = jsonGuide.optJSONObject("logo");
			if(logosJson != null){
				guide.setLogoSHref(logosJson.optString("small"));
				guide.setLogoMHref(logosJson.optString("medium"));
				guide.setLogoLHref(logosJson.optString("large"));
			}
			
			Log.d(TAG, "Name:" + guide.getName());
			
			JSONArray broadcastsJson = jsonGuide.optJSONArray("broadcasts");
			Log.d(TAG,"NUMBER OF BROADCASTS:" + broadcastsJson.length());
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
		programType.setId(jsonProgramType.optString("id"));
		programType.setName(jsonProgramType.optString("name"));
		programType.setAlias(jsonProgramType.optString("alias"));
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

	public ArrayList<TvDate> parseDates(JSONArray mainArray) throws Exception {
		ArrayList<TvDate> tvDates = new ArrayList<TvDate>();

		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject jsonTvDate = mainArray.getJSONObject(i);

			TvDate tvDate = new TvDate();
			tvDate.setId(jsonTvDate.optString("id"));
			tvDate.setName(jsonTvDate.optString("name"));
			tvDate.setAlias(jsonTvDate.optString("alias"));
			tvDate.setDate(jsonTvDate.optString("date"));

			tvDates.add(tvDate);
		}
		return tvDates;
	}

	public Channel parseChannel(JSONObject jsonChannel) throws Exception {
		Channel channel = new Channel();
		channel.setId(jsonChannel.optString("channelId"));
		channel.setName(jsonChannel.optString("name"));

		JSONObject jsonPoster = jsonChannel.getJSONObject("poster");
		if (jsonPoster != null) {
			channel.setLogoSUrl(jsonPoster.optString("small"));
			channel.setLogoMUrl(jsonPoster.optString("medium"));
			channel.setLogoLUrl(jsonPoster.optString("large"));
		}
		return channel;
	}

	public Broadcast parseBroadcast(JSONObject jsonBroadcast) throws Exception {
		Broadcast broadcast = new Broadcast();
		broadcast.setBroadcastId(jsonBroadcast.optString("broadcastId"));
		broadcast.setBeginTime(jsonBroadcast.optString("beginTime"));
		broadcast.setEndTime(jsonBroadcast.optString("endTime"));

		JSONObject jsonChannel = jsonBroadcast.optJSONObject("channel");
		if (jsonChannel != null) {
	
			//broadcast.setChannel(parseChannel(jsonChannel));
			broadcast.setChannelUrl(jsonChannel.optString("href"));
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject("program");
		if (jsonProgram != null) {
			broadcast.setProgram(parseProgram(jsonProgram));
		}
		return broadcast;
	}

	public Program parseProgram(JSONObject jsonProgram) throws Exception {
		Program program = new Program();
		program.setProgramId(jsonProgram.optString("programId"));
		program.setProgramTypeId(jsonProgram.optString("programTypeId"));
		program.setTitle(jsonProgram.optString("title"));

		JSONObject jsonPoster = jsonProgram.optJSONObject("poster");
		if (jsonPoster != null) {
			program.setPosterSUrl(jsonPoster.optString("small"));
			program.setPosterMUrl(jsonPoster.optString("medium"));
			program.setPosterLUrl(jsonPoster.optString("large"));
		}

		program.setCast(jsonProgram.optString("cast"));
		program.setYear(jsonProgram.optString("year"));
		program.setRuntime(jsonProgram.optString("runtime"));

		JSONArray jsonGenres = jsonProgram.optJSONArray("genres");
		if (jsonGenres != null) {
			ArrayList<String> genres = new ArrayList<String>();
			for (int k = 0; k < jsonGenres.length(); k++) {
				genres.add(jsonGenres.getString(k));
			}
			program.setGenres(genres);
		}

		program.setSeason(jsonProgram.optString("season"));
		program.setEpisode(jsonProgram.optString("episode"));
		program.setDescription(jsonProgram.optString("description"));
		return program;
	}

	public TvDate parseTvDate(JSONObject jsonTvDate) throws Exception {
		TvDate tvDate = new TvDate();
		tvDate.setId(jsonTvDate.optString("id"));
		tvDate.setName(jsonTvDate.optString("name"));
		tvDate.setAlias(jsonTvDate.optString("alias"));
		tvDate.setDate(jsonTvDate.optString("date"));
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
