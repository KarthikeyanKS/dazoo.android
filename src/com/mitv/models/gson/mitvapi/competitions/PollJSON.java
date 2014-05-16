
package com.mitv.models.gson.mitvapi.competitions;



public class PollJSON 
{
	protected String pollId;
	protected String contentType;
	protected String contentId;
	protected int voteOptions;
	protected boolean isClosed;
	
	
	public PollJSON(){}

	

	public String getPollId() {
		return pollId;
	}


	public String getContentType() {
		return contentType;
	}


	public String getContentId() {
		return contentId;
	}


	public int getVoteOptions() {
		return voteOptions;
	}


	public boolean isClosed() {
		return isClosed;
	}
}
