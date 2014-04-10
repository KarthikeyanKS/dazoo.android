
package com.mitv.models.gson.disqus;



import java.util.List;

import com.mitv.models.gson.disqus.base.DisqusResponseWithCursorJSON;



public class DisqusCommentsJSON 
	extends DisqusResponseWithCursorJSON
{
	protected List<DisqusJSONPost> response;
	
	
	
	public DisqusCommentsJSON(){}



	public List<DisqusJSONPost> getResponse() {
		return response;
	}
}
