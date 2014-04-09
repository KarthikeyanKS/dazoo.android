
package com.mitv.models.gson.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.objects.mitvapi.ProfileImage;
import com.mitv.models.objects.mitvapi.UserFieldsData;



public class UserLoginDataJSON
	implements GSONDataFieldValidation
{
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
