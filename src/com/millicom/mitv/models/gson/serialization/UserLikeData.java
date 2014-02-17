
package com.millicom.mitv.models.gson.serialization;

import com.millicom.mitv.enums.ContentTypeEnum;



public class UserLikeData
{
	private ContentTypeEnum likeType;
	private String entityId;
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLikeData()
	{}


	
	public ContentTypeEnum getLikeType() {
		return likeType;
	}



	public void setLikeType(ContentTypeEnum likeType) {
		this.likeType = likeType;
	}



	public String getEntityId() {
		return entityId;
	}


	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
}
