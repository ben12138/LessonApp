package com.lesson.activity;

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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.example.lesson.R;
import com.lesson.adapter.Test1Adapter;
import com.lesson.adapter.TestAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.bean.Test;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class CourseTestActivity extends Activity{
	
	private Context context;
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	private LinearLayout hasQuestionLinearLayout;
	private LinearLayout noQuestionLinearLayout;
	
	private HttpClient client;
	
	private ListView testListView;
	private Button submitButton;
	private List<Test> tests;
	private Button backButton;
	
	private TestAdapter adapter;
	private Test1Adapter adapter1;
	public static List<String> answers;
	public static List<String> correctAnswers;
	private int correctnum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.course_test_layout);
		isConnectedLinearLayout = (LinearLayout) findViewById(R.id.isConnection);
		isNotConnectedLinearLayout = (LinearLayout) findViewById(R.id.isnotConnection);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info != null && info.isConnected()){
			//联网状态
			isNotConnectedLinearLayout.setVisibility(View.GONE);
			isConnectedLinearLayout.setVisibility(View.VISIBLE);
			System.out.println("联网");
			init();
		}else{
			//断网状态
			isNotConnectedLinearLayout.setVisibility(View.VISIBLE);
			isConnectedLinearLayout.setVisibility(View.GONE);
			System.out.println("断网");
			isNotConnectedInit();
		}
	}
	
	private void isNotConnectedInit(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		reConnectLinearLayout = (LinearLayout) findViewById(R.id.reconnect);
		reConnectLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = manager.getActiveNetworkInfo();
				if(info != null && info.isConnected()){
					//联网状态
					isNotConnectedLinearLayout.setVisibility(View.GONE);
					isConnectedLinearLayout.setVisibility(View.VISIBLE);
					init();
				}else{
					//断网状态
					isNotConnectedLinearLayout.setVisibility(View.VISIBLE);
					isConnectedLinearLayout.setVisibility(View.GONE);
					isNotConnectedInit();
				}
			}
		});
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
		hasQuestionLinearLayout = (LinearLayout) findViewById(R.id.hasquestion);
		noQuestionLinearLayout = (LinearLayout) findViewById(R.id.noquestion);
		testListView = (ListView) findViewById(R.id.test_content);
		getTests();
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			exit();
		}
	};
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		exit();
	};
	
	private void getTests(){
		String url = NetConnectionDate.url+"/TestServlet?courseid="+GOLBALVALUE.course.getId();
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
				if(result.equals("null")){
					noQuestionLinearLayout.setVisibility(View.VISIBLE);
					hasQuestionLinearLayout.setVisibility(View.GONE);
				}else{
					noQuestionLinearLayout.setVisibility(View.GONE);
					hasQuestionLinearLayout.setVisibility(View.VISIBLE);
					tests = new ArrayList<Test>();
					answers = new ArrayList<String>();
					correctAnswers = new ArrayList<String>();
					JSONArray jsonarray = new JSONArray(result);
					for(int i=0;i<jsonarray.length();i++){
						JSONObject json = jsonarray.getJSONObject(i);
						Test test = new Test();
						test.setCourseid(json.getInt("courseid"));
						test.setQuestion(json.getString("question"));
						test.setChoiceA(json.getString("choiceA"));
						test.setChoiceB(json.getString("choiceB"));
						test.setChoiceC(json.getString("choiceC"));
						test.setChoiceD(json.getString("choiceD"));
						test.setAnswer(json.getString("answer"));
						correctAnswers.add(json.getString("answer"));
						tests.add(test);
						answers.add("");
					}
					adapter = new TestAdapter(context, R.layout.test_item, tests);
					testListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					submitButton = (Button) findViewById(R.id.submit);
					submitButton.setOnClickListener(submitButtonOnClickListener);
				}
			}
			
		}.execute(url);
	}
	
	private OnClickListener submitButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("提交");
			builder.setMessage("是否确定要提交?");
			builder.setCancelable(false);
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showResult();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			builder.show();
		}
	};
	
	private void showResult(){
		for(int i=0;i<answers.size();i++){
			if(answers.get(i).equals(correctAnswers.get(i))){
				correctnum++;
			}
			System.out.println(answers.get(i));
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("得分");
		builder.setMessage("您的分数是："+correctnum*100/correctAnswers.size()+"分");
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				adapter1 = new Test1Adapter(context, R.layout.test1_item, tests);
				testListView.setAdapter(adapter1);
				adapter1.notifyDataSetChanged();
				submitButton.setVisibility(View.GONE);
				backButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ActivityCollector.removeActivity(CourseTestActivity.this);
						CourseTestActivity.this.finish();
					}
				});
			}
		});
		builder.show();
	}
	
	private void exit(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("退出");
		builder.setMessage("你确定要退出?退出后数据将不被保存。");
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ActivityCollector.removeActivity(CourseTestActivity.this);
				CourseTestActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.show();
	}
	
}
