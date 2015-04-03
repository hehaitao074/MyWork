package com.storm.fliplayout.helper;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class CallListener implements OnClickListener {
	private AlertDialog.Builder builder;
	private Context mcontext;

	public CallListener(Context context) {
		this.mcontext = context;
		// 确认对话框
		builder = new Builder(mcontext);

	}

	@Override
	public void onClick(View v) {
		final String phone = (String) v.getTag();
		builder.setMessage("是否拨打电话" + phone);
		builder.setTitle("提示");
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phone));
						mcontext.startActivity(intent);
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create();
		builder.show();
	}
}
