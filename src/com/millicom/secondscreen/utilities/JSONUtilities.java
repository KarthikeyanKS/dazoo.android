package com.millicom.secondscreen.utilities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtilities {

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
		return jsonArray.toString();
	}
}
