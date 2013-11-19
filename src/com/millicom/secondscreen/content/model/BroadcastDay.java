package com.millicom.secondscreen.content.model;

import java.util.List;

public class BroadcastDay implements Comparable<BroadcastDay> {
	public final String mTvDateDate;

	private final List<Broadcast>	mBroadcastList;

	private boolean				mSorted;

	public BroadcastDay(String tvDateDate, List<Broadcast> broadcastList) {
		this.mTvDateDate = tvDateDate;
		this.mBroadcastList = broadcastList;
	}

	public List<Broadcast> getSessionList() {
		return mBroadcastList;
	}

	public void add(Broadcast broadcast) {
		mBroadcastList.add(broadcast);
	}

	public boolean isSorted() {
		return mSorted;
	}

	public void sort() {

		if (isSorted()) {
			return;
		}

		Broadcast.fixBroadcastList(mBroadcastList);
		mSorted = true;
	}

	@Override
	public int compareTo(BroadcastDay another) {
		return this.mTvDateDate.compareTo(another.mTvDateDate);
	}
}
