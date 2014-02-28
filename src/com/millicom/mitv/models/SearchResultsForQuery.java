package com.millicom.mitv.models;

public class SearchResultsForQuery {
	String queryString;
	TVSearchResults searchResults;
	
	public SearchResultsForQuery(String queryString, TVSearchResults searchResults) {
		this.queryString = queryString;
		this.searchResults = searchResults;
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public TVSearchResults getSearchResults() {
		return searchResults;
	}
}
