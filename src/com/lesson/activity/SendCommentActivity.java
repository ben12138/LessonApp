package com.lesson.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.lesson.adapter.CommentsAdapter;
import com.lesson.bean.Comments;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.fragment.CourseCommentsFragment;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;
import com.mysql.fabric.xmlrpc.Client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendCommentActivity extends Activity{
	
	private Context context;
	private Button backButton;
	private EditText contentEditText;
	private TextView sendTextView;
	private Button sendButton;
	
	private HttpClient client;
	private int courseinfid = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.send_comment_layout);
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
		ActivityCollector.addActivity(SendCommentActivity.this);
		Intent intent = getIntent();
		if(intent.getIntExtra("courseinfid", 0) != 0){
			courseinfid = intent.getIntExtra("courseinfid", 0);
		}
		System.out.println(courseinfid);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		sendTextView = (TextView) findViewById(R.id.send_up);
		sendTextView.setOnClickListener(sendOnClickListener);
		sendButton = (Button) findViewById(R.id.send_down);
		sendButton.setOnClickListener(sendOnClickListener);
		contentEditText = (EditText) findViewById(R.id.comment_content);
	}
	
	private OnClickListener sendOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(contentEditText.getText().toString().trim()!=null && 
					!contentEditText.getText().toString().trim().equals("")){
				send();
			}else{
				Toast.makeText(context, "输入信息不能为空", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(SendCommentActivity.this);
			SendCommentActivity.this.finish();
		}
	};
	
	private void send(){
		String url = NetConnectionDate.url+"AddAllCommentsServlet";
		new AsyncTask<String, Void, Integer>() {
			@Override
			protected Integer doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("sendernickname", GOLBALVALUE.user.getNickname()));
				list.add(new BasicNameValuePair("senderheadimage", GOLBALVALUE.headimageUrl));
				list.add(new BasicNameValuePair("content", contentEditText.getText().toString().trim()));
				list.add(new BasicNameValuePair("sender", GOLBALVALUE.user.getUsername()));
				list.add(new BasicNameValuePair("courseinfid", courseinfid+""));
				try {
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					HttpResponse response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					if(result.trim().equals("failure")){
						return 0;
					}else {
						return Integer.parseInt(result);
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result == 0){
					Toast.makeText(context, "糟糕，出错了", Toast.LENGTH_SHORT).show();
				}else{
					Comments comment = new Comments();
					comment.setId(result);
					comment.setContent(contentEditText.getText().toString().trim());
					comment.setSender(GOLBALVALUE.user.getUsername());
					comment.setPraisenum(0);
					comment.setSendtime("刚刚");
					comment.setSenderheadImage(GOLBALVALUE.headimageUrl);
					comment.setSenderNickName(GOLBALVALUE.user.getNickname());
					if(courseinfid == 0){
						Message msg = new Message();
						msg.obj = comment;
						msg.what = GOLBALVALUE.ADDCOMMENT;
						CommentsActivity1.handler.sendMessage(msg);
						ActivityCollector.removeActivity(SendCommentActivity.this);
						SendCommentActivity.this.finish();
					}else{
						Message msg = new Message();
						msg.obj = comment;
						CourseCommentsFragment.handler.sendMessage(msg);
						ActivityCollector.removeActivity(SendCommentActivity.this);
						SendCommentActivity.this.finish();
					}
				}
			}
		}.execute(url);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityCollector.removeActivity(SendCommentActivity.this);
		SendCommentActivity.this.finish();
	}
}
