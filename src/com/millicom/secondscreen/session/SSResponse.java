package com.millicom.secondscreen.session;

import org.json.JSONObject;

import android.util.Log;

public class SSResponse {

	private static final String TAG = "SSResponse";
	
	public static final int		HTTP_STATUS_OK							= 200;
	public static final int		HTTP_STATUS_BAD_REQUEST					= 400;
	public static final int		HTTP_STATUS_FAILED						= HTTP_STATUS_BAD_REQUEST;

	public static final int		RESPONSE_CODE_NONE						= 0;											// This is assumed to mean OK too
	public static final int		RESPONSE_CODE_OK						= 1;											// This seems to be OK now
	public static final int		RESPONSE_CODE_FAILED					= -1;											// General internal error, exception and unexpected failures
	public static final int		RESPONSE_CODE_UNKNOWN					= RESPONSE_CODE_FAILED - 1;						// No response code found
	
	private static final String	JSON_CODE_VALUE_NAME					= "code";
	private static final String	JSON_HTTP_STATUS_VALUE_NAME				= "httpStatus";
	private static final String	JSON_MESSAGE_VALUE_NAME					= "message";
	private static final String	JSON_SUCCESS_VALUE_NAME					= "success";

	private int					mHttpStatus								= HTTP_STATUS_FAILED;
	private int					mCode									= RESPONSE_CODE_FAILED;

	public SSResponse() {
	}

	public SSResponse(JSONObject jsonObject) {
		parseResponse(jsonObject);

	}

	public SSResponse(int code) {
		setCode(code);
	}

	public SSResponse(int httpStatus, int code) {
		setHttpStatus(httpStatus);
		setCode(code);
	}

	public int getHttpStatus() {
		return mHttpStatus;
	}

	public int getCode() {
		return mCode;
	}

	public boolean isSuccess() {
		return (mHttpStatus == HTTP_STATUS_OK) && ((mCode == RESPONSE_CODE_OK) || (mCode == RESPONSE_CODE_NONE));
	}

	public void setFailed() {
		mHttpStatus = HTTP_STATUS_FAILED;
		mCode = RESPONSE_CODE_FAILED;
	}

	public void setHttpStatus(int httpStatus) {
		Log.d(TAG, "Http status is :" + httpStatus);
		mHttpStatus = httpStatus;
	}

	public void setCode(int code) {
		Log.d(TAG, "Response code is :" + code);
		mCode = code;
	}

	public void setSucceeded() {
		mHttpStatus = HTTP_STATUS_OK;
		mCode = RESPONSE_CODE_OK;
	}

	protected void parseResponse(JSONObject jsonObject) {
		try {
			Log.d(TAG, "Get response code");
			// Assume response code unknown, that is no response code in json
			setCode(RESPONSE_CODE_UNKNOWN);
			// Get the code
			setCode(jsonObject.getInt(JSON_CODE_VALUE_NAME));
			Log.d(TAG, JSON_CODE_VALUE_NAME + " = " + getCode());

			// If we have no code assume OK
			if (getCode() == RESPONSE_CODE_NONE) {
				setCode(RESPONSE_CODE_OK);
			}

			try {
				// Get the http status
				setHttpStatus(jsonObject.getInt(JSON_HTTP_STATUS_VALUE_NAME));
				Log.d(TAG, JSON_HTTP_STATUS_VALUE_NAME + " = " + getHttpStatus());
			} catch (Exception e) {
				// This is ok, not all requests has a http status
			}
			
			// If it indicates success
			if (jsonObject.getBoolean(JSON_SUCCESS_VALUE_NAME)) {
				// Make sure the codes indicate success as well
				setSucceeded();
			} else {
				Log.d(TAG, JSON_MESSAGE_VALUE_NAME + " = " + jsonObject.getString(JSON_MESSAGE_VALUE_NAME));
			}
		} catch (Exception e) {
			// This is ok, not all requests has a response code
		}
	}
}
