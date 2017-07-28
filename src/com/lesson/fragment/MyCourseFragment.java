package com.lesson.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.lesson.adapter.MyCourseAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MyCourseFragment extends Fragment{
	
	private HttpClient client;
	private Context context;
	private List<CourseInf> myCourses;
	private ListView myCoursesListView;
	private MyCourseAdapter adapter;
	private LinearLayout noneLinearLayout;
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	
	public static Handler handler;

	public static final int ADDCOURSE = 0;
	public static final int DELETECOURSE = 1;
	
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
		View view = inflater.inflate(R.layout.my_course_layout, container,false);
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
		myCoursesListView = (ListView) view.findViewById(R.id.my_courses_listview);
		noneLinearLayout = (LinearLayout) view.findViewById(R.id.none);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == ADDCOURSE){
					CourseInf course = (CourseInf) msg.obj;
					if(myCourses == null){
						myCourses = new ArrayList<CourseInf>();
					}
					myCourses.add(course);
					adapter = new MyCourseAdapter(context, R.layout.my_course_item, myCourses, myCoursesListView);
					myCoursesListView.setAdapter(adapter);
					myCoursesListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context, SpecificCourseActivity.class);
							intent.putExtra("course", myCourses.get(position));
							startActivity(intent);
						}
					});
					adapter.notifyDataSetChanged();
					noneLinearLayout.setVisibility(View.GONE);
					myCoursesListView.setVisibility(View.VISIBLE);
				}else if(msg.what == DELETECOURSE){
					CourseInf course = (CourseInf) msg.obj;
					System.out.println(myCourses);
					Iterator<CourseInf> it = myCourses.iterator();
					while(it.hasNext()){
						CourseInf temp = it.next();
						if(temp.getId() == course.getId()){
							it.remove();
						}
					}
					if(myCourses.size() == 0){
						noneLinearLayout.setVisibility(View.VISIBLE);
						myCoursesListView.setVisibility(View.GONE);
					}
					myCoursesListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					System.out.println("delete:"+myCourses.size());
				}
			}
		};
		getMyCourses();
	}
	
	private void getMyCourses(){
		String url = NetConnectionDate.url
				+ "GetCourseInf?type=phone&getInf=getmycourse&username="
				+ GOLBALVALUE.user.getUsername();
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(urlString);
				try {
					HttpResponse response = client.execute(get);
					String result = EntityUtils.toString(response.getEntity());
					return result;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result.equals("null")){
					noneLinearLayout.setVisibility(View.VISIBLE);
					myCoursesListView.setVisibility(View.GONE);
				}else{
					noneLinearLayout.setVisibility(View.GONE);
					myCoursesListView.setVisibility(View.VISIBLE);
					JSONArray jsonarray = new JSONArray(result);
					myCourses = new ArrayList<>();
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
						myCourses.add(course);
					}
					adapter = new MyCourseAdapter(context, R.layout.my_course_item, myCourses, myCoursesListView);
					myCoursesListView.setAdapter(adapter);
					myCoursesListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context, SpecificCourseActivity.class);
							intent.putExtra("course", myCourses.get(position));
							startActivity(intent);
						}
					});
				}
			}
		}.execute(url);
	}
	
}
