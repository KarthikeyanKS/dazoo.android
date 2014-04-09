
package com.mitv.models.objects.mitvapi;



import java.util.List;

import com.mitv.models.gson.mitvapi.TVSearchResultsJSON;



public class TVSearchResults
	extends TVSearchResultsJSON
{
	public TVSearchResults() {}
	
	
	
	public TVSearchResults(List<TVSearchResult> results) 
	{
		this.results = results;
	}
}