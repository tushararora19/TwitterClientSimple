package com.example.apps.mytwitterapp.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apps.mytwitterapp.R;
import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TweetAdapter;

public class BaseFragment extends Fragment {

	protected static final String TAG = "BaseFragment";
	ArrayList<Tweet> tweet_results = new ArrayList<Tweet>();
	TweetAdapter tweet_adap;
	eu.erikw.PullToRefreshListView lv_tweetTimeline;
	public String max_id = "" ; public String since_id = "";

	private static View view;
	
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
		} catch (InflateException ie) {
			
		} catch (Exception e) {
			
		}
		return view;
	}

	public static boolean isNetworkAvailable(Context c) {
		ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		tweet_adap = new TweetAdapter(getActivity(), tweet_results);
		lv_tweetTimeline = (eu.erikw.PullToRefreshListView) getActivity().findViewById(R.id.lv_tweets);
		lv_tweetTimeline.setAdapter(tweet_adap);
	}

	public ArrayList<Tweet> getTweet_results() {
		return tweet_results;
	}

	public eu.erikw.PullToRefreshListView getLv_tweetTimeline() {
		return lv_tweetTimeline;
	}

	public TweetAdapter getAdapter(){
		return tweet_adap;
	}
}
