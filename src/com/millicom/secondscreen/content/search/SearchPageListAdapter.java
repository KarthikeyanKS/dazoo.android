package com.millicom.secondscreen.content.search;

import java.util.ArrayList;
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
import com.millicom.secondscreen.Consts.ENTITY_TYPE;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.SearchResult;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.content.model.Series;
import com.millicom.secondscreen.customviews.FontTextView;
import com.millicom.secondscreen.http.MiTVCallback;
import com.millicom.secondscreen.manager.FontManager;
import com.millicom.secondscreen.utilities.CustomTypefaceSpan;

public class SearchPageListAdapter extends ArrayAdapter<SearchResultItem> implements Filterable {
	private static final String TAG = "SearchListAdapter";
	
	private ArrayList<SearchResultItem> mSearchResultItems;
	private String mQuery;
	private String mLastSearch = "";
	private Context mContext;
	private AQuery mAq;
	private SearchActivityListeners mViewListener;
	private static LayoutInflater mInflater;
	
	public SearchPageListAdapter(Context context, SearchActivityListeners listener) {
		super(context, 0);
		this.mContext = context;
		this.mAq = new AQuery(context);
		this.mViewListener = listener;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
		
	@Override
	public int getCount() {
		int count = 0;
		if(mSearchResultItems != null) {
			count = mSearchResultItems.size();
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
		if(mSearchResultItems != null) {
			if(mSearchResultItems.size() > 0) {
				item = mSearchResultItems.get(position);
			}
		}
		return item;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}
	
	public ArrayList<SearchResultItem> getSearchResultItems() {
		return mSearchResultItems;
	}

	public void setSearchResultItems(ArrayList<SearchResultItem> searchResultItems) {
		this.mSearchResultItems = searchResultItems;
	}
	

	
	private void setTimeString(ViewHolder viewHolder, SearchResultItem resultItem) {
		Broadcast closestBroadcastInTime = resultItem.getNextBroadcast();


		String timeString = "";
		int textColor;
		if(closestBroadcastInTime.isRunning()) {
			timeString = mContext.getString(R.string.search_on_air_now);
			textColor = mContext.getResources().getColor(R.color.red);
		} else {
			timeString = closestBroadcastInTime.getStartsInTimeString();
			textColor = mContext.getResources().getColor(R.color.grey3);
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
		spannable.setSpan( new CustomTypefaceSpan(FontManager.getFontLight(mContext)), partOneStart, partOneEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan( new CustomTypefaceSpan(FontManager.getFontBold(mContext)), partTwoStart, partTwoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan( new CustomTypefaceSpan(FontManager.getFontLight(mContext)), partThreeStart, partThreeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
			rowView = mInflater.inflate(R.layout.row_search_result, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mMetaDataContainer = (LinearLayout) rowView.findViewById(R.id.row_search_result_meta_data_container);
			viewHolder.mTitle = (FontTextView) rowView.findViewById(R.id.row_search_result_title);
			viewHolder.mType = (FontTextView) rowView.findViewById(R.id.row_search_result_type);
			viewHolder.mTime = (FontTextView) rowView.findViewById(R.id.row_search_result_time);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (mSearchResultItems != null && mSearchResultItems.size() > 0) {
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
			holder.mTitle.setText(mContext.getString(R.string.search_empty));
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
					if (!mQuery.equals(mLastSearch) && !(mQuery.length() == 0)) {
						
						// Retrieve the autocomplete results.
						mSearchResultItems = autocomplete(constraint.toString());
						mLastSearch = mQuery;
					}
					// Assign the data to the FilterResults
					filterResults.values = mSearchResultItems;
					filterResults.count = mSearchResultItems.size();
					
					if(mSearchResultItems.size() == 0) {
						noSearchResult();
					}
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				}
			}
		};
		return filter;
	}
	
	public ArrayList<SearchResultItem> autocomplete(String q) {

		
		
		
		((SearchPageActivity)mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mViewListener != null) {
					mViewListener.showProgressLoading(true);
				}
			}
		});
		
		MiTVCallback cb = new MiTVCallback<String>() {
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
		
		mAq.ajax(completeSearchUrl, String.class, -1, cb);
		cb.block();
		
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
		
		((SearchPageActivity)mContext).runOnUiThread(new Runnable() {
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
