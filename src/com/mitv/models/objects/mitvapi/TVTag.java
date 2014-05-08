
package com.mitv.models.objects.mitvapi;



import java.util.List;

import android.content.Context;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.models.gson.mitvapi.TVTagJSON;
import com.mitv.models.objects.mitvapi.competitions.Competition;
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
	
	
	
	public static TVTag getAllCategoriesTVTag() 
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		String displayName = context.getString(R.string.all_categories_tv_tag);
		TVTag allCategoriesTag = new TVTag(Constants.ALL_CATEGORIES_TAG_ID, displayName);
		return allCategoriesTag;
	}
	
	
	
	public Competition getMatchingCompetition(List<Competition> competitions)
	{
		Competition matchingCompetition = null;
		
		for(Competition competition : competitions)
		{
			if(id.equalsIgnoreCase("FIFA"))
			{
				matchingCompetition = competition;
				break;
			}
		}
		
		return matchingCompetition;
	}
}