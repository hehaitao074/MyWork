package com.storm.fliplayout.effect;

import com.storm.fliplayout.R;
import com.storm.fliplayout.R.anim;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ScrollListView extends ListView implements OnScrollListener {

	/**
	 * 下滑时刷新
	 */
	private final int DOWNREFRESH = 1;

	/**
	 * 上滑是刷新
	 */
	private final int UPREFRESH = 0;

	/**
	 * 初始状态下第一个 条目
	 */
	private int startfirstItemIndex;

	/**
	 * 初始状态下最后一个 条目
	 */
	private int startlastItemIndex;

	/**
	 * 滑动后的第一个条目
	 */
	private int endfirstItemIndex;

	/**
	 * 滑动后的最后一个条目
	 */
	private int endlastItemIndex;

	private View view;

	private Animation animation;

	private Handler handler;

	private Runnable run;

	private Message message;

	public ScrollListView(Context context) {

		this(context, null);
	}

	public ScrollListView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.listViewStyle);

	}

	public ScrollListView(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnScrollListener(this);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int result = (Integer) msg.obj;
				switch (result) {
					case DOWNREFRESH: {
						//获得最后一个item
						view = getChildAt(getChildCount() - 1);
						break;
					}
					case UPREFRESH: {
						//获得第一个item
						view = getChildAt(0);
						break;
					}
					default:
						break;
				}
				if (null != view) {
					//加载动画
					animation = AnimationUtils.loadAnimation(context, R.anim.translate);
					view.startAnimation(animation);
				}
			}
		};

	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		startfirstItemIndex = arg1;
		startlastItemIndex = arg1 + arg2 - 1;
		// 判断向下或者向上滑动了
		if ((endfirstItemIndex > startfirstItemIndex) && (endfirstItemIndex > 0)) {
			RunThread(UPREFRESH);
		} else if ((endlastItemIndex < startlastItemIndex) && (endlastItemIndex > 0)) {
			RunThread(DOWNREFRESH);
		}
		endfirstItemIndex = startfirstItemIndex;
		endlastItemIndex = startlastItemIndex;
	}

	private void RunThread(final int state) {
		// TODO Auto-generated method stub
		run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				message = handler.obtainMessage(1, state);
				handler.sendMessage(message);
			}
		};
		run.run();
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
