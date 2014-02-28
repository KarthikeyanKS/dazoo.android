
package com.millicom.mitv.models;



import java.util.List;

import com.millicom.mitv.models.gson.TVSearchResultsJSON;



public class TVSearchResults
	extends TVSearchResultsJSON
{
	public TVSearchResults(List<TVSearchResult> results) {
		this.results = results;
	}
}