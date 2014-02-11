package com.example.apps.mytwitterapp.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.apps.mytwitterapp.EndlessScrollListener;
import com.example.apps.mytwitterapp.R;
import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TwitterClientapp;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class HomeTimeLineFragment extends BaseFragment {

	BaseFragment base_frag_home;
	private static boolean initial_call_done = false;

	public HomeTimeLineFragment(){ 
		super();
	}
	
	public static HomeTimeLineFragment newInstance(int page, String title) {
		HomeTimeLineFragment fragmentFirst = new HomeTimeLineFragment();
		Bundle args = new Bundle();
		args.putInt("pgNo", page);
		args.putString("Home", title);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			base_frag_home = (BaseFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fl_container);
			// this needs to be called once
			if (isNetworkAvailable(getActivity().getApplicationContext())) {
				fetchTimelineData();
			}
			else {
				fetchOfflineTimelineData();
				Toast.makeText(getActivity(), "No Internet Connection..No Refresh / Load", Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	@Override
	public void onResume() {
		super.onResume();
		if ((base_frag_home.getLv_tweetTimeline()!= null) && initial_call_done){
			base_frag_home.getLv_tweetTimeline().setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					Log.d(TAG, "Refreshing tweets");
					fetchRefreshTimelineData();
					base_frag_home.getLv_tweetTimeline().onRefreshComplete();
				}
			});
			setupEndlessScrolling();
			// also refresh data
			//fetchRefreshTimelineData();
			initial_call_done = false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		initial_call_done = true;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void setupEndlessScrolling() {
		base_frag_home.getLv_tweetTimeline().setOnScrollListener(new EndlessScrollListener(0){
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d(TAG, "current page: " + page + " total item count= " + totalItemsCount);
				Toast.makeText(getActivity(), "Loading More..", Toast.LENGTH_SHORT).show();
				fetchMoreTimelineData();
			}
		});
	}

	private void fetchMoreTimelineData(){
		TwitterClientapp.getRestClient().getHomeMoreTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				Log.d(TAG, "Successfully fetched MORE: " + tweets.toString());
				base_frag_home.getAdapter().addAll(Tweet.parseJsonArray(tweets, base_frag_home));
			}
		}, base_frag_home.max_id );
	}

	private void fetchRefreshTimelineData(){
		TwitterClientapp.getRestClient().getHomeRefreshTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				if (tweets.length() > 0) {
					Log.d(TAG, "Successfully fetched REFRESH: " + tweets.toString());
					base_frag_home.getTweet_results().addAll(0, Tweet.parseJsonArray(tweets, base_frag_home));
					base_frag_home.getAdapter().notifyDataSetChanged();

				} 
			}
		}, base_frag_home.since_id);
	}

	private void fetchTimelineData(){
		TwitterClientapp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			protected void handleFailureMessage(Throwable arg0, String arg1) {
				Log.d(TAG, "FAILURE !! ");
			}
			@Override
			public void onSuccess(int arg0, JSONObject arg1) {
				Log.d(TAG, "OBJECT SUCCESS");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				Log.d(TAG, "Successfully fetched: " + tweets.toString());
				//				tweet_results = Tweet.parseJsonArray(tweets);
				base_frag_home.getAdapter().addAll(Tweet.parseJsonArray(tweets, base_frag_home));

				if ((base_frag_home.getLv_tweetTimeline()!= null) && initial_call_done ){
					base_frag_home.getLv_tweetTimeline().setOnRefreshListener(new OnRefreshListener() {
						@Override
						public void onRefresh() {
							Log.d(TAG, "Refreshing tweets");
							fetchRefreshTimelineData();
							base_frag_home.getLv_tweetTimeline().onRefreshComplete();
						}
					});
				}
				setupEndlessScrolling();
			}
		});
	}


	private void fetchOfflineTimelineData(){

		// ArrayList<Tweet> tweet_offlineData = (ArrayList<Tweet>) Tweet.offlineFromJson();
		//		tweet_adap = new TweetAdapter(getApplicationContext(), tweet_offlineData);
		//		lv_tweetTimeline.setAdapter(tweet_adap);

	}
}
