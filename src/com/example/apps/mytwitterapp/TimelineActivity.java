package com.example.apps.mytwitterapp;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.apps.mytwitterapp.fragments.BaseFragment;
import com.example.apps.mytwitterapp.fragments.HomeTimeLineFragment;
import com.example.apps.mytwitterapp.fragments.MentionsTimeLineFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

/*
 * IMPORTANT
 *  Endless scrolling should load more tweets from the point where last one ended: Do NOT construct adapter (tweet_adapter) again at all. 
 *  Adapter maintains its own state; therefore, if u do new Adapter, it will lose its own state 
 * tweet_results.addAll(...)
 * tweet_adap.notifyDataSetChanged();
 * 
 * Also don't keep adding lv.setAdapter (not required everytime)
 * or directly tweet_adap.addAll(Tweet.parseJsonArray(tweets))
 * 
 */

/*
 * TO DO:
Optional: When a network request goes out, user sees an indeterminate progress indicator
Optional: User can "reply" to any tweet on their home timeline
The user that wrote the original tweet is automatically "@" replied in compose
Optional: User can click on a tweet to be taken to a "detail view" of that tweet
Optional: User can take favorite (and unfavorite) or reweet actions on a tweet
Optional: User can search for tweets matching a particular query and see results
Optional: User can view their direct messages (or send new ones)
 */

public class TimelineActivity extends FragmentActivity implements TabListener{

	ArrayList<User> myself;
	private static final int REQ_CODE = 10;
	private static final int REQ_CODE_2 = 20;
	private static final String TAG = "TimelineActivity";
	android.support.v4.app.FragmentManager manager;
	HomeTimeLineFragment home_frag = new HomeTimeLineFragment();
	MentionsTimeLineFragment mention_frag = new MentionsTimeLineFragment();
	ActionBar action_bar;
	Tab home_tab, mentions_tab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		if (savedInstanceState == null ){
			manager = getSupportFragmentManager();

		}
		setupNavigation();
		//setupNavigationSlider();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.mi_compose:
			composeNewTweet();
			return true;
		case R.id.mi_MyProfile:
			goToUserPage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setupNavigationSlider() {
		ActionBar action_bar = getActionBar();
		action_bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		//ArrayList<Fragment> frags = new ArrayList<Fragment>();
		//frags.add(home_frag);
		//frags.add(mention_frag);
		//PagerAdapter pg_adap = new PagerAdapter(super.getSupportFragmentManager(), frags);

		//ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
		//MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
		//vpPager.setAdapter(adapterViewPager);
	}

	public void setupNavigation() { 
		action_bar = getActionBar();

		action_bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		home_tab = action_bar.newTab().setText("Home").setTag("HomeTimeLine").setTabListener(this);
		mentions_tab = action_bar.newTab().setText("Mentions").setTag("MentionTimeLine").setTabListener(this);

		action_bar.addTab(home_tab);
		action_bar.addTab(mentions_tab);
		action_bar.selectTab(home_tab);

	}

	private void startIntent(){
		Intent compose_intent = new Intent(getApplicationContext(), ComposeTweetActivity.class);
		compose_intent.putExtra("class", "TimlineActivity");
		compose_intent.putExtra("userData", myself.get(0));
		startActivityForResult(compose_intent, REQ_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();
		if (!mention_frag.isDetached())
			trans.detach(mention_frag);

		if (!home_frag.isDetached())
			trans.detach(home_frag);

		action_bar.selectTab(home_tab);

		trans.attach(home_frag);
		trans.replace(R.id.fl_container, home_frag);

		BaseFragment.resume_type = "home";
		try {
			// refresh time line
			BaseFragment.home_frag.initial_call_done_home = true;
			TwitterClientapp.getRestClient().first_call_home = true;
			TwitterClientapp.getRestClient().first_call_mention = true;
			
		} catch (Exception e ){

		}
		trans.commit();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();

		if (tab.getTag().equals("HomeTimeLine")){
			trans.attach(home_frag);
			trans.replace(R.id.fl_container, home_frag);
		} else {
			trans.attach(mention_frag);
			trans.replace(R.id.fl_container, mention_frag);
		}
		trans.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();

		if (tab.getTag().equals("HomeTimeLine")){
			trans.detach(home_frag);
		} else {
			trans.detach(mention_frag);
		}
		trans.commit();
	}

	public void composeNewTweet(){
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
					startIntent();
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT).show();
		}
	}
	public void goToUserPage() {
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
					Intent user_intent = new Intent(getApplicationContext(), UserProfileActivity.class);
					user_intent.putExtra("userData", myself.get(0));
					startActivityForResult(user_intent, REQ_CODE_2);;				
				}
			});
		}
	}

	public static class MyPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 2;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0: // Fragment # 0 - This will show FirstFragment
				return HomeTimeLineFragment.newInstance(0, "HomeTimeline");
			case 1: // Fragment # 0 - This will show FirstFragment different title
				return MentionsTimeLineFragment.newInstance(1, "MentionsTimeline");
			default:
				return null;
			}
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}

	}
}
