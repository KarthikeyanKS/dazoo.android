
package com.mitv.models.gson.mitvapi.competitions;



public class PollVoteJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = PollVoteJSON.class.getName();
	
	
	private String pollId;
	private int voteSelection;
	private String voteDate;
	
	
	
	public PollVoteJSON(){}



	public String getPollId() {
		return pollId;
	}



	public int getVoteSelection() {
		return voteSelection;
	}



	public String getVoteDate() {
		return voteDate;
	}
}
