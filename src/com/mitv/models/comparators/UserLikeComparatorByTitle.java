
package com.mitv.models.comparators;



import com.mitv.models.gson.UserLikeJSON;



public class UserLikeComparatorByTitle 
extends BaseComparator<UserLikeJSON> 
{	
	@Override
	public int compare(UserLikeJSON a, UserLikeJSON b) 
	{
		return a.getTitle().compareTo(b.getTitle());
	}
}