
package com.millicom.mitv.models.gson;



public class UserFieldsData 
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String userId;
	private String email;
	private String firstName;
	private String lastName;
	private boolean created;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserFieldsData()
	{}
	
	
	
	public String getUserId() {
		return userId;
	}



	public String getEmail() {
		return email;
	}


	
	public String getFirstName() {
		return firstName;
	}



	public String getLastName() {
		return lastName;
	}


	
	public boolean isCreated() {
		return created;
	}
}
