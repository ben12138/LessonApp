package com.lesson.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lesson.R;
import com.lesson.activity.FullScreenActivity;
import com.lesson.bean.Comments;
import com.lesson.bean.CourseInf;
import com.lesson.bean.CourseUrl;
import com.lesson.databasehelper.MyDownLoadsDataBasesHelper;

public class MyDownloadsAdapter extends ArrayAdapter<CourseUrl>{
	
	private int resourceId;
	private Context context;
	private ListView courseUrlListView;
	private List<CourseUrl> courseurls;
	
	public MyDownloadsAdapter(Context context,int textViewResourceId,List<CourseUrl> courseurls ,ListView listView) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, courseurls);
		resourceId = textViewResourceId;
		this.context = context;
		this.courseUrlListView = listView;
		this.courseurls = courseurls;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view;
		ViewHolder viewHolder;
		final CourseUrl courseurl = getItem(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.my_download_item, null);
			viewHolder.bofangImageView = (ImageView) view.findViewById(R.id.bofang);
			viewHolder.coursenameTextView = (TextView) view.findViewById(R.id.coursename);
			viewHolder.deletecourseImageView = (ImageView) view.findViewById(R.id.deletecourse);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.coursenameTextView.setText(courseurl.getCoursename());
		viewHolder.bofangImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,FullScreenActivity.class);
				intent.putExtra("courseurl", courseurl.getCourseurl());
				intent.putExtra("coursename", courseurl.getCoursename());
				context.startActivity(intent);
			}
		});
		viewHolder.coursenameTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,FullScreenActivity.class);
				intent.putExtra("courseurl", courseurl.getCourseurl());
				intent.putExtra("coursename", courseurl.getCoursename());
				context.startActivity(intent);
			}
		});
		viewHolder.deletecourseImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("删除");
				builder.setMessage("是否确定要删除?");
				builder.setCancelable(false);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						MyDownLoadsDataBasesHelper dbHelper = new MyDownLoadsDataBasesHelper(context, "downloads.db3", 1);
						SQLiteDatabase db = dbHelper.getReadableDatabase();
						db.delete("downloads", "coursename=?", new String[]{courseurl.getCoursename()});
						File file = new File(courseurl.getCourseurl());
						if(file.exists()){
							file.delete();
						}
						courseurls.remove(position);
						courseUrlListView.setAdapter(new MyDownloadsAdapter(context, R.layout.my_download_item, courseurls, courseUrlListView));
						db.close();
						dbHelper.close();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.show();
			}
		});
		getVideoImageView(viewHolder.bofangImageView, courseurl.getCourseurl());
		return view;
	}
	
	private void getVideoImageView(final ImageView videoImageView,final String url){
		new AsyncTask<Void, Void, Bitmap>() {
			@SuppressLint("NewApi")
			@Override
			protected Bitmap doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				Bitmap bitmap = null;  
		        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一  
				// 的接口，用于从输入的媒体文件中取得帧和元数据；
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				// 根据文件路径获取缩略图
				retriever.setDataSource(url);
				// 获得第一帧图片
				bitmap = retriever.getFrameAtTime();
				return bitmap;
			}
			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				System.out.println(result);
				videoImageView.setImageBitmap(result);
			}
		}.execute();
	}
	
	private class ViewHolder{
		ImageView bofangImageView;
		TextView coursenameTextView;
		ImageView deletecourseImageView;
	}
	
}
