
package com.mitv.models.gson.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;

import android.util.Log;



public class FacebookLoginDataJSON
	extends BaseObjectJSON
	implements GSONDataFieldValidation
{
	private static final String TAG = FacebookLoginDataJSON.class.getName();
	
	
	private String id;
	private String first_name;
	private String last_name;
	private String name;
	private String gender;
	private String email;
	private String link;
	
	
	
	public FacebookLoginDataJSON(){}
	
	
	
	public String getId() 
	{
		if(id == null)
		{
			id = "";
			
			Log.w(TAG, "id is null");
		}
		
		return id;
	}


	
	public String getFirstName() 
	{
		if(first_name == null)
		{
			first_name = "";
			
			Log.w(TAG, "first_name is null");
		}
		
		return first_name;
	}



	public String getLastName()
	{
		if(last_name == null)
		{
			last_name = "";
			
			Log.w(TAG, "last_name is null");
		}
		
		return last_name;
	}



	public String getName() 
	{
		if(name == null)
		{
			name = "";
			
			Log.w(TAG, "name is null");
		}
		
		return name;
	}



	public String getGender()
	{
		if(gender == null)
		{
			gender = "";
			
			Log.w(TAG, "gender is null");
		}
		
		return gender;
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



	public String getLink() 
	{
		if(link == null)
		{
			link = "";
			
			Log.w(TAG, "link is null");
		}
		
		return link;
	}
	
	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (getId().isEmpty() == false &&
								  getFirstName().isEmpty() == false &&
								  getLastName().isEmpty() == false &&
								  getName().isEmpty() == false &&
								  getGender().isEmpty() == false &&
								  getEmail().isEmpty() == false &&
								  getLink().isEmpty() == false);

		return areFieldsValid;
	}
}