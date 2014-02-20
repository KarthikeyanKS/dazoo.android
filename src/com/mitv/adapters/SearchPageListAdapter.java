package com.mitv.adapters;

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
import com.millicom.mitv.activities.SearchPageActivity;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.customviews.CustomTypefaceSpan;
import com.mitv.customviews.FontTextView;
import com.mitv.handlers.SearchActivityListeners;
import com.mitv.http.MiTVCallback;
import com.mitv.http.SSHttpClient;
import com.mitv.manager.FontManager;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldProgram;
import com.mitv.model.OldSearchResult;
import com.mitv.model.OldSearchResultItem;
import com.mitv.model.OldSeries;
import com.mitv.model.OldTVChannel;

public class SearchPageListAdapter extends ArrayAdapter<OldSearchResultItem> implements Filterable {
	private static final String TAG = "SearchListAdapter";
	
	private ArrayList<OldSearchResultItem> mSearchResultItems;
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
	public OldSearchResultItem getItem(int position) {
		OldSearchResultItem item = null;
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
	
	public ArrayList<OldSearchResultItem> getSearchResultItems() {
		return mSearchResultItems;
	}

	public void setSearchResultItems(ArrayList<OldSearchResultItem> searchResultItems) {
		this.mSearchResultItems = searchResultItems;
	}
	

	
	private void setTimeString(ViewHolder viewHolder, OldSearchResultItem resultItem) {
		OldBroadcast closestBroadcastInTime = resultItem.getNextBroadcast();


		String timeString = "";
		int textColor = mContext.getResources().getColor(R.color.grey3);
		if (closestBroadcastInTime != null) {
			if (closestBroadcastInTime.isRunning()) {
				timeString = mContext.getString(R.string.search_on_air_now);
				textColor = mContext.getResources().getColor(R.color.red);
			} else {
				timeString = closestBroadcastInTime.getStartsInTimeString();
			}
		} else {
			timeString = mContext.getString(R.string.search_no_upcoming_broadcasts);
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
		
	private void populateProgramView(ViewHolder viewHolder, OldSearchResultItem resultItem, OldProgram program) {
		String programType = program.getProgramType();
		
		if(programType.equals(Consts.PROGRAM_TYPE_TV_EPISODE)) {
			programType = mContext.getString(R.string.search_result_tv_episode);
		} else if(programType.equals(Consts.PROGRAM_TYPE_MOVIE)) {
			programType = mContext.getString(R.string.search_result_movie);
		} else if(programType.equals(Consts.PROGRAM_TYPE_OTHER)) {
			programType = program.getCategory();
		} else if(programType.equals(Consts.PROGRAM_TYPE_SPORT)) {
			programType = mContext.getString(R.string.search_result_sport);
		}
		
		viewHolder.mType.setText(programType);
		
		String title = program.getTitle();
		if(title.length() == 0) {
			title = "No title";
		}
		
		setTitleString(viewHolder, title);
		
		setTimeString(viewHolder, resultItem);
	}
		
	private void populateSeriesView(ViewHolder viewHolder, OldSearchResultItem resultItem, OldSeries series) {
		viewHolder.mType.setText(mContext.getString(R.string.search_result_series));
		
		String title = series.getName();
		setTitleString(viewHolder, title);

		setTimeString(viewHolder, resultItem);
	}
	
	private void populateChannelView(ViewHolder viewHolder, OldTVChannel channel) {
		viewHolder.mType.setText(mContext.getString(R.string.search_result_channel));
		
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
			OldSearchResultItem resultItem = getItem(position);

			ContentTypeEnum type = resultItem.getEntityType();
			switch (type) {
			case PROGRAM: {
				OldProgram program = (OldProgram) resultItem.getEntity();
				populateProgramView(holder, resultItem, program);
				break;
			}
			case SERIES: {
				OldSeries series = (OldSeries) resultItem.getEntity();
				populateSeriesView(holder, resultItem, series);
				break;
			}
			case CHANNEL: {
				OldTVChannel channel = (OldTVChannel) resultItem.getEntity();
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
	
	public ArrayList<OldSearchResultItem> autocomplete(String q) {

		
		
		
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

		String completeSearchUrl = String.format(Locale.getDefault(), Consts.URL_SEARCH_OLD, q);
		completeSearchUrl = SSHttpClient.urlByAppendingLocaleAndTimezoneWithAndChar(completeSearchUrl);
		
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
		
		OldSearchResult searchResult = new OldSearchResult(jsonFromString); //gson.fromJson(result, SearchResult.class);
		ArrayList<OldSearchResultItem> resultItems = searchResult.getItems();
		
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
