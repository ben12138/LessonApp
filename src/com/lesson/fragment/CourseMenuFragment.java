package com.lesson.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.lesson.R;
import com.lesson.activity.SpecificCourseActivity;
import com.lesson.adapter.CourseMenuAdapter;
import com.lesson.bean.CourseUrl;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("NewApi")
public class CourseMenuFragment extends Fragment{
	
	private Context context;
	private List<CourseUrl> courseMenuList;
	private ListView courseMenuListView;
	private CourseMenuAdapter adapter;
	private LinearLayout noneLinearLayout;
	
	private HttpClient client;
	
	@SuppressLint("NewApi")
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
		View view = inflater.inflate(R.layout.course_menu_layout, container,false);
		init(view);
		return view;
	}
	
	private void init(View view){
		courseMenuListView = (ListView) view.findViewById(R.id.course_menu);
		noneLinearLayout = (LinearLayout) view.findViewById(R.id.none);
		getcourseurl();
	}
	
	private void getcourseurl(){
		String url = NetConnectionDate.url+"/GetCourseVideoServlet?courseinfid="+GOLBALVALUE.course.getId();
		System.out.println(url);
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String stringUrl = arg0[0];
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(stringUrl);
				try {
					HttpResponse response = client.execute(get);
					return EntityUtils.toString(response.getEntity());
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
					courseMenuListView.setVisibility(View.VISIBLE);
					JSONArray jsonarray = new JSONArray(result);
					courseMenuList = new ArrayList<>();
					for(int i=0;i<jsonarray.length();i++){
						JSONObject json = jsonarray.getJSONObject(i);
						CourseUrl course = new CourseUrl();
						course.setId(json.getInt("id"));
						course.setCourseinfid(json.getInt("courseinfid"));
						course.setCoursename(json.getString("coursename"));
						course.setCourseurl(json.getString("courseurl"));
						courseMenuList.add(course);
					}
					adapter = new CourseMenuAdapter(context, R.layout.course_menu_item, courseMenuList, courseMenuListView);
					courseMenuListView.setAdapter(adapter);
					courseMenuListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = SpecificCourseActivity.CHANGEVIDEOVIEW;
							msg.obj = courseMenuList.get(position).getCourseurl();
							SpecificCourseActivity.handler.sendMessage(msg);
						}
					});
				}else{
					noneLinearLayout.setVisibility(View.VISIBLE);
					courseMenuListView.setVisibility(View.GONE);
				}
			}
		}.execute(url);
	}
	
}
