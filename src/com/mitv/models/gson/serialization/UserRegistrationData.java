
package com.mitv.models.gson.serialization;






public class UserRegistrationData
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	

	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserRegistrationData()
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



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}