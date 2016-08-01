package com.raihanbd.easyrambooster;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import geniuscloud.memory.booster.R;

public class MainActivity extends ActionBarActivity implements TabListener {

	private ActionBar bar = null;
	private ViewPager pager = null;
	private MemoryBoosterAdapter pagerAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);// For Actionbar Compat
		setContentView(R.layout.activity_main);

		/**
		 * Initialize ViewPager and adapter
		 */
		this.pager = (ViewPager) findViewById(R.id.pager);
		this.pagerAdapter = new MemoryBoosterAdapter(
				getSupportFragmentManager());
		this.pager.setAdapter(pagerAdapter);

		// Initialize actionbar and tab
		bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(false);
		bar.addTab(bar.newTab().setText("Boost").setTabListener(this));
		bar.addTab(bar.newTab().setText("Tasks").setTabListener(this));
		bar.addTab(bar.newTab().setText("More").setTabListener(this));
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// Set Actionbar Navigation Mode to Tab Mode

		// ViewPager Page Scrolling Listener
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				bar.setSelectedNavigationItem(pos);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	// tab listener
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction trans) {
		pager.setCurrentItem(tab.getPosition(), true);
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}

}
