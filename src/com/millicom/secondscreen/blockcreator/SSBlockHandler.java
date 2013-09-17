package com.millicom.secondscreen.blockcreator;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class SSBlockHandler {
	private Activity			mActivity;

	private static final String	TAG	= "VPSectionBlockCreator";

	public SSBlockHandler(Activity activity) {
		this.mActivity = activity;
	}
	
	// TODO 

	// Creates a list of views from a list of products
	//public boolean populateBlocks(View v, ArrayList<SSBlock> blocks) {
//
	//	boolean flag = false;
//
//		// The container that will be filled up with block views
//		LinearLayout container = (LinearLayout) v.findViewById(R.id.block_container_layout);
//
//		// Remove potential old views
//		container.removeAllViews();
//
//		// Create a row for each block
//		for (int i = 0; i < blocks.size(); i++) {
//
//			Log.d(TAG, "********* BLOCK ********** : " + blocks.get(i).getTitle());
//			Log.d(TAG, "Block Style : " + blocks.get(i).getStyle());
//
//			String style = blocks.get(i).getStyle();
//
//			// Add all the new ones
//			container.addView(getBlockPopulator(style).getPopulatedBlock(blocks.get(i)));
//
//			flag = true;
//		}
//		return flag;
//	}
//
//	// Get a block creator depending on style
//	private SSBlockCreator getBlockPopulator(String style) {
//
//		SSBlockCreator blockPopulator;
//
//		// TODO
//		
//		return blockPopulator;
//	}
	
}
