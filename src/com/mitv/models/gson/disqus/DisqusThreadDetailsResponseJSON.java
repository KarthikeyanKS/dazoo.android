
package com.mitv.models.gson.disqus;



import com.mitv.models.gson.disqus.base.DisqusBaseResponseJSON;



public class DisqusThreadDetailsResponseJSON 
	extends DisqusBaseResponseJSON
{
	protected String category;
	protected int reactions;
	protected String forum;
	protected String title;
	protected int dislikes;
	protected boolean isDeleted;
	protected String author;
	protected int userScore;
	protected String id;
	protected boolean isClosed;
	protected int posts;
	protected String link;
	protected int likes;
	protected String message;
	protected String ipAddress;
	protected String slug;
	protected String createdAt;
	
	
	public DisqusThreadDetailsResponseJSON(){}


	public String getForum() {
		return forum;
	}


	public String getTitle() {
		return title;
	}


	public int getDislikes() {
		return dislikes;
	}


	public boolean isDeleted() {
		return isDeleted;
	}


	public String getAuthor() {
		return author;
	}


	public int getUserScore() {
		return userScore;
	}


	public String getId() {
		return id;
	}


	public boolean isClosed() {
		return isClosed;
	}


	public int getPosts() {
		return posts;
	}


	public String getLink() {
		return link;
	}


	public int getLikes() {
		return likes;
	}


	public String getMessage() {
		return message;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public String getSlug() {
		return slug;
	}


	public String getCreatedAt() {
		return createdAt;
	}
}