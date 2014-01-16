package com.millicom.secondscreen.content.model;

import android.content.res.Configuration;

import com.millicom.secondscreen.SecondScreenApplication;

public class ThreeImageResolutions {
	
//	protected String imageUrlLandscapeLow;
//	protected String imageUrlLandscapeMedium;
//	protected String imageUrlLandscapeHigh;
	
	protected String imageUrlLowRes;
	protected String imageUrlMediumRes;
	protected String imageUrlHighRes;
	
	public String getImageUrl() {
		int screenSize = SecondScreenApplication.getInstance().getScreenSizeMask();
		boolean connectedToWifi = SecondScreenApplication.getInstance().isConnectedToWifi();

		String logoUrl;

		if (connectedToWifi) {
			switch (screenSize) {
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				logoUrl = imageUrlHighRes;
				break;
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				logoUrl = imageUrlMediumRes;
				break;
			case Configuration.SCREENLAYOUT_SIZE_SMALL:
				logoUrl = imageUrlLowRes;
				break;
			default:
				logoUrl = imageUrlMediumRes;
			}
		} else {
			logoUrl = imageUrlMediumRes;
		}
		
		//TODO use code above
		logoUrl = imageUrlHighRes;
		
		return logoUrl;
	}
	
//	public String getImageUrlLandscape() {
//		int screenSize = SecondScreenApplication.getInstance().getScreenSizeMask();
//		boolean connectedToWifi = SecondScreenApplication.getInstance().isConnectedToWifi();
//
//		String logoUrl;
//
//		if (connectedToWifi) {
//			switch (screenSize) {
//			case Configuration.SCREENLAYOUT_SIZE_LARGE:
//				logoUrl = imageUrlLandscapeHigh;
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//				logoUrl = imageUrlLandscapeMedium;
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_SMALL:
//				logoUrl = imageUrlLandscapeLow;
//				break;
//			default:
//				logoUrl = imageUrlLandscapeMedium;
//			}
//		} else {
//			logoUrl = imageUrlLandscapeMedium;
//		}
//		
//		//TODO use code above
//		logoUrl = imageUrlLandscapeHigh;
//		
//		return logoUrl;
//	}

//	public String getImageUrlLandscapeLow() {
//		return imageUrlLandscapeLow;
//	}
//	public String getImageUrlLandscapeMedium() {
//	return imageUrlLandscapeMedium;
//}
//public String getImageUrlLandscapeHigh() {
//return imageUrlLandscapeHigh;
//}
//public String getImageUrlPortraitOrSquareLow() {
//return imageUrlPortraitOrSquareLow;
//}
//public String getImageUrlPortraitOrSquareMedium() {
//return imageUrlPortraitOrSquareMedium;
//}

//public String getImageUrlPortraitOrSquareHigh() {
//return imageUrlPortraitOrSquareHigh;
//}

//	public void setImageUrlLandscapeLow(String imageUrlLandscapeLow) {
//		this.imageUrlLandscapeLow = imageUrlLandscapeLow;
//	}
//
//	public void setImageUrlLandscapeMedium(String imageUrlLandscapeMedium) {
//		this.imageUrlLandscapeMedium = imageUrlLandscapeMedium;
//	}
//
//	public void setImageUrlLandscapeHigh(String imageUrlLandscapeHigh) {
//		this.imageUrlLandscapeHigh = imageUrlLandscapeHigh;
//	}
	
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
