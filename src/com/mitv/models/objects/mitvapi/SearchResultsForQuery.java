package com.mitv.models.objects.mitvapi;

public class SearchResultsForQuery {
	String queryString;
	TVSearchResults searchResults;
	
	public SearchResultsForQuery(String queryString, TVSearchResults searchResults) 
	{
		this.queryString = queryString;
		this.searchResults = searchResults;
		
		TVSearchResult.setSearchQuery(queryString);
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public TVSearchResults getSearchResults() {
		return searchResults;
	}
}
