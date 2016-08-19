package com.booster.avivast;

import com.booster.avivast.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.booster.avivast.process.AppShort;
import com.booster.avivast.process.PackagesInfo;
import com.booster.avivast.process.ProcessListAdapter;
import com.booster.avivast.process.TaskInfo;

public class TaskFragment extends Fragment implements OnDismissCallback,
		OnClickListener, TaskDialog.DialogDismissListener, TaskKillDialog.DialogTaskKillListener {

	private ListView swipeListView;
	private ProcessListAdapter adapter = null;
	public static int mem = 0;
	private ProgressDialog pd = null;
	private Button btnKill;
	private ActivityManager acm = null;
	private long beforeMemory;
	private long aftermemory;
	private int processesKilled;
	private int ramFreed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.task_list_layout, container, false);
		
		swipeListView = (ListView) v.findViewById(R.id.list);
		btnKill = (Button) v.findViewById(R.id.btnKill);
		btnKill.setOnClickListener(this);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		new TaskList(getActivity()).execute();
		acm = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		
		if (menuVisible) {
			 DisplayList(getActivity());
		}
	}

	public void DisplayList(Context context) {
		
		ActivityManager am = (ActivityManager) context
				.getSystemService(getActivity().ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = am
				.getRunningAppProcesses();// get all running task
		
		this.mem = 0;
		ArrayList<TaskInfo> arrList = new ArrayList<TaskInfo>();
		PackagesInfo pInfo = new PackagesInfo(context);
		Iterator<ActivityManager.RunningAppProcessInfo> iterator = list
				.iterator();
		do {// iterate all running task
			if (!iterator.hasNext()) {
				break;
			}
			ActivityManager.RunningAppProcessInfo runproInfo = (ActivityManager.RunningAppProcessInfo) iterator
					.next();
			String s = runproInfo.processName;
			if (!s.contains(getActivity().getPackageName())) {

				if (runproInfo.importance == 130
						|| runproInfo.importance == 300
						|| runproInfo.importance == 100
						|| runproInfo.importance == 400) {

					TaskInfo info = new TaskInfo(context, runproInfo);
					info.getAppInfo(pInfo);
					if (!isImportant(s)) {
						info.setChceked(true);
					}

					if (info.isGoodProcess()) {
						int j = runproInfo.pid;
						int i[] = new int[1];
						i[0] = j;
						Debug.MemoryInfo memInfo[] = am.getProcessMemoryInfo(i);
						int k = memInfo.length;
						for (int l = 0; l < memInfo.length; l++) {
							Debug.MemoryInfo mInfo = memInfo[l];
							int m = mInfo.getTotalPss() * 1024;
							info.setMem(m);
							int jl = mInfo.getTotalPss() * 1024;
							int kl = mem;
							if (jl > kl)
								mem = mInfo.getTotalPss() * 1024;
						}
						if (mem > 0)
							arrList.add(info);
					}
				}
			}
		} while (true);

		AppShort shortmem = new AppShort();
		Collections.sort(arrList, shortmem);// shorting list based on total memory used each process
		adapter = new ProcessListAdapter(context, arrList);
		swipeListView.setFocusableInTouchMode(true);
		ScaleInAnimationAdapter scaleAnimAdapter = new ScaleInAnimationAdapter(
				new SwipeDismissAdapter(adapter, this));
		scaleAnimAdapter.setAbsListView(swipeListView);
		swipeListView.setAdapter(scaleAnimAdapter);
		
		/*
		 * SwipeDismissListViewTouchListener listener = new
		 * SwipeDismissListViewTouchListener(swipeListView, mCallBack);
		 * swipeListView.setOnScrollListener(listener.makeScrollListener());
		 * swipeListView.setOnTouchListener(listener);
		 */
	}

	private boolean isImportant(String pname) {
		
		if (pname.equals("android") || pname.equals("android.process.acore")
				|| pname.equals("system") || pname.equals("com.android.phone")
				|| pname.equals("com.android.systemui")
				|| pname.equals("com.android.launcher")) {
			return true;
		} else {
			return false;
		}
	}

	// listview swipe listener event, when swipe left or right
	// clean specific process from the list 
	@Override
	public void onDismiss(AbsListView list, int[] reversepos) {
		
		for (int pos : reversepos) {
			TaskInfo info = (TaskInfo) adapter.getItem(pos);
			if (isImportant(info.getPackageName())) {
				TaskKillDialog dailog = new TaskKillDialog();
				dailog.setPos(pos);
				dailog.setIcon(info.getIcon());
				dailog.setAppName(info.getTitle());
				dailog.setDialgTaskKillListener(TaskFragment.this);
				dailog.setCancelable(false);
				dailog.show(getFragmentManager(), "killfrag");
				adapter.notifyDataSetChanged();
			}else {
				acm.killBackgroundProcesses(info.getPackageName());
				adapter.remove(adapter.getItem(pos));
				adapter.notifyDataSetChanged();
			}
		}
		
	}

	public class TaskList extends AsyncTask<Void, Void, ArrayList<TaskInfo>> {

		private Activity context;

		public TaskList(Activity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected ArrayList<TaskInfo> doInBackground(Void... arg0) {
			ActivityManager am = (ActivityManager) context
					.getSystemService(getActivity().ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> list = am
					.getRunningAppProcesses();
			mem = 0;
			ArrayList<TaskInfo> arrList = new ArrayList<TaskInfo>();
			PackagesInfo pInfo = new PackagesInfo(context);
			Iterator<ActivityManager.RunningAppProcessInfo> iterator = list
					.iterator();

			do {
				if (!iterator.hasNext()) {
					break;
				}
				ActivityManager.RunningAppProcessInfo runproInfo = (ActivityManager.RunningAppProcessInfo) iterator
						.next();
				String s = runproInfo.processName;
				if (!s.contains(getActivity().getPackageName())) {

					if (runproInfo.importance == 130
							|| runproInfo.importance == 300
							|| runproInfo.importance == 100
							|| runproInfo.importance == 400) {

						TaskInfo info = new TaskInfo(context, runproInfo);
						info.getAppInfo(pInfo);
						if (!isImportant(s)) {
							info.setChceked(true);
						}

						if (info.isGoodProcess()) {
							int j = runproInfo.pid;
							int i[] = new int[1];
							i[0] = j;
							Debug.MemoryInfo memInfo[] = am
									.getProcessMemoryInfo(i);
							int k = memInfo.length;
							for (int l = 0; l < memInfo.length; l++) {
								Debug.MemoryInfo mInfo = memInfo[l];
								int m = mInfo.getTotalPss() * 1024;
								info.setMem(m);
								int jl = mInfo.getTotalPss() * 1024;
								int kl = mem;
								if (jl > kl)
									mem = mInfo.getTotalPss() * 1024;
							}
							if (mem > 0)
								arrList.add(info);
						}
					}
				}
			} while (true);

			return arrList;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskInfo> arrList) {
			
			AppShort shortmem = new AppShort();
			Collections.sort(arrList, shortmem);
			adapter = new ProcessListAdapter(context, arrList);
			swipeListView.setFocusableInTouchMode(true);
			ScaleInAnimationAdapter scaleAnimAdapter = new ScaleInAnimationAdapter(
					new SwipeDismissAdapter(adapter, TaskFragment.this));
			scaleAnimAdapter.setAbsListView(swipeListView);
			swipeListView.setAdapter(scaleAnimAdapter);

			if (pd != null) {
				pd.dismiss();
				pd = null;
			}
		}
	}

	@Override
	public void onClick(View v) {

		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		acm.getMemoryInfo(memInfo);
		beforeMemory = memInfo.availMem;
		new DialogTask().execute();
	}

	private class DialogTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(getActivity());
			pd.setMessage("please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				int j = 0;
				TaskInfo info = null;
				while (adapter.getCount() > j) {
					info = adapter.getItem(j);
					if (info.isChceked()) {
						Log.d("TaskList: ", info.getPackageName());
						acm.killBackgroundProcesses(info.getPackageName());
					}
					j++;
				}

				processesKilled = j;

				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (pd != null) {
				pd.dismiss();
			}

			ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
			acm.getMemoryInfo(memInfo);
			aftermemory = memInfo.availMem;
			if (aftermemory > beforeMemory) {
				ramFreed = (int) (aftermemory - beforeMemory);
			} else {
				ramFreed = 0;
			}

			TaskDialog dialog = new TaskDialog();
			dialog.setDialogListener(TaskFragment.this);
			dialog.setProcessKilled(String.valueOf(processesKilled));
			dialog.setMemoryCleaned(BoostFragment.formatMemSize(ramFreed, 0));
			dialog.setCancelable(false);
			dialog.show(getFragmentManager(), "dialog");
		}

	}

	@Override
	public void onDialogDismiss() {
		new TaskList(getActivity()).execute();
	}

	@Override
	public void onTaskKIll(int pos) {
		
		TaskInfo info = (TaskInfo) adapter.getItem(pos);
		acm.killBackgroundProcesses(info.getPackageName());
		adapter.remove(adapter.getItem(pos));
		adapter.notifyDataSetChanged();
	}

}
