package com.lesson.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.example.lesson.R;
import com.lesson.adapter.CoursesAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("unused")
public class SearchCourseActivity extends Activity{
	
	private Context context;
	private List<CourseInf> courses;
	private ListView coursesListView;
	private EditText searchEditText;
	private TextView cancelTextView;
	private CoursesAdapter adapter;
	private LinearLayout noneLinearLayout;
	
	private HttpClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_layout);
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
		searchEditText = (EditText) findViewById(R.id.search_edittext);
		searchEditText.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
					if(searchEditText.getText().toString().trim().equals("")){
						Toast.makeText(context, "关键字不能为空", Toast.LENGTH_SHORT).show();
					}else{
						search();
					}
				}
				return false;
			}
		});
		cancelTextView = (TextView) findViewById(R.id.cancel);
		cancelTextView.setOnClickListener(cancelTextViewOnClickListener);
		coursesListView = (ListView) findViewById(R.id.courses);
		noneLinearLayout = (LinearLayout) findViewById(R.id.none);
		noneLinearLayout.setVisibility(View.GONE);
		coursesListView.setVisibility(View.GONE);
	}
	
	private OnClickListener cancelTextViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(SearchCourseActivity.this);
			SearchCourseActivity.this.finish();
		}
	};
	
	public void onBackPressed() {
		ActivityCollector.removeActivity(SearchCourseActivity.this);
		SearchCourseActivity.this.finish();
	};
	
	private void search(){
		String url = NetConnectionDate.url+"/GetCourseInf";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlSring = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlSring);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("type", "phone"));
				list.add(new BasicNameValuePair("getInf", "searchcourses"));
				list.add(new BasicNameValuePair("name", searchEditText.getText().toString().trim()));
				try {
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					HttpResponse response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					System.out.println(result);
					return result;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "null";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "null";
				}
				
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(!result.equals("null")){
					noneLinearLayout.setVisibility(View.GONE);
					coursesListView.setVisibility(View.VISIBLE);
					JSONArray jsonarray = new JSONArray(result);
					courses = new ArrayList<>();
					for(int i=0;i<jsonarray.length();i++){
						JSONObject json = jsonarray.getJSONObject(i);
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
					adapter = new CoursesAdapter(context, R.layout.course_item, courses, coursesListView);
					coursesListView.setAdapter(adapter);
					coursesListView.setOnItemClickListener(new OnItemClickListener() {
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
					noneLinearLayout.setVisibility(View.VISIBLE);
					coursesListView.setVisibility(View.GONE);
				}
			}
			
		}.execute(url);
	}
	
}
