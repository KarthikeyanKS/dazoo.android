
package com.mitv.models.gson;



import java.io.Serializable;

import com.mitv.interfaces.GSONDataFieldValidation;



public class UserFieldsDataJSON
	implements GSONDataFieldValidation, Serializable
{
	private static final long serialVersionUID = 5838874085556849944L;
	
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
	public UserFieldsDataJSON()
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



	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (userId != null && userId.length() > 0 &&
								  email != null && email.length() > 0 &&
								  firstName != null && firstName.length() > 0 &&
								  lastName != null && lastName.length() > 0);

		return areFieldsValid;
	}
}
