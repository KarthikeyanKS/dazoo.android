package com.millicom.seconscreen.http;

import java.io.InputStream;

import android.util.Log;

public class SSHttpRawDataClient<T_Result> extends SSHttpClient<T_Result> {

	private final String	TAG					= "HttpRawDataClient";

	private String			mRawDataEncoding	= null;

	// Set this to use a custom encoding
	public void setRawDataEncoding(String aEncoding) {
		mRawDataEncoding = aEncoding;
	}

	@Override
	protected void getResponseFromStream(SSHttpClientGetResult aHttpClientGetResult, InputStream aResponseStream) throws Exception {

		Log.d(TAG, "Get response stream as string");

		String response;

		// If a data encoding has been specified
		if (mRawDataEncoding != null) {

			// Get response stream as a string with the specified encoding
			response = NetworkUtils.convertStreamToString(aResponseStream, mRawDataEncoding);

		} else {

			// Get response stream as a string
			response = NetworkUtils.convertStreamToString(aResponseStream);
		}

		// Set raw data in result
		aHttpClientGetResult.setRawData(response);

	}

}
