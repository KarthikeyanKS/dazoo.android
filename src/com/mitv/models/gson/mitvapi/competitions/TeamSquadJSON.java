package com.mitv.models.gson.mitvapi.competitions;

public class TeamSquadJSON {

	private long id;
	private long teamId;
	private long personId;
	private String person;
	private String personShort;
	private String nation;
	private long birthDay;
	private int functionType;
	private String function;
	private int shirtNumber;
	private int matches;
	private int matchesStart;
	private int goals;
	private int assists;
	private int yellowCards;
	private int redCards;
	
	public TeamSquadJSON() {}

	public long getId() {
		return id;
	}

	public long getTeamId() {
		return teamId;
	}

	public long getPersonId() {
		return personId;
	}

	public String getPerson() {
		return person;
	}
	
	public String getPersonShort() {
		personShort = "Herhalter";
		return personShort;
	}

	public String getNation() {
		return nation;
	}

	public long getBirthDay() {
		return birthDay;
	}

	public int getFunctionType() {
		return functionType;
	}

	public String getFunction() {
		return function;
	}
	
	public int getShirtNumber() {
		shirtNumber = 23;
		return shirtNumber;
	}

	public int getMatches() {
		return matches;
	}

	public int getMatchesStart() {
		return matchesStart;
	}

	public int getGoals() {
		return goals;
	}

	public int getAssists() {
		return assists;
	}

	public int getYellowCards() {
		return yellowCards;
	}

	public int getRedCards() {
		return redCards;
	}
	
	
}
