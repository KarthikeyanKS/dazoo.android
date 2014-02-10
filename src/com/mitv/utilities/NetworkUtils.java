
package com.mitv.utilities;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import com.mitv.R;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;



public class NetworkUtils
{	
	private static final String	TAG = "NetworkUtils";
	
	
	
	public static String convertStreamToString(InputStream inputStream) 
	{
		return convertStreamToString(inputStream, Charset.defaultCharset().toString());
	}
	
	
	
	public static String convertStreamToString(
			InputStream is, 
			String encoding)
	{
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new InputStreamReader(is, encoding));
		} 
		catch (UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		
		String line = null;
		
		try 
		{
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line);
				sb.append("\n");
			}
		} 
		catch (IOException ioex)
		{
			Log.w(TAG, ioex.getMessage(), ioex);
		} 
		finally
		{
			try 
			{
				is.close();
			} 
			catch (IOException ioex) 
			{
				Log.w(TAG, ioex.getMessage(), ioex);
			}
		}
		return sb.toString();
	}
	
	
	
	
	public static Boolean isConnectedAndHostIsReachable(final Context context)
	{
		// TODO : Revert this setting and use the real methods
		return true;
		//return isConnected(context) && isHostReachable(context);
	}
	
	
	
	
	public static boolean isConnected(final Context context)
    {
		boolean isConnected = false;
		
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        
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
	
	
	
	public static boolean isHostReachable(final Context context)
	{
		boolean isHostReachable = false;
		
	    try 
	    {
	    	URL url = new URL(context.getResources().getString(R.string.host_name_for_connectivity_check));
	    	
	    	HttpURLConnection urlc = (HttpURLConnection) (url).openConnection();
	    	urlc.setRequestProperty("User-Agent", "Test");
	    	urlc.setRequestProperty("Connection", "close");
	    	urlc.setConnectTimeout(context.getResources().getInteger(R.integer.timeout_in_miliseconds_for_connectivity_check)); 
	    	urlc.connect();
	    	
	    	isHostReachable = (urlc.getResponseCode() == 200);
	    	
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("Success: Host ");
	    	sb.append(context.getResources().getString(R.string.host_name_for_connectivity_check));
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