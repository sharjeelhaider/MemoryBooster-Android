package com.raihanbd.easyrambooster;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import geniuscloud.memory.booster.R;

public class MainActivity extends AppCompatActivity {

	private ActionBar bar = null;
	private ViewPager pager = null;
	private MemoryBoosterAdapter pagerAdapter = null;
	private TabLayout tabLayout;
	Toolbar toolbar;

	private String[] title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);// For Actionbar Compat
		setContentView(R.layout.activity_main);
		title = getResources().getStringArray(R.array.my_tab_array);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		tabLayout = (TabLayout) findViewById(R.id.tabs);

		pager = (ViewPager) findViewById(R.id.pager);

		/*final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
				getResources().getDisplayMetrics());

		this.pager.setPageMargin(pageMargin);
		this.pager.setOffscreenPageLimit(1);*/
		/**
		 * Initialize ViewPager and adapter
		 */
		pagerAdapter = new MemoryBoosterAdapter(getSupportFragmentManager(),title);
		pager.setAdapter(pagerAdapter);
		tabLayout.setupWithViewPager(pager);
		// Initialize actionbar and tab
		/*bar = getSupportActionBar();
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
		});*/
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/*// tab listener
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction trans) {
		pager.setCurrentItem(tab.getPosition(), true);
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}*/

}
