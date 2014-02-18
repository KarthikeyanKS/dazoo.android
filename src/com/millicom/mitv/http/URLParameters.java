
package com.millicom.mitv.http;

import java.io.Serializable;
import java.util.ArrayList;

public class URLParameters 
	implements Serializable
{
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
		URLParameter queryStringParameter = new URLParameter(header, value);
		
		urlParameters.add(queryStringParameter);
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
