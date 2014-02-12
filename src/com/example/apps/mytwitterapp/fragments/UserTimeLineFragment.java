package com.example.apps.mytwitterapp.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TweetAdapter;

public class UserTimeLineFragment extends BaseFragment {

	//	private BaseFragment base_frag_user;

	public String max_id = "" ; 
	public String since_id = "";
	TweetAdapter tweet_adap_user;
	ArrayList<Tweet> tweet_arr_user = new ArrayList<Tweet>();
	String screen_id = "";
	boolean initial_call_done_user = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (isNetworkAvailable(getActivity().getApplicationContext())) {
			// empty string since 2nd parameter is only for user timeline
			fetchTimeLine("user", screen_id);
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
		return tweet_adap_user;
	}
	public ArrayList<Tweet> getTweet_results() {
		return tweet_arr_user;
	}

	@Override
	public void onResume() {
		BaseFragment.resume_type = "user";
		BaseFragment.resume_screen_name = screen_id;
		super.onResume();
		BaseFragment.user_frag.initial_call_done_user = false;
	}

	@Override
	public void onPause() {
		BaseFragment.user_frag.initial_call_done_user = true;
		super.onPause();
	}


	public void setScreenId (String s) {
		this.screen_id = s;
	}

}
