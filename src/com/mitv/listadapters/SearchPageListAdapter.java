
package com.mitv.listadapters;



import java.util.ArrayList;

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

import com.mitv.FontManager;
import com.mitv.R;
import com.mitv.enums.ContentTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVProgram;
import com.mitv.models.TVSearchResult;
import com.mitv.models.TVSearchResultEntity;
import com.mitv.models.TVSeries;
import com.mitv.ui.elements.CustomTypefaceSpan;
import com.mitv.ui.elements.FontTextView;
import com.mitv.utilities.LanguageUtils;



public class SearchPageListAdapter extends ArrayAdapter<TVSearchResult> implements Filterable {
	@SuppressWarnings("unused")
	private static final String TAG = SearchPageListAdapter.class.getName();

	private ArrayList<TVSearchResult> searchResultItems;
	private String queryString;
	private Context context;
	private static LayoutInflater inflater;

	public SearchPageListAdapter(Context context) {
		super(context, 0);
		this.context = context;
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

		if (title != null && matchedString != null && title.length() > 0 && matchedString.length() > 0) 
		{
			String titleLowercaseOnly = title.toLowerCase(LanguageUtils.getCurrentLocale());

			String matchedStringLowercaseOnly = matchedString.toLowerCase(LanguageUtils.getCurrentLocale());

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

	//

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

	private boolean setTitleString(ViewHolder viewHolder, String title, String matchedString) {
		Spannable spannable = getCustomFontSpannable(title, matchedString);

		boolean titleMatched = false;

		if (spannable != null) {
			viewHolder.title.setText(spannable);
			titleMatched = true;
		} else {
			viewHolder.title.setText(title);
		}

		return titleMatched;
	}

	private boolean setTitleString(ViewHolder viewHolder, String title) {
		return setTitleString(viewHolder, title, queryString);
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

	private void populateSeriesView(ViewHolder viewHolder, TVSearchResult resultItem, boolean fromProgram) {
		TVBroadcastWithChannelInfo closestBroadcastInTime = resultItem.getNextBroadcast();
		TVSearchResultEntity searchResultEntity = resultItem.getEntity();

		TVProgram program = null;
		if (fromProgram) {
			program = searchResultEntity.getProgram();
		} else {
			program = closestBroadcastInTime.getProgram();
		}

		TVSeries series = program.getSeries();
		String episodeTitle = program.getTitle();

		StringBuilder sb = new StringBuilder(context.getString(R.string.search_result_tv_episode));
		if (!episodeTitle.isEmpty()) {
			sb.append(" - ");
			sb.append(episodeTitle);
		}

		String seriesString = sb.toString();

		String title = series.getName();
		boolean titleMatched = setTitleString(viewHolder, title);
		if (titleMatched) {
			viewHolder.type.setText(seriesString);
		} else {
			Spannable episodeTitleAsSpannable = getCustomFontSpannable(seriesString, queryString);
			if (episodeTitleAsSpannable != null) {
				viewHolder.type.setText(episodeTitleAsSpannable);
			} else {
				viewHolder.type.setText(seriesString);
			}
		}

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

		holder.time.setVisibility(View.VISIBLE);
		holder.type.setVisibility(View.VISIBLE);
		holder.title.setVisibility(View.VISIBLE);

		if (searchResultItems != null && searchResultItems.size() > 0) {
			holder.typeAndTimeContainer.setVisibility(View.VISIBLE);
			TVSearchResult resultItem = getItem(position);

			ContentTypeEnum type = resultItem.getEntityType();

			TVSearchResultEntity searchEntity = resultItem.getEntity();

			switch (type) {
			case PROGRAM: {
				TVProgram program = searchEntity.getProgram();

				if (program.getSeries() != null) {
					populateSeriesView(holder, resultItem, true);
				} else {
					populateProgramView(holder, resultItem, program);
				}

				break;
			}
			case SERIES: {
				populateSeriesView(holder, resultItem, false);
				break;
			}
			case CHANNEL: {
				TVChannel channel = searchEntity.getChannel();
				populateChannelView(holder, channel);
				break;
			}

			}
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

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();

				// Assign the data to the FilterResults
				filterResults.values = searchResultItems;
				filterResults.count = searchResultItems.size();
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
}
