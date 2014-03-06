
package com.mitv.models.comparators;



import java.util.Comparator;
import com.mitv.models.gson.UserLikeJSON;



public class UserLikeComparatorByTitle 
	implements Comparator<UserLikeJSON> 
{	
	@Override
	public int compare(UserLikeJSON a, UserLikeJSON b) 
	{
		return a.getTitle().compareTo(b.getTitle());
	}
}