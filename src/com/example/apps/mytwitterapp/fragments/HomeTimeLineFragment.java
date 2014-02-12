package com.example.apps.mytwitterapp.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TweetAdapter;

public class HomeTimeLineFragment extends BaseFragment {

	//	BaseFragment base_frag_home;
	public String max_id = "" ; 
	public String since_id = "";
	public boolean initial_call_done_home = false; // keeps track of pause and resume 

	TweetAdapter tweet_adap_home;
	ArrayList<Tweet> tweet_arr_home = new ArrayList<Tweet>();

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (isNetworkAvailable(getActivity().getApplicationContext())) {
			// empty string since 2nd parameter is only for user timeline
			fetchTimeLine("home", "");
		}
		else {
			fetchOfflineTimelineData();
			Toast.makeText(getActivity(), "No Internet Connection..No Refresh / Load", Toast.LENGTH_SHORT).show();
		}
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	public ArrayList<Tweet> getTweet_results() {
		return tweet_arr_home;
	}

	public TweetAdapter getAdapter(){
		return tweet_adap_home;
	}

	@Override
	public void onResume() {
		BaseFragment.resume_type = "home";
		super.onResume();
		BaseFragment.home_frag.initial_call_done_home = false;
	}

	@Override
	public void onPause() {
		BaseFragment.home_frag.initial_call_done_home = true;
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void fetchOfflineTimelineData(){

		// ArrayList<Tweet> tweet_offlineData = (ArrayList<Tweet>) Tweet.offlineFromJson();
		//		tweet_adap = new TweetAdapter(getApplicationContext(), tweet_offlineData);
		//		lv_tweetTimeline.setAdapter(tweet_adap);

	}
}
