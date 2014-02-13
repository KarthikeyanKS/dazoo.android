package com.mitv.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitv.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class Program implements Parcelable {

	private String				programId;
	private String				programType;
	private String				title;
	private String				synopsisShort;
	private String				synopsisLong;

	// private String posterSUrl;
	// private String posterMUrl;
	// private String posterLUrl;

	private String				landSUrl;
	private String				landMUrl;
	private String				landLUrl;
	private String				portSUrl;
	private String				portMUrl;
	private String				portLUrl;

	private ArrayList<String>	tags	= new ArrayList<String>();
	private ArrayList<Credit>	credits	= new ArrayList<Credit>();
	// specific for programType = "TV_EPISODE"
	private int					episodeNumber;
	private Season				season;
	private Series				series;
	// specific for programType = "MOVIE"
	private int					year;
	private String				genre;
	// specific for programType = "SPORT"
	private SportType			sportType;
	private String				tournament;
	// specific for programType = "OTHER"
	private String				category;

	public Program() {
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramId() {
		return this.programId;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getProgramType() {
		return this.programType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setSynopsisShort(String synopsisShort) {
		this.synopsisShort = synopsisShort;
	}

	public String getSynopsisShort() {
		return this.synopsisShort;
	}

	public void setSynopsisLong(String synopsisLong) {
		this.synopsisLong = synopsisLong;
	}

	public String getSynopsisLong() {
		return this.synopsisLong;
	}
	
	public Program(JSONObject jsonProgram) {
		this.setProgramId(jsonProgram.optString(Consts.PROGRAM_ID));

		String programType = jsonProgram.optString(Consts.PROGRAM_TYPE);
		this.setProgramType(programType);

		String temp = jsonProgram.optString(Consts.PROGRAM_TITLE);
		if (temp.length() > 0) {
			this.setTitle(temp);
		} else {
			this.setTitle("");
		}

		temp = jsonProgram.optString(Consts.PROGRAM_SYNOPSIS_SHORT);
		if (temp.length() > 0) this.setSynopsisShort(temp);
		else this.setSynopsisShort("");

		temp = jsonProgram.optString(Consts.PROGRAM_SYNOPSISS_LONG);
		if (temp.length() > 0) this.setSynopsisLong(temp);
		else this.setSynopsisShort("");

		// JSONObject jsonPoster = jsonProgram.optJSONObject(Consts.PROGRAM_POSTER);
		// if (jsonPoster != null) {
		// program.setPosterSUrl(jsonPoster.optString(Consts.IMAGE_SMALL));
		// program.setPosterMUrl(jsonPoster.optString(Consts.IMAGE_MEDIUM));
		// program.setPosterLUrl(jsonPoster.optString(Consts.IMAGE_LARGE));
		// }

		JSONObject jsonImages = jsonProgram.optJSONObject(Consts.PROGRAM_IMAGES);
		if (jsonImages != null) {

			// landscape
			JSONObject landscape = jsonImages.optJSONObject(Consts.IMAGE_TYPE_LANDSCAPE);
			this.setLandSUrl(landscape.optString(Consts.IMAGE_SMALL));
			this.setLandMUrl(landscape.optString(Consts.IMAGE_MEDIUM));
			this.setLandLUrl(landscape.optString(Consts.IMAGE_LARGE));

			// portrait
			JSONObject portrait = jsonImages.optJSONObject(Consts.IMAGE_TYPE_PORTRAIT);
			this.setPortSUrl(portrait.optString(Consts.IMAGE_SMALL));
			this.setPortMUrl(portrait.optString(Consts.IMAGE_MEDIUM));
			this.setPortLUrl(portrait.optString(Consts.IMAGE_LARGE));
		}

		JSONArray jsonTags = jsonProgram.optJSONArray(Consts.PROGRAM_TAGS);
		if (jsonTags != null) {
			ArrayList<String> tags = new ArrayList<String>();
			for (int k = 0; k < jsonTags.length(); k++) {
				try {
					tags.add(jsonTags.getString(k));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			this.setTags(tags);
		}

		JSONArray jsonCredits = jsonProgram.optJSONArray(Consts.PROGRAM_CREDITS);
		if (jsonCredits != null) {
			ArrayList<Credit> credits = new ArrayList<Credit>();
			for (int k = 0; k < jsonCredits.length(); k++) {
				JSONObject jsonCredit;
				try {
					jsonCredit = jsonCredits.getJSONObject(k);
					Credit credit = new Credit(jsonCredit);
					credits.add(credit);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			this.setCredits(credits);
		}

		if ((Consts.PROGRAM_TYPE_TV_EPISODE).equals(programType)) {

			int tempInt = jsonProgram.optInt(Consts.PROGRAM_EPISODE);
			if (temp.length() > 0) {
				this.setEpisodeNumber(tempInt);
			} else {
				this.setEpisodeNumber(-1);
			}
			JSONObject seasonJSON = jsonProgram.optJSONObject(Consts.PROGRAM_SEASON);
			Season season = new Season(seasonJSON);
			this.setSeason(season);
			JSONObject seriesJSON = jsonProgram.optJSONObject(Consts.PROGRAM_SERIES);
			if (seriesJSON != null) {
				Series series = new Series(seriesJSON);
				this.setSeries(series);
			}
		} else if ((Consts.PROGRAM_TYPE_MOVIE).equals(programType)) {

			this.setYear(jsonProgram.optInt(Consts.PROGRAM_YEAR));
			this.setGenre(jsonProgram.optString(Consts.PROGRAM_GENRE));
		} else if ((Consts.PROGRAM_TYPE_SPORT).equals(programType)) {
			this.setTournament(jsonProgram.optString(Consts.PROGRAM_TOURNAMENT));
			JSONObject sportTypeJSON = jsonProgram.optJSONObject(Consts.PROGRAM_SPORTTYPE);
			if (sportTypeJSON != null) {
				SportType sportType = new SportType(sportTypeJSON);
				this.setSportType(sportType);
			}
		} else if ((Consts.PROGRAM_TYPE_OTHER).equals(programType)) {
			this.setCategory(jsonProgram.optString(Consts.PROGRAM_CATEGORY));
		}

	}
 
	// public void setPosterSUrl(String posterSUrl){
	// this.posterSUrl = posterSUrl;
	// }
	//
	// public String getPosterSUrl(){
	// return posterSUrl;
	// }
	//
	// public void setPosterMUrl(String posterMUrl){
	// this.posterMUrl = posterMUrl;
	// }
	//
	// public String getPosterMUrl(){
	// return this.posterMUrl;
	// }
	//
	// public void setPosterLUrl(String posterLUrl){
	// this.posterLUrl = posterLUrl;
	// }
	//
	// public String getPosterLUrl(){
	// return this.posterLUrl;
	// }

	public void setLandSUrl(String url) {
		this.landSUrl = url;
	}

	public String getLandSUrl() {
		return this.landSUrl;
	}

	public void setLandMUrl(String url) {
		this.landMUrl = url;
	}

	public String getLandMUrl() {
		return this.landMUrl;
	}

	public void setLandLUrl(String url) {
		this.landLUrl = url;
	}

	public String getLandLUrl() {
		return this.landLUrl;
	}

	public void setPortSUrl(String url) {
		this.portSUrl = url;
	}

	public String getPortSUrl() {
		return this.portSUrl;
	}

	public void setPortMUrl(String url) {
		this.portMUrl = url;
	}

	public String getPortMUrl() {
		return this.portMUrl;
	}

	public void setPortLUrl(String url) {
		this.portSUrl = url;
	}

	public String getPortLUrl() {
		return this.portSUrl;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public ArrayList<String> getTags() {
		return this.tags;
	}

	public void setCredits(ArrayList<Credit> credits) {
		this.credits = credits;
	}

	public ArrayList<Credit> getCredits() {
		return this.credits;
	}

	public void setEpisodeNumber(int episodeNumber) {
		this.episodeNumber = episodeNumber;
	}

	public int getEpisodeNumber() {
		return this.episodeNumber;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public Series getSeries() {
		return this.series;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return this.year;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getGenre() {
		return this.genre;
	}

	public void setSportType(SportType sportType) {
		this.sportType = sportType;
	}

	public SportType getSportType() {
		return this.sportType;
	}

	public void setTournament(String tournament) {
		this.tournament = tournament;
	}

	public String getTournament() {
		return this.tournament;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return this.category;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(programId);
		dest.writeString(programType);
		dest.writeString(title);
		dest.writeString(synopsisShort);
		dest.writeString(synopsisLong);
		// dest.writeString(posterSUrl);
		// dest.writeString(posterMUrl);
		// dest.writeString(posterLUrl);
		dest.writeString(landSUrl);
		dest.writeString(landMUrl);
		dest.writeString(landLUrl);
		dest.writeString(portSUrl);
		dest.writeString(portMUrl);
		dest.writeString(portLUrl);

		dest.writeSerializable(tags);
		// dest.writeTypedList(credits);
		dest.writeInt(episodeNumber);
		dest.writeParcelable(season, flags);
		dest.writeParcelable(series, flags);
		dest.writeInt(year);
		dest.writeString(genre);
		dest.writeParcelable(sportType, flags);
		dest.writeString(tournament);
		dest.writeString(category);
	}

	public Program(Parcel in) {
		programId = in.readString();
		programType = in.readString();
		title = in.readString();
		synopsisShort = in.readString();
		synopsisLong = in.readString();
		// posterSUrl = in.readString();
		// posterMUrl = in.readString();
		// posterLUrl = in.readString();
		landSUrl = in.readString();
		landMUrl = in.readString();
		landLUrl = in.readString();
		portSUrl = in.readString();
		portMUrl = in.readString();
		portLUrl = in.readString();

		tags = (ArrayList<String>) in.readSerializable();
		// credits = in.readArrayList(Credit.class.getClassLoader());
		episodeNumber = in.readInt();
		season = in.readParcelable(Season.class.getClassLoader());
		series = in.readParcelable(Series.class.getClassLoader());
		year = in.readInt();
		genre = in.readString();
		sportType = in.readParcelable(SportType.class.getClassLoader());
		tournament = in.readString();
		category = in.readString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Program) {
			Program other = (Program) o;
			if (getProgramId() != null && other.getProgramId() != null && getProgramId().equals(other.getProgramId())) {
				return true;
			}
		}
		return false;
	}

	public static final Parcelable.Creator<Program>	CREATOR	= new Parcelable.Creator<Program>() {
		public Program createFromParcel(Parcel in) {
			return new Program(in);
		}

		public Program[] newArray(int size) {
			return new Program[size];
		}
	};

	@Override
	public String toString() {
		return "Id: " + programId + "\n programType: " + programType + "\n title: " + title + "\n year: "
				+ year + "\n season: " + season + "\n synopsisShort" + synopsisShort + "\n synopsisLong" + synopsisLong;
	}
}
