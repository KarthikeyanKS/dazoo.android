
package com.mitv.models;



import com.mitv.models.gson.TVTagJSON;



public class TVTag 
	extends TVTagJSON 
{
	
	public TVTag(String id, String displayName) 
	{
		this.id = id;
		this.displayName = displayName;
	}
}