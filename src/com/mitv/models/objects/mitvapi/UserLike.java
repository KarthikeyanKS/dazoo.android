
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;
import android.util.Log;

import com.mitv.enums.LikeTypeRequestEnum;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.UserLikeJSON;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Team;



public class UserLike 
	extends UserLikeJSON 
	implements GSONDataFieldValidation
{
	private static final String	TAG	= UserLike.class.getName();
	
	
	
	protected String contentId;
	protected boolean wasAddedManually;
	
	
	
	
	public UserLike(final Competition competition)
	{
		this.wasAddedManually = true;
		
		this.title = competition.getDisplayName();
		this.likeType = LikeTypeResponseEnum.COMPETITION.toString();
		this.contentId = competition.getCompetitionId().toString();
		
		setIDFieldWithLikeType();
	}
	
	
	
	public UserLike(final Team team)
	{
		this.wasAddedManually = true;
		
		this.title = team.getDisplayName();
		this.likeType = LikeTypeResponseEnum.TEAM.toString();
		this.contentId = Long.valueOf(team.getTeamId()).toString();
		
		setIDFieldWithLikeType();
	}
	
	
	
	public UserLike(
			final String teamDisplayName,
			final long teamId)
	{
		this.wasAddedManually = true;
		
		this.title = teamDisplayName;
		this.likeType = LikeTypeResponseEnum.TEAM.toString();
		this.contentId = Long.valueOf(teamId).toString();
		
		setIDFieldWithLikeType();
	}
	
	
	
	public UserLike(final TVProgram tvProgram)
	{
		this.wasAddedManually = true;
		
		this.title = tvProgram.getTitle();
		this.likeType = LikeTypeResponseEnum.getLikeTypeEnumFromTVProgram(tvProgram).toString();
		this.contentId = getContentIdFromTVProgram(tvProgram);
		
		setIDFieldWithLikeType();
	}
	
	
	
	public UserLike(
			final boolean wasAddedManually,
			final String title,
			final LikeTypeResponseEnum likeType,
			final String contentId)
	{
		this.wasAddedManually = wasAddedManually;
		
		this.title = title;
		this.likeType = likeType.toString();
		this.contentId = contentId;
		
		setIDFieldWithLikeType();
	}
	
	
	
	
	private void setIDFieldWithLikeType()
	{
		LikeTypeResponseEnum linkeType = getLikeType();
		
		switch(linkeType)
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
			
			case COMPETITION:
			{
				try
				{
					this.competitionId = Long.parseLong(contentId);
				}
				catch(NumberFormatException nfex)
				{
					this.competitionId = Long.valueOf(0);
					
					Log.w(TAG, "Failed to parse competitionID");
				}
				
				break;
			}
			
			case TEAM:
			{
				try
				{
					this.teamId = Long.parseLong(contentId);
				}
				catch(NumberFormatException nfex)
				{
					this.teamId = Long.valueOf(0);
					
					Log.w(TAG, "Failed to parse teamID");
				}
			
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled like type.");
				break;
			}
		}
	}
	

	
	public static String getContentIdFromTVProgram(TVProgram tvProgarm) 
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
		if(contentId == null)
		{
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
					
					case COMPETITION:
					{
						contentId = competitionId.toString();
						break;
					}
					
					case TEAM:
					{
						contentId = teamId.toString();
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
		
			case SPORT_TYPE: 
			{
				likeTypeRequest = LikeTypeRequestEnum.SPORT_TYPE;
				break;
			}
			
			case PROGRAM:
			{
				likeTypeRequest = LikeTypeRequestEnum.PROGRAM;
				break;
			}
			
			case COMPETITION:
			{
				likeTypeRequest = LikeTypeRequestEnum.COMPETITION;
				break;
			}
			
			case TEAM:
			{
				likeTypeRequest = LikeTypeRequestEnum.TEAM;
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
	
	
	
	public boolean wasAddedManually() 
	{
		return wasAddedManually;
	}
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((likeType == null) ? 0 : likeType.hashCode());
		result = prime * result + ((getContentId() == null) ? 0 : getContentId().hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		UserLike other = (UserLike) obj;
		
		if (getLikeType() == null) 
		{
			if (other.getLikeType() != null)
			{
				return false;
			}
		} 
		else if (getLikeType().equals(other.getLikeType()) == false)
		{
			return false;
		}
		
		if (getContentId() == null) 
		{
			if (other.getContentId() != null)
			{
				return false;
			}
		} 
		else if (getContentId().equals(other.getContentId()) == false)
		{
			return false;
		}
		
		return true;
	}


	
	@Override
	public boolean areDataFieldsValid() 
	{
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