package com.mitv;



import org.json.JSONObject;

import android.content.Context;



public class AuthenticationService 
{
	private static final String	TAG	= "AuthenticationService";

	
	//TODO fix this... needed?
	public static boolean storeUserInformation(Context context, JSONObject jsonString) 
	{
//		String userDataString = jsonString.optString(Consts.API_USER);
//		
//		String profileImageString = jsonString.optString(Consts.API_PROFILEIMAGE);
//
//		if (profileImageString != null && TextUtils.isEmpty(profileImageString) != true) 
//		{
//			JSONObject avatarUrlJSON;
//			
//			try 
//			{
//				avatarUrlJSON = new JSONObject(profileImageString);
//				
//				if (avatarUrlJSON != null) 
//				{
//					String avatarUrl = avatarUrlJSON.optString(Consts.API_URL);
//					
//					((SecondScreenApplication) context.getApplicationContext()).setUserAvatarUrl(avatarUrl);
//					
//					Log.d(TAG, "User Avatar Url: " + avatarUrl);
//				}
//			} 
//			catch (JSONException e)
//			{
//				e.printStackTrace();
//			}
//		}
//
//		if (userDataString != null && TextUtils.isEmpty(userDataString) != true)
//		{
//			JSONObject userJSON;
//			
//			try
//			{
//				userJSON = new JSONObject(userDataString);
//				Log.d(TAG, "userJSON:" + userJSON);
//
//				String userFirstName = userJSON.optString(Consts.API_FIRSTNAME);
//				((SecondScreenApplication) context.getApplicationContext()).setUserFirstName(userFirstName);
//				Log.d(TAG, "First Name: " + userFirstName + " is saved");
//
//				String userLastName = userJSON.optString(Consts.API_LASTNAME);
//				((SecondScreenApplication) context.getApplicationContext()).setUserLastName(userLastName);
//				Log.d(TAG, "Last Name: " + userLastName + " is saved");
//
//				String userId = userJSON.optString(Consts.API_USER_ID);
//				((SecondScreenApplication) context.getApplicationContext()).setUserId(userId);
//				Log.d(TAG, "User Id: " + userId + " is saved");
//
//				boolean userExistingFlag = userJSON.optBoolean(Consts.API_CREATED);
//				((SecondScreenApplication) context.getApplicationContext()).setUserExistringFlag(userExistingFlag);
//				Log.d(TAG, "User login first time: " + userExistingFlag);
//
//			} 
//			catch (JSONException e)
//			{
//				e.printStackTrace();
//			}
//			
//			return true;
//		} 
//		else
//		{
//			return false;
//		}
		return false;
	}
}
