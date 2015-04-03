package com.storm.fliplayout.effect;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storm.fliplayout.R;

/**
 * 自定义listview 上拉与下拉的刷新同时加入了轻微下拉出现搜索框
 * 
 * @author hehaitao
 */
public class SearchListView extends ListView implements OnScrollListener {
	private float xDistance;
	private float yDistance;
	private float xLast;
	private float yLast;

	// private static final String TAG = "listview";

	public final static int RELEASE_To_REFRESH = 0;

	public final static int PULL_To_REFRESH = 1;

	public final static int REFRESHING = 2;

	public final static int DONE = 3;

	public final static int LOADING = 4;

	/**
	 * 判断是上拉还是下拉，true为是下拉，false是上拉
	 */
	private boolean isTop = true;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 2;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private LinearLayout footView;

	private TextView tipsTextview;

	private TextView lastUpdatedTextView;

	private ImageView arrowImageView;

	private ProgressBar progressBar;

	private TextView foottipsTextview;

	private TextView footlastUpdatedTextView;

	private ImageView footarrowImageView;

	private ProgressBar footprogressBar;

	private RotateAnimation animation;

	private RotateAnimation reverseAnimation;

	private RotateAnimation footanimation;

	private RotateAnimation footreverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private boolean isBottomRecored;

	private int headContentWidth;

	private int headContentHeight;

	private int footContentWidth;

	private int footContentHeight;

	private int startY;

	private int startBottomY;

	private int firstItemIndex;

	public int state;

	private int bottomstate;

	private boolean isBack;

	private boolean isBottomBack;

	private OnRefreshListener refreshListener;

	/**
     * 
     */
	private boolean isRefreshable;

	private boolean isBackRefreshable;

	// listview当前显示页面的最后一条数据

	private int lastItem;

	// listview当前显示页面的第一条数据
	private int totalItemCount;

	/** 是否显示上拉刷新 **/
	private boolean isFootVisible = true;

	/** 是否显示下拉刷新 **/
	private boolean isHeaderVisible = true;

	/** 下拉图片资 源id **/
	private int upImageResources = R.drawable.arrow_01;
	/** 底图片资 源id **/
	private int bottomImageResources = R.drawable.arrow_02;
	/** 文字大小 **/
	private int textSize = 16;
	/** 文字 颜色 **/
	private int textColor = android.R.color.black;

	/** 内容显示 **/
	private RelativeLayout layoutContent;
	private int headerSearchHeight; // 记录头部的搜索框高度

	public SearchListView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param isFootVisible
	 * @param isHeaderVisible
	 */

	public SearchListView(Context context, boolean isFootVisible,
			boolean isHeaderVisible) {
		super(context);
		this.isFootVisible = isFootVisible;
		this.isHeaderVisible = isHeaderVisible;
		init(context);
	}

