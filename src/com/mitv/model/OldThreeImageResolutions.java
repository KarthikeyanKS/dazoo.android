package com.mitv.model;

import android.content.res.Configuration;

import com.mitv.SecondScreenApplication;

public class OldThreeImageResolutions {
		
	protected String imageUrlLowRes;
	protected String imageUrlMediumRes;
	protected String imageUrlHighRes;
	
	public String getImageUrl() {
		int screenSize = SecondScreenApplication.sharedInstance().getScreenSizeMask();
		boolean connectedToWifi = SecondScreenApplication.sharedInstance().isConnectedToWifi();

		String logoUrl;

//		if (connectedToWifi) {
//			switch (screenSize) {
//			case Configuration.SCREENLAYOUT_SIZE_LARGE:
//				logoUrl = imageUrlHighRes;
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//				logoUrl = imageUrlMediumRes;
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_SMALL:
//				logoUrl = imageUrlLowRes;
//				break;
//			default:
//				logoUrl = imageUrlMediumRes;
//			}
//		} else {
//			logoUrl = imageUrlMediumRes;
//		}
		
		//TODO use code above
		logoUrl = imageUrlHighRes;
		
		return logoUrl;
	}
		
	public void setAllImageUrls(String imageUrl) {
		setImageUrlPortraitOrSquareLow(imageUrl);
		setImageUrlPortraitOrSquareMedium(imageUrl);
		setImageUrlPortraitOrSquareHigh(imageUrl);
	}

	public void setImageUrlPortraitOrSquareLow(String imageUrlPortraitOrSquareLow) {
		this.imageUrlLowRes = imageUrlPortraitOrSquareLow;
	}

	public void setImageUrlPortraitOrSquareMedium(String imageUrlPortraitOrSquareMedium) {
		this.imageUrlMediumRes = imageUrlPortraitOrSquareMedium;
	}

	public void setImageUrlPortraitOrSquareHigh(String imageUrlPortraitOrSquareHigh) {
		this.imageUrlHighRes = imageUrlPortraitOrSquareHigh;
	}
	
	
}
