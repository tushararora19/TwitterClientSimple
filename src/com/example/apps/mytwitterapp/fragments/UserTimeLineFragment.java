package com.example.apps.mytwitterapp.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apps.mytwitterapp.EndlessScrollListener;
import com.example.apps.mytwitterapp.R;
import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TwitterClientapp;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class UserTimeLineFragment extends BaseFragment {

	private BaseFragment base_frag_user;
	String screen_id = "";
	private static boolean user_call_done = false;
	PullToRefreshListView lv_userTimeline; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		base_frag_user = (BaseFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fl_myContainer);
		if (isNetworkAvailable(getActivity().getApplicationContext())) {
			fetchUserTimelineData(screen_id);
		}
		else {
			Toast.makeText(getActivity(), "No Internet Connection..No Refresh / Load", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweets,  container, false);
		lv_tweetTimeline = (PullToRefreshListView) v.findViewById(R.id.lv_tweets);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if ((base_frag_user.getLv_tweetTimeline()!= null) && user_call_done){
			base_frag_user.getLv_tweetTimeline().setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					Log.d(TAG, "Refreshing tweets");
					fetchRefreshTimelineData();
					base_frag_user.getLv_tweetTimeline().onRefreshComplete();
				}
			});
			setupEndlessScrolling();
			// also refresh data
			//fetchRefreshTimelineData();
			user_call_done = false;
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		user_call_done = true;
	}
	
	
	public void setScreenId (String s) {
		this.screen_id = s;
	}
	
	public void fetchUserTimelineData(String screen){
		TwitterClientapp.getRestClient().getSpecificUserTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray usertweets) {
				Log.d(TAG, "Successfully fetched User Tweets: " + usertweets.toString());
				base_frag_user.getAdapter().addAll(Tweet.parseJsonArray(usertweets, base_frag_user));
				
				if ((base_frag_user.getLv_tweetTimeline()!= null) && user_call_done ){
					base_frag_user.getLv_tweetTimeline().setOnRefreshListener(new OnRefreshListener() {
						@Override
						public void onRefresh() {
							Log.d(TAG, "Refreshing User tweets");
							fetchRefreshTimelineData();
							base_frag_user.getLv_tweetTimeline().onRefreshComplete();
						}
					});
				}
				setupEndlessScrolling();
			}
		}, screen);
	}
	
	private void fetchRefreshTimelineData(){
		TwitterClientapp.getRestClient().getSpecificUserTimelineRefresh(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				if (tweets.length() > 0) {
					Log.d(TAG, "Successfully fetched REFRESH: " + tweets.toString());
					base_frag_user.getTweet_results().addAll(0, Tweet.parseJsonArray(tweets, base_frag_user));
					base_frag_user.getAdapter().notifyDataSetChanged();

				} 
			}
		}, base_frag_user.since_id, screen_id);
	}
	
	private void setupEndlessScrolling() {
		base_frag_user.getLv_tweetTimeline().setOnScrollListener(new EndlessScrollListener(0){
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d(TAG, "current page: " + page + " total item count= " + totalItemsCount);
				Toast.makeText(getActivity(), "Loading More..", Toast.LENGTH_SHORT).show();
				fetchMoreTimelineData();
			}
		});
	}

	private void fetchMoreTimelineData(){
		TwitterClientapp.getRestClient().getSpecificUserTimelineMore(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				Log.d(TAG, "Successfully fetched MORE: " + tweets.toString());
				base_frag_user.getAdapter().addAll(Tweet.parseJsonArray(tweets, base_frag_user));
			}
		}, base_frag_user.max_id, screen_id);
	}

	
}
