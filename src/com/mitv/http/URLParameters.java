
package com.mitv.http;



import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.util.Log;

import com.mitv.Constants;



public class URLParameters 
	implements Serializable
{
	private static final String TAG = URLParameters.class.getName();
	
	
	private static final long serialVersionUID = 5091937835853178956L;
	private static final String QUERYSTRING_FIRST_SEPARATOR = "?";
	private static final String QUERYSTRING_SEPARATOR = "&";

	
	
	private ArrayList<URLParameter> urlParameters;
	
	
	
	public URLParameters()
	{
		urlParameters = new ArrayList<URLParameter>();
	}

	
	
	public void add(
			final String header,
			final String value)
	{
		if(value != null)
		{
			String encodedValue;
			
			try 
			{
				encodedValue = URLEncoder.encode(value, Constants.HTTP_CORE_DEAFULT_ENCODING);
				
				encodedValue.replace("+", "%20");
			} 
			catch (UnsupportedEncodingException ueex) 
			{
				ueex.printStackTrace();
				
				encodedValue = value;
			}
			
			URLParameter queryStringParameter = new URLParameter(header, encodedValue);
			
			urlParameters.add(queryStringParameter);
		}
		else
		{
			if(header != null)
			{
				Log.w(TAG, "The parameter value for key " + header + " is null and will not be added to urlParameters");
			}
			else
			{
				Log.w(TAG, "The parameter value is null and will not be added to urlParameters");
			}
		}
	}
	
	
	
	public void add(
			final String header,
			final int value)
	{
		Integer valueAsInteger = new Integer(value);
		
		add(header, valueAsInteger.toString());
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		if(urlParameters.isEmpty() == false)
		{
			sb.append(QUERYSTRING_FIRST_SEPARATOR);
			sb.append(urlParameters.get(0).toString());
		}
		// No need for else
		
		for(int i=1; i<urlParameters.size(); i++)
		{
			sb.append(QUERYSTRING_SEPARATOR);
			sb.append(urlParameters.get(i).toString());
		}

		return sb.toString();
	}
}
