package com.raihanbd.easyrambooster.process;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class PackagesInfo {

	private List appList;

	public PackagesInfo(Context context) {
		List list = context.getApplicationContext().getPackageManager()
				.getInstalledApplications(0);
		this.appList = list;
	}

	public PackagesInfo(Context context, String s) {
		List list = context.getApplicationContext().getPackageManager()
				.getInstalledApplications(128);
		this.appList = list;
	}

	public ApplicationInfo getInfo(String s) {
		
		ApplicationInfo applicationInfo = null;
		if (s != null) {
			for (Iterator iterator = appList.iterator(); iterator.hasNext();) {
				applicationInfo = (ApplicationInfo) iterator.next();
				String s1 = applicationInfo.processName;
				if (s.equals(s1)){
					Log.d("SHORT: ", s+" "+s1);
					break;
				}	
			}
		}

		return applicationInfo;
	}
}
