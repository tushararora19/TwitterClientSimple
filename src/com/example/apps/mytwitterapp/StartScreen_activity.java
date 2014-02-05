package com.example.apps.mytwitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codepath.oauth.OAuthLoginActivity;
/*
 * https://dev.twitter.com/apps/5750376/show contains all my key info
 * 
 * 
 */
public class StartScreen_activity extends OAuthLoginActivity<TwitterClient> {

	TextView signIn;
	private static final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen_activity);
		
		signIn = (TextView) findViewById(R.id.tv_signIn);
		
		signIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Connecting");
				getClient().connect();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_screen_activity, menu);
		return true;
	}

	@Override
	public void onLoginSuccess() {
		Log.d(TAG, "Login Success");
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
	}

	@Override
	public void onLoginFailure(Exception e) {
		Log.d(TAG, "Login Failed");
		e.printStackTrace();
	}
	
	public void login_rest (View v){
		Log.d(TAG, "Connecting");
		getClient().connect();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			try {
				Log.d(TAG, "Sleeping...");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
