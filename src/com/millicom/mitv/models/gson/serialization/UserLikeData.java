
package com.millicom.mitv.models.gson.serialization;



public class UserLikeData
{
	private String likeType;
	private String entityId;
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLikeData()
	{}


	public String getLikeType() {
		return likeType;
	}


	public void setLikeType(String likeType) {
		this.likeType = likeType;
	}


	public String getEntityId() {
		return entityId;
	}


	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
}
