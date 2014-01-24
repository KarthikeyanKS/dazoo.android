package com.millicom.secondscreen.content.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.millicom.secondscreen.Consts.ENTITY_TYPE;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.SearchResult;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.content.model.Series;
import com.millicom.secondscreen.content.search.SearchTask.SearchResultCallback;
import com.millicom.secondscreen.customviews.FontTextView;
import com.millicom.secondscreen.http.MiTVCallback;

public class SearchPageListAdapter extends ArrayAdapter<SearchResultItem> implements Filterable {
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
	
	public SearchPageListAdapter(Context context, SearchActivityListeners listener) {
		super(context, 0);
		this.context = context;
		this.aq = new AQuery(context);
		this.mViewListener = listener;
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
		if(count == 0) {
			count = 1; // no results 
		}
		return count;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	

	@Override
	public SearchResultItem getItem(int position) {
		SearchResultItem item = null;
		if(searchResultItems != null) {
			if(searchResultItems.size() > 0) {
				item = searchResultItems.get(position);
			}
		}
		return item;
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
	

	
	public void setTimeString(ViewHolder viewHolder, SearchResultItem resultItem) {
		Broadcast closestBroadcastInTime = resultItem.getNextBroadcast();


		String timeString = "";
		int textColor;
		if(closestBroadcastInTime.isRunning()) {
			timeString = context.getString(R.string.search_on_air_now);
			textColor = context.getResources().getColor(R.color.red);
		} else {
			timeString = closestBroadcastInTime.getStartsInTimeString();
			textColor = context.getResources().getColor(R.color.grey3);
		}
		
		viewHolder.mTime.setTextColor(textColor);
		viewHolder.mTime.setText(timeString);
	}
	
	public void populateProgramView(ViewHolder viewHolder, SearchResultItem resultItem, Program program) {
		String programType = program.getProgramType();
		
		if(programType.equals(Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE)) {
			programType = Consts.DAZOO_PROGRAM_DISPLAY_STRING_TV_EPISODE;
		} else if(programType.equals(Consts.DAZOO_PROGRAM_TYPE_MOVIE)) {
			programType = Consts.DAZOO_PROGRAM_DISPLAY_STRING_MOVIE;
		} else if(programType.equals(Consts.DAZOO_PROGRAM_TYPE_OTHER)) {
			programType = Consts.DAZOO_PROGRAM_DISPLAY_STRING_OTHER;
		} else if(programType.equals(Consts.DAZOO_PROGRAM_TYPE_SPORT)) {
			programType = Consts.DAZOO_PROGRAM_DISPLAY_STRING_SPORT;
		}
		
		viewHolder.mType.setText(programType);
		
		String title = program.getTitle();
		if(title.length() == 0) {
			title = "No title";
		}
		viewHolder.mTitle.setText(title);
		
		setTimeString(viewHolder, resultItem);
	}
		
	public void populateSeriesView(ViewHolder viewHolder, SearchResultItem resultItem, Series series) {
		viewHolder.mType.setText(Consts.DAZOO_SERIES_DISPLAY_STRING);
		
		String title = series.getName();
		viewHolder.mTitle.setText(title);

		setTimeString(viewHolder, resultItem);
	}
	
	public void populateChannelView(ViewHolder viewHolder, Channel channel) {
		viewHolder.mType.setText(Consts.DAZOO_CHANNEL_DISPLAY_STRING);
		
		String title = channel.getName();
		viewHolder.mTitle.setText(title);
		
		viewHolder.mTime.setVisibility(View.GONE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			rowView = inflater.inflate(R.layout.row_search_result, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTitle = (FontTextView) rowView.findViewById(R.id.row_search_result_title);
			viewHolder.mType = (FontTextView) rowView.findViewById(R.id.row_search_result_type);
			viewHolder.mTime = (FontTextView) rowView.findViewById(R.id.row_search_result_time);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (searchResultItems != null && searchResultItems.size() > 0) {

			SearchResultItem resultItem = getItem(position);

			ENTITY_TYPE type = resultItem.getEntityType();
			switch (type) {
			case PROGRAM: {
				Program program = (Program) resultItem.getEntity();
				populateProgramView(holder, resultItem, program);
				break;
			}
			case SERIES: {
				Series series = (Series) resultItem.getEntity();
				populateSeriesView(holder, resultItem, series);
				break;
			}
			case CHANNEL: {
				Channel channel = (Channel) resultItem.getEntity();
				populateChannelView(holder, channel);
				break;
			}
			}
		} else {
			holder.mTitle.setText(context.getString(R.string.search_empty));
		}

		return rowView;
	}

	static class ViewHolder {
		public FontTextView mTitle;
		public FontTextView mType;
		public FontTextView mTime;
	}
	
	private void noSearchResult() {
		//TODO do stuff
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
					
					if(searchResultItems.size() == 0) {
						noSearchResult();
					}
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					clear();
					addAll((List<SearchResultItem>) results.values);
				}
				notifyDataSetChanged();
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

		String completeSearchUrl = String.format(Locale.getDefault(), Consts.MILLICOM_SECONDSCREEN_SEARCH, q);
		
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
		} catch (NullPointerException e) {
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
