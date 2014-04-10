package com.mitv.models.gson.disqus;



public class DisqusJSONPost 
{
	protected String parent;
	protected String likes;
	protected String forum;
	protected String thread;
	protected String isApproved;
	protected boolean isFlagged;
	protected int dislikes;
	protected String raw_message;
	protected String createdAt;
	protected String id;
	protected int numReports;
	protected boolean isDeleted;
	protected boolean isEdited;
	protected String message;
	protected boolean isSpam;
	protected boolean isHighlighted;
	protected int points;
	protected DisqusUserJSON author;
	
	
	
	public DisqusJSONPost(){}


	
	public String getParent() {
		return parent;
	}


	public String getLikes() {
		return likes;
	}


	public String getForum() {
		return forum;
	}


	public String getThread() {
		return thread;
	}


	public String getIsApproved() {
		return isApproved;
	}


	public boolean isFlagged() {
		return isFlagged;
	}


	public int getDislikes() {
		return dislikes;
	}


	public String getRaw_message() {
		return raw_message;
	}


	public String getCreatedAt() {
		return createdAt;
	}


	public String getId() {
		return id;
	}


	public int getNumReports() {
		return numReports;
	}


	public boolean isDeleted() {
		return isDeleted;
	}


	public boolean isEdited() {
		return isEdited;
	}


	public String getMessage() {
		return message;
	}


	public boolean isSpam() {
		return isSpam;
	}


	public boolean isHighlighted() {
		return isHighlighted;
	}


	public int getPoints() {
		return points;
	}


	public DisqusUserJSON getAuthor() {
		return author;
	}
}
