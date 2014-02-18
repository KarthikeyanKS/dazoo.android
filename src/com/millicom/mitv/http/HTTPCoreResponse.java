
package com.millicom.mitv.http;



import java.io.Serializable;



public class HTTPCoreResponse
	implements Serializable 
{
	private static final long serialVersionUID = 2977961269214430910L;
	
	
	private String requestURL;
	private int statusCode;
	private String responseString;


	
	public HTTPCoreResponse(
			final String requestURL,
			final int statusCode)
	{
		this.requestURL = requestURL;
		this.statusCode = statusCode;
		this.responseString = new String();
	}



	public HTTPCoreResponse(
			final String requestURL,
			final int statusCode,
			final String responseString)
	{
		this.requestURL = requestURL;
		this.statusCode = statusCode;
		this.responseString = responseString;
	}
	
	
	
	/**
	 * @return the original request url
	 */
	public String getRequestURL() 
	{
		return requestURL;
	}

	
	
	/**
	 * @return the status code
	 */
	 public int getStatusCode()
	{
		return statusCode;
	}



	/**
	 * @return the responseString
	 */
	 public String getResponseString()
	{
		 return responseString;
	}



	 public Boolean hasResponseString()
	 {
		 return (this.responseString != null &&
				 this.responseString.length() > 0 &&
				 this.responseString.equalsIgnoreCase("null") == false);
	 }


	 
	 @Override
	 public String toString()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append("{");
		 sb.append(requestURL);
		 sb.append(", ");
		 sb.append(statusCode);
		 sb.append(", ");
		 sb.append(responseString);
		 sb.append("}");

		 return sb.toString();
	 }
}
