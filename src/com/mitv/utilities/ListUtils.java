
package com.mitv.utilities;



import java.util.Collections;
import java.util.List;

import com.mitv.models.comparators.BaseComparator;



public class ListUtils 
{	
	public static <E> boolean deepEquals(List<E> rhs, List<E> lhs, BaseComparator<E> comparator) 
	{
		boolean equals = true;
		
		if(rhs.size() == lhs.size()) 
		{
			/* Sort both list so that we can compare them one by one*/
			Collections.sort(rhs, comparator);
			Collections.sort(lhs, comparator);
			
			/* Compare if same, one by one, order are guaranteed. */
			for(int i = 0; i < rhs.size(); ++i) 
			{
				E rhsElement = rhs.get(i);
				E lhsElement = lhs.get(i);
				
				if(!rhsElement.equals(lhsElement)) 
				{
					equals = false;
					break;
				}
			}
		} 
		else 
		{
			equals = false;
		}
		
		return equals;
	}
}