	public SearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.setDividerHeight(1);
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) getHeaderView();
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		if (this.isHeaderVisible) {
			addHeaderView(headView, null, false);
		}
		footView = (LinearLayout) getFootView();

		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footContentWidth = footView.getMeasuredWidth();
		footView.setPadding(0, 0, 0, -1 * footContentHeight);

		footView.invalidate();
		if (this.isFootVisible) {
			addFooterView(footView, null, false);
		}
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		footanimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		footanimation.setInterpolator(new LinearInterpolator());
		footanimation.setDuration(250);
		footanimation.setFillAfter(true);

		footreverseAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		footreverseAnimation.setInterpolator(new LinearInterpolator());
		footreverseAnimation.setDuration(200);
		footreverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		bottomstate = DONE;
		isBackRefreshable = false;
	}

	private View getFootView() {
		if(footView==null){
			footView = (LinearLayout) inflater.inflate(R.layout.listview_footer,
					null);
			footarrowImageView = (ImageView) footView
					.findViewById(R.id.foot_arrowImageView);
			footarrowImageView.setMinimumWidth(70);
			footarrowImageView.setMinimumHeight(50);
			footprogressBar = (ProgressBar) footView
					.findViewById(R.id.foot_progressBar);
			foottipsTextview = (TextView) footView
					.findViewById(R.id.foot_tipsTextView);
			footlastUpdatedTextView = (TextView) footView
					.findViewById(R.id.foot_lastUpdatedTextView);
		}
		return footView;
		
	}

	private View getHeaderView() {
		if (headView == null) {		

			headView = (LinearLayout) inflater.inflate(
					R.layout.listview_header_search, null);

			arrowImageView = (ImageView) headView
					.findViewById(R.id.head_arrowImageView);
			arrowImageView.setMinimumWidth(70);
			arrowImageView.setMinimumHeight(50);
			progressBar = (ProgressBar) headView
					.findViewById(R.id.head_progressBar);
			tipsTextview = (TextView) headView
					.findViewById(R.id.head_tipsTextView);
			lastUpdatedTextView = (TextView) headView
					.findViewById(R.id.head_lastUpdatedTextView);
			layoutContent = (RelativeLayout) headView
					.findViewById(R.id.layoutContent);
		}
		return headView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) { // 计算出lastitem的值
		lastItem = firstVisibleItem + visibleItemCount;
		// 同样拿出lastitem的值
		this.totalItemCount = totalItemCount;
		firstItemIndex = firstVisibleItem;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean isReturn  = false;
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					// Log.v(TAG, "在down时候记录当前位置‘");
				}

				if (lastItem == totalItemCount && !isBottomRecored) {
					isBottomRecored = true;
					startBottomY = (int) event.getY();
					// Log.v(TAG, "在bottomdown时候记录当前位置‘");
				}

				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						if(isReturn){
							state = DONE;
							changeHeaderViewByState();
						}else{
							headView.setPadding(0, -1 * headContentHeight
									+ headerSearchHeight, 0, 0);
							isReturn = false;
						}
						

						// Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();

						isTop = true;

						onRefresh(isTop);

						// Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				// Log.v(TAG, "bottomstate== PULL_To_REFRESH" + bottomstate +
				// "=="
				// + PULL_To_REFRESH);
				if (bottomstate != REFRESHING && bottomstate != LOADING) {
					if (bottomstate == DONE) {

					}
					if (bottomstate == PULL_To_REFRESH) {
						bottomstate = DONE;
						changeFootViewByState();

						// Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (bottomstate == RELEASE_To_REFRESH) {
						bottomstate = REFRESHING;
						changeFootViewByState();

						isTop = false;
						onRefresh(isTop);

						// Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isBottomRecored = false;
				isBottomBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					// Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往下拉了，，但是还没有拉到全部显示的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							// Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							// Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {

						setSelection(0);						 

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight*0.5) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							// Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0&&tempY - startY>-0.75*headerSearchHeight) {
							state = DONE;
							changeHeaderViewByState();

							// Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						if(tempY - startY>1.2*headerSearchHeight){
							headView.setPadding(0, -1 * headContentHeight
									+ (tempY - startY) / RATIO, 0, 0);
							isReturn = true;
						}else if(tempY - startY>0.8*headerSearchHeight&&tempY - startY<1.2*headerSearchHeight){
							headView.setPadding(0, -1 * headContentHeight
									+ headerSearchHeight, 0, 0);
							isReturn = false;
						}
						

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				// Log.v(TAG, "isBottomRecored=" + bottomstate + "" + lastItem
				// + "==" + totalItemCount);

				if (!isBottomRecored && lastItem == totalItemCount) {
					// Log.v(TAG, "在bottommove时候记录下位置");
					isBottomRecored = true;
					startBottomY = tempY;
				}

				if (bottomstate != REFRESHING && isBottomRecored
						&& bottomstate != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (bottomstate == RELEASE_To_REFRESH) {

						setSelection(totalItemCount - 1);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((startBottomY - tempY) / RATIO < footContentHeight)
								&& (startBottomY - tempY) > 0) {
							bottomstate = PULL_To_REFRESH;
							changeFootViewByState();

							// Log.v(TAG, "由松开刷新状态转变到上拉刷新状态");
						}
						// 一下子推到顶了
						else if (startBottomY - tempY <= 0) {
							bottomstate = DONE;
							changeFootViewByState();

							// Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (bottomstate == PULL_To_REFRESH) {

						setSelection(totalItemCount - 1);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((startBottomY - tempY) / RATIO >= footContentHeight) {
							bottomstate = RELEASE_To_REFRESH;
							isBottomBack = true;
							changeFootViewByState();

							// Log.v(TAG, "由done或者上拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (startBottomY - tempY < 0) {
							bottomstate = DONE;
							changeFootViewByState();

							// Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (bottomstate == DONE) {
						if (startBottomY - tempY > 0) {
							bottomstate = PULL_To_REFRESH;
							changeFootViewByState();
						}
					}

					// 更新headView的size
					if (bottomstate == PULL_To_REFRESH) {
						if ((startBottomY - tempY) <= 0) {
							footView.setPadding(0, 0, 0, footContentHeight + 1
									* (startBottomY - tempY) / RATIO);
						}
					}

					// 更新headView的paddingTop
					if (bottomstate == RELEASE_To_REFRESH) {
						footView.setPadding(0, 0, 0, (startBottomY - tempY)
								/ RATIO - footContentHeight);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	public void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开刷新");

			// Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			// Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(upImageResources);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态，done");
			break;
		}
	}

	// 当状态改变时候，调用该方法，以更新界面
	public void changeFootViewByState() {
		switch (bottomstate) {
		case RELEASE_To_REFRESH:
			footarrowImageView.setVisibility(View.VISIBLE);
			footprogressBar.setVisibility(View.GONE);
			foottipsTextview.setVisibility(View.VISIBLE);
			footlastUpdatedTextView.setVisibility(View.VISIBLE);

			footarrowImageView.clearAnimation();
			footarrowImageView.startAnimation(footanimation);

			foottipsTextview.setText("松开加载");

			// Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			footprogressBar.setVisibility(View.GONE);
			foottipsTextview.setVisibility(View.VISIBLE);
			footlastUpdatedTextView.setVisibility(View.VISIBLE);
			footarrowImageView.clearAnimation();
			footarrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBottomBack) {
				isBottomBack = false;
				footarrowImageView.clearAnimation();
				footarrowImageView.startAnimation(footreverseAnimation);

				foottipsTextview.setText("上拉加载更多");
			} else {
				foottipsTextview.setText("上拉加载更多");
			}
			// Log.v(TAG, "当前状态，上拉刷新");
			break;

		case REFRESHING:

			footView.setPadding(0, 0, 0, 0);

			footprogressBar.setVisibility(View.VISIBLE);
			footarrowImageView.clearAnimation();
			footarrowImageView.setVisibility(View.GONE);
			foottipsTextview.setText("正在加载...");
			footlastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			footView.setPadding(0, 0, 0, -1 * footContentHeight);

			footprogressBar.setVisibility(View.GONE);
			footarrowImageView.clearAnimation();
			footarrowImageView.setImageResource(bottomImageResources);
			foottipsTextview.setText("上拉加载更多");
			footlastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener onRefreshListener) {
		this.refreshListener = onRefreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh(boolean isTop);
	}

	public void onRefreshComplete() {
		state = DONE;
		bottomstate = DONE;
		lastUpdatedTextView.setText("最近更新:" + getTime());
		footlastUpdatedTextView.setText("最近更新:" + getTime());
		changeHeaderViewByState();
		changeFootViewByState();
	}

	private void onRefresh(boolean isTop) {
		if (refreshListener != null) {
			refreshListener.onRefresh(isTop);
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText("最近更新:" + getTime());
		super.setAdapter(adapter);
	}

	public void setIsTop(boolean isTop) {
		this.isTop = isTop;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = 0;
			yDistance = 0;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float curX = ev.getX();
			float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false; // 表示向下传递事件
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	public String getTime() {
		long time = System.currentTimeMillis();// long now =
												// android.os.SystemClock.uptimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		Date d1 = new Date(time);
		String t1 = format.format(d1);
		return t1;
	}

	/**
	 * 添加头部的view 与下拉刷新不冲突
	 * 
	 * @param view
	 *            视图
	 */
	public void addHeaderView(View header) {
		// TODO Auto-generated method stub
		if (null == header) {
			return;
		}

		layoutContent.addView(header);

		measureView(header);
		headerSearchHeight = header.getMeasuredHeight();

		headContentHeight += headerSearchHeight;

		getHeaderView().setPadding(0, -1 * headContentHeight, 0, 0);
	}

	/**
	 * 移除头部自定义添加内容
	 */
	public void removeHeader() {
		layoutContent.removeAllViews();

		measureView(getHeaderView()); // 防止多次添加导致高度计算失误
		headContentHeight = getHeaderView().getMeasuredHeight();
	}

}
