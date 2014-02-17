
package com.millicom.mitv.models.gson.serialization;



public class UserFacebookTokenData 
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String facebookToken;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserFacebookTokenData()
	{}



	public String getFacebookToken() {
		return facebookToken;
	}



	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}
}