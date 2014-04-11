
package com.mitv.models.gson.disqus;



import com.mitv.models.gson.disqus.base.DisqusResponseWithCursorJSON;



public class DisqusThreadDetailsJSON 
	extends DisqusResponseWithCursorJSON
{
	protected DisqusThreadDetailsResponseJSON response;
	
	
	
	public DisqusThreadDetailsJSON(){}



	public DisqusThreadDetailsResponseJSON getResponse() 
	{
		return response;
	}
}
