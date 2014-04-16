
package com.mitv.models.objects.disqus;



import com.mitv.models.gson.disqus.base.DisqusBaseResponseJSON;



public class DisqusBaseResponse 
	extends DisqusBaseResponseJSON
{
	public DisqusBaseResponse(){}
	
	
	
	public boolean wasSuccessful()
	{
		return code == 0;
	}
}
