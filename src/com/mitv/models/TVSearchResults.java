
package com.mitv.models;



import java.util.List;

import com.mitv.models.gson.TVSearchResultsJSON;



public class TVSearchResults
	extends TVSearchResultsJSON
{
	public TVSearchResults() {}
	
	
	
	public TVSearchResults(List<TVSearchResult> results) 
	{
		this.results = results;
	}
}