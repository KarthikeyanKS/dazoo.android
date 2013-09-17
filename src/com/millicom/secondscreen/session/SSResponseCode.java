package com.millicom.secondscreen.session;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;


public class SSResponseCode extends SSResponse{

	public static final String	TAG							= "SSResponseCode";
	private static final String	JSON_RESPONSE_CODE_OBJ_NAME	= "responseCode";

	public SSResponseCode() {
	}

	public SSResponseCode(SSResponse response) {
		this(response.getHttpStatus(), response.getCode());
	}

	public SSResponseCode(JSONObject jsonObject) {
		JSONObject responseCode = jsonObject;
		try {
			Log.d(TAG, "Get response code json object");
			// Assume response code unknown, that is no response code object in json
			setCode(RESPONSE_CODE_UNKNOWN);
			// Get response code json object
			responseCode = jsonObject.getJSONObject(JSON_RESPONSE_CODE_OBJ_NAME);
		}
		catch (Exception e) {
			// This is ok, not all requests has a response code object
		}
		// Let parent parse response code
		parseResponse(responseCode);
	}
	
	public SSResponseCode(JSONArray jsonArray){
		JSONArray responseCode = jsonArray;
		try{
			Log.d(TAG, "Get response code json object");
			// Assume response code unknown, that is no response code object in json
			//setCode(RESPONSE_CODE_UNKNOWN);
			// Get response code json object
			//responseCode = jsonArray.getJSONArray(JSON_RESPONSE_CODE_OBJ_NAME);
			setCode(RESPONSE_CODE_OK);
		}
		catch(Exception e) {
			// This is ok, not all requests has a response code object
		}
		// Let parent parse response code
		//parseResponse(responseCode);
		setCode(RESPONSE_CODE_OK);
	}
	

	public SSResponseCode(int code) {
		setCode(code);
	}

	public SSResponseCode(int httpStatus, int code) {
		setHttpStatus(httpStatus);
		setCode(code);
	}
}
