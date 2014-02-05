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
	String user_screen_id;
	
	@Column(name="username")
	String user_name;
	
	@Column(name="user_img_url")
	String my_image_url;

	public User () {

	}

	public User(String id, String name, String url) {
		user_screen_id = id;
		user_name = name;
		my_image_url = url;
	}

	public String getUser_id() {
		return user_screen_id;
	}
	public String getUser_name() {
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

}
