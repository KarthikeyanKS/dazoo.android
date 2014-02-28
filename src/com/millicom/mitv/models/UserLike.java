
package com.millicom.mitv.models;



import java.util.Comparator;

import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.models.gson.UserLikeJSON;



public class UserLike 
	extends UserLikeJSON
{
	public UserLike(LikeTypeResponseEnum likeType, String title)
	{
		this.likeType = likeType.toString();	
		this.title = title;
	}
	
	
	
	public static class UserLikeComparatorByTitle implements Comparator<UserLikeJSON> 
	{	
		@Override
		public int compare(UserLikeJSON a, UserLikeJSON b) 
		{
			return a.getTitle().compareTo(b.getTitle());
		}
	}
}
