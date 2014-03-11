
package com.mitv.models.gson;



import java.io.Serializable;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.ProfileImage;
import com.mitv.models.UserFieldsData;



public class UserLoginDataJSON
	implements GSONDataFieldValidation, Serializable
{
	private static final long serialVersionUID = -5672249990860974431L;
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	protected String token;
	protected UserFieldsData user;
	protected ProfileImage profileImage;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLoginDataJSON()
	{}
	
	
	public String getToken() {
		return token;
	}


	public UserFieldsData getUser() {
		return user;
	}

	
	public ProfileImage getProfileImage() {
		return profileImage;
	}


	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (token != null && token.length() > 0 &&
								  user != null && user.areDataFieldsValid());

		return areFieldsValid;
	}
}
