package com.example.apps.mytwitterapp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.example.apps.mytwitterapp.fragments.BaseFragment;
import com.example.apps.mytwitterapp.fragments.HomeTimeLineFragment;
import com.example.apps.mytwitterapp.fragments.MentionsTimeLineFragment;

@Table(name="Tweets")
public class Tweet extends Model{

	private static final int MAX_COUNT = 25;
	private static final String TAG = "Tweet";
	public static BaseFragment base_frag_home = null;
	public static BaseFragment base_frag_mention = null;
	public static BaseFragment base_frag_user = null;
//	static Hashtable<String, String> img_name = new Hashtable<String, String>();

	@Column(name="row_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	static int row = 0;
	
	@Column(name="tweet_msg")
	String tweet_text;

	@Column(name="username")
	String username;

	@Column(name="user_retweeted")
	String user_retweeted;

	@Column(name="user_screenName")
	String screen_name;

	@Column(name="user_img_url")
	String user_image_url;

	@Column(name="time_created")
	String time_created;

	@Column(name="tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	String tweet_id;

	public Tweet(String text, String user, String user_r, String screen_n, String url, String time, String tw_id, int r){
		this.tweet_text = text;
		this.username = user;
		this.user_retweeted = user_r;
		this.screen_name = screen_n;
		this.user_image_url = url;
		this.time_created = time;
		this.tweet_id = tw_id;
		row = r;
		//		this.save();
	}

	public Tweet(){

	}
	
	public String getTweet_id() {
		return tweet_id;
	}
	public String getTweet_text() {
		return tweet_text;
	}
	public String getUsername() {
		return username;
	}
	public String getUser_retweeted() {
		return user_retweeted;
	}
	public String getScreen_name() {
		return screen_name;
	}
	public String getUser_image_url() {
		return user_image_url;
	}
	public String getTimeCreated() {
		return time_created;
	}
	public static ArrayList<Tweet> parseJsonArray (JSONArray tweets, BaseFragment base){
		ArrayList<Tweet> tweet_results = new ArrayList<Tweet>();
		
		// using bulk inserts
		ActiveAndroid.beginTransaction();
		try {
			for (int i=0; i<tweets.length(); i++){
				String user = "";
				String tweetText = "";
				String user_r = "";
				String screen_n = "";
				String url = "";
				String time = "";
				String tweet_id = "";
				JSONObject user_json = null;

				JSONObject obj = tweets.getJSONObject(i);
				try{
					JSONObject rtweet = obj.getJSONObject("retweeted_status");
					tweetText = rtweet.getString("text");
					user_json = rtweet.getJSONObject("user");
					time = rtweet.getString("created_at");
					tweet_id = rtweet.getString("id_str");

					user_r = obj.getJSONObject("user").getString("name");

				} catch (JSONException je){
					// means retweeted_status doesn't exist 
					// TO DO: get screen name in case its not a retweet
					tweetText = obj.getString("text");
					user_json = obj.getJSONObject("user");
					time = obj.getString("created_at");
					tweet_id = obj.getString("id_str");
				}

				if (user_json!=null){
					user = user_json.getString("name");
					screen_n = user_json.getString("screen_name");
					url = user_json.getString("profile_image_url");
				}

				
				if (!base.max_id.equals("")){
					if (tweet_id.compareToIgnoreCase(base.max_id) < 0) { // keeping track of lowest tweet id received
						base.max_id = tweet_id;
					}
				} else {
					base.max_id = tweet_id;
				}


				if (base.since_id.equals("")){
					base.since_id = tweet_id;
				} else {
					if (tweet_id.compareToIgnoreCase(base.since_id) >= 0){
						base.since_id = tweet_id;
					}
				}

				if (row+1 > MAX_COUNT) {
					row = 0;
				}
				//Log.d(TAG, "user: " + user + " Text: " +tweetText + "user_r: " +user_r);
				try{
					Model old = new Select().from(Tweet.class).where("row_id = ?", row+1).executeSingle();
					old.delete();
				} catch (NullPointerException ne) {
				} catch (Exception e) {
					Log.d(TAG, "no old element found with exc: " +e.toString());
				}
				
				Tweet t = new Tweet(tweetText, user, user_r, screen_n, url, time, tweet_id, row+1);
				t.save();
				tweet_results.add(t);
			}
			
			if (base instanceof HomeTimeLineFragment) 
				base_frag_home = base;
			else if (base instanceof MentionsTimeLineFragment)
				base_frag_mention = base;
			else 
				base_frag_user = base;
			
			ActiveAndroid.setTransactionSuccessful();

		} catch (Exception e) {
		} finally {
			ActiveAndroid.endTransaction();
			
		}
		return tweet_results;
	}

	// shows only first 20 tweets
	public static List<Tweet> offlineFromJson() {
		return new Select()
		.from(Tweet.class)
		.orderBy("tweet_id DESC")
		.limit(20)
		.execute();
	}

	public static int getCount() {
		return (new Select()
		.from(Tweet.class)
		.execute()).size();
	}
}
