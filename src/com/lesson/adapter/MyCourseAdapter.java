package com.lesson.adapter;

import java.util.List;

import org.apache.http.client.HttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.example.lesson.R;
import com.lesson.bean.CourseInf;
import com.lesson.util.LoadImage;

public class MyCourseAdapter extends ArrayAdapter<CourseInf> implements OnScrollListener{

	private int resourceId;
	private Context context;
	private Bitmap bitmap;
	private HttpClient client;
	private LoadImage loadImage;
	private int mStart;
	private int mEnd;
	public static String[] URLS;
	private boolean mFirstIn;
	
	public MyCourseAdapter(Context context,int textViewResourceId,List<CourseInf> courses ,ListView listView) {
		super(context, textViewResourceId, courses);
		resourceId = textViewResourceId;
		this.context = context;
		loadImage = new LoadImage(listView);
		URLS = new String[courses.size()];
		for(int i=0;i<courses.size();i++){
			URLS[i] = courses.get(i).getAndroidimage();
		}
		listView.setOnScrollListener(this);
		mFirstIn = true;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		final ViewHolder viewHolder;
		CourseInf course = getItem(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.my_course_item, null);
			viewHolder.mycourseImageView = (ImageView) view.findViewById(R.id.my_course_image);
			viewHolder.mycoursenameTextView = (TextView) view.findViewById(R.id.my_course_name);
			viewHolder.mycourseInformationTextView = (TextView) view.findViewById(R.id.my_course_information);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.mycoursenameTextView.setText(course.getCoursename());
		viewHolder.mycourseInformationTextView.setText(course.getCourseintroduction());
		loadImage.showImageByAsyncTask(viewHolder.mycourseImageView, course.getAndroidimage());
		return view;
	}
	
	private class ViewHolder{
		ImageView mycourseImageView;
		TextView mycoursenameTextView;
		TextView mycourseInformationTextView;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItem) {
		// TODO Auto-generated method stub
		mStart = firstVisibleItem;
		mEnd = firstVisibleItem + visibleItemCount;//可见的起始项
		//第一次显示调用
		if(mFirstIn && visibleItemCount > 0){
			loadImage.loadMyCoursesImages(mStart, mEnd);
			mFirstIn = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(scrollState == SCROLL_STATE_IDLE){
			//当前状态处于停止状态，加载可见项
			loadImage.loadMyCoursesImages(mStart, mEnd);
		}else{
			//停止任务
			loadImage.cancelAllTasks();
		}
	}
	
}
