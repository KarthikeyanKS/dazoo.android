
package com.millicom.mitv.models;



import java.util.Comparator;

import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.models.gson.UserLikeJSON;



public class UserLike 
	extends UserLikeJSON
{
	@SuppressWarnings("unused")
	private static final String	TAG	= UserLike.class.getName();
	
	public static UserLike userLikeFromBroadcast(TVBroadcastWithChannelInfo broadcastWithChannelInfo) {
		String title = broadcastWithChannelInfo.getProgram().getTitle();
		LikeTypeResponseEnum likeTypeFromBroadcast = LikeTypeResponseEnum.getLikeTypeEnumFromBroadcast(broadcastWithChannelInfo);
		String contentId = getContentIdFromBroadcast(broadcastWithChannelInfo);
		
		UserLike userLikeFromBroadcast = new UserLike(title, likeTypeFromBroadcast, contentId);
		return userLikeFromBroadcast;
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
	
	private static String getContentIdFromBroadcast(TVBroadcastWithChannelInfo broadcastWithChannelInfo) {
		LikeTypeResponseEnum likeTypeFromBroadcast = LikeTypeResponseEnum.getLikeTypeEnumFromBroadcast(broadcastWithChannelInfo);
		
		String contentId;
		
		switch(likeTypeFromBroadcast)
		{
			case PROGRAM:
			{
				contentId = broadcastWithChannelInfo.getProgram().getProgramId();
				break;
			}
			
			case SERIES:
			{
				contentId = broadcastWithChannelInfo.getProgram().getSeries().getSeriesId();
				break;
			}
			
			case SPORT_TYPE:
			{
				contentId = broadcastWithChannelInfo.getProgram().getSportType().getSportTypeId();
				break;
			}
			
			default:
			{
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
				contentId = "";
			}
		}
		
		return contentId;
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
