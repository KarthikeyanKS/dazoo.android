package com.millicom.secondscreen.content.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;
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
import com.millicom.secondscreen.manager.FontManager;
import com.millicom.secondscreen.utilities.CustomTypefaceSpan;

public class SearchPageListAdapter extends ArrayAdapter<SearchResultItem> implements Filterable {
	private static final String TAG = "SearchListAdapter";
	
	private ArrayList<SearchResultItem> searchResultItems;
	private String mQuery;
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
	

	
	private void setTimeString(ViewHolder viewHolder, SearchResultItem resultItem) {
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
	
	private Spannable getCustomFontSpannableUsingThreeStrings(String beforeBold, String toBold, String afterBold) {		
		int partOneStart = 0;
		int partOneEnd = beforeBold.length();
		int partTwoStart = partOneEnd;
		int partTwoEnd = partTwoStart + toBold.length();
		int partThreeStart = partTwoEnd;
		int partThreeEnd = partThreeStart + afterBold.length();
		
		// Create a new spannable with the two strings
		Spannable spannable = new SpannableString(beforeBold + toBold + afterBold);

		// Set the custom typeface to span over a section of the spannable object
		spannable.setSpan( new CustomTypefaceSpan(FontManager.getFontLight(context)), partOneStart, partOneEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan( new CustomTypefaceSpan(FontManager.getFontBold(context)), partTwoStart, partTwoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan( new CustomTypefaceSpan(FontManager.getFontLight(context)), partThreeStart, partThreeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannable;
	}
	
	private Spannable getCustomFontSpannable(String title, String matchedString) {
		Spannable spannable = null;
		if (title != null && matchedString != null && title.length() > 0 && matchedString.length() > 0) {
			String titleLowercaseOnly = title.toLowerCase(SecondScreenApplication.getCurrentLocale());
			String matchedStringLowercaseOnly = matchedString.toLowerCase(SecondScreenApplication.getCurrentLocale());
			int matchedStringStartIndex = titleLowercaseOnly.indexOf(matchedStringLowercaseOnly);
			if (matchedStringStartIndex >= 0) {
				/* Title contains matchedString */
				String beforeBold = title.substring(0, matchedStringStartIndex);
				
				int toBoldStartIndex = beforeBold.length();
				int toBoldEndIndex = toBoldStartIndex + matchedString.length();
				String toBold = title.substring(toBoldStartIndex, toBoldEndIndex);
				
				int afterBoldStartIndex = beforeBold.length() + toBold.length();
				String afterBold = title.substring(afterBoldStartIndex, title.length());
				spannable = getCustomFontSpannableUsingThreeStrings(beforeBold, toBold, afterBold);
			}
		}
		return spannable;
	}
	
	private void setTitleString(ViewHolder viewHolder, String title, String matchedString) {
		Spannable spannable = getCustomFontSpannable(title, matchedString);
		
		if(spannable != null) {
			viewHolder.mTitle.setText(spannable);
		} else {
			viewHolder.mTitle.setText(title);
		}
	}
	
	private void setTitleString(ViewHolder viewHolder, String title) {
		setTitleString(viewHolder, title, mQuery);
	}
		
	private void populateProgramView(ViewHolder viewHolder, SearchResultItem resultItem, Program program) {
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
		
		setTitleString(viewHolder, title);
		
		setTimeString(viewHolder, resultItem);
	}
		
	private void populateSeriesView(ViewHolder viewHolder, SearchResultItem resultItem, Series series) {
		viewHolder.mType.setText(Consts.DAZOO_SERIES_DISPLAY_STRING);
		
		String title = series.getName();
		setTitleString(viewHolder, title);

		setTimeString(viewHolder, resultItem);
	}
	
	private void populateChannelView(ViewHolder viewHolder, Channel channel) {
		viewHolder.mType.setText(Consts.DAZOO_CHANNEL_DISPLAY_STRING);
		
		String title = channel.getName();
		setTitleString(viewHolder, title);
		
		viewHolder.mTime.setVisibility(View.GONE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			rowView = inflater.inflate(R.layout.row_search_result, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mMetaDataContainer = (LinearLayout) rowView.findViewById(R.id.row_search_result_meta_data_container);
			viewHolder.mTitle = (FontTextView) rowView.findViewById(R.id.row_search_result_title);
			viewHolder.mType = (FontTextView) rowView.findViewById(R.id.row_search_result_type);
			viewHolder.mTime = (FontTextView) rowView.findViewById(R.id.row_search_result_time);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (searchResultItems != null && searchResultItems.size() > 0) {
			holder.mMetaDataContainer.setVisibility(View.VISIBLE);
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
			holder.mMetaDataContainer.setVisibility(View.GONE);
			holder.mTitle.setText(context.getString(R.string.search_empty));
		}

		return rowView;
	}

	static class ViewHolder {
		public LinearLayout mMetaDataContainer;
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
					mQuery = constraint.toString().trim();
					if (!mQuery.equals(lastSearch) && !(mQuery.length() == 0)) {
						
						// Retrieve the autocomplete results.
						searchResultItems = autocomplete(constraint.toString());
						lastSearch = mQuery;
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
