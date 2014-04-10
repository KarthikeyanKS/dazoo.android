
package com.mitv.models.gson.disqus;



public class DisqusCursorJSON 
{
	protected String prev;
	protected boolean hasNext;
	protected String next;
    protected boolean hasPrev;
    protected String id;
	protected boolean more;
	
	
	
	public DisqusCursorJSON(){}


	
	public String getPrev() {
		return prev;
	}


	public boolean isHasNext() {
		return hasNext;
	}


	public String getNext() {
		return next;
	}


	public boolean isHasPrev() {
		return hasPrev;
	}


	public String getId() {
		return id;
	}


	public boolean isMore() {
		return more;
	}
}
