
package com.millicom.mitv.models.gson;



public class UserLoginData
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String token;
	private UserFieldsData user;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLoginData()
	{}
	
	
	public String getToken() {
		return token;
	}



	public UserFieldsData getUser() {
		return user;
	}
}
