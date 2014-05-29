
package com.mitv.enums;



public enum CompetitionCategoryEnum
{
	SOCCER(0);
	
	
	
	private final int id;
	
	
	
	CompetitionCategoryEnum(int id) 
	{
		this.id = id;
	}
	
	
	
	public int getId() 
	{
		return id;
	}
	
	
	
	public static CompetitionCategoryEnum getTypeEnumFromCode(int code)
	{
		for(CompetitionCategoryEnum result: CompetitionCategoryEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return CompetitionCategoryEnum.SOCCER;
	}



	public static CompetitionCategoryEnum getTypeEnumFromCode(String codeAsString)
	{
		int value = CompetitionCategoryEnum.SOCCER.getId();

		if(codeAsString != null)
		{
			try
			{
				value = Integer.parseInt(codeAsString);
			}
			catch(NumberFormatException nfex)
			{
				return CompetitionCategoryEnum.SOCCER;
			}
		}

		return getTypeEnumFromCode(value);
	}
}
