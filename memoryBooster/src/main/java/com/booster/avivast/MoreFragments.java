package com.booster.avivast;

import com.booster.avivast.R;
import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.booster.avivast.antivirus.AntivirusActivity;
import com.booster.avivast.antivirus.AppData;
import com.booster.avivast.antivirus.EulaActivity;
import com.todddavies.components.progressbar.ProgressWheel;

public class MoreFragments extends Fragment {

	private TextView txtTotal, txtAvail, txtFree;
	private TextView txtExTotal, txtExAvail, txtExFree;
	private ProgressWheel pw = null;
	private ProgressWheel exPW = null;
	private LinearLayout layourExternal;
	private ImageView anti;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.memory_info_layout, container, false);

		pw = (ProgressWheel) v.findViewById(R.id.pw_spinner_internal);
		exPW = (ProgressWheel) v.findViewById(R.id.pw_spinner_external);
		anti = (ImageView) v.findViewById(R.id.antivirus);

		layourExternal = (LinearLayout) v.findViewById(R.id.layourExternal);

		txtTotal = (TextView) v.findViewById(R.id.txtTotalInternalMemory);
		txtAvail = (TextView) v.findViewById(R.id.txtAvailInternalMemory);
		txtFree = (TextView) v.findViewById(R.id.txtFreeInternalMemory);

		txtExTotal = (TextView) v.findViewById(R.id.txtTotalExternalMemory);
		txtExAvail = (TextView) v.findViewById(R.id.txtAvailExternalMemory);
		txtExFree = (TextView) v.findViewById(R.id.txtFreeExternalMemory);

		anti.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final AppData appData = AppData.getInstance(getActivity());

				Thread timerThread = new Thread(){
					public void run(){
						try{
							sleep(500);
						}catch(InterruptedException e){
							e.printStackTrace();
						}finally{

							Intent intent = new Intent(getActivity(),AntivirusActivity.class);
							startActivity(intent);
							/*if(appData.getEulaAccepted())
							{
								Intent intent = new Intent(getActivity(),AntivirusActivity.class);
								startActivity(intent);
							}else
							{
								Intent intent = new Intent(getActivity(),EulaActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								startActivity(intent);
							}*/

						}
					}
				};
				timerThread.start();

			}
		});


		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		/**
		 * Get device memory info and display
		 */

		DeviceMemoryInfo info = new DeviceMemoryInfo(getActivity());

		String total = Formatter.formatFileSize(getActivity(),
				info.getTotalInternalMemorySize());
		String avail = Formatter.formatFileSize(getActivity(),
				info.getAvailableInternalMemorySize());

		txtTotal.setText("Total: " + total);
		txtAvail.setText("Available: " + avail);
		txtFree.setText("Used: " + info.getFreeInternalMemorySize());

		float f = (float) info.getAvailableInternalMemorySize();
		float f1 = (float) info.getTotalInternalMemorySize();
		int i = (int) ((f / f1) * 100F);
		setInternalPercentage(i);

		String[] dir = info.getStorageDirectories();
		for (int j = 0; j < dir.length; j++) {
			if (!TextUtils.isEmpty(dir[j])) {
				File file = new File(dir[j]);
				if (file.exists() && file.length() > 0) {
					ShowExternalInfo(file);
				}
			}
		}
	}

	private void ShowExternalInfo(File file) {
		layourExternal.setVisibility(View.VISIBLE);
		String exTotal = Formatter.formatFileSize(getActivity(),
				DeviceMemoryInfo.getTotalExternalMemorySize(file));
		String exAvail = Formatter.formatFileSize(getActivity(),
				DeviceMemoryInfo.getAvailableExternalMemorySize(file));

		txtExTotal.setText("Total: " + exTotal);
		txtExAvail.setText("Available: " + exAvail);
		txtExFree.setText("Used: "
				+ DeviceMemoryInfo.getFreeExternalMemorySize(file));

		float exf = (float) DeviceMemoryInfo
				.getAvailableExternalMemorySize(file);
		float exf1 = (float) DeviceMemoryInfo.getTotalExternalMemorySize(file);
		int t = (int) ((exf / exf1) * 100F);
		setExternalPercentage(t);
	}

	public void setInternalPercentage(int info) {

		String s = (new StringBuilder()).append(info).append("%").toString();
		pw.setText(s);
		int j = (int) ((double) info * 3.6000000000000001D);
		pw.setProgress(j);

	}

	public void setExternalPercentage(int info) {

		String s = (new StringBuilder()).append(info).append("%").toString();
		exPW.setText(s);
		int j = (int) ((double) info * 3.6000000000000001D);
		exPW.setProgress(j);

	}

}
