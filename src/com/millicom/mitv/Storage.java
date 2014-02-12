package com.millicom.mitv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.util.SparseArray;

import com.millicom.mitv.models.TVChannelId;
import com.mitv.model.AdzerkAd;
import com.mitv.model.Broadcast;
import com.mitv.model.ChannelGuide;
import com.mitv.model.FeedItem;
import com.mitv.model.TVChannel;
import com.mitv.model.TVDate;
import com.mitv.model.TVTag;

public class Storage {

	private ArrayList<TVTag> tvTags;
	private ArrayList<TVDate> tvDates;
	private ArrayList<TVChannelId> tvChannelIds;
	private ArrayList<TVChannel> tvChannels;
	private ArrayList<ChannelGuide> channelGuides;

	private ArrayList<String> likeIds;
	
	private Calendar likeIdsFetchedTimestamp;
	private Calendar userChannelIdsFetchedTimestamp;
	
	private ArrayList<FeedItem> activityFeed;
	private ArrayList<Broadcast> popularFeed;
	
	private String userToken;
	
	/* Ads */
	private HashMap<String, SparseArray<AdzerkAd>> mFragmentToAdsMap;

	public ArrayList<TVTag> getTvTags() {
		return tvTags;
	}

	public void setTvTags(ArrayList<TVTag> tvTags) {
		this.tvTags = tvTags;
	}

	public ArrayList<TVDate> getTvDates() {
		return tvDates;
	}

	public void setTvDates(ArrayList<TVDate> tvDates) {
		this.tvDates = tvDates;
	}

	public ArrayList<TVChannelId> getTvChannelIds() {
		return tvChannelIds;
	}

	public void setTvChannelIds(ArrayList<TVChannelId> tvChannelIds) {
		this.tvChannelIds = tvChannelIds;
	}

	public ArrayList<TVChannel> getTvChannels() {
		return tvChannels;
	}

	public void setTvChannels(ArrayList<TVChannel> tvChannels) {
		this.tvChannels = tvChannels;
	}

	public ArrayList<ChannelGuide> getChannelGuides() {
		return channelGuides;
	}

	public void setChannelGuides(ArrayList<ChannelGuide> channelGuides) {
		this.channelGuides = channelGuides;
	}

	public ArrayList<String> getLikeIds() {
		return likeIds;
	}

	public void setLikeIds(ArrayList<String> likeIds) {
		this.likeIds = likeIds;
	}

	public Calendar getLikeIdsFetchedTimestamp() {
		return likeIdsFetchedTimestamp;
	}

	public void setLikeIdsFetchedTimestamp(Calendar likeIdsFetchedTimestamp) {
		this.likeIdsFetchedTimestamp = likeIdsFetchedTimestamp;
	}

	public Calendar getUserChannelIdsFetchedTimestamp() {
		return userChannelIdsFetchedTimestamp;
	}

	public void setUserChannelIdsFetchedTimestamp(Calendar userChannelIdsFetchedTimestamp) {
		this.userChannelIdsFetchedTimestamp = userChannelIdsFetchedTimestamp;
	}

	public ArrayList<FeedItem> getActivityFeed() {
		return activityFeed;
	}

	public void setActivityFeed(ArrayList<FeedItem> activityFeed) {
		this.activityFeed = activityFeed;
	}

	public ArrayList<Broadcast> getPopularFeed() {
		return popularFeed;
	}

	public void setPopularFeed(ArrayList<Broadcast> popularFeed) {
		this.popularFeed = popularFeed;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public HashMap<String, SparseArray<AdzerkAd>> getmFragmentToAdsMap() {
		return mFragmentToAdsMap;
	}

	public void setmFragmentToAdsMap(HashMap<String, SparseArray<AdzerkAd>> mFragmentToAdsMap) {
		this.mFragmentToAdsMap = mFragmentToAdsMap;
	}
	
	
	
}
