package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageFragment;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSStartPage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.GuideTime;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.TvDate;

public class TVGuideFragment extends SSPageFragment {

	private static final String	TAG				= "TVGuideFragment";
	private View				mRootView;
	private RelativeLayout		mTvGuideContainerLayout;
	private ListView			mTvGuideListView;
	private LinearLayout		mClockIndexView;

	private TVGuideListAdapter	mTvGuideListAdapter;
	private SSStartPage			mPage;
	private ArrayList<Guide>	mGuide;

	private Activity			mActivity;

	protected GestureDetector	mGestureDetector;

	protected Vector<GuideTime>	timeVector;

	protected int				totalListSize	= 0;

	/**
	 * list with items for side index
	 */
	private ArrayList<Object[]>	indexList		= null;

	/**
	 * list with row number for side index
	 */
	protected List<Integer>		dealList		= new ArrayList<Integer>();

	/**
	 * height of left side index
	 */
	protected int				sideIndexHeight;

	/**
	 * number of items in the side index
	 */
	private int					indexListSize;

	/**
	 * x and y coordinates within our side index
	 */
	protected static float		sideIndexX;
	protected static float		sideIndexY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		timeVector = getIndexedTime();
		
		mGestureDetector = new GestureDetector(mActivity,
                new ListIndexGestureListener());

		mActivity = getActivity();
		mPage = SSStartPage.getInstance();

		// Register for when a child selects a new sorting
		// LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSortingUpdatedReceiver, new IntentFilter(mSectionId));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide_fragment, container, false);
		mTvGuideContainerLayout = (RelativeLayout) mRootView.findViewById(R.id.tvguide_list_container);
		mTvGuideListView = (ListView) mRootView.findViewById(R.id.listview);
		mClockIndexView = (LinearLayout) mRootView.findViewById(R.id.side_clock_index);
	
		super.initRequestCallbackLayouts(mRootView);

