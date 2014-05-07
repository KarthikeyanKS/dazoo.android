
package com.mitv.models.gson.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class TeamImageSetJSON 
{
	private ImageSetSizeJSON flag;
	private ImageSetSizeJSON banner;
	
	
	public TeamImageSetJSON()
	{
		// TODO - Remove this
		flag = new ImageSetSizeJSON("http://www.clubpenguininsiders.com/forum/uploads/gallery/album_679/gallery_65742_679_11051.jpg", "http://www.clubpenguininsiders.com/forum/uploads/gallery/album_679/gallery_65742_679_11051.jpg", "http://www.clubpenguininsiders.com/forum/uploads/gallery/album_679/gallery_65742_679_11051.jpg");
		banner = new ImageSetSizeJSON("http://www.clubpenguininsiders.com/forum/uploads/gallery/album_679/gallery_65742_679_11051.jpg", "http://www.clubpenguininsiders.com/forum/uploads/gallery/album_679/gallery_65742_679_11051.jpg", "http://www.clubpenguininsiders.com/forum/uploads/gallery/album_679/gallery_65742_679_11051.jpg");
	}


	
	public ImageSetSizeJSON getFlag() {
		return flag;
	}


	public ImageSetSizeJSON getBanner() {
		return banner;
	}
}
