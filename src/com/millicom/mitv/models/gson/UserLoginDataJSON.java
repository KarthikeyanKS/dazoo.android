
package com.millicom.mitv.models.gson;



import java.io.Serializable;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.UserFieldsData;



public class UserLoginDataJSON
	implements GSONDataFieldValidation, Serializable
{
	private static final long serialVersionUID = -5672249990860974431L;
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	protected String token;
	protected UserFieldsData user;
	
	
	
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


	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (token != null && token.length() > 0 &&
								  user != null && user.areDataFieldsValid());

		return areFieldsValid;
	}
}
