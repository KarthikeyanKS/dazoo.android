package com.millicom.secondscreen.http;


public  interface SSHttpClientCallback <T_Result> {

	public T_Result onHandleHttpGetResultInBackground(SSHttpClientGetResult aHttpClientGetResult);
	public void onHttpGetResultFinal(T_Result aResult);

}

