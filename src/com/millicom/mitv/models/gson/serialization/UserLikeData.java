
package com.millicom.mitv.models.gson.serialization;



import com.millicom.mitv.enums.LikeTypeRequestEnum;



public class UserLikeData
{
	private LikeTypeRequestEnum likeType;
	private String entityId;
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLikeData()
	{}


	
	public LikeTypeRequestEnum getLikeType() {
		return likeType;
	}



	public void setLikeType(LikeTypeRequestEnum likeType) {
		this.likeType = likeType;
	}



	public String getEntityId() {
		return entityId;
	}


	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
}
