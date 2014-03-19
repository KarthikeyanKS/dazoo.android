
package com.mitv.listadapters;



import java.util.ArrayList;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import com.mitv.R;
import com.mitv.enums.ContentTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVProgram;
import com.mitv.models.TVSearchResult;
import com.mitv.models.TVSearchResultEntity;
import com.mitv.models.TVSeries;
import com.mitv.ui.elements.FontTextView;
import com.mitv.utilities.SpannableUtils;



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

		if (searchResultItems != null && !searchResultItems.isEmpty()) {
			item = searchResultItems.get(position);
		}

		return item;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	public void setSearchResultItemsForQueryString(ArrayList<TVSearchResult> searchResultItems, String queryString) {
		this.searchResultItems = searchResultItems;
		this.queryString = queryString;
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

	private boolean setTitleString(ViewHolder viewHolder, String title, String matchedString) {
		Spannable spannable = SpannableUtils.getCustomFontSpannable(title, matchedString);

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
		default:{/* Do nothing */break;}
		}

		viewHolder.type.setText(programTypeString);

		String title = program.getTitle();
		if (title.length() == 0) {
			title = context.getString(R.string.search_no_title);
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
			Spannable episodeTitleAsSpannable = SpannableUtils.getCustomFontSpannable(seriesString, queryString);
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
