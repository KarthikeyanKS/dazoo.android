package com.mitv.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitv.Consts;
import com.mitv.SecondScreenApplication;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class JSONUtilities {

	private static final String	TAG	= "JSONUtilities";

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
						output.add(channelIdJSON.getString(Consts.API_CHANNEL_ID));

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
						output.add(channelIdJSON.getString(Consts.API_CHANNEL_ID));

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
