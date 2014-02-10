package com.example.apps.mytwitterapp;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeTweetActivity extends Activity {

	private static final String TAG = "ComposeTweet";
	private static final int MAX_COUNT = 140;
	TextView user_name;
	TextView user_id;
	ImageView img_url;
	EditText status;
	MenuItem mi_charsLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_tweet);

		@SuppressWarnings("unchecked")
		Serializable user = getIntent().getSerializableExtra("userData");

		user_name = (TextView) findViewById(R.id.tv_myname);
		user_id = (TextView) findViewById(R.id.tv_myid);
		img_url = (ImageView) findViewById(R.id.iv_myimage);
		status = (EditText) findViewById(R.id.et_tweetMessage);

		user_name.setText(((User) user).getUser_name());
		user_id.setText("@" +((User) user).getUser_id());
		ImageLoader.getInstance().displayImage(((User)user).getMy_image_url(), img_url);

		status.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				Log.d(TAG, arg0.toString());
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				//mi_charsLeft = (MenuItem) findViewById(R.id.mi_charsLeft);
				int left = MAX_COUNT - status.getText().toString().length();
				if (left >=0 )
					mi_charsLeft.setTitle(left+"");
				else 
					Toast.makeText(getApplicationContext(), "Max 140 chars allowed", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose_tweet, menu);
		mi_charsLeft= (MenuItem) menu.findItem(R.id.mi_charsLeft);

		return true;
	}

	public void CancelTweet (MenuItem mi_cancel){
		if (!status.getText().toString().equals("")){

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Cancel Tweet");
			alert.setMessage("Are you sure you want to cancel tweet ?");

			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					//Editable value = input.getText();
					Log.d(TAG, "Cancel tweet YES !!");
					finish();
				}
			});

			alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Log.d(TAG, "Cancel tweet NO !!");
				}
			});

			alert.show();
		} else {
			finish();
		}
	}
	public void TweetMessage (MenuItem mi_tweet) {
		if (!status.getText().toString().equals("")){
			TwitterClientapp.getRestClient().updateTweet(status.getText().toString(), new JsonHttpResponseHandler(){

				@Override
				public void onFailure(Throwable arg0, JSONArray arg1) {
					Log.d(TAG, "FAILED : " + arg1.toString());
				}

				@Override
				public void onSuccess(JSONArray arg0) {
					Log.d(TAG, "SUCCESS: " + arg0.toString());
					Intent goBack_intent = new Intent (getApplicationContext(), TimelineActivity.class);
					goBack_intent.putExtra("demo", "demoString");
					setResult(RESULT_OK, goBack_intent);
					finish();
				}

				@Override
				public void onSuccess(JSONObject arg0) {
					Log.d(TAG, "SUCCESS 2: " + arg0.toString());
					Intent goBack_intent = new Intent (getApplicationContext(), TimelineActivity.class);
					goBack_intent.putExtra("demo", "demoString");
					setResult(RESULT_OK, goBack_intent);
					finish();
				}

			});
		} else {
			Toast.makeText(getApplicationContext(), "Tweet message can't be empty.", Toast.LENGTH_SHORT).show();
		}
	}
}
