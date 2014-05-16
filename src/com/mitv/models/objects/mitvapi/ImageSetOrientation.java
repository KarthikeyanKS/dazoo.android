
package com.mitv.models.objects.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.ImageSetOrientationJSON;
import com.mitv.models.sql.NotificationSQLElement;



/**
 * Extra helper methods in this class
 * @author consultant_hdme
 *
 */
public class ImageSetOrientation 
	extends ImageSetOrientationJSON 
	implements GSONDataFieldValidation
{
	public ImageSetOrientation(NotificationSQLElement item) 
	{
		String smallLandscape = item.getProgramImageSmallLandscape();
		String mediumLandscape = item.getProgramImageMediumLandscape();
		String largeLandscape = item.getProgramImageLargeLandscape();
		String smallPortrait = item.getProgramImageSmallPortrait();
		String mediumPortrait = item.getProgramImageMediumPortrait();
		String largePortrait = item.getProgramImageLargePortrait();
		
		this.landscape = new ImageSetSize(smallLandscape, mediumLandscape, largeLandscape);
		
		this.portrait = new ImageSetSize(smallPortrait, mediumPortrait, largePortrait);
	}

	
	
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
