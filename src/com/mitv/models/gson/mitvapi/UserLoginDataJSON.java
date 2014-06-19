
package com.mitv.models.gson.mitvapi;



import android.util.Log;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.objects.mitvapi.ProfileImage;
import com.mitv.models.objects.mitvapi.UserFieldsData;



public class UserLoginDataJSON
	implements GSONDataFieldValidation
{
	private static final String TAG = UserLoginDataJSON.class.getName();
	
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	protected String token;
	protected UserFieldsData user;
	protected ProfileImage profileImage;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLoginDataJSON(){}
	
	
	
	public String getToken() 
	{
		if(token == null)
		{
			token = "";
			
			Log.w(TAG, "token is null");
		}
		
		return token;
	}


	
	public UserFieldsData getUser() 
	{
		if(user == null)
		{
			user = new UserFieldsData();
			
			Log.w(TAG, "user is null");
		}
		
		return user;
	}

	
	
	public ProfileImage getProfileImage() 
	{
		if(profileImage == null)
		{
			profileImage = new ProfileImage();
			
			Log.w(TAG, "profileImage is null");
		}
		
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
