package com.lesson.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lesson.R;
import com.lesson.activity.SpecificCourseActivity;
import com.lesson.adapter.CoursesAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.net.NetConnectionDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

@SuppressLint("NewApi")
public class AllCourseFragment extends Fragment{
	
	private Context context;
	private LinearLayout screenLinearLayout;
	private ListView courseListView;
	private List<CourseInf> courses;
	private TextView classesNameTextView,courseNumTextView;
	private View view ;
	private View view1 ;
	private CoursesAdapter adapter;
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	
	private HttpClient client;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.all_course_layout, container,false);
		view1 = inflater.inflate(R.layout.show_classes_pop_window,null,false);
		isConnectedLinearLayout = (LinearLayout) view.findViewById(R.id.isConnection);
		isNotConnectedLinearLayout = (LinearLayout) view.findViewById(R.id.isnotConnection);
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info != null && info.isConnected()){
			//联网状态
			isNotConnectedLinearLayout.setVisibility(View.GONE);
			isConnectedLinearLayout.setVisibility(View.VISIBLE);
			System.out.println("联网");
			init(view);
		}else{
			//断网状态
			isNotConnectedLinearLayout.setVisibility(View.VISIBLE);
			isConnectedLinearLayout.setVisibility(View.GONE);
			System.out.println("断网");
			isNotConnectedInit(view);
		}
		return view;
	}
	
	private void isNotConnectedInit(final View view){
		reConnectLinearLayout = (LinearLayout) view.findViewById(R.id.reconnect);
		reConnectLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = manager.getActiveNetworkInfo();
				if(info != null && info.isConnected()){
					//联网状态
					isNotConnectedLinearLayout.setVisibility(View.GONE);
					isConnectedLinearLayout.setVisibility(View.VISIBLE);
					init(view);
				}else{
					//断网状态
					isNotConnectedLinearLayout.setVisibility(View.VISIBLE);
					isConnectedLinearLayout.setVisibility(View.GONE);
					isNotConnectedInit(view);
				}
			}
		});
	}
	
	private void init(View view){
		screenLinearLayout = (LinearLayout) view.findViewById(R.id.screen);
		screenLinearLayout.setOnClickListener(screenOnClickListener);
		classesNameTextView = (TextView) view.findViewById(R.id.classes);
		courseNumTextView = (TextView) view.findViewById(R.id.num);
		courseListView = (ListView) view.findViewById(R.id.courses_listview);
		getCourses();
	}
	
	private void getCourses(){
		String url = NetConnectionDate.url+"GetCourseInf?type=phone&getInf=allcourses";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				HttpGet get = new HttpGet(urlString);
				client = new DefaultHttpClient();
				String json = null;
				HttpResponse response;
				try {
					response = client.execute(get);
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
				if(result != null){
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
				}
			}
			
		}.execute(url);
	}
	
	private OnClickListener screenOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final PopupWindow popup = new PopupWindow(view1,WindowManager.LayoutParams.MATCH_PARENT,900,true);
			popup.setFocusable(true);
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			popup.setBackgroundDrawable(dw);
//			popup.showAsDropDown(view1);
			popup.showAtLocation(view.findViewById(R.id.screen), Gravity.TOP, 0, 220);
			popup.setAnimationStyle(R.style.mypopwindow_anim_style);
			popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popup.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					
				}
			});
		}
	};
	
}