		// Forced reload will be loaded in onResume
		if (!shouldForceReload()) loadPage();
		return mRootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Stop listening to broadcast events
		// LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mSortingUpdatedReceiver);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Cancel any get page request
		mPage.cancelGetPage();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Should Force Reload : " + shouldForceReload());
		if (shouldForceReload()) {

			// Create a new page
			// mPage = new VPSectionPage((VPSection) getArguments().getParcelable(VPConsts.INTENT_EXTRA_SECTION));

			// Reload the new page
			loadPage();
		}
	}

	BroadcastReceiver	mSortingUpdatedReceiver	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// Reload the page with new sorting
			// TODO
		}
	};

	private void getPage() {
		mPage.getPage(new SSPageCallback() {

			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {

				if (!pageHoldsData()) {
					// // Request failed
					updateUI(REQUEST_STATUS.FAILED);
				}
			}
		});
	}

	@Override
	protected void loadPage() {
		updateUI(Consts.REQUEST_STATUS.LOADING);

		if (!pageHoldsData()) {
			getPage();
		}
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;
		mGuide = mPage.getGuide();
		Log.d(TAG, "Guide : " + mGuide);
		if (mGuide != null) {
			if (mGuide.isEmpty()) {
				updateUI(REQUEST_STATUS.FAILED);
				Log.d(TAG, "pageHoldsData: Failed");
			} else {
				updateUI(REQUEST_STATUS.SUCCESFUL);
			}
			result = true;
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		Log.d(TAG, "update UI :" + status);
		if (super.requestIsSuccesfull(status)) {
			mTvGuideListAdapter = new TVGuideListAdapter(mActivity, mGuide);
			mTvGuideListView.setAdapter(mTvGuideListAdapter);
			getDisplayListOnChange();
		}
	}

	private Vector<GuideTime> getIndexedTime() {

		Vector<GuideTime> v = new Vector<GuideTime>();
		// Add default item
		for (int i = 0; i < 25; i++) {
			GuideTime time = new GuideTime();
			time.setTimeOfDay(i);
			v.add(time);
		}
		
		return v;
	}

	/**
	 * TODO
	 * displayListItem is method used to display the row from the list on scrool or touch.
	 */
	public void displayListItem() {

		// compute number of pixels for every side index item
		double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

		// compute the item index for given event position belongs to
		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);
		
		Toast.makeText(mActivity, Integer.toString(itemPosition), Toast.LENGTH_SHORT).show();
		
		//if (itemPosition < 0) {
		//	itemPosition = 0;
		//} else if (itemPosition >= dealList.size()) {
		//	itemPosition = dealList.size() - 1;
		//}
		//
		//int listLocation = dealList.get(itemPosition) + itemPosition;
		//
		//if (listLocation > totalListSize) {
		//	listLocation = totalListSize;
		//}
		//
		//mTvGuideListView.setSelection(listLocation);
	}

	private ArrayList<Object[]> getListArrayIndex(int[] intArr) {
		ArrayList<Object[]> tmpIndexList = new ArrayList<Object[]>();
		Object[] tmpIndexItem = null;

		int tmpPos = 0;
		int tmpLetter = 0;
		int currentLetter = 0;

		for (int j = 0; j < intArr.length; j++) {
			currentLetter = intArr[j];

			// every time new letters comes
			// save it to index list
			if (currentLetter != tmpLetter) {
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = tmpLetter;
				tmpIndexItem[1] = tmpPos - 1;
				tmpIndexItem[2] = j - 1;

				tmpLetter = currentLetter;
				tmpPos = j + 1;
				tmpIndexList.add(tmpIndexItem);
			}
		}

		// save also last letter
		tmpIndexItem = new Object[3];
		tmpIndexItem[0] = tmpLetter;
		tmpIndexItem[1] = tmpPos - 1;
		tmpIndexItem[2] = intArr.length - 1;
		tmpIndexList.add(tmpIndexItem);

		// and remove first temporary empty entry
		if (tmpIndexList != null && tmpIndexList.size() > 0) {
			tmpIndexList.remove(0);
		}

		return tmpIndexList;
	}

	/**
	 * getDisplayListOnChange method display the list from the index.
	 * 
	 * @param displayScreen
	 *            , specify which screen it belongs
	 */
	public void getDisplayListOnChange() {
		sideIndexHeight = mClockIndexView.getHeight();

		if (sideIndexHeight == 0) {
			Display display = mActivity.getWindowManager().getDefaultDisplay();
			sideIndexHeight = display.getHeight();
			// Navigation Bar + Tab space comes approximately 80dip
		}

		mClockIndexView.removeAllViews();

		/**
		 * temporary TextView for every visible item
		 */
		TextView tmpTV = null;

		/**
		 * we will create the index list
		 */
		// String[] strArr = new String[timeVector.size()];
		int[] intArr = new int[timeVector.size()];

		int j = 0;

		for (GuideTime time : timeVector) {
			intArr[j++] = time.getTimeOfDay();
		}

		indexList = getListArrayIndex(intArr);

		/**
		 * number of items in the index List
		 */
		indexListSize = indexList.size();

		/**
		 * maximal number of item, which could be displayed
		 */
		int indexMaxSize = (int) Math.floor(sideIndexHeight / 25);

		int tmpIndexListSize = indexListSize;

		// /**
		// * handling that case when indexListSize > indexMaxSize, if true display
		// * the half of the side index otherwise full index.
		// */
		// while (tmpIndexListSize > indexMaxSize) {
		// tmpIndexListSize = tmpIndexListSize / 2;
		// }

		/**
		 * computing delta (only a part of items will be displayed to save a place, without compact
		 */
		double delta = indexListSize / tmpIndexListSize;

		String tmpLetter = null;
		Object[] tmpIndexItem = null;

		for (double i = 1; i <= indexListSize; i = i + delta) {
			tmpIndexItem = indexList.get((int) i - 1);
			tmpLetter = tmpIndexItem[0].toString();
			tmpTV = new TextView(mActivity);
			tmpTV.setText(tmpLetter);
			tmpTV.setGravity(Gravity.CENTER);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
			tmpTV.setLayoutParams(params);
			mClockIndexView.addView(tmpTV);
		}

		mClockIndexView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				sideIndexX = event.getX();
				sideIndexY = event.getY();
			
				// TODO
				displayListItem();

				return false;
			}
		});
	}
	  
    /**
     * ListIndexGestureListener method gets the list on scroll.
     */
    private class ListIndexGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {

            /**
             * we know already coordinates of first touch we know as well a
             * scroll distance
             */
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            /**
             * when the user scrolls within our side index, we can show for
             * every position in it a proper item in the list
             */
            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    
}
