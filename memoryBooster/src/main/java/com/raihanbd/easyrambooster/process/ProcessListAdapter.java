package com.raihanbd.easyrambooster.process;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import geniuscloud.memory.booster.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ProcessListAdapter extends ArrayAdapter<TaskInfo> implements
		OnClickListener {

	private ActivityManager am = null;
	private boolean check = false;
	private Context context;
	private ArrayList<TaskInfo> taskList = null;
	private PackageManager pm = null;

	public ProcessListAdapter(Context context, ArrayList<TaskInfo> taskList) {
		
		super(context, R.layout.process_list_layout, taskList);
		this.context = context;
		this.taskList = taskList;
		this.pm = context.getPackageManager();
		this.am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	@Override
	public int getCount() {
		return taskList.size();
	}

	@Override
	public TaskInfo getItem(int pos) {
		return taskList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	class ViewHolder {
		private ImageView taskIcon;
		private TextView tastTitle;
		private TextView taskMem;
		private TextView taskCPU;
		private CheckBox taskCheck;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		ViewHolder holder;
		final TaskInfo info;
		ApplicationInfo appInfo;
		
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater
					.inflate(R.layout.process_list_layout, parent, false);
			holder = new ViewHolder();
			holder.taskIcon = (ImageView) view.findViewById(R.id.imgTaskIcon);
			holder.tastTitle = (TextView) view.findViewById(R.id.txtTaskTitle);
			holder.taskMem = (TextView) view.findViewById(R.id.txtTaskMemory);
			holder.taskCPU = (TextView) view.findViewById(R.id.txtTaskCPU);
			holder.taskCheck = (CheckBox) view
					.findViewById(R.id.chkTaskChecked);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		info = taskList.get(position);
		appInfo = info.getAppinfo();
		Drawable icon = appInfo.loadIcon(pm);
		holder.taskIcon.setImageDrawable(icon);
		
		if (info.mem == 0L) {
			holder.taskMem.setText("");
		} else {
			holder.tastTitle.setText(info.getTitle());
			String m = Formatter.formatFileSize(context, info.mem);
			holder.taskMem.setText("MEM: " + m);
			DecimalFormat format = new DecimalFormat("#.##");
			String cpu = (new StringBuilder(String.valueOf(format
					.format(getCPUTime(info.getRuninfo().pid)))).append("%"))
					.toString();
			holder.taskCPU.setText("CPU: " + cpu);
			holder.taskCheck.setChecked(info.isChceked());
			holder.taskCheck.setTag(new Integer(position));
			holder.taskCheck.setOnClickListener(this);
		}

		return view;
	}

	public double getCPUTime(int pid) {
		
		double d = 0;
		String path = (new StringBuilder("/proc/")).append(pid).append("/stat")
				.toString();
		File file = new File(path);
		DataInputStream dis = null;
		try {
			FileInputStream is = new FileInputStream(file);
			dis = new DataInputStream(is);
			String[] as = dis.readLine().split("\\s+");
			int j = 13;
			while (j < 17) {
				int k = Integer.parseInt(as[j]);
				double dl = k;
				d += dl;
				j++;
			}

			d /= 1000D;
			while (d > 10D) {
				d /= 10D;
			}

			if (d < 0.01D) {
				if (d == 0D) {
					d = (double) ((new Random()).nextInt(22) + 3) / 100D;
					return (double) (int) (d * 0F) / 100D;
				}
				d *= 10D;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return d;
	}

	@Override
	public void onClick(View view) {
		CheckBox chk = (CheckBox) view;
		int pos = (Integer) chk.getTag();
		TaskInfo info = taskList.get(pos);
		info.setChceked(chk.isChecked());
	}
	
	

}
