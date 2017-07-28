package com.lesson.fragment;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.example.lesson.R;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.bean.Teacher;
import com.lesson.net.NetConnectionDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CourseInfFragment extends Fragment{
	
	private Context context;
	
	private TextView coursenameTextView;
	private TextView watchernumTextView;
	private TextView degreeTextView;
	private ImageView teacherHeadImageView;
	private TextView teachernameTextView;
	private TextView teacherIntroductionTextView;
	private TextView courseIntroductionTextView;
	
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
		View view = inflater.inflate(R.layout.course_inf_layout, container,false);
		init(view);
		return view;
	}
	
	private void init(View view){
		coursenameTextView = (TextView) view.findViewById(R.id.course_name);
		watchernumTextView = (TextView) view.findViewById(R.id.watcher_num);
		degreeTextView = (TextView) view.findViewById(R.id.degree);
		teacherHeadImageView = (ImageView) view.findViewById(R.id.teacher_headimage);
		teachernameTextView = (TextView) view.findViewById(R.id.teacher_name);
		teacherIntroductionTextView = (TextView) view.findViewById(R.id.teacher_introduction);
		courseIntroductionTextView = (TextView) view.findViewById(R.id.course_introduction);
		coursenameTextView.setText(GOLBALVALUE.course.getCoursename());
		watchernumTextView.setText("观看人数   "+GOLBALVALUE.course.getWatchernum());
		degreeTextView.setText("课程评分  "+GOLBALVALUE.course.getCoursedegree());
		courseIntroductionTextView.setText(GOLBALVALUE.course.getCourseintroduction());
		getTeacherInf();
	}
	
	private void getTeacherInf(){
		String url = NetConnectionDate.url+"/GetTeacherInfServlet?id="+GOLBALVALUE.course.getTeacherid();
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(arg0[0]);
				try {
					HttpResponse response = client.execute(get);
					return EntityUtils.toString(response.getEntity());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				if(result!=null){
					Teacher teacher = new Teacher();
					JSONObject json = new JSONObject(result);
					teacher.setTeacherimage(json.getString("teacherimage"));
					teacher.setTeacherintroduction(json.getString("teacherintroduction"));
					teacher.setTeachername(json.getString("teachername"));
					teacherIntroductionTextView.setText(teacher.getTeacherintroduction());
					teachernameTextView.setText(teacher.getTeachername());
					loadImageVolly(teacherHeadImageView, teacher.getTeacherimage());
				}
			}
		}.execute(url);
	}
	
	public void loadImageVolly(ImageView imageView ,String url){
		RequestQueue requsetQueue = Volley.newRequestQueue(context);
		String imageUrl = url;
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		//缓存操作
		final LruCache<String, Bitmap> lurcache = new LruCache<String, Bitmap>(20);
		ImageCache imageCache = new ImageCache() {
			
			@Override
			public void putBitmap(String key, Bitmap value) {
				// TODO Auto-generated method stub
				lurcache.put(key, value);
			}
			
			@Override
			public Bitmap getBitmap(String key) {
				// TODO Auto-generated method stub
				return lurcache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
		imageLoader.get(imageUrl, listener);
	}
	
}
