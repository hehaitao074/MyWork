package com.storm.fliplayout.effect;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 
 * @author heht
 * 仿网易云音乐 专辑图片折叠轮播
 */

public class ChangeImageView extends ImageView {

	private List<Bitmap> mbitmaps;
	private Paint paint;
	private Xfermode xfermode;
	private boolean change; // 当前是否变换
	private int bhjg = 5000; // 变换间隔时间
	private int bhsj = 50; // 变换时间
	private int frontindex;// 前景索引
	private int backindex;// 背景索引
	private boolean setbitmap = false; // 是否设置bitmap;
	private Matrix matrix;
	private int count = 0;
	private int henght;

	public ChangeImageView(Context context) {
		this(context, null);
	}

	public ChangeImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ChangeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
		paint.setAntiAlias(true);
		xfermode = new PorterDuffXfermode(Mode.SRC_OVER);
		change = false;
		matrix = new Matrix();
		mbitmaps = new ArrayList<Bitmap>();

		setBitmaps(mbitmaps);
		henght = mbitmaps.get(0).getHeight();
		bhjg = (int) Math.random() * (5000 - 2000) + 2000;
	}

	public void setBitmaps(List<Bitmap> mbitmaps) {
		this.mbitmaps = mbitmaps;
		setbitmap = true;
		if (mbitmaps.size() >= 2) {
			frontindex = 0;
			backindex = 1;
		} else {
			frontindex = 0;
			backindex = 0;
		}
		new Thread() {
			public void run() {
				handler.postDelayed(run, bhjg);
				handler1.postDelayed(run1, bhsj);
			};
		}.start();
	}

	Runnable run1 = new Runnable() {
		@Override
		public void run() {
			if (change) {
				ChangeImageView.this.invalidate();
			}
			handler1.postDelayed(run1, bhsj);
		}
	};
	Runnable run = new Runnable() {
		@Override
		public void run() {
			change = true;
			handler.postDelayed(run, bhjg);
		}
	};
	Handler handler1 = new Handler();
	Handler handler = new Handler();

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Bitmap bmp = mbitmaps.get(backindex);
		setMeasuredDimension(bmp.getWidth(), bmp.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (change) {
			Bitmap bmp = mbitmaps.get(frontindex);
			Bitmap bmp1 = mbitmaps.get(backindex);
			count++;
			if (bmp1.getHeight() - 5 * count >= bmp1.getHeight() / 2) {
				// 绘制上半部分图像
				canvas.save();
				canvas.clipRect(0, 0, bmp1.getWidth(), bmp1.getHeight() / 2);
				canvas.drawBitmap(bmp1, 0, 0, paint);
				canvas.restore();
				// 绘制下班部分图像
				canvas.save();
				canvas.clipRect(0, bmp1.getHeight() / 2, bmp1.getWidth(),
						bmp1.getHeight());
				canvas.drawBitmap(bmp, 0, 0, paint);
				canvas.restore();
				// 动态改变下半部分图像
				float[] src = { 0, bmp1.getHeight() / 2,//
						bmp1.getWidth(), bmp1.getHeight() / 2,//
						bmp1.getWidth(), bmp1.getHeight(),//
						0, bmp1.getHeight() };
				henght = bmp1.getHeight() - 5 * count;
				float[] dst = { 0, bmp1.getHeight() / 2,//
						bmp1.getWidth(), bmp1.getHeight() / 2,//
						bmp1.getWidth(), henght,//
						0, henght };
				System.out.println(count + "");
				canvas.save();
				matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
				canvas.clipRect(0, bmp1.getHeight() / 2, bmp1.getWidth(),
						henght);
				paint.setXfermode(null);
				paint.setXfermode(xfermode);
				canvas.drawBitmap(bmp1, matrix, paint);
				canvas.restore();
				paint.setXfermode(null);
			} else {
				// 绘制上半部分图像
				canvas.save();
				canvas.clipRect(0, 0, bmp1.getWidth(), bmp1.getHeight() / 2);
				canvas.drawBitmap(bmp1, 0, 0, paint);
				canvas.restore();
				// 绘制下班部分图像
				canvas.save();
				canvas.clipRect(0, bmp1.getHeight() / 2, bmp1.getWidth(),
						bmp1.getHeight());
				canvas.drawBitmap(bmp, 0, 0, paint);
				canvas.restore();
				// 动态改变上半部分图像
				float[] src = { 0, 0,//
						bmp1.getWidth(), 0,//
						bmp1.getWidth(), bmp1.getHeight() / 2,//
						0, bmp1.getHeight() / 2 };
				henght = bmp1.getHeight() - 5 * count;
				float[] dst = { 0, henght,//
						bmp1.getWidth(), henght,//
						bmp1.getWidth(), bmp1.getHeight() / 2,//
						0, bmp1.getHeight() / 2 };
				System.out.println(count + "");
				canvas.save();
				matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
				canvas.clipRect(0, bmp1.getHeight() / 2, bmp1.getWidth(),
						henght);
				paint.setXfermode(null);
				paint.setXfermode(xfermode);
				canvas.drawBitmap(bmp, matrix, paint);
				canvas.restore();
				paint.setXfermode(null);
				if (henght <= 0) { // 说明动画结束 重置动画参数
					count = 0;
					change = false;
					int temp = frontindex;
					frontindex = backindex;
					backindex = temp;
					System.out.println("change false");
					bhjg = (int) (Math.random() * (8000 - 5000)) + 5000;
					System.out.println("bhjg: " + bhjg + "");
				}
			}
		} else {
			if (!setbitmap) {
				canvas.drawBitmap(mbitmaps.get(0), matrix, paint);
			}
		}
	}
}
