package com.mitv.adapters;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.millicom.mitv.activities.SearchPageActivity;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.TVSearchResult;
import com.millicom.mitv.models.TVSeries;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.customviews.CustomTypefaceSpan;
import com.mitv.customviews.FontTextView;
import com.mitv.handlers.SearchActivityListeners;
import com.mitv.http.SSHttpClient;
import com.mitv.manager.FontManager;

public class SearchPageListAdapter extends ArrayAdapter<TVSearchResult> implements Filterable {
	@SuppressWarnings("unused")
	private static final String TAG = SearchPageListAdapter.class.getName();

	private ArrayList<TVSearchResult> searchResultItems;
	private String queryString;
	private String lastSearch = "";
	private Context context;
	private AQuery aQuery;
	private SearchActivityListeners viewListener;
	private static LayoutInflater inflater;

	public SearchPageListAdapter(Context context, SearchActivityListeners listener) {
		super(context, 0);
		this.context = context;
		this.aQuery = new AQuery(context);
		this.viewListener = listener;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		int count = 0;

		if (searchResultItems != null) {
			count = searchResultItems.size();
		}

		if (count == 0) {
			count = 1; // no results
		}

		return count;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public TVSearchResult getItem(int position) {
		TVSearchResult item = null;

		if (searchResultItems != null) {
			if (searchResultItems.size() > 0) {
				item = searchResultItems.get(position);
			}
		}

		return item;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	public ArrayList<TVSearchResult> getSearchResultItems() {
		return searchResultItems;
	}

	public void setSearchResultItems(ArrayList<TVSearchResult> searchResultItems) {
		this.searchResultItems = searchResultItems;
	}

	private void setTimeString(ViewHolder viewHolder, TVSearchResult resultItem) {
		TVBroadcastWithChannelInfo closestBroadcastInTime = resultItem.getNextBroadcast();

		String timeString = "";

		int textColor = context.getResources().getColor(R.color.grey3);

		if (closestBroadcastInTime != null) {
			if (closestBroadcastInTime.isBroadcastCurrentlyAiring()) {
				timeString = context.getString(R.string.search_on_air_now);

				textColor = context.getResources().getColor(R.color.red);
			} else {
				timeString = closestBroadcastInTime.getStartingTimeAsString();
			}
		} else {
			timeString = context.getString(R.string.search_no_upcoming_broadcasts);
		}

		viewHolder.time.setTextColor(textColor);
		viewHolder.time.setText(timeString);
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
		spannable.setSpan(new CustomTypefaceSpan(FontManager.getFontLight(context)), partOneStart, partOneEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan(FontManager.getFontBold(context)), partTwoStart, partTwoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan(FontManager.getFontLight(context)), partThreeStart, partThreeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

		if (spannable != null) {
			viewHolder.title.setText(spannable);
		} else {
			viewHolder.title.setText(title);
		}
	}

	private void setTitleString(ViewHolder viewHolder, String title) {
		setTitleString(viewHolder, title, queryString);
	}

	private void populateProgramView(ViewHolder viewHolder, TVSearchResult resultItem, TVProgram program) {
		ProgramTypeEnum programType = program.getProgramType();

		String programTypeString = null;

		switch (programType) {
		case TV_EPISODE: {
			programTypeString = context.getString(R.string.search_result_tv_episode);
			break;
		}
		case MOVIE: {
			programTypeString = context.getString(R.string.search_result_movie);
			break;
		}
		case OTHER: {
			programTypeString = program.getCategory();
			break;
		}
		case SPORT: {
			programTypeString = context.getString(R.string.search_result_sport);
			break;
		}
		}

		viewHolder.type.setText(programTypeString);

		String title = program.getTitle();

		if (title.length() == 0) {
			title = "No title";
		}

		setTitleString(viewHolder, title);

		setTimeString(viewHolder, resultItem);
	}

	private void populateSeriesView(ViewHolder viewHolder, TVSearchResult resultItem, TVSeries series) {
		viewHolder.type.setText(context.getString(R.string.search_result_series));

		String title = series.getName();
		setTitleString(viewHolder, title);

		setTimeString(viewHolder, resultItem);
	}

	private void populateChannelView(ViewHolder viewHolder, TVChannel channel) {
		viewHolder.type.setText(context.getString(R.string.search_result_channel));

		String title = channel.getName();
		setTitleString(viewHolder, title);

		viewHolder.time.setVisibility(View.GONE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			rowView = inflater.inflate(R.layout.row_search_result, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.typeAndTimeContainer = (LinearLayout) rowView.findViewById(R.id.row_search_result_meta_data_container);
			viewHolder.title = (FontTextView) rowView.findViewById(R.id.row_search_result_title);
			viewHolder.type = (FontTextView) rowView.findViewById(R.id.row_search_result_type);
			viewHolder.time = (FontTextView) rowView.findViewById(R.id.row_search_result_time);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (searchResultItems != null && searchResultItems.size() > 0) {
			holder.typeAndTimeContainer.setVisibility(View.VISIBLE);
			TVSearchResult resultItem = getItem(position);

			ContentTypeEnum type = resultItem.getEntityType();

			// TODO NewArc fix TVSearchResultEntity to have support not only for series
			// switch (type) {
			// case PROGRAM: {
			// TVProgram program = (TVProgram) resultItem.getEntity();
			// populateProgramView(holder, resultItem, program);
			// break;
			// }
			// case SERIES: {
			// TVSeries series = (TVSeries) resultItem.getEntity();
			// populateSeriesView(holder, resultItem, series);
			// break;
			// }
			// case CHANNEL: {
			// TVChannel channel = (TVChannel) resultItem.getEntity();
			// populateChannelView(holder, channel);
			// break;
			// }
			// }
		} else {
			holder.typeAndTimeContainer.setVisibility(View.GONE);
			holder.title.setText(context.getString(R.string.search_empty));
		}

		return rowView;
	}

	static class ViewHolder {
		public LinearLayout typeAndTimeContainer;
		public FontTextView title;
		public FontTextView type;
		public FontTextView time;
	}

	private void noSearchResult() {
		// TODO do stuff
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();

				if (constraint != null) {
					queryString = constraint.toString().trim();

					if (!queryString.equals(lastSearch) && !(queryString.length() == 0)) {
						// TODO NewArc finialize this!
						// ContentManager.sharedInstance().performSearch();

						// Retrieve the autocomplete results.
						searchResultItems = autocomplete(constraint.toString());
						lastSearch = queryString;
					}

					// Assign the data to the FilterResults
					filterResults.values = searchResultItems;
					filterResults.count = searchResultItems.size();

					if (searchResultItems.size() == 0) {
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

	public ArrayList<TVSearchResult> autocomplete(String q) {
		((SearchPageActivity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (viewListener != null) {
					viewListener.showProgressLoading(true);
				}
			}
		});

		// TODO use ContentManager and approriate task for search
		// MiTVCallback cb = null;//new MiTVCallback<String>() {
		// @Override
		// public void mitvCallback(String url, String json, AjaxStatus status) {
		// switch (status.getCode()) {
		// case Consts.GOOD_RESPONSE:
		// Log.d(TAG, "Successful");
		// break;
		// case Consts.BAD_RESPONSE:
		// Log.e(TAG, "bad resp");
		// break;
		// case Consts.BAD_RESPONSE_MISSING_TOKEN:
		// Log.e(TAG, "bad resp missing token");
		// break;
		// case Consts.BAD_RESPONSE_INVALID_TOKEN:
		// Log.e(TAG, "bad resp invalid token");
		// break;
		// case Consts.BAD_RESPONSE_TIMEOUT:
		// Log.e(TAG, "bad resp timeout");
		// break;
		//
		// default:
		// break;
		// }
		//
		// }
		// };

		String completeSearchUrl = String.format(Locale.getDefault(), Consts.URL_SEARCH_OLD, q);

		completeSearchUrl = SSHttpClient.urlByAppendingLocaleAndTimezoneWithAndChar(completeSearchUrl);

		// mAq.ajax(completeSearchUrl, String.class, -1, cb);
		// cb.block();
		// TODO NewArc use a-query?

		String jsonString = null;// (String) cb.getResult();

		JSONObject jsonFromString = null;

		try {
			jsonFromString = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// TVSearchResult searchResult = new TVSearchResult(jsonFromString); //gson.fromJson(result, SearchResult.class);
		// ArrayList<TVSearchResult> resultItems = searchResult.getItems();

		((SearchPageActivity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (viewListener != null) {
					viewListener.showProgressLoading(false);
				}
			}
		});

		// return resultItems;
		return null;
	}
}
