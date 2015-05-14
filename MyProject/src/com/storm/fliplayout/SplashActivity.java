package com.storm.fliplayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.image.SmartImageView;
import com.storm.fliplayout.helper.Utils;

public class SplashActivity extends AppBaseActivity {

	private static final int FINISH = 1;
	private Context context = this;
	@ViewInject(R.id.img_splash)
	private SmartImageView smartImageView;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FINISH:
				Intent intent = new Intent();
				intent.setClass(context, MainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
				break;

			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ViewUtils.inject(this);
		// 初始化百度推送
		if (!Utils.hasBind(this)) {
			Utils.initWithApiKey(this);

		}
		smartImageView.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.alpha));
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				// Message message = handler.obtainMessage();
				// message.what = FINISH;
				handler.sendEmptyMessage(FINISH);
			}
		}, 3000);

	}

}
