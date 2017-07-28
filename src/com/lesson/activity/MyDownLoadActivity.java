package com.lesson.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.lesson.R;
import com.lesson.adapter.MyDownloadsAdapter;
import com.lesson.bean.CourseUrl;
import com.lesson.bean.UserInf;
import com.lesson.databasehelper.MyDataBasesHelper;
import com.lesson.databasehelper.MyDownLoadsDataBasesHelper;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

public class MyDownLoadActivity extends Activity{

	private Context context;
	
	private Button backButton;
	private ListView downloadsListView;
	private List<CourseUrl> courses;
	private MyDownloadsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_download_layout);
		init();
	}
	
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		context = this;
		ActivityCollector.addActivity(this);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		downloadsListView = (ListView) findViewById(R.id.mydownloads);
		courses = new ArrayList<CourseUrl>();
		getCourses();
		adapter = new MyDownloadsAdapter(context, R.layout.my_download_item, courses, downloadsListView);
		downloadsListView.setAdapter(adapter);
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(MyDownLoadActivity.this);
			MyDownLoadActivity.this.finish();
		}
	};
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ActivityCollector.removeActivity(MyDownLoadActivity.this);
		MyDownLoadActivity.this.finish();
	};
	
	private void getCourses(){
		MyDownLoadsDataBasesHelper dbHelper = new MyDownLoadsDataBasesHelper(context, "downloads.db3", 1);
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
				"select * from downloads", new String[]{});
		while (cursor.moveToNext()) {
			if(cursor.getString(2) != null && cursor.getString(1) != null){
				CourseUrl course = new CourseUrl();
				course.setCoursename(cursor.getString(1));
				course.setCourseurl(cursor.getString(2));
				courses.add(course);
				System.out.println(course);
			}
		}
		dbHelper.close();
	}
	
}
