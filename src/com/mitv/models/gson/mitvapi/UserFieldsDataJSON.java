
package com.mitv.models.gson.mitvapi;



import android.util.Log;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;



public class UserFieldsDataJSON
	extends BaseObjectJSON
	implements GSONDataFieldValidation
{
	private static final String TAG = UserFieldsDataJSON.class.getName();
	
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	protected String userId;
	protected String email;
	protected String firstName;
	protected String lastName;
	protected boolean created;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserFieldsDataJSON(){}
	
	
	
	public String getUserId() 
	{
		if(userId == null)
		{
			userId = "";
			
			Log.w(TAG, "userId is null");
		}
		
		return userId;
	}



	public String getEmail() 
	{
		if(email == null)
		{
			email = "";
			
			Log.w(TAG, "email is null");
		}
		
		return email;
	}


	
	public String getFirstName() 
	{
		if(firstName == null)
		{
			firstName = "";
			
			Log.w(TAG, "firstName is null");
		}
		
		return firstName;
	}



	public String getLastName() 
	{
		if(lastName == null)
		{
			lastName = "";
			
			Log.w(TAG, "lastName is null");
		}
		
		return lastName;
	}



	public boolean isCreated() 
	{
		return created;
	}



	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (getUserId().isEmpty() == false &&
								  getEmail().isEmpty() == false &&
								  getFirstName().isEmpty() == false &&
								  lastName != null && lastName.length() > 0);

		return areFieldsValid;
	}
}
