
package com.mitv.models;



import android.text.TextUtils;
import android.util.Log;

import com.mitv.enums.LikeTypeRequestEnum;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.UserLikeJSON;



public class UserLike 
	extends UserLikeJSON implements GSONDataFieldValidation
{
	private static final String	TAG	= UserLike.class.getName();
	
	protected String contentId;
	protected boolean wasAddedManually = false;
	
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
		this.wasAddedManually = true;
		
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
			
			default:
			{
				Log.w(TAG, "Unhandled like type.");
				break;
			}
		}
	}
	
	public boolean wasAddedManually() {
		return wasAddedManually;
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
		if(contentId == null) {
		
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
		
			case SPORT_TYPE: {
				likeTypeRequest = LikeTypeRequestEnum.SPORT_TYPE;
				break;
			}
			case PROGRAM:
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
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((likeType == null) ? 0 : likeType.hashCode());
		result = prime * result + ((getContentId() == null) ? 0 : getContentId().hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserLike other = (UserLike) obj;
		if (getLikeType() == null) {
			if (other.getLikeType() != null)
				return false;
		} else if (!getLikeType().equals(other.getLikeType()))
			return false;
		if (getContentId() == null) {
			if (other.getContentId() != null)
				return false;
		} else if (!getContentId().equals(other.getContentId()))
			return false;
		return true;
	}


	
	@Override
	public boolean areDataFieldsValid() {
		boolean commonFieldsOk = (
				getTitle() != null && getLikeType() != null
				);
		
		

		boolean specificFieldsOk = false;
		switch (getLikeType())
		{
			case SERIES:
			{
				specificFieldsOk = !TextUtils.isEmpty(getSeriesId());
				break;
			}
			
			case SPORT_TYPE:
			{
				specificFieldsOk = !TextUtils.isEmpty(getSportTypeId());
				break;
			}
			
			default:
			case PROGRAM:
			{
				boolean programIdOk = !TextUtils.isEmpty(getProgramId()) && getProgramType() != null;
				
				boolean programSpecificFieldsOk = false;
				switch (getProgramType()) 
				{
					case MOVIE:
					{
						programSpecificFieldsOk = getGenre() != null && !TextUtils.isEmpty(getProgramId()) && getYear() != null;
						break;
					}
					
					default:
					case OTHER:
					{
						programSpecificFieldsOk = getCategory() != null;
						break;
					}
				}

				specificFieldsOk = programIdOk && programSpecificFieldsOk;
				
				break;
			}
		}
		
		boolean areDataFieldsValid = commonFieldsOk && specificFieldsOk;
		
		if(getBroadcastCount() != null && getBroadcastCount() > 0) {
			boolean isNextBroadcastFieldsOk = getNextBroadcast().areDataFieldsValid();
			areDataFieldsValid = areDataFieldsValid && isNextBroadcastFieldsOk;
		}
				
		return areDataFieldsValid;
	}
	
	
}
