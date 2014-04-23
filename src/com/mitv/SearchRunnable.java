
package com.mitv;



import android.util.Log;
import com.mitv.activities.SearchPageActivity;
import com.mitv.managers.ContentManager;
import com.mitv.ui.elements.InstantAutoCompleteView;



public class SearchRunnable
	implements Runnable 
{
	private static final String TAG = SearchRunnable.class.getName();
	
	
	private SearchPageActivity activity;
	private InstantAutoCompleteView instantAutoCompleteView;
	private boolean cancelled = false;
	
	
	
	public SearchRunnable(SearchPageActivity activity, InstantAutoCompleteView instantAutoCompleteView)
	{
		this.activity = activity;
		this.instantAutoCompleteView = instantAutoCompleteView;
	}
	
	
	
	@Override
	public void run() 
	{
		if(!cancelled) 
		{
			String searchQuery = instantAutoCompleteView.getText().toString();
			
			activity.setLoading();
			
			Log.d(TAG, "Search was not cancelled, calling ContentManager search!!!");
			
			ContentManager.sharedInstance().getElseFetchFromServiceSearchResultForSearchQuery(activity, false, searchQuery);
		} 
		else 
		{
			Log.d(TAG, "Search runnable was cancelled");
		}
	}

	public void cancel() 
	{
		cancelled = true;
	}
}