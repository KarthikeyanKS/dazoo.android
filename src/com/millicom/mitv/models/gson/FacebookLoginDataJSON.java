
package com.millicom.mitv.models.gson;



import com.millicom.mitv.interfaces.GSONDataFieldValidation;



public class FacebookLoginDataJSON 
	implements GSONDataFieldValidation
{
	protected String id;
	protected String first_name;
	protected String last_name;
	protected String name;
	protected String gender;
	protected String email;
	protected String link;
	
	
	
	public FacebookLoginDataJSON()
	{}
	
	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	public String getId() {
		return id;
	}



	public String getFirst_name() {
		return first_name;
	}



	public String getLast_name() {
		return last_name;
	}



	public String getName() {
		return name;
	}



	public String getGender() {
		return gender;
	}



	public String getEmail() {
		return email;
	}



	public String getLink() {
		return link;
	}
}