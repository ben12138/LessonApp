package com.lesson.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

public class Utils {

	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static String savePhoto(Bitmap photoBitmap, String path,
			String photoName) {
		String localPath = null;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName + ".png");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) { 
						localPath = photoFile.getPath();
						fileOutputStream.flush();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				localPath = null;
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				localPath = null;
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
						fileOutputStream = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return localPath;
	}

	public static Bitmap toRoundBitmap(Bitmap bitmap, Uri tempUri) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
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

		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint); 

		return output;
	}
	
	public static Bitmap getBitmap(byte[] b){
		System.out.println(b.length);
		System.out.println("bbb---->"+BitmapFactory.decodeByteArray(b,0, b.length));
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = false;
		option.inSampleSize = 2;
		return BitmapFactory.decodeByteArray(b,0, b.length,option);
	}
	
	public static Bitmap getBitmap(byte[] b,int n){
		System.out.println(b.length);
		System.out.println("bbb---->"+BitmapFactory.decodeByteArray(b,0, b.length));
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = false;
		option.inSampleSize = n;
		return BitmapFactory.decodeByteArray(b,0, b.length,option);
	}
	
	public static Bitmap getBitmap(String filename){
		File file = new File(filename);
		return BitmapFactory.decodeFile(filename);
	}
	
}
