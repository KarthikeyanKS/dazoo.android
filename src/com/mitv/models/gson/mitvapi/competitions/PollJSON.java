
package com.mitv.models.gson.mitvapi.competitions;



public class PollJSON 
{
	@SuppressWarnings("unused")
	private static final String TAG = PollVoteJSON.class.getName();
	
	
	private String pollId;
	private String contentType;
	private String contentId;
	private int voteOptions;
	private boolean isClosed;
	
	
	public PollJSON(){}

	

	public String getPollId()
	{
		return pollId;
	}


	
	public String getContentType() 
	{
		return contentType;
	}


	
	public String getContentId() 
	{
		return contentId;
	}


	
	public int getVoteOptions() 
	{
		return voteOptions;
	}


	public boolean isClosed() 
	{
		return isClosed;
	}
}
