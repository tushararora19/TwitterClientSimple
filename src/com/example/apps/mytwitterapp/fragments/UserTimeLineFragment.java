package com.example.apps.mytwitterapp.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.apps.mytwitterapp.R;
import com.example.apps.mytwitterapp.Tweet;
import com.example.apps.mytwitterapp.TwitterClientapp;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimeLineFragment extends BaseFragment {

	private BaseFragment base_frag_user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		base_frag_user = (BaseFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fl_myContainer);
		
		if (isNetworkAvailable(getActivity().getApplicationContext())) {
			fetchUserTimelineData();
		}
		else {
			Toast.makeText(getActivity(), "No Internet Connection..No Refresh / Load", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void fetchUserTimelineData(){
		TwitterClientapp.getRestClient().getUserTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d(TAG, "Failed fetching");
			}
			@Override
			public void onSuccess(JSONArray usertweets) {
				Log.d(TAG, "Successfully fetched User Tweets: " + usertweets.toString());
				base_frag_user.getAdapter().addAll(Tweet.parseJsonArray(usertweets, base_frag_user));
			}
		});

	}
}
