
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.competitions.TeamJSON;



public class Team 
	extends TeamJSON
{
//	private static final String FLAG_FILE_PREFIX = "flag_";


	
	public Team()
	{}
	
	
	
//	public Drawable getLocalFlagDrawableResource()
//	{
//		if(localFlagDrawable == null)
//		{
//			String resourceName = FLAG_FILE_PREFIX + nationCode.toLowerCase();
//			
//			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
//					
//			try
//			{
//				localFlagDrawable = context.getResources().getDrawable(context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName()));
//			}
//			catch(Exception e)
//			{
//				// Do nothing
//			}
//		}
//		
//		return localFlagDrawable;
//	}
//	
//	
//	
//	public boolean isLocalFlagDrawableResourceAvailable()
//	{
//		Drawable drawableResource = getLocalFlagDrawableResource();
//		
//		return (drawableResource != null);
//	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + (int) teamId;
		
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
		
		Team other = (Team) obj;
		
		if (this.teamId != other.teamId) 
		{
			return false;
		}
		
		return true;
	}
}
