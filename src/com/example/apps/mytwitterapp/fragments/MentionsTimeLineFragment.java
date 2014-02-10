package com.example.apps.mytwitterapp.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.apps.mytwitterapp.EndlessScrollListener;
import com.example.apps.mytwitterapp.R;
import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TwitterClientapp;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

// To CHECK: check if Tweet.parse if fine or if you need another class for mentions.

public class MentionsTimeLineFragment extends BaseFragment {

	BaseFragment base_frag_mentions;
	private static boolean call_done = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			base_frag_mentions = (BaseFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fl_container);
			// this needs to be called only once
			if (isNetworkAvailable(getActivity().getApplicationContext())) {
				fetchMentionsTimeline();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if ((base_frag_mentions.getLv_tweetTimeline()!= null) && call_done){
			base_frag_mentions.getLv_tweetTimeline().setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					Log.d(TAG, "Refreshing mentions");
					fetchRefreshMentions();
					base_frag_mentions.getLv_tweetTimeline().onRefreshComplete();
				}
			});
			setupEndlessScrolling();
			fetchRefreshMentions();
			call_done = false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		call_done =true;
	}

	private void fetchMoreMentionsData(){
		TwitterClientapp.getRestClient().getMentionsMoreTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray mentions) {
				Log.d(TAG, "Successfully fetched MORE mentions: " + mentions.toString());
				base_frag_mentions.getAdapter().addAll(Tweet.parseJsonArray(mentions, base_frag_mentions));
			}
		}, base_frag_mentions.max_id);
	}

	private void fetchRefreshMentions(){
		TwitterClientapp.getRestClient().getMentionsRefreshTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray mentions) {
				if (mentions.length() > 0) {
					Log.d(TAG, "Successfully fetched REFRESH: " + mentions.toString());
					base_frag_mentions.getTweet_results().addAll(0, Tweet.parseJsonArray(mentions, base_frag_mentions));
					base_frag_mentions.getAdapter().notifyDataSetChanged();

				} 
			}
		}, base_frag_mentions.since_id);
	}

	private void fetchMentionsTimeline() {
		TwitterClientapp.getRestClient().getMentions(new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray mentions) {
				Log.d(TAG, "Successfully fetched mentions: " + mentions.toString());
				base_frag_mentions.getAdapter().addAll(Tweet.parseJsonArray(mentions, base_frag_mentions));

				if (base_frag_mentions.getLv_tweetTimeline()!= null){
					base_frag_mentions.getLv_tweetTimeline().setOnRefreshListener(new OnRefreshListener() {
						@Override
						public void onRefresh() {
							Log.d(TAG, "Refreshing mentions");
							fetchRefreshMentions();
							base_frag_mentions.getLv_tweetTimeline().onRefreshComplete();
						}
					});
					setupEndlessScrolling();
				}
			}			
		});
	}

	private void setupEndlessScrolling() {
		base_frag_mentions.getLv_tweetTimeline().setOnScrollListener(new EndlessScrollListener(0){
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d(TAG, "current page: " + page + " total item count= " + totalItemsCount);
				Toast.makeText(getActivity(), "Loading More..", Toast.LENGTH_SHORT).show();
				fetchMoreMentionsData();
			}
		});
	}

}
