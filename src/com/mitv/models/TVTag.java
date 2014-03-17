
package com.mitv.models;



import com.mitv.models.gson.TVTagJSON;
import com.mitv.models.orm.TVTagORM;



public class TVTag 
	extends TVTagJSON 
{
	public TVTag(String id, String displayName) 
	{
		this.id = id;
		this.displayName = displayName;
	}
	
	
	
	public TVTag(TVTagORM data) 
	{
		this.id = data.getId();
		this.displayName = data.getDisplayName();
	}
}