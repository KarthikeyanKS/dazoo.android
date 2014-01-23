package com.millicom.secondscreen.content.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.SearchResult;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.content.search.SearchTask.SearchResultCallback;
import com.millicom.secondscreen.customviews.FontTextView;
import com.millicom.secondscreen.http.MiTVCallback;

public class SearchPageListAdapter extends ArrayAdapter<SearchResultItem> implements Filterable {
	
	public interface SearchActivityListeners {
		public void showProgressLoading(boolean isLoading);
		
		public void isRecentListEmpty(boolean isEmpty);
	}

	private static final String TAG = "SearchListAdapter";
	
	private ArrayList<SearchResultItem> searchResultItems;
	private String query;
	private String lastSearch = "";
	private Context context;
	private AQuery aq;
	private SearchActivityListeners mViewListener;
	private static LayoutInflater inflater;
	private SearchTask mSearchTask;
	private MiTVCallback cb;
	
	public SearchPageListAdapter(Context context) {
		super(context, 0);
		this.context = context;
		this.aq = new AQuery(context);
//		this.mViewListener = (SearchActivityListeners) context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.mSearchTask = initSearchTask();
	}
	
	private SearchResultCallback initCallback() {
		return new SearchResultCallback() {
			@Override
			public void onResult(SearchResult result) {
				boolean noResult = true;

				if (result != null) {
					String suggestion = result.getSuggestion(); // TODO
																// use
																// this?
					ArrayList<SearchResultItem> items = result.getItems();

					if (items != null && !items.isEmpty()) {
						noResult = false;
						setSearchResultItems(items);
					}

					Log.e(TAG, "Suggestion: " + suggestion);
				}

				if (noResult) {
					// TODO display "no result found"
				}

				notifyDataSetChanged();
			}
		};
	}
	
	private SearchTask initSearchTask() {
		return new SearchTask(initCallback());
	}
	
	@Override
	public int getCount() {
		int count = 0;
		if(searchResultItems != null) {
			count = searchResultItems.size();
		}
		return count;
	}

	@Override
	public SearchResultItem getItem(int position) {
		return searchResultItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}
	
	public ArrayList<SearchResultItem> getSearchResultItems() {
		return searchResultItems;
	}

	public void setSearchResultItems(ArrayList<SearchResultItem> searchResultItems) {
		this.searchResultItems = searchResultItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.row_search_result, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTextView = (FontTextView) rowView.findViewById(R.id.row_search_result_textview);

			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		SearchResultItem resultItem = getItem(position);
		String displayText = resultItem.getDisplayText();
		holder.mTextView.setText(displayText);
		
		return rowView;
	}

	static class ViewHolder {
		public FontTextView mTextView;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					query = constraint.toString().trim();
					if (!query.equals(lastSearch) && !(query.length() == 0)) {
						
						// Retrieve the autocomplete results.
						searchResultItems = autocomplete(constraint.toString());
						lastSearch = query;
					}
					// Assign the data to the FilterResults
					filterResults.values = searchResultItems;
					filterResults.count = searchResultItems.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					clear();
					addAll((List<SearchResultItem>) results.values);
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}
	
	public ArrayList<SearchResultItem> autocomplete(String q) {

		
		
		
		((SearchPageActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mViewListener != null) {
					mViewListener.showProgressLoading(true);
				}
			}
		});
		
		cb = new MiTVCallback<String>() {
			@Override
			public void mitvCallback(String url, String json, AjaxStatus status) {				
		    	switch (status.getCode()) {
					case Consts.GOOD_RESPONSE:
						Log.d(TAG, "Successful");
						break;
					case Consts.BAD_RESPONSE:
						Log.e(TAG, "bad resp");
						break;
					case Consts.BAD_RESPONSE_MISSING_TOKEN:
						Log.e(TAG, "bad resp missing token");
						break;
					case Consts.BAD_RESPONSE_INVALID_TOKEN:
						Log.e(TAG, "bad resp invalid token");
						break;
					case Consts.BAD_RESPONSE_TIMEOUT:
						Log.e(TAG, "bad resp timeout");
						break;
						
					default:
						break;
					}
			
			}
		};

		String completeSearchUrl = new StringBuilder(Consts.MILLICOM_SECONDSCREEN_SEARCH).append(q).toString();
		
		aq.ajax(completeSearchUrl, String.class, -1, cb);
		cb.block();
		
		
//		cb.url(completeSearchUrl);
//		aq.sync(cb);
		
		String jsonString = (String) cb.getResult();
		JSONObject jsonFromString = null;
		try {
			jsonFromString = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		SearchResult searchResult = new SearchResult(jsonFromString); //gson.fromJson(result, SearchResult.class);
		ArrayList<SearchResultItem> resultItems = searchResult.getItems();
		
		((SearchPageActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mViewListener != null) {
					mViewListener.showProgressLoading(false);
				}
			}
		});
		
		return resultItems;
	}
}
