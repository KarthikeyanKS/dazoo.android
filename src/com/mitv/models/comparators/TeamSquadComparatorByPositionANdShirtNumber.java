
package com.mitv.models.comparators;



import com.mitv.enums.EventLineUpPositionEnum;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public class TeamSquadComparatorByPositionANdShirtNumber 
	extends BaseComparator<TeamSquad> 
{
	@Override
	public int compare(TeamSquad lhs, TeamSquad rhs) 
	{
		EventLineUpPositionEnum leftPosition = lhs.getPosition();
		EventLineUpPositionEnum rightPosition = rhs.getPosition();
		
		if(leftPosition != rightPosition)
		{
			int leftPositionCode = lhs.getPosition().getCode();
			int rightPositionCode = rhs.getPosition().getCode();
			
			if (leftPositionCode > rightPositionCode) 
			{
				return 1;
			} 
			else if (leftPositionCode < rightPositionCode) 
			{
				return -1;
			}
			else 
			{
				return 0;
			}
		}
		else
		{
			int leftShirtNumber = lhs.getShirtNumber();
			int rightShirtNumber = rhs.getShirtNumber();
			
			if (leftShirtNumber > rightShirtNumber) 
			{
				return 1;
			} 
			else if (leftShirtNumber < rightShirtNumber) 
			{
				return -1;
			} 
			else 
			{
				return 0;
			}
		}
	}
}