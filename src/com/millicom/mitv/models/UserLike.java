
package com.millicom.mitv.models;



import java.util.Comparator;

import android.util.Log;

import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.models.gson.UserLikeJSON;



public class UserLike 
	extends UserLikeJSON
{
	private static final String	TAG	= UserLike.class.getName();
	
	
	
	public UserLike(String title, LikeTypeResponseEnum likeType, String contentId)
	{
		this.likeType = likeType.toString();	
		this.title = title;
		
		switch(likeType)
		{
			case PROGRAM:
			{
				this.programId = contentId;
				break;
			}
			
			case SERIES:
			{
				this.seriesId = contentId;
				break;
			}
			
			case SPORT_TYPE:
			{
				this.sportTypeId = contentId;
				break;
			}
		}
	}
	
	
	
	public boolean isEqual(UserLike userlikeToCompare)
	{
		boolean isEqual = false;
		
		String titleToCompare =  userlikeToCompare.getTitle();
		LikeTypeResponseEnum likeTypeToCompare = userlikeToCompare.getLikeType();
		String contenIdToCompare;
		
		switch(likeTypeToCompare)
		{
			case PROGRAM:
			{
				contenIdToCompare = userlikeToCompare.getProgramId();
				break;
			}
			
			case SERIES:
			{
				contenIdToCompare = userlikeToCompare.getSeriesId();
				break;
			}
			
			case SPORT_TYPE:
			{
				contenIdToCompare = userlikeToCompare.getSportTypeId();
				break;
			}
			default:
			{
				contenIdToCompare = "";
				
				Log.w(TAG, "Unhandled like type.");
			}
		}
		
		String title =  this.getTitle();
		LikeTypeResponseEnum likeType = this.getLikeType();
		String contenId;
		
		switch(likeType)
		{
			case PROGRAM:
			{
				contenId = userlikeToCompare.getProgramId();
				break;
			}
			
			case SERIES:
			{
				contenId = userlikeToCompare.getSeriesId();
				break;
			}
			
			case SPORT_TYPE:
			{
				contenId = userlikeToCompare.getSportTypeId();
				break;
			}
			default:
			{
				contenId = "";
				
				Log.w(TAG, "Unhandled like type.");
			}
		}
		
		if(titleToCompare.equals(title) &&
		  likeTypeToCompare == likeType &&
		  contenIdToCompare.equals(contenId))
		{
			isEqual = true;
		}
		
		return isEqual;
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
