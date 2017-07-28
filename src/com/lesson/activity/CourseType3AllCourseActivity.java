package com.lesson.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.example.lesson.R;
import com.lesson.adapter.CoursesAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class CourseType3AllCourseActivity extends Activity{
	
	private Context context;
	private LinearLayout screenLinearLayout;
	private ListView courseListView;
	private List<CourseInf> courses;
	private TextView classesNameTextView,courseNumTextView;
	private View view ;
	private View view1 ;
	private CoursesAdapter adapter;
	private String coursetype1;
	private String coursetype2;
	private String coursetype3;
	private Button backButton;
	private ImageView searchImageView;
	
	public static final int CHANGECOURSE = 0;

	private HttpClient client;
	
	public static Handler handler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coursetype3_allcourse_layout);
		GOLBALVALUE.isStartActivity = true;
		init();
	}
	
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		view = getWindow().getDecorView();
		context = this;
		ActivityCollector.addActivity(CourseType3AllCourseActivity.this);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view1 = inflater.inflate(R.layout.show_classes_pop_window,null,false);
		handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(msg.what == CHANGECOURSE){
					Bundle data = msg.getData();
					coursetype1 = data.getString("coursetype1");
					coursetype2 = data.getString("coursetype2");
					coursetype3 = data.getString("coursetype3");
					getCourses();
				}
			};
		};
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		searchImageView = (ImageView) findViewById(R.id.search);
		searchImageView.setOnClickListener(searchImageViewOnClickListener);
		screenLinearLayout = (LinearLayout) findViewById(R.id.screen);
		screenLinearLayout.setOnClickListener(screenOnClickListener);
		classesNameTextView = (TextView) findViewById(R.id.classes);
		courseNumTextView = (TextView) findViewById(R.id.num);
		courseListView = (ListView) findViewById(R.id.courses_listview);
		Intent intent = getIntent();
		coursetype1 = intent.getStringExtra("coursetype1");
		coursetype2 = intent.getStringExtra("coursetype2");
		coursetype3 = intent.getStringExtra("coursetype3");
		getCourses();
	}
	
	private void getCourses(){
		String url = NetConnectionDate.url+"GetCourseInf";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("type", "phone"));
				list.add(new BasicNameValuePair("getInf", "getcourses"));
				list.add(new BasicNameValuePair("coursetype1", coursetype1));
				list.add(new BasicNameValuePair("coursetype2", coursetype2));
				list.add(new BasicNameValuePair("coursetype3", coursetype3));
				client = new DefaultHttpClient();
				String json = null;
				HttpResponse response;
				try {
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					response = client.execute(post);
					json = EntityUtils.toString(response.getEntity());
					return json;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				if(result != null && !result.equals("null")){
					courses = new ArrayList<>();
					JSONArray jsonarray = new JSONArray(result);
					for(int i=0;i<jsonarray.length();i++){
						net.sf.json.JSONObject json = (net.sf.json.JSONObject) jsonarray.get(i);
						CourseInf course = new CourseInf();
						course.setId(json.getInt("id"));
						course.setCourseid(json.getInt("courseid"));
						course.setTeacherid(json.getInt("teacherid"));
						course.setCoursename(json.getString("coursename"));
						course.setCourseintroduction(json.getString("courseintroduction"));
						course.setCoursedegree(json.getDouble("coursedegree"));
						course.setCoursecomments(json.getString("coursecomments"));
						course.setCatalogue(json.getString("catalogue"));
						course.setAndroidimage(json.getString("androidimage"));
						course.setWatchernum(json.getInt("watchernum"));
						courses.add(course);
					}
					classesNameTextView.setText(coursetype3);
					adapter = new CoursesAdapter(context, R.layout.course_item, courses, courseListView);
					courseListView.setAdapter(adapter);
					courseNumTextView.setText(jsonarray.length()+"");
					courseListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int positon, long id) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context,SpecificCourseActivity.class);
							intent.putExtra("course", courses.get(positon));
							startActivity(intent);
						}
					});
				}else{
					courses = new ArrayList<CourseInf>();
					adapter = new CoursesAdapter(context, R.layout.course_item, courses, courseListView);
					courseListView.setAdapter(adapter);
					classesNameTextView.setText(coursetype3);
					courseNumTextView.setText("0");
				}
			}
			
		}.execute(url);
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(CourseType3AllCourseActivity.this);
			CourseType3AllCourseActivity.this.finish();
			GOLBALVALUE.isStartActivity = false;
		}
	};
	
	public void onBackPressed() {
		ActivityCollector.removeActivity(CourseType3AllCourseActivity.this);
		CourseType3AllCourseActivity.this.finish();
		GOLBALVALUE.isStartActivity = false;
	};
	
	private OnClickListener searchImageViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,SearchCourseActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener screenOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			final PopupWindow popup = new PopupWindow(view1,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
			popup.setFocusable(true);
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			popup.setBackgroundDrawable(dw);
			popup.showAtLocation(view.findViewById(R.id.screen), Gravity.CENTER, 0, 0);
			popup.setAnimationStyle(R.style.mypopwindow_anim_style);
			popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popup.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					
				}
			});
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		GOLBALVALUE.isStartActivity = false;
	};
	
}
