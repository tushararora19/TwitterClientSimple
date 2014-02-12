package com.example.apps.mytwitterapp;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apps.mytwitterapp.fragments.BaseFragment;
import com.example.apps.mytwitterapp.fragments.UserTimeLineFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserProfileActivity extends FragmentActivity {

	TextView tv_myname, tv_myid, tv_mytweets, tv_myfollowing, tv_followers;
	ArrayList<Tweet> my_tweets;
	FrameLayout fl_myTweets;
	ImageView iv_myImg;
	ArrayList<User> myself;
	private static final int REQ_CODE = 100;
	MenuItem mi_goBack;
	UserTimeLineFragment user_frag = new UserTimeLineFragment();
	private static final String TAG = "UserProfileActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		tv_myname = (TextView) findViewById(R.id.tv_myname);
		tv_myid = (TextView) findViewById(R.id.tv_myid);
		tv_mytweets = (TextView) findViewById(R.id.tv_tweetCount);
		tv_myfollowing = (TextView) findViewById(R.id.tv_followingCount);
		tv_followers = (TextView) findViewById(R.id.tv_followersCount);
		fl_myTweets = (FrameLayout) findViewById(R.id.fl_myContainer);
		iv_myImg = (ImageView) findViewById(R.id.iv_mypic);

		Serializable me = getIntent().getSerializableExtra("userData");

		tv_myname.setText(User.getUser_name());
		tv_myid.setText("@" +User.getUser_id());
		ImageLoader.getInstance().displayImage(((User)me).getMy_image_url(), iv_myImg);

		user_frag.setScreenId(User.getUser_id());

		fetchUserData();
//		fetchUserTimeline();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.mi_compTweet:
	    	composeNewTweetMsg();
	    	return true;
	    case R.id.mi_gobk:
	    	GoBackToTimeline();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void fetchUserData(){
		TwitterClientapp.getRestClient().getUserInfo(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
			}

			@Override
			public void onSuccess(JSONObject userinfo) {
				Log.d(TAG, "UserData: " + userinfo.toString());
				User.parseUser (userinfo);
				tv_followers.setText(User.getFollowers_count()+" FOLLOWERS");
				tv_myfollowing.setText(User.getFollowing_count()+" FOLLOWING");
				tv_mytweets.setText(User.getNo_tweets()+" TWEETS");

				android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();
				trans.replace(R.id.fl_myContainer, user_frag);
				trans.commit();
			}

		});
	}

	public void GoBackToTimeline () {
		Intent goBack_intent1 = new Intent (getApplicationContext(), TimelineActivity.class);
		goBack_intent1.putExtra("tweetComposed", "yes");
		BaseFragment.resume_screen_name = "";
		setResult(RESULT_OK, goBack_intent1);
		finish();	
	}

//	public void fetchUserTimeline(){
//		TwitterClientapp.getRestClient().getUserTimeline(new JsonHttpResponseHandler(){
//
//			@Override
//			public void onFailure(Throwable arg0, JSONArray arg1) {
//				// TODO Auto-generated method stub
//				super.onFailure(arg0, arg1);
//			}
//
//			@Override
//			public void onSuccess(int arg0, JSONArray arg1) {
//				// TODO Auto-generated method stub
//				super.onSuccess(arg0, arg1);
//			}
//		});
//	}

	public void composeNewTweetMsg() { 
		if (BaseFragment.isNetworkAvailable(getApplicationContext())){
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
					Intent compose_intent = new Intent(getApplicationContext(), ComposeTweetActivity.class);
					compose_intent.putExtra("class", "UserProfileActivity");
					compose_intent.putExtra("userData", myself.get(0));
					startActivityForResult(compose_intent, REQ_CODE);				
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Intent goToTimeline_intent = new Intent(getApplicationContext(), TimelineActivity.class);
		goToTimeline_intent.putExtra("tweetComposed", data.getStringExtra("tweetComposed").toString());
		startActivity(goToTimeline_intent);
	}

}
