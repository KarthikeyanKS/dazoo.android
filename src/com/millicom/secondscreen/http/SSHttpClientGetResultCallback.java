package com.millicom.secondscreen.http;


public abstract class SSHttpClientGetResultCallback implements SSHttpClientCallback <SSHttpClientGetResult> {

	public SSHttpClientGetResult onHandleHttpGetResultInBackground(SSHttpClientGetResult aHttpClientGetResult) {
	
		return aHttpClientGetResult;
	}
	
	public abstract void onHttpGetResultFinal(SSHttpClientGetResult aHttpClientGetResult);

}

