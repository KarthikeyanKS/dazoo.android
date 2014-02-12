package com.millicom.mitv.http;

import java.io.Serializable;

public class URLParameter
	implements Serializable
{
	private static final long serialVersionUID = 7023709735148035918L;
	private static final String QUERYSTRING_EQUALS = "=";
	
	
	private String header;
	private String value;
	
	
	
	public URLParameter(
			String header,
			String value)
	{
		this.header = header;
		this.value = value;
	}
	


	public String getHeader()
	{
		return header;
	}


	
	public String getValue()
	{
		return value;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		if(header.length() > 0)
		{
			sb.append(header);
			sb.append(QUERYSTRING_EQUALS);
		}
		// No need for else
		
		sb.append(value);
		
		return sb.toString();
	}
}
