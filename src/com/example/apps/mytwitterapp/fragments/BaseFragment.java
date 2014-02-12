package com.example.apps.mytwitterapp.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apps.mytwitterapp.EndlessScrollListener;
import com.example.apps.mytwitterapp.R;
import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TweetAdapter;
import com.example.apps.mytwitterapp.TwitterClientapp;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class BaseFragment extends Fragment {

	protected static final String TAG = "BaseFragment";

	eu.erikw.PullToRefreshListView lv_tweetTimeline;

	public static HomeTimeLineFragment home_frag = new HomeTimeLineFragment();
	public static MentionsTimeLineFragment mention_frag = new MentionsTimeLineFragment();
	public static UserTimeLineFragment user_frag = new UserTimeLineFragment();

	private static View view;
	public static String resume_type = "";
	public static String resume_screen_name = "";


	/*
	 * onCreateView must be defined for every fragment. this is where we inflate xml
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}
		try{ 
			view = inflater.inflate(R.layout.fragment_tweets, container, false);
			lv_tweetTimeline = (eu.erikw.PullToRefreshListView) view.findViewById(R.id.lv_tweets);

		} catch (InflateException ie) {

		} catch (Exception e) {

		}
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		home_frag.tweet_adap_home = new TweetAdapter(getActivity(), home_frag.tweet_arr_home);
		mention_frag.tweet_adap_mention = new TweetAdapter(getActivity(), mention_frag.tweet_arr_mention);
		user_frag.tweet_adap_user = new TweetAdapter(getActivity(), user_frag.tweet_arr_user);
	}

	@Override
	public void onResume() {
		super.onResume();
		if ((resume_type.equals("home") && (home_frag.initial_call_done_home)) || 
				(resume_type.equals("mention") && (mention_frag.initial_call_done_mention)) || 
				(resume_type.equals("user") && (user_frag.initial_call_done_user)) ) {
			
			fetchTimeLine(resume_type, resume_screen_name);
			
			if (lv_tweetTimeline!= null){
				lv_tweetTimeline.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.d(TAG, "Refreshing tweets");
						fetchTimeLineRefresh(resume_type, resume_screen_name);
						lv_tweetTimeline.onRefreshComplete();
					}
				});
				setupEndlessScrolling(resume_type, resume_screen_name);
				// also refresh data
				//fetchRefreshTimelineData();
				fetchTimeLineRefresh (resume_type, resume_screen_name);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	protected void setupEndlessScrolling(final String type, final String screen_name) {
		lv_tweetTimeline.setOnScrollListener(new EndlessScrollListener(0){
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d(TAG, "current page: " + page + " total item count= " + totalItemsCount);
				Toast.makeText(getActivity(), "Loading More..", Toast.LENGTH_SHORT).show();
				fetchMoreTimeLine(type, screen_name);
			}
		});
	}

	protected void fetchTimeLine (final String type, final String screen_name) {

		if (type.equals("home"))
			lv_tweetTimeline.setAdapter(home_frag.tweet_adap_home);
		else if (type.equals("mention"))
			lv_tweetTimeline.setAdapter(mention_frag.tweet_adap_mention);
		else 
			lv_tweetTimeline.setAdapter(user_frag.tweet_adap_user);

		TwitterClientapp.getRestClient().getTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			protected void handleFailureMessage(Throwable arg0, String arg1) {
				Log.d(TAG, "FAILURE !! ");
				Toast.makeText(getActivity(), "Twitter API calls limit exceeded. Please wait 15 mins", Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onSuccess(int arg0, JSONObject arg1) {
				Log.d(TAG, "OBJECT SUCCESS");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				Log.d(TAG, "Successfully fetched: " + tweets.toString());
				//	tweet_results = Tweet.parseJsonArray(tweets);
				if (tweets.length() > 0)
					updateAdapter(tweets, type);

				if ((lv_tweetTimeline!= null)){
					lv_tweetTimeline.setOnRefreshListener(new OnRefreshListener() {
						@Override
						public void onRefresh() {
							Log.d(TAG, "Refreshing tweets");
							fetchTimeLineRefresh(type, screen_name);
							lv_tweetTimeline.onRefreshComplete();
						}
					});
				}
				setupEndlessScrolling(type, screen_name);
			}
		}, type, screen_name);
	}

	protected void fetchMoreTimeLine (final String type, final String screen_name) {
		TwitterClientapp.getRestClient().getMoreTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				Log.d(TAG, "Successfully fetched MORE: " + tweets.toString());
				updateAdapter(tweets, type);
			}
		}, home_frag.max_id, mention_frag.max_id, user_frag.max_id, type, screen_name);
	}

	protected void fetchTimeLineRefresh (final String type, final String screen_name) {
		TwitterClientapp.getRestClient().getRefreshTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray tweets) {
				if (tweets.length() > 0) {
					Log.d(TAG, "Successfully fetched REFRESH: " + tweets.toString());

					if (type.equals("home")){
						home_frag.getTweet_results().addAll(0, Tweet.parseJsonArray(tweets, "home"));
						home_frag.getAdapter().notifyDataSetChanged();
					}
					else if (type.equals("mention")){
						mention_frag.getTweet_results().addAll(0, Tweet.parseJsonArray(tweets, "mention"));
						mention_frag.getAdapter().notifyDataSetChanged();
					}
					else {
						user_frag.getTweet_results().addAll(0, Tweet.parseJsonArray(tweets, "user"));
						user_frag.getAdapter().notifyDataSetChanged();
					}
				} 
			}
		}, home_frag.since_id, mention_frag.since_id, user_frag.since_id, type, screen_name);
	}

	public void updateAdapter(JSONArray tweets, String type){

		if (type.equals("home")) {
			home_frag.getAdapter().addAll(Tweet.parseJsonArray(tweets, "home"));
		}
		else if (type.equals("mention")) {
			mention_frag.getAdapter().addAll(Tweet.parseJsonArray(tweets, "mention"));
		}
		else 
			user_frag.getAdapter().addAll(Tweet.parseJsonArray(tweets, "user"));	
	}

	public static boolean isNetworkAvailable(Context c) {
		ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public eu.erikw.PullToRefreshListView getLv_tweetTimeline() {
		return lv_tweetTimeline;
	}


}
