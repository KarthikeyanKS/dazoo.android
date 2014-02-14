
package com.millicom.mitv.models.gson;



import java.io.Serializable;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;



public class UserData 
	implements Serializable, JsonDeserializer<UserData>
{
	private String userToken;
	private String userId;
	private String email;
	private String firstName;
	private String lastName;
	private boolean created;
	
	
	
	@Override
	public UserData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
			throws JsonParseException 
	{
		return null;
	}
}
