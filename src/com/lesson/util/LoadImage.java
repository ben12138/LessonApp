package com.lesson.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.example.lesson.R;
import com.lesson.adapter.CommentsAdapter;
import com.lesson.adapter.CoursesAdapter;
import com.lesson.adapter.MyCourseAdapter;
import com.lesson.bean.Comments;
import com.lesson.fragment.MyCourseFragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

public class LoadImage {
	
	private ImageView mImageView;
	private String mUrl;
	//创建Cache
	private LruCache<String, Bitmap> mCache;
	private ListView mListView;
	private Set<CommentAsyncTask> mTask;
	
	@SuppressLint("NewApi")
	public LoadImage(ListView listView) {
		mListView = listView;
		mTask = new HashSet<>();
		//获取最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory/4;
		mCache = new LruCache<String,Bitmap>(cacheSize){
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				//在每次存入缓存的时候调用
				return value.getByteCount();
			}
		};
	}
	
	//增加到缓存
	@SuppressLint("NewApi")
	public void addBitmapToCache(String url,Bitmap bitmap){
		if(getBitmapFromCache(url) == null){
			mCache.put(url, bitmap);
		}
	}
	
	@SuppressLint("NewApi")
	public Bitmap getBitmapFromCache(String url){
		return mCache.get(url);
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			mImageView.setImageBitmap((Bitmap)msg.obj);
		};
	};
	
	public void showImageByAsyncTask(ImageView imageview,String url){
		//从缓存中取出对应图片
		Bitmap bitmap = getBitmapFromCache(url);
		//若缓存中没有则从网络中下载
		if(bitmap == null){
			imageview.setImageResource(R.drawable.default_personal_image);
			CommentAsyncTask task = new CommentAsyncTask(imageview,url);
			task.execute(url);
			mTask.add(task);
		}else{
			imageview.setImageBitmap(bitmap);
		}
		
	}
	
	public void loadCommentsImages(int start,int end){
		for(int i=start;i<end;i++){
			System.out.println("start:"+start+"---------end:"+end);
			String url = CommentsAdapter.URLS.get(i);
			//从缓存中取出对应图片
			Bitmap bitmap = getBitmapFromCache(url);
			//若缓存中没有则从网络中下载
			if(bitmap == null){
				CommentAsyncTask task = new CommentAsyncTask(url);
				System.out.println("task");
				task.execute(url);
				mTask.add(task);
			}else{
				ImageView imageview = (ImageView) mListView.findViewWithTag(url);
				if(imageview != null){
					imageview.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	public void loadCoursesImages(int start,int end){
		for(int i=start;i<end;i++){
			System.out.println("start:"+start+"---------end:"+end);
			String url = CoursesAdapter.URLS[i];
			//从缓存中取出对应图片
			Bitmap bitmap = getBitmapFromCache(url);
			//若缓存中没有则从网络中下载
			if(bitmap == null){
				CommentAsyncTask task = new CommentAsyncTask(url);
				System.out.println("task");
				task.execute(url);
				mTask.add(task);
			}else{
				ImageView imageview = (ImageView) mListView.findViewWithTag(url);
				if(imageview != null){
					imageview.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	public void loadMyCoursesImages(int start,int end){
		for(int i=start;i<end;i++){
			System.out.println("start:"+start+"---------end:"+end);
			String url = MyCourseAdapter.URLS[i];
			//从缓存中取出对应图片
			Bitmap bitmap = getBitmapFromCache(url);
			//若缓存中没有则从网络中下载
			if(bitmap == null){
				CommentAsyncTask task = new CommentAsyncTask(url);
				System.out.println("task");
				task.execute(url);
				mTask.add(task);
			}else{
				ImageView imageview = (ImageView) mListView.findViewWithTag(url);
				if(imageview != null){
					imageview.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	private class CommentAsyncTask extends AsyncTask<String, Void, Bitmap>{
		
		private String mUrl;
		private ImageView imageView;
		
		public CommentAsyncTask(String url) {
			// TODO Auto-generated constructor stub
			mUrl = url;
		}
		
		public CommentAsyncTask(ImageView imageView,String url) {
			// TODO Auto-generated constructor stub
			mUrl = url;
			this.imageView = imageView;
		}
		
		@Override
		protected Bitmap doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			//从网络获取图片
			Bitmap bitmap = loadImage(mUrl);
			if(bitmap!=null){
				//将图片保存至缓存
				addBitmapToCache(arg0[0], bitmap);
			}
			return bitmap;
		}
		
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			ImageView imageview = (ImageView) mListView.findViewWithTag(mUrl);
			System.out.println("async imageview:"+imageView);
			System.out.println("async bitmap:"+result);
			if(imageView != null && result != null){
				imageView.setImageBitmap(result);
			}
		}
		
	}
	
	public Bitmap loadImage(final String imageUrl){
		Bitmap bitmap;
		URL url;
		try {
			url = new URL(imageUrl);
			InputStream is = url.openStream();
			bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void cancelAllTasks(){
		if(mTask != null){
			for(CommentAsyncTask task:mTask){
				task.cancel(false);
			}
		}
	}
	
}
