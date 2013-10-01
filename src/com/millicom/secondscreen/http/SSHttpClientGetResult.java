package com.millicom.secondscreen.http;

import org.json.JSONArray;
import org.json.JSONObject;

public class SSHttpClientGetResult {

	public static final int	HTTP_STATUS_OK			= 200;
	public static final int	HTTP_STATUS_BAD_REQUEST	= 400;
	public static final int	HTTP_STATUS_FAILED		= HTTP_STATUS_BAD_REQUEST;

	private JSONObject		mJsonObject				= null;
	private JSONArray		mJsonArray				= null;
	private int				mResult					= HTTP_STATUS_FAILED;
	private String			mUri					= "";
	private String			mRawData				= "";

	public SSHttpClientGetResult() {
	}

	public SSHttpClientGetResult(String aUri, JSONObject aJsonObject, int aResult) {
		setJson(aJsonObject);
		setResult(aResult);
		setUri(aUri);
	}

	public JSONObject getJson() {
		return mJsonObject;
	}

	public int getResult() {
		return mResult;
	}

	public String getUri() {
		return mUri;
	}

	public String getRawData() {
		return mRawData;
	}

	public boolean isSuccess() {

		return mResult == HTTP_STATUS_OK;
	}

	public void setJson(JSONObject aJsonObject) {
		mJsonObject = aJsonObject;
	}

	public void setJsonArray(JSONArray jsonArray) {
		mJsonArray = jsonArray;
	}

	public JSONArray getJsonArray() {
		return mJsonArray;
	}

	public void setResult(int aResult) {
		mResult = aResult;
	}

	public void setUri(String aUri) {
		mUri = aUri;
	}

	public void setRawData(String raw) {
		this.mRawData = raw;
	}
}
