package com.lesson.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.lesson.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lesson.activity.AllClassesActivity;
import com.lesson.activity.CourseType3AllCourseActivity;
import com.lesson.activity.SpecificCourseActivity;
import com.lesson.adapter.ViewPagerAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.net.NetConnectionDate;
import com.nostra13.universalimageloader.*;
/**
 * 推荐界面
 * @author 19339
 *
 */
@SuppressLint("NewApi")
public class TuiJianFragment extends Fragment{
	
	private static final int ADDTUISONGPICTURE = 1;
	private static final int CHANGETUISONGINF = 2;
	private String images[]={
			"http://192.168.191.1:8080/LessonServer/tuisongImage/1.jpg",
			"http://192.168.191.1:8080/LessonServer/tuisongImage/2.png",
			"http://192.168.191.1:8080/LessonServer/tuisongImage/3.jpg",
			"http://192.168.191.1:8080/LessonServer/tuisongImage/4.jpg",
			"http://192.168.191.1:8080/LessonServer/tuisongImage/5.jpg"};
	private String titles[] = {
			"第一张",
			"第二张",
			"第三张",
			"第四张",
			"第五张"};
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	
	private List<ListView> views; 
	private List<ImageView> imageViews;
	private List<View> dots;
	private int currentItem;
	//记录上一次点的位置
	private int oldPosition = 0;
	private ViewPager mViewPaper;
	private TextView title;
	private ViewPagerAdapter adapter;
	private ScheduledExecutorService scheduledExecutorService;
	private HashMap<String, ArrayList<CourseInf>> map = null;
	private HttpClient client;
	public static Handler handler;
	private Context context;
	private TextView class1TextView;
	private ImageView class1image1ImageView;
	private TextView class1text1TextView;
	private TextView class1people1TextView;
	private ImageView class1image2ImageView;
	private TextView class1text2TextView;
	private TextView class1people2TextView;
	private ImageView class1image3ImageView;
	private TextView class1text3TextView;
	private TextView class1people3TextView;
	private ImageView class1image4ImageView;
	private TextView class1text4TextView;
	private TextView class1people4TextView;
	
	private TextView class2TextView;
	private ImageView class2image1ImageView;
	private TextView class2text1TextView;
	private TextView class2people1TextView;
	private ImageView class2image2ImageView;
	private TextView class2text2TextView;
	private TextView class2people2TextView;
	private ImageView class2image3ImageView;
	private TextView class2text3TextView;
	private TextView class2people3TextView;
	private ImageView class2image4ImageView;
	private TextView class2text4TextView;
	private TextView class2people4TextView;
	
