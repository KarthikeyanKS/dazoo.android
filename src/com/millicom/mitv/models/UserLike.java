
package com.millicom.mitv.models;



import java.util.Comparator;
import com.millicom.mitv.models.gson.UserLikeJSON;



public class UserLike 
	extends UserLikeJSON
{
	public static class UserLikeComparatorByTitle implements Comparator<UserLikeJSON> 
	{
		@Override
		public int compare(UserLikeJSON a, UserLikeJSON b) 
		{
			return a.getTitle().compareTo(b.getTitle());
		}
	}
}
