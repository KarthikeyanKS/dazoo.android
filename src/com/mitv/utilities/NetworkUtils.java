
package com.mitv.utilities;



import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.SecondScreenApplication;



public abstract class NetworkUtils
{	
	private static final String TAG = NetworkUtils.class.getName();
	
	
	
	public static String getActiveNetworkTypeAsString()
	{
		String activeNetworkTypeAsString = "Unknown";
		
		final Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(cm != null)
		{
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			
			if(networkInfo != null)
			{
				int activeNetworkType = networkInfo.getType();
				
				switch(activeNetworkType)
				{
					case ConnectivityManager.TYPE_MOBILE:
					{
						TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
						
						activeNetworkTypeAsString = tm.getNetworkOperatorName();
						
						break;
					}
					
					case ConnectivityManager.TYPE_WIFI:
					default:
					{
						activeNetworkTypeAsString = networkInfo.getTypeName();
						break;
					}
				}
			}
		}
		
		return activeNetworkTypeAsString;
	}
	
	
	
	/* 
	 * IMPORTANT: This method will block the interface if invoked on the UI thread
	 *  
	 */
	public static Boolean isConnectedAndHostIsReachable()
	{
		return isConnected() && isHostReachable();
	}
	
	
	
	public static boolean isConnected()
    {
		final Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		boolean isConnected = false;
		
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
    	isConnected = (cm != null && 
    				   cm.getActiveNetworkInfo() != null && 
    				   cm.getActiveNetworkInfo().isConnectedOrConnecting());
    	
    	if(isConnected)
    	{
    		Log.d(TAG, "Connection: ConnectivityManager reported that a connection is active");
    	}
    	else
    	{
    		Log.d(TAG, "No connection: ConnectivityManager reported that no connection is active");
    	}
    	
    	return isConnected;
    }
	
	
	
	private static boolean isHostReachable()
	{
		boolean isHostReachable = false;
		
	    try 
	    {
	    	URL url = new URL(Constants.HOST_NAME_FOR_CONNECTIVITY_CHECK);
	    	
	    	HttpURLConnection urlc = (HttpURLConnection) (url).openConnection();
	    	urlc.setRequestProperty("User-Agent", "Test");
	    	urlc.setRequestProperty("Connection", "close");
	    	urlc.setConnectTimeout(Constants.HOST_TIMEOUT_IN_MILISECONDS_FOR_CONNECTIVITY_CHECK); 
	    	urlc.connect();
	    	
	    	isHostReachable = (urlc.getResponseCode() == 200);
	    	
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("Success: Host ");
	    	sb.append(Constants.HOST_NAME_FOR_CONNECTIVITY_CHECK);
	    	sb.append(" is reachable.");
	    	Log.d(TAG, sb.toString());
	    } 
	    catch (Exception e) 
	    {
	    	Log.d(TAG, "No connection: Host is unreachable.");
	    }
		
	    return isHostReachable;
	}
}