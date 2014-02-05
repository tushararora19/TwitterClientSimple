package com.example.apps.mytwitterapp;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

/*
 * To do:
 * 1. add timestamp (relative time) DONE
 * 2. User can load more tweets once they reach the bottom of the list Using "Load More" Button or "Lazy Endless" Scrolling without duplicates DONE
 * 3. Endless scrolling should load more tweets from the point where last one ended TO DO
 * 4. User can refresh timeline by pulling down (i.e pull-to-refresh) without duplicates DONE (check where to put the newly fed tweet: should be at start of array)
 * 5. User can open the twitter app offline and see recent tweets. Tweets are persisted into sqlite and displayed from the local DB DONE
 */
public class TimelineActivity extends Activity {

	eu.erikw.PullToRefreshListView lv_tweetTimeline;
	ArrayList<Tweet> tweet_results;
	TweetAdapter tweet_adap;
	ArrayList<User> myself;
	private static final int REQ_CODE = 10;

	private static final String TAG = "TimelineActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		lv_tweetTimeline = (eu.erikw.PullToRefreshListView) findViewById(R.id.lv_tweets);
		tweet_results = new ArrayList<Tweet>();

		// add condition if its in offline mode or online mode
		if (isNetworkAvailable())
			fetchTimelineData();
		else 
			fetchOfflineTimelineData();

		if (isNetworkAvailable()) {
			lv_tweetTimeline.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					Log.d(TAG, "Refreshing tweets");
					fetchRefreshTimelineData();
					lv_tweetTimeline.onRefreshComplete();
				}
			});

		}
		else {
			Toast.makeText(getApplicationContext(), "No Internet Connection..No Refresh", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus){
			setupEndlessScrolling();
		}
	}

	private void setupEndlessScrolling() {
		lv_tweetTimeline.setOnScrollListener(new EndlessScrollListener(0){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				Log.d(TAG, "current page: " + page + " total item count= " + totalItemsCount);
				customLoadMoreDataFromApi(page); 
				// or customLoadMoreDataFromApi(totalItemsCount);
			}

		});
	}

	public void customLoadMoreDataFromApi(int offset) {
		// This method probably sends out a network request and appends new data items to your adapter. 
		// Use the offset value and add it as a parameter to your API request to retrieve paginated data.
		// Deserialize API response and then construct new objects to append to the adapter		
		// Log.d(TAG, "current offset: " + offset);
		if (isNetworkAvailable()) {
			Toast.makeText(getApplicationContext(), "Loading More..", Toast.LENGTH_SHORT).show();
			fetchMoreTimelineData();
		}
		else {
			Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT).show();
		}
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
				tweet_results.addAll(Tweet.parseJsonArray(tweets));
				tweet_adap = new TweetAdapter(getApplicationContext(), tweet_results);
				//Log.d(TAG, "TwitterAdapter: " +tweet_adap.getCount() + " --> " +tweet_adap.toString());
				lv_tweetTimeline.setAdapter(tweet_adap);
			}
		} );
		//List<Model> t = Tweet.getRandom();
		//Log.d(TAG, "Random Tweet text:" + t.toString());
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
					tweet_results.addAll(0, Tweet.parseJsonArray(tweets));
					tweet_adap = new TweetAdapter(getApplicationContext(), tweet_results);
					//Log.d(TAG, "TwitterAdapter: " +tweet_adap.getCount() + " --> " +tweet_adap.toString());
					lv_tweetTimeline.setAdapter(tweet_adap);
				} 
				//tweet_adap.notifyDataSetChanged();
			}
		});
		//List<Model> t = Tweet.getRandom();
		//Log.d(TAG, "Random Tweet text:" + t.toString());
	}

	private void fetchTimelineData(){
		TwitterClientapp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}

			@Override
			public void onSuccess(JSONArray tweets) {
				Log.d(TAG, "Successfully fetched: " + tweets.toString());
				tweet_results.addAll(Tweet.parseJsonArray(tweets));
				tweet_adap = new TweetAdapter(getApplicationContext(), tweet_results);
				//Log.d(TAG, "TwitterAdapter: " +tweet_adap.getCount() + " --> " +tweet_adap.toString());
				lv_tweetTimeline.setAdapter(tweet_adap);
			}
		});
		//List<Model> t = Tweet.getRandom();
		//Log.d(TAG, "Random Tweet text:" + t.toString());
	}

	private void fetchOfflineTimelineData(){

		ArrayList<Tweet> tweet_offlineData = (ArrayList<Tweet>) Tweet.offlineFromJson();
		tweet_adap = new TweetAdapter(getApplicationContext(), tweet_offlineData);
		lv_tweetTimeline.setAdapter(tweet_adap);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	public void composeNewTweet(MenuItem mi_compose){
		TwitterClientapp.getRestClient().getUserTimeline(new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, JSONArray user) {
				Log.d(TAG, "Failed : " + user.toString());
			}

			@Override
			public void onSuccess(JSONArray user) {
				Log.d(TAG, "Success : " + user.toString()); 
				myself = User.parseJsonUserResult(user);
				//user_adap = new UserAdapter(getApplicationContext(), myself);
				startIntent();
			}
		});
	}

	private void startIntent(){
		Intent compose_intent = new Intent(getApplicationContext(), ComposeTweetActivity.class);
		compose_intent.putExtra("userData", myself.get(0));
		startActivityForResult(compose_intent, REQ_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_CODE && resultCode == RESULT_OK){
			fetchRefreshTimelineData();
		}
	}
}
