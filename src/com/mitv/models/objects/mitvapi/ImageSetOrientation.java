
package com.mitv.models.objects.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.ImageSetOrientationJSON;



/**
 * Extra helper methods in this class
 * @author consultant_hdme
 *
 */
public class ImageSetOrientation 
	extends ImageSetOrientationJSON 
	implements GSONDataFieldValidation
{	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areDataFieldsValid = (landscape != null && landscape.containsImages() && 
									  portrait != null && portrait.containsImages());
		
		return areDataFieldsValid;
	}
	
	
	
	public boolean containsLandscapeImageSet()
	{
		return (landscape != null && landscape.containsImages());
	}
	
	
	
	public boolean containsPortraitImageSet()
	{
		return (portrait != null && portrait.containsImages());
	}
}
