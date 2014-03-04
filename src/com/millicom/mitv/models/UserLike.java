
package com.millicom.mitv.models;



import java.util.Comparator;

import android.util.Log;

import com.millicom.mitv.enums.LikeTypeRequestEnum;
import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.models.gson.UserLikeJSON;



public class UserLike 
	extends UserLikeJSON
{
	private static final String	TAG	= UserLike.class.getName();
	
	
	
	public static UserLike userLikeFromTVProgram(TVProgram tvProgram)
	{
		String title = tvProgram.getTitle();
		
		LikeTypeResponseEnum likeTypeFromBroadcast = LikeTypeResponseEnum.getLikeTypeEnumFromTVProgram(tvProgram);
		
		String contentId = getContentIdFromTVProgram(tvProgram);
		
		UserLike userLikeFromTVProgram = new UserLike(title, likeTypeFromBroadcast, contentId);
		
		return userLikeFromTVProgram;
	}
	
	
	
	public static UserLike userLikeFromBroadcast(TVBroadcastWithChannelInfo broadcastWithChannelInfo)
	{
		return userLikeFromTVProgram(broadcastWithChannelInfo.getProgram());
	}
	
	
	
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
	
	
	private static String getContentIdFromTVProgram(TVProgram tvProgarm) 
	{
		LikeTypeResponseEnum likeTypeFromBroadcast = LikeTypeResponseEnum.getLikeTypeEnumFromTVProgram(tvProgarm);
		
		String contentId;
		
		switch(likeTypeFromBroadcast)
		{
			case PROGRAM:
			{
				contentId = tvProgarm.getProgramId();
				break;
			}
			
			case SERIES:
			{
				contentId = tvProgarm.getSeries().getSeriesId();
				break;
			}
			
			case SPORT_TYPE:
			{
				contentId = tvProgarm.getSportType().getSportTypeId();
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled like type.");
				contentId = "";
				break;
			}
		}
		
		return contentId;
	}
	
	
	
	public String getContentId()
	{
		String contentId;
		
		LikeTypeResponseEnum likeType = getLikeType();
		
		switch(likeType)
		{
			case PROGRAM:
			{
				contentId = this.programId;
				break;
			}
			
			case SERIES:
			{
				contentId = this.seriesId;
				break;
			}
			
			case SPORT_TYPE:
			{
				contentId = this.sportTypeId;
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled like type.");
				contentId = "";
				break;
			}
		}
		
		return contentId;
	}
	
	
	
	public LikeTypeRequestEnum getLikeTypeForRequest()
	{
		LikeTypeRequestEnum likeTypeRequest;
		
		switch (getLikeType())
		{
			case SERIES:
			{
				likeTypeRequest = LikeTypeRequestEnum.SERIES;
				break;
			}
		
			case PROGRAM:
			case SPORT_TYPE:
			{
				likeTypeRequest = LikeTypeRequestEnum.PROGRAM;
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled like type.");
				likeTypeRequest = LikeTypeRequestEnum.PROGRAM;
				break;
			}
		}
		
		return likeTypeRequest;
	}
	
	
	
	public boolean equals(UserLike userlikeToCompare)
	{
		boolean isEqual = false;
		
		String titleToCompare =  userlikeToCompare.getTitle();
		LikeTypeResponseEnum likeTypeToCompare = userlikeToCompare.getLikeType();
		String contenIdToCompare = userlikeToCompare.getContentId();
				
		String title =  this.getTitle();
		LikeTypeResponseEnum likeType = this.getLikeType();
		String contenId = this.getContentId();
		
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
