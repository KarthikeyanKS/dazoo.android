package com.millicom.secondscreen.authentication;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;

public class AuthenticationService {

	private static final String TAG = "AuthenticationService";
	
	public static boolean storeUserInformation(Context context, String jsonString) {
		if (jsonString != null && TextUtils.isEmpty(jsonString) != true) {
			JSONObject userJSON;
			try {
				userJSON = new JSONObject(jsonString);
				Log.d(TAG,"userJSON:" + userJSON);

				String userFirstName = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME);
				((SecondScreenApplication) context.getApplicationContext()).setUserFirstName(userFirstName);
				Log.d(TAG, "First Name: " + userFirstName + " is saved");

				String userLastName = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_LASTNAME);
				((SecondScreenApplication) context.getApplicationContext()).setUserLastName(userLastName);
				Log.d(TAG, "Last Name: " + userLastName + " is saved");

				String userId = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER_ID);
				((SecondScreenApplication) context.getApplicationContext()).setUserId(userId);
				Log.d(TAG, "User Id: " + userId + " is saved");

				JSONObject avatarUrlJSON = userJSON.optJSONObject(Consts.MILLICOM_SECONDSCREEN_API_PROFILEIMAGE);
				if(avatarUrlJSON!=null){
					String avatarUrl = avatarUrlJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_URL);
					((SecondScreenApplication) context.getApplicationContext()).setUserAvatarUrl(avatarUrl);
					Log.d(TAG,"User Avatar Url: " + avatarUrl);
				}
				
				boolean userExistingFlag = userJSON.optBoolean(Consts.MILLICOM_SECONDSCREEN_API_CREATED);
				((SecondScreenApplication) context.getApplicationContext()).setUserExistringFlag(userExistingFlag);
				Log.d(TAG, "User login first time: " + userExistingFlag);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		} else return false;
	}
}
