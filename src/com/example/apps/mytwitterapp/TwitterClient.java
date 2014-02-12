package com.example.apps.mytwitterapp;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.net.Uri;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // base API URL
	public static final String REST_CONSUMER_KEY = "BdduaWde0rBFeXOmGOkThw";       
	public static final String REST_CONSUMER_SECRET = "3WhLrlZhIIStxVoM4BBfekgya4joe07nE44vjkVE"; 
	public static final String REST_CALLBACK_URL = "oauth://mytwitterapp"; // Change this (here and in manifest)
	boolean first_call_home= true;
	boolean first_call_mention = true;
	boolean first_call_user= true;


	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getTimeline(AsyncHttpResponseHandler handler, String type, String screen_id){

		String url = "";
		if (type.equals("home") && first_call_home) {
			url = getApiUrl("statuses/home_timeline.json") +"?count=15";
			first_call_home = false;
		}
		else if (type.equals("mention") && first_call_mention) {
			url = getApiUrl("statuses/mentions_timeline.json") +"?count=15";
			first_call_mention = false;
		}
		else if (type.equals("user") && first_call_user){
			url = getApiUrl("statuses/user_timeline.json?screen_name="+Uri.encode(screen_id));
			first_call_user = false;
		}

		client.get(url, handler);
	}

	public void getMoreTimeline(AsyncHttpResponseHandler handler, String max_id_home, String max_id_mention, String max_id_user, String type, String screen_id){
		String url = "";
		if (type.equals("home"))
			url = getApiUrl("statuses/home_timeline.json") +"?count=10&max_id="+(Long.parseLong(max_id_home) -1); // subtract -1 here from max_id (since its inclusive)
		else if (type.equals("mention"))
			url = getApiUrl("statuses/mentions_timeline.json") +"?count=10&max_id="+(Long.parseLong(max_id_mention) -1); // subtract -1 here from max_id (since its inclusive)
		else {
			url = getApiUrl("statuses/user_timeline.json?screen_name="+Uri.encode(screen_id)); 
			url+="?count=10&max_id="+(Long.parseLong(max_id_user) -1); // need to modify this to allow max_id
		}

		client.get(url, handler);
	}

	public void getRefreshTimeline(AsyncHttpResponseHandler handler, String since_id_home, String since_id_mention, String since_id_user, String type, String screen_id){
		// subtract -1 here from max_id (since its inclusive). since_id is not inclusive, so don't need to subtract -1
		String url = "";
		if (type.equals("home"))
			url = getApiUrl("statuses/home_timeline.json") +"?count=5&since_id="+since_id_home;	
		else if (type.equals("mention"))
			url = getApiUrl("statuses/mentions_timeline.json") +"?count=5&since_id="+since_id_mention;	
		else {
			url = getApiUrl("statuses/user_timeline.json?screen_name="+Uri.encode(screen_id)); 
			url += "?count=5&since_id="+since_id_user;
		}

		client.get(url, handler);	
	}

	public void getUserTimeline (AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/user_timeline.json");
		client.get(url, null, handler);
	}

	public void updateTweet(String status, AsyncHttpResponseHandler handler) {
		String url = getApiUrl("statuses/update.json");
		url += "?status=" + Uri.encode(status);
		client.post(url, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler){
		String url = getApiUrl("users/show.json?screen_name="+ User.getUser_id());
		client.get(url, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}