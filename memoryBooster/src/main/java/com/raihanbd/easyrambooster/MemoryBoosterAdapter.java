package com.raihanbd.easyrambooster;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MemoryBoosterAdapter extends FragmentStatePagerAdapter {

	public MemoryBoosterAdapter(FragmentManager fm) {
		super(fm);
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
		return frag;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
