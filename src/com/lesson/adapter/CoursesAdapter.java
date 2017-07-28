package com.lesson.adapter;

import java.util.List;

import org.apache.http.client.HttpClient;

import com.example.lesson.R;
import com.lesson.bean.Comments;
import com.lesson.bean.CourseInf;
import com.lesson.util.LoadImage;

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


public class CoursesAdapter extends ArrayAdapter<CourseInf> implements OnScrollListener{
	
	private int resourceId;
	private Context context;
	private Bitmap bitmap;
	private HttpClient client;
	private LoadImage loadImage;
	private int mStart;
	private int mEnd;
	public static String[] URLS;
	private boolean mFirstIn;
	
	private final static int CHANGEHEADIMAGE = 0;
	
	public CoursesAdapter(Context context,int textViewResourceId,List<CourseInf> courses ,ListView listView) {
		// TODO Auto-generated constructor stub
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
			view = LayoutInflater.from(context).inflate(R.layout.course_item, null);
			viewHolder.courseImageView = (ImageView) view.findViewById(R.id.course_image);
			viewHolder.courseNameTextView = (TextView) view.findViewById(R.id.course_name);
			viewHolder.courseIntroductionTextView = (TextView) view.findViewById(R.id.course_introduction);
			viewHolder.courseNumTextView = (TextView) view.findViewById(R.id.course_num);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.courseIntroductionTextView.setText(course.getCourseintroduction());
		viewHolder.courseNameTextView.setText(course.getCoursename());
		viewHolder.courseNumTextView.setText("已经有"+course.getWatchernum()+"人报名");
		loadImage.showImageByAsyncTask(viewHolder.courseImageView, course.getAndroidimage());
		return view;
	}
	
	private class ViewHolder{
		ImageView courseImageView;
		TextView courseNameTextView;
		TextView courseIntroductionTextView;
		TextView courseNumTextView;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItem) {
		// TODO Auto-generated method stub
		mStart = firstVisibleItem;
		mEnd = firstVisibleItem + visibleItemCount;//可见的起始项
		//第一次显示调用
		if(mFirstIn && visibleItemCount > 0){
			loadImage.loadCoursesImages(mStart, mEnd);
			mFirstIn = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(scrollState == SCROLL_STATE_IDLE){
			//当前状态处于停止状态，加载可见项
			loadImage.loadCoursesImages(mStart, mEnd);
		}else{
			//停止任务
			loadImage.cancelAllTasks();
		}
	}
}