	private LinearLayout itLayout;
	private LinearLayout designLayout;
	private LinearLayout languageLayout;
	private LinearLayout professionLayout;
	private LinearLayout graduateLayout;
	private LinearLayout interestLayout;
	private LinearLayout otherLayout;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuijian_layout, container, false);
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
		getTuiSongInf();
		map = new HashMap<String, ArrayList<CourseInf>>();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == ADDTUISONGPICTURE){
					Bitmap bitmap = (Bitmap) msg.obj;
					Drawable drawable =new BitmapDrawable(bitmap);
					imageViews.get(msg.arg1).setBackground(drawable);
				}else if(msg.what == CHANGETUISONGINF){
					final CourseInf course = (CourseInf) msg.obj;
					Bundle bundle = msg.getData();
					Bitmap bitmap = bundle.getParcelable("image");
					if(msg.arg1 == 1){
						if(msg.arg2 == 1){
							Drawable background = new BitmapDrawable(bitmap);
							class1image1ImageView.setBackground(background);
							class1text1TextView.setText(course.getCoursename());
							class1people1TextView.setText(course.getWatchernum()+"人观看");
							class1image1ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}else if(msg.arg2 == 2){
							Drawable background = new BitmapDrawable(bitmap);
							class1image2ImageView.setBackground(background);
							class1text2TextView.setText(course.getCoursename());
							class1people2TextView.setText(course.getWatchernum()+"人观看");
							class1image2ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}else if(msg.arg2 == 3){
							Drawable background = new BitmapDrawable(bitmap);
							class1image3ImageView.setBackground(background);
							class1text3TextView.setText(course.getCoursename());
							class1people3TextView.setText(course.getWatchernum()+"人观看");
							class1image3ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}else if(msg.arg2 == 4){
							Drawable background = new BitmapDrawable(bitmap);
							class1image4ImageView.setBackground(background);
							class1text4TextView.setText(course.getCoursename());
							class1people4TextView.setText(course.getWatchernum()+"人观看");
							class1image4ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}
					}else if(msg.arg1 == 2){
						if(msg.arg2 == 1){
							Drawable background = new BitmapDrawable(bitmap);
							class2image1ImageView.setBackground(background);
							class2text1TextView.setText(course.getCoursename());
							class2people1TextView.setText(course.getWatchernum()+"人观看");
							class2image1ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}else if(msg.arg2 == 2){
							Drawable background = new BitmapDrawable(bitmap);
							class2image2ImageView.setBackground(background);
							class2text2TextView.setText(course.getCoursename());
							class2people2TextView.setText(course.getWatchernum()+"人观看");
							class2image2ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}else if(msg.arg2 == 3){
							Drawable background = new BitmapDrawable(bitmap);
							class2image3ImageView.setBackground(background);
							class2text3TextView.setText(course.getCoursename());
							class2people3TextView.setText(course.getWatchernum()+"人观看");
							class2image3ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}else if(msg.arg2 == 4){
							Drawable background = new BitmapDrawable(bitmap);
							class2image4ImageView.setBackground(background);
							class2text4TextView.setText(course.getCoursename());
							class2people4TextView.setText(course.getWatchernum()+"人观看");
							class2image4ImageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(context,SpecificCourseActivity.class);
									intent.putExtra("course", course);
									context.startActivity(intent);
								}
							});
						}
					}
				}
			}
		};
		
		itLayout = (LinearLayout) view.findViewById(R.id.it);
		designLayout = (LinearLayout) view.findViewById(R.id.design);
		languageLayout = (LinearLayout) view.findViewById(R.id.language);
		professionLayout = (LinearLayout) view.findViewById(R.id.profession);
		graduateLayout = (LinearLayout) view.findViewById(R.id.graduate);
		interestLayout = (LinearLayout) view.findViewById(R.id.interest);
		otherLayout = (LinearLayout) view.findViewById(R.id.other);
		
		if (isAdded()) {
			itLayout.setOnClickListener(itLayoutOnClickListener);
			designLayout.setOnClickListener(designLayoutClickListener);
			languageLayout
					.setOnClickListener(languageLinearLayoutOnClickListener);
			professionLayout
					.setOnClickListener(professionLinearLayoutOnClickListener);
			graduateLayout.setOnClickListener(graduateLayoutOnClickListener);
			interestLayout.setOnClickListener(interestLayoutOnClickListener);
			otherLayout.setOnClickListener(otherLayoutOnClickListener);
		}
		
		mViewPaper = (ViewPager) view.findViewById(R.id.vp);
		imageViews = new ArrayList<ImageView>();
		for(int i = 0; i < images.length; i++){
			ImageView imageView = new ImageView(view.getContext());
			imageView.setBackgroundResource(R.drawable.pictures_no);
			imageViews.add(imageView);
		}
		//显示的小点
		dots = new ArrayList<View>();
		dots.add(view.findViewById(R.id.dot_0));
		dots.add(view.findViewById(R.id.dot_1));
		dots.add(view.findViewById(R.id.dot_2));
		dots.add(view.findViewById(R.id.dot_3));
		dots.add(view.findViewById(R.id.dot_4));

		title = (TextView) view.findViewById(R.id.title);
		title.setText(titles[0]);
		adapter = new ViewPagerAdapter(imageViews);
		mViewPaper.setAdapter(adapter);
		
		mViewPaper.setOnPageChangeListener(mViewPaperOnPagerChangeListener);
		for(int i=0;i<images.length;i++){
			loadImage(images[i],i);
		}
		
		class1TextView = (TextView) view.findViewById(R.id.class1);
		class1image1ImageView = (ImageView) view.findViewById(R.id.class1_image1);
		class1text1TextView = (TextView) view.findViewById(R.id.class1_text1);
		class1people1TextView = (TextView) view.findViewById(R.id.class1_people1);
		class1image2ImageView = (ImageView) view.findViewById(R.id.class1_image2);
		class1text2TextView = (TextView) view.findViewById(R.id.class1_text2);
		class1people2TextView = (TextView) view.findViewById(R.id.class1_people2);
		class1image3ImageView = (ImageView) view.findViewById(R.id.class1_image3);
		class1text3TextView = (TextView) view.findViewById(R.id.class1_text3);
		class1people3TextView = (TextView) view.findViewById(R.id.class1_people3);
		class1image4ImageView = (ImageView) view.findViewById(R.id.class1_image4);
		class1text4TextView = (TextView) view.findViewById(R.id.class1_text4);
		class1people4TextView = (TextView) view.findViewById(R.id.class1_people4);
		
		class2TextView = (TextView) view.findViewById(R.id.class2);
		class2image1ImageView = (ImageView) view.findViewById(R.id.class2_image1);
		class2text1TextView = (TextView) view.findViewById(R.id.class2_text1);
		class2people1TextView = (TextView) view.findViewById(R.id.class2_people1);
		class2image2ImageView = (ImageView) view.findViewById(R.id.class2_image2);
		class2text2TextView = (TextView) view.findViewById(R.id.class2_text2);
		class2people2TextView = (TextView) view.findViewById(R.id.class2_people2);
		class2image3ImageView = (ImageView) view.findViewById(R.id.class2_image3);
		class2text3TextView = (TextView) view.findViewById(R.id.class2_text3);
		class2people3TextView = (TextView) view.findViewById(R.id.class2_people3);
		class2image4ImageView = (ImageView) view.findViewById(R.id.class2_image4);
		class2text4TextView = (TextView) view.findViewById(R.id.class2_text4);
		class2people4TextView = (TextView) view.findViewById(R.id.class2_people4);
	}
	
	private ViewPager.OnPageChangeListener mViewPaperOnPagerChangeListener = new ViewPager.OnPageChangeListener() {
		

		@Override
		public void onPageSelected(int position) {
			title.setText(titles[position]);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			
			oldPosition = position;
			currentItem = position;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	private OnClickListener itLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 2);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener designLayoutClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 3);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener languageLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 1);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener professionLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 4);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener graduateLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 5);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener interestLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 6);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener otherLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AllClassesActivity.class);
			intent.putExtra("selected", 0);
			context.startActivity(intent);
		}
	};
	
	public void loadImage(final String imageUrl,final int i){
		new Thread(){
			public void run() {
				URL url;
				try {
					url = new URL(imageUrl);
					InputStream is = url.openStream();
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					Message msg = new Message();
					msg.obj = bitmap;
					msg.what = ADDTUISONGPICTURE;
					msg.arg1 = i;
					handler.sendMessage(msg);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	/**
	 * 利用线程池定时执行动画轮播
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(
				new ViewPageTask(), 
				2, 
				4, 
				TimeUnit.SECONDS);
	}
	
	
	private class ViewPageTask implements Runnable{

		@Override
		public void run() {
			currentItem = (currentItem + 1) % images.length;
			mHandler.sendEmptyMessage(0);
		}
	}
	
	/**
	 * 接收子线程传递过来的数据
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			mViewPaper.setCurrentItem(currentItem);
		};
	};
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private void getTuiSongInf(){
		String url = NetConnectionDate.url+"GetCourseInf";
		new AsyncTask<String, Void, String>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("type", "phone"));
				list.add(new BasicNameValuePair("getInf", "tuisong"));
				try {
					post.setEntity(new UrlEncodedFormEntity(list));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HttpResponse response;
				try {
					response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					return result;
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
				JSONArray jsonarray = new JSONArray(result);
				String coursename = "";
				for(int i=0;i<jsonarray.length();i++){
					JSONObject json = jsonarray.getJSONObject(i);
					if(json.has("coursetype1")){
						map.put(json.getString("coursetype1"), new ArrayList<CourseInf>());
						coursename = json.getString("coursetype1");
					}else{
						CourseInf course = new CourseInf();
						course.setId(Integer.parseInt(json.getString("id")));
						course.setCourseid(Integer.parseInt(json.getString("courseid")));
						course.setTeacherid(Integer.parseInt(json.getString("teacherid")));
						course.setCoursename(json.getString("coursename"));
						course.setCourseintroduction(json.getString("courseintroduction"));
						course.setCoursedegree(Double.parseDouble(json.getString("coursedegree")));
						course.setCoursecomments(json.getString("coursecomments"));
						course.setCatalogue(json.getString("catalogue"));
						course.setAndroidimage(json.getString("androidimage"));
						course.setWatchernum(Integer.parseInt(json.getString("watchernum")));
						map.get(coursename).add(course);
					}
				}
				int j = 0;
				for(String key : map.keySet()){
					j++;
					if(j==1){
						class1TextView.setText(key);
					}else if(j == 2){
						class2TextView.setText(key);
					}
					ArrayList<CourseInf> course = map.get(key);
					for(int i=0;i<course.size();i++){
						getImage(j,i+1,course.get(i));
					}
				}
			}
			
		}.execute(url,"phone","tuisong");
	}
	
	public void getImage(final int i,final int j,final CourseInf course){
		new Thread() {
			public void run() {
				URL imgUrl = null;
				Bitmap bitmap = null;
				try {
					imgUrl = new URL(course.getAndroidimage());
					HttpURLConnection conn = (HttpURLConnection) imgUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(is);
					Message msg = new Message();
					msg.what = CHANGETUISONGINF;
					msg.obj = course;
					msg.arg1 = i;
					msg.arg2 = j;
					Bundle bundle = new Bundle();
					bundle.putParcelable("image", bitmap);
					msg.setData(bundle);
					handler.sendMessage(msg);
					is.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
