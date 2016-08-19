package com.booster.avivast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MemoryBoosterAdapter extends FragmentStatePagerAdapter {

	private String[] title;
	private static final String ARG_PARAM1 = "param1";

	public MemoryBoosterAdapter(FragmentManager fm, String[] title) {
		super(fm);
		this.title = title;
	}

	@Override
	public Fragment getItem(int index) {// for three tab page
		Fragment frag = null;
		if (index == 0) {
			frag = new BoostFragment();
		}

		if (index == 1) {
			frag = new TaskFragment();
		}

		if (index == 2) {
			frag = new MoreFragments();
		}
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, title[index]);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return title[position];
	}

}
