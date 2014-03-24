
package com.mitv.models.gson.serialization;



public class UserLoginDataPost
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String email;
	private String password;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLoginDataPost()
	{}
	
	
	
	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}
}