package com.example.apps.mytwitterapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apps.mytwitterapp.fragments.BaseFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetAdapter extends ArrayAdapter<Tweet> {

	public static final long MINUTE_IN_MILLIS = 60000;
	public static final long WEEK_IN_MILLIS = 604800000;
	private static final String TAG = "TweetAdapter";
	//	public static Hashtable<ImageView, String> iv_url = new Hashtable<ImageView, String>();
	private static final int REQ_CODE_3 = 30;


	public TweetAdapter(Context context, ArrayList<Tweet> items) {
		super(context, R.layout.tweet_item, items);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Tweet tweet = (Tweet) getItem(position);

		if (convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet_item, null);
		} else {
			// Log.d("TweetAdapter", "converView is NOT null");
		}
		TextView user = (TextView)convertView.findViewById(R.id.tv_username);
		TextView tweetText = (TextView)convertView.findViewById(R.id.tv_tweetText);
		TextView user_r = (TextView)convertView.findViewById(R.id.tv_retweet);
		TextView screen_name = (TextView)convertView.findViewById(R.id.tv_screenName);
		ImageView user_image = (ImageView) convertView.findViewById(R.id.iv_userimg);
		TextView time_diff = (TextView) convertView.findViewById(R.id.tv_timeDiff);
		// add image also here

		user.setText(tweet.getUsername());
		tweetText.setText(tweet.getTweet_text());
		if (!tweet.getUser_retweeted().equals("")){
			user_r.setText(tweet.getUser_retweeted() + " retweeted");
		} else {
			user_r.setText("");
		}
		if (!tweet.getScreen_name().equals("")){ 
			screen_name.setText("@" +tweet.getScreen_name());
		} else { 
			screen_name.setText("");
		}
		ImageLoader.getInstance().displayImage(tweet.getUser_image_url(), user_image);
		// set tag to be image url
		user_image.setTag(tweet.getUser_image_url());

		user_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView img = (ImageView) v.findViewById(v.getId());
				Log.d(TAG, "IMG VIEW: " +img.toString() + "IMAGE URL : " + img.getTag());
				String screen = tweet.getScreen_name();
				//String screen = Tweet.img_name.get(TweetAdapter.getImageUrl(img).toString());
				Log.d(TAG, "Screen name is: " + tweet.getScreen_name());

				if (BaseFragment.isNetworkAvailable(getContext())){
					TwitterClientapp.getRestClient().getSpecificUserTimeline(new JsonHttpResponseHandler() {
						@Override
						public void onFailure(Throwable arg0, JSONArray user) {
							Log.d(TAG, "Failed : " + user.toString());
						}
						@Override
						public void onSuccess(JSONArray user) {
							Log.d(TAG, "Success : " + user.toString());
							Intent user_intent = new Intent(getContext(), UserProfileActivity.class);
							user_intent.putExtra("userData", User.parseJsonUserResult(user).get(0));
							getContext().startActivity(user_intent);		
						}
					}, screen);
				}
			}
		});


		time_diff.setText(calculateTimeDiff(tweet.getTimeCreated()));

		return convertView;
	}

	//	public static String getImageUrl (ImageView iv){
	//		return (String) iv_url.get(iv);
	//	}

	private String calculateTimeDiff(String time_created) {
		String timeDiff = "";
		try {
			Date created_at = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(time_created);
			/*timeDiff = (String) DateUtils.getRelativeDateTimeString(getContext(), 
					created_at.getTime(),
					MINUTE_IN_MILLIS, 
					WEEK_IN_MILLIS,
					0);*/
			timeDiff = (String) DateUtils.getRelativeTimeSpanString(created_at.getTime(), new Date().getTime(), MINUTE_IN_MILLIS, 0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (timeDiff.contains("ago")){
			timeDiff = timeDiff.substring(0, timeDiff.length()-4); // 3 for ago and one for space before ago

			if (timeDiff.contains("hour")){ // covers for both hour and hours
				timeDiff = timeDiff.substring(0,timeDiff.indexOf("hour")-1)+"h";
			} else if (timeDiff.contains("minute")){
				timeDiff = timeDiff.substring(0,timeDiff.indexOf("minute")-1)+"m";
			} else if (timeDiff.contains("days")){
				timeDiff = timeDiff.substring(0,timeDiff.indexOf("days")-1)+"d";
			} 
		} else if (timeDiff.contains("Yesterday")) {
			timeDiff = "1d";
		} 
		return timeDiff;
	}
}
