package com.example.apps.mytwitterapp;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="User")
public class User extends Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -736827265126122900L;
	@Column(name="user_screen_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	static String user_screen_id;
	
	@Column(name="username")
	static String user_name;
	
	@Column(name="user_img_url")
	static String my_image_url;

	static int followers_count = 0;
	static int following_count = 0;
	static int no_tweets = 0;
	
	public User () {

	}

	public User(String id, String name, String url) {
		user_screen_id = id;
		user_name = name;
		my_image_url = url;
	}

	public static String getUser_id() {
		return user_screen_id;
	}
	public static String getUser_name() {
		return user_name;
	}
	public String getMy_image_url() {
		return my_image_url;
	}

	public static ArrayList<User> parseJsonUserResult (JSONArray userobjs){

		ArrayList<User> users = new ArrayList<User>();

		try{
			for (int i=0;i<userobjs.length();i++){
				JSONObject user = userobjs.getJSONObject(i);

				String screen_id = user.getJSONObject("user").getString("screen_name");
				String name = user.getJSONObject("user").getString("name");
				String url_img = user.getJSONObject("user").getString("profile_image_url");

				users.add(new User(screen_id, name, url_img));
			}
		}
		catch (JSONException je) {

		} catch (Exception e){

		}
		return users;
	}
	
	public static void parseUser (JSONObject userob){
		
		 try {
			followers_count = Integer.parseInt(userob.getString("followers_count"));
			following_count = Integer.parseInt(userob.getString("friends_count"));
			no_tweets = Integer.parseInt(userob.getString("statuses_count"));
			
			user_name = userob.getString("name");
			user_screen_id = userob.getString("screen_name");
			my_image_url = userob.getString("profile_image_url");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getFollowers_count() {
		return followers_count;
	}

	public static int getFollowing_count() {
		return following_count;
	}

	public static int getNo_tweets() {
		return no_tweets;
	}

}
