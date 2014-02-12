package com.example.apps.mytwitterapp.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TweetAdapter;

// To CHECK: check if Tweet.parse is fine or if you need another class for mentions.

public class MentionsTimeLineFragment extends BaseFragment {

//	BaseFragment base_frag_mentions;
	public String max_id = "" ; 
	public String since_id = "";
	TweetAdapter tweet_adap_mention;
	ArrayList<Tweet> tweet_arr_mention= new ArrayList<Tweet>();
	boolean initial_call_done_mention = false;


	public static HomeTimeLineFragment newInstance(int page, String title) {
		HomeTimeLineFragment fragmentFirst = new HomeTimeLineFragment();
		Bundle args = new Bundle();
		args.putInt("pgNo", page);
		args.putString("name", title);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (isNetworkAvailable(getActivity().getApplicationContext())) {
			// empty string since 2nd parameter is only for user timeline
			fetchTimeLine("mention", "");
		}
		else {
			Toast.makeText(getActivity(), "No Internet Connection..No Refresh / Load", Toast.LENGTH_SHORT).show();
		}
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public TweetAdapter getAdapter(){
		return tweet_adap_mention;
	}
	public ArrayList<Tweet> getTweet_results() {
		return tweet_arr_mention;
	}
	
	@Override
	public void onResume() {
		BaseFragment.resume_type = "mention";
		super.onResume();
		BaseFragment.mention_frag.initial_call_done_mention = false;
	}

	@Override
	public void onPause() {
		BaseFragment.mention_frag.initial_call_done_mention = true;
		super.onPause();
	}
}
