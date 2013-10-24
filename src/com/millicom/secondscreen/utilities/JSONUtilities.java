package com.millicom.secondscreen.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class JSONUtilities {

	private static final String	TAG	= "JSONUtilities";
	
	public static boolean storeUserInformation(Context context, String jsonString) {
		if (jsonString != null && TextUtils.isEmpty(jsonString) != true) {
			// if (jsonString != null && jsonString.isEmpty() != true) {
			JSONObject userJSON;
			try {
				userJSON = new JSONObject(jsonString);

				String userFirstName = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME);
				((SecondScreenApplication) context.getApplicationContext()).setUserFirstName(userFirstName);
				Log.d(TAG, "First Name: " + userFirstName + " is saved");

				String userLastName = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_LASTNAME);
				((SecondScreenApplication) context.getApplicationContext()).setUserLastName(userLastName);
				Log.d(TAG, "Last Name: " + userLastName + " is saved");

				String userId = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER_ID);
				((SecondScreenApplication) context.getApplicationContext()).setUserId(userId);
				Log.d(TAG, "User Id: " + userId + " is saved");

				boolean userExistingFlag = userJSON.optBoolean(Consts.MILLICOM_SECONDSCREEN_API_CREATED);
				((SecondScreenApplication) context.getApplicationContext()).setUserExistringFlag(userExistingFlag);
				Log.d(TAG, "User login first time: " + userExistingFlag);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		} else return false;
	}

	public static final String createJSONArrayWithOneJSONObjectType(String jsonKey, ArrayList<String> jsonValues) {
		JSONArray jsonArray = new JSONArray();

		for (int i = 0; i < jsonValues.size(); i++) {
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put(jsonKey, jsonValues.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsonArray.put(jsonObj);
		}
		Log.d(TAG, "JSON Array: " + jsonArray.toString());

		return jsonArray.toString();
	}

	public static final String createJSONArrayWithOneJSONObjectType(String jsonKey, LinkedHashSet<String> jsonValues) {
		JSONArray jsonArray = new JSONArray();

		Iterator iterator = jsonValues.iterator();
		while (iterator.hasNext()) {
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put(jsonKey, iterator.next());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsonArray.put(jsonObj);

		}

		Log.d(TAG, "JSON Array: " + jsonArray.toString());

		return jsonArray.toString();
	}

	public static final JSONObject createJSONObjectWithKeysValues(List<String> keys, List<String> values) {
		JSONObject holder = new JSONObject();
		for (int i = 0; i < keys.size(); i++) {
			try {
				holder.put(keys.get(i), values.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG, "JSON holder:" + holder.toString());

		return holder;
	}

	public static final ArrayList<String> stringWithJSONtoArrayList(String input) {
		ArrayList<String> output = new ArrayList<String>();
		//if (input != null && input.isEmpty() != true) {
		if (input != null && TextUtils.isEmpty(input) != true) {
			// // JSONObject jObj = new JSONObject(responseStr);
			JSONArray jArray;
			try {
				jArray = new JSONArray(input);
				if (jArray != null) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject channelIdJSON = jArray.getJSONObject(i);
						output.add(channelIdJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID));

						Log.d(TAG, "ChannelId json: " + channelIdJSON.toString());
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	public static final LinkedHashSet<String> stringWithJSONtoOrderedSet(String input) {
		LinkedHashSet<String> output = new LinkedHashSet<String>();
		//if (input != null && input.isEmpty() != true) {
		if(input !=null && TextUtils.isEmpty(input)!=true){
			// // JSONObject jObj = new JSONObject(responseStr);
			JSONArray jArray;
			try {
				jArray = new JSONArray(input);
				if (jArray != null) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject channelIdJSON = jArray.getJSONObject(i);
						output.add(channelIdJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID));

						Log.d(TAG, "ChannelId json: " + channelIdJSON.toString());
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return output;
	}
}
