package com.storm.fliplayout.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 
 * @author heht 图片和bitmap工具类
 * 
 */
public class PicUtils {

	public static final String TAG = "PicUtils";

	/**
	 * 通过资源id转化成Bitmap
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		if (inSampleSize < 1)
			inSampleSize = 1;
		return inSampleSize;
	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示, 根据width，height的较小的缩放进行压缩。而不是把图片比率压缩
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath, int width, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		if (TextUtils.isEmpty(path))
			return;
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	/**
	 * 保存到相册
	 * 
	 * @param context
	 * @param bmp
	 * @return
	 */
	public static boolean galleryAddPic(Context context, Bitmap bmp) {
		String uriStr = MediaStore.Images.Media.insertImage(
				context.getContentResolver(), bmp, "", "");
		if (uriStr == null) {
			return false;
		}
		Uri uri = Uri.parse(uriStr);
		String picPath = null;
		if ("content".equals(uri.getScheme())) {
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { MediaColumns.DATA }, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				picPath = cursor.getString(0);
				cursor.close();
			}
		} else {
			picPath = uri.getPath();
		}
		if (picPath == null) {
			return false;
		}
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues(4);
		values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
		values.put(MediaColumns.MIME_TYPE, "image/png");
		values.put(ImageColumns.ORIENTATION, 0);
		values.put(MediaColumns.DATA, picPath);
		contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
		return true;
	}

	/**
	 * 保存到指定的目录
	 * 
	 * @param b
	 * @param strFileName
	 */
	public static void savePic(Bitmap b, String savePath, int sample) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savePath);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, sample, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取ScrollView的截屏
	 * 
	 * @param scrollView
	 * @param bgColor
	 * @return
	 */
	public static Bitmap getScrollViewBitmap(ScrollView scrollView, int bgColor) {
		int h = 0;
		Bitmap bitmap = null;
		// 获取ScrollView实际高度
		for (int i = 0; i < scrollView.getChildCount(); i++) {
			h += scrollView.getChildAt(i).getHeight();
		}
		Log.d(TAG, "实际高度:" + h);
		Log.d(TAG, " 高度:" + scrollView.getHeight());
		// 创建对应大小的bitmap
		try {
			bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
					Bitmap.Config.ARGB_8888);
			final Canvas canvas = new Canvas(bitmap);
			canvas.drawColor(bgColor);
			scrollView.draw(canvas);
		} catch (Exception e) {
			System.gc();
		}
		return bitmap;
	}

	/**
	 * 获取一个View的截屏
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getViewBitmap(View view) {
		int h = view.getHeight();
		Bitmap bitmap = null;
		// 创建对应大小的bitmap
		try {
			bitmap = Bitmap.createBitmap(view.getWidth(), h,
					Bitmap.Config.ARGB_8888);
			final Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
		} catch (Exception e) {
			bitmap = null;
			System.gc();
		}
		return bitmap;
	}

	/**
	 * 获取ListView的截屏
	 * 
	 * @param listView
	 * @return
	 */
	public static Bitmap getListViewBitmap(ListView listView) {
		int h = 0;
		Bitmap bitmap = null;
		// 获取listView实际高度
		for (int i = 0; i < listView.getChildCount(); i++) {
			h += listView.getChildAt(i).getHeight();
		}
		Log.d(TAG, "实际高度:" + h);
		Log.d(TAG, "list 高度:" + listView.getHeight());
		// 创建对应大小的bitmap
		bitmap = Bitmap.createBitmap(listView.getWidth(), h,
				Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);
		listView.draw(canvas);
		return bitmap;
	}

	/**
	 * 将两张位图拼接成一张(竖向拼接)
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Bitmap add2Bitmap(Bitmap bmpFirst, Bitmap bmpSecond) {
		int height = bmpFirst.getHeight() + bmpSecond.getHeight();
		int width = Math.max(bmpFirst.getWidth(), bmpSecond.getWidth());
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bmpFirst, 0, 0, null);
		canvas.drawBitmap(bmpSecond, 0, bmpFirst.getHeight(), null);
		return result;
	}

	/**
	 * 压缩图片
	 * 
	 * @param context
	 * @param filePath
	 * @param outputPath
	 * @param width
	 * @param height
	 * @param q
	 * @throws FileNotFoundException
	 */
	public static boolean compressImage(String filePath, String outputPath,
			int width, int height, int q) {
		FileOutputStream out = null;
		try {
			Bitmap bm = getSmallBitmap(filePath, width, height);
			if (!FileUtil.createFile(outputPath))
				return false;
			out = new FileOutputStream(outputPath);
			bm.compress(Bitmap.CompressFormat.JPEG, q, out);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * 获取压缩图片的质量
	 * 
	 * @param image
	 * @param size
	 * @return
	 */
	public static int getQuality(Bitmap image, int maxFileSize, int maxQuality,
			int minQuality) {
		int quality = maxQuality;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		while (baos.toByteArray().length / 1024 > maxFileSize
				&& quality > minQuality) {
			baos.reset();
			quality -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		}
		return quality;
	}

	/**
	 * 压缩图片
	 * 
	 * @param filePath
	 * @param outputPath
	 * @param width
	 * @param height
	 * @return
	 */
	public static boolean compressImage(String filePath, String outputPath,
			int width, int height) {
		FileOutputStream out = null;
		try {
			Bitmap bm = getSmallBitmap(filePath, width, height);
			if (bm == null)
				return false;
			if (!FileUtil.createFile(outputPath))
				return false;
			out = new FileOutputStream(outputPath);
			int outHeight = bm.getHeight();
			int outWidth = bm.getWidth();
			int size = outHeight * outWidth / 10000;
			if (size > 400) {
				size = 400;
			} else if (size < 10) {
				size = 10;
			}
			int qulity = getQuality(bm, size, 80, 30);
			bm.compress(Bitmap.CompressFormat.JPEG, qulity, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * 获取缩略图
	 * 
	 * @param bitmap
	 * @param scale
	 * @return
	 */
	public static Bitmap getSmapleBitmap(Bitmap bitmap, float scale) {
		Matrix m = new Matrix();
		m.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), m, true);
	}

	/**
	 * 回收bitmap
	 * 
	 * @param bmp
	 */
	public static void recycleBitmap(Bitmap bmp) {
		if (bmp != null && !bmp.isRecycled())
			bmp.recycle();
	}

	// 图片压缩后小于100KB，失真度不明显
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	/**
	 * 缩放图片
	 * 
	 * @param bm
	 *            要缩放图片
	 * @param newWidth
	 *            宽度
	 * @param newHeight
	 *            高度
	 * @return处理后的图片
	 */
	public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
		if (bm == null) {
			return null;
		}
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		if (bm != null & !bm.isRecycled()) {
			bm.recycle();// 销毁原图片
			bm = null;
		}
		return newbm;
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// 获得带倒影的图片方法
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 
	 * @param bitmap
	 * @return 返回圆形图片
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return output;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 *            旋转角度
	 * @param bitmap
	 *            要处理的Bitmap
	 * @return 处理后的Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return resizedBitmap;
	}

}
