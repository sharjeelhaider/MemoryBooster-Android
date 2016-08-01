package com.raihanbd.easyrambooster.process;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.CheckBox;

public class TaskInfo {

	private ApplicationInfo appinfo;
	public long mem;
	private PackagesInfo pkgInfo;
	private PackageManager pm;
	private ActivityManager.RunningAppProcessInfo runinfo;
	private String title;
	private CheckBox chkTask;
	private boolean chceked;

	public TaskInfo(Context context,
			ActivityManager.RunningAppProcessInfo runinfo) {
		
		this.appinfo = null;
		this.pkgInfo = null;
		this.title = null;
		this.runinfo = runinfo;
		PackageManager pkm = context.getApplicationContext()
				.getPackageManager();
		this.pm = pkm;
	}

	public TaskInfo(Context context, ApplicationInfo appinfo) {
		
		this.appinfo = null;
		this.pkgInfo = null;
		this.runinfo = null;
		this.title = null;
		this.appinfo = appinfo;
		PackageManager pkm = context.getApplicationContext()
				.getPackageManager();
		this.pm = pkm;
	}

	public void getAppInfo(PackagesInfo pkInfo) {
		
		if (appinfo == null) {
			try {
				String s = runinfo.processName;
				this.appinfo = pm.getApplicationInfo(s, 128);
			} catch (Exception e) {
			}
		}
	}

	public int getIcon() {
		return appinfo.icon;
	}

	public String getPackageName() {
		return appinfo.packageName;
	}

	public String getTitle() {
		
		if (title == null) {
			try {
				this.title = appinfo.loadLabel(pm).toString();
			} catch (Exception e) {

			}
		}
		return title;
	}

	public void setMem(int mem) {
		this.mem = mem;
	}

	public boolean isGoodProcess() {
		
		if (appinfo != null) {
			return true;
		} else {
			return false;
		}
	}

	public ApplicationInfo getAppinfo() {
		return appinfo;
	}

	public void setAppinfo(ApplicationInfo appinfo) {
		this.appinfo = appinfo;
	}

	public PackagesInfo getPkgInfo() {
		return pkgInfo;
	}

	public void setPkgInfo(PackagesInfo pkgInfo) {
		this.pkgInfo = pkgInfo;
	}

	public ActivityManager.RunningAppProcessInfo getRuninfo() {
		return runinfo;
	}

	public void setRuninfo(ActivityManager.RunningAppProcessInfo runinfo) {
		this.runinfo = runinfo;
	}

	public boolean isChceked() {
		return chceked;
	}

	public void setChceked(boolean chceked) {
		this.chceked = chceked;
	}

	public void setChkTask(CheckBox chkTask) {
		this.chkTask = chkTask;
	}

}
