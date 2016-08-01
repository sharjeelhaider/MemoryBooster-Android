package com.raihanbd.easyrambooster;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import geniuscloud.memory.booster.R;

public class TaskKillDialog extends DialogFragment implements OnClickListener {

	private Dialog dialog = null;
	private Button btnTaskKill;
	private Button btnTaskCancel;
	private TextView txtTaskTitle;
	private ImageView imgTaskIcon;
	private int pos = 0;
	private int icon;
	private String appName;
	private DialogTaskKillListener listener;

	public TaskKillDialog() {

	}

	public interface DialogTaskKillListener {
		public void onTaskKIll(int pos);
	}

	public void setDialgTaskKillListener(TaskFragment context) {
		this.listener = (DialogTaskKillListener) context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		dialog = new Dialog(getActivity());
		dialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.taskclean_dialog);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));

		txtTaskTitle = (TextView) dialog.findViewById(R.id.txtTaskTitle);
		imgTaskIcon = (ImageView) dialog.findViewById(R.id.imgTaskIcon);
		btnTaskKill = (Button) dialog.findViewById(R.id.btnTaskClean);
		btnTaskCancel = (Button) dialog.findViewById(R.id.btnTaskCancel);
		btnTaskKill.setOnClickListener(this);
		btnTaskCancel.setOnClickListener(this);

		if (icon > 0) {
			imgTaskIcon.setImageResource(icon);
		}
		
		if (!TextUtils.isEmpty(appName)) {
			txtTaskTitle.setText(appName);
		} else {
			txtTaskTitle.setText(getActivity().getResources().getString(
					R.string.dialog_title));
		}

		return dialog;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public void setIcon(int icon) {
		this.icon = icon;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTaskClean:
			listener.onTaskKIll(pos);
			TaskKillDialog.this.dismiss();
			break;

		case R.id.btnTaskCancel:
			TaskKillDialog.this.dismiss();
			break;
		}
	}
}
