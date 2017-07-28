package com.lesson.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import com.lesson.adapter.CommentsAdapter;
import com.lesson.bean.Comments;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;
import com.lesson.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentsActivity1 extends Activity{
	
	private Context context;
	
	private Button backButton;
	private LinearLayout sendcommentsLinearLayout;
	private ListView allcommentsListView;
	private List<Comments> comments;
	private CommentsAdapter adapter;
	private LinearLayout noneLinearLayout;
	private TextView noneTextView;
	private ImageView headImageImageView;
	public static Handler handler;
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	
	private HttpClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comments1_layout);
		isConnectedLinearLayout = (LinearLayout) findViewById(R.id.isConnection);
		isNotConnectedLinearLayout = (LinearLayout) findViewById(R.id.isnotConnection);
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
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		sendcommentsLinearLayout = (LinearLayout) findViewById(R.id.send_comment);
		sendcommentsLinearLayout.setOnClickListener(sendcommentsLinearLayoutOnClickListener);
		allcommentsListView = (ListView) findViewById(R.id.all_comments);
		noneLinearLayout = (LinearLayout) findViewById(R.id.none);
		noneLinearLayout.setVisibility(View.GONE);
		noneTextView = (TextView) findViewById(R.id.none_text);
		noneTextView.setVisibility(View.GONE);
		headImageImageView = (ImageView) findViewById(R.id.headimage);
		headImageImageView.setImageBitmap(Utils.getBitmap(GOLBALVALUE.user.getHeadImage()));
		System.out.println("aaaa");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == GOLBALVALUE.ADDCOMMENT){
					if(comments == null){
						comments = new ArrayList<Comments>();
					}
					noneLinearLayout.setVisibility(View.GONE);
					noneTextView.setVisibility(View.GONE);
					allcommentsListView.setVisibility(View.VISIBLE);
					Comments comment = (Comments) msg.obj;
					comments.add(0, comment);
					allcommentsListView.setSelection(0);
					adapter = new CommentsAdapter(context, R.layout.comment_item, comments,allcommentsListView);
					allcommentsListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
			}
		};
		getAllComments();
	}
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(CommentsActivity1.this);
			CommentsActivity1.this.finish();
		}
	};
	private OnClickListener sendcommentsLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,SendCommentActivity.class);
			startActivity(intent);
		}
	};
	private void getAllComments(){
		String url = NetConnectionDate.url+"GetAllCommentsServlet";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("courseinfid", "0"));
				
				String jsonarray = null;
				try {
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					HttpResponse response = client.execute(post);
					jsonarray = EntityUtils.toString(response.getEntity());
					return jsonarray;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "faliure";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "faliure";
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result.equals("failure")){
					Toast.makeText(context, "糟糕，网络出问题了", Toast.LENGTH_SHORT).show();
				}else{
					comments = new ArrayList<Comments>();
					JSONArray jsonarray = new JSONArray(result);
					for(int i=0;i<jsonarray.length();i++){
						JSONObject json = jsonarray.getJSONObject(i);
						Comments comment = new Comments();
						comment.setId(json.getInt("id"));
						comment.setContent(json.getString("content"));
						comment.setPraisenum(json.getInt("praisenum"));
						comment.setSender(json.getString("sender"));
						comment.setSenderheadImage(json.getString("senderheadimage"));
						comment.setSenderNickName(json.getString("sendernickname"));
						comment.setSendtime(json.getString("sendertime"));
						comments.add(comment);
						System.out.println(comment.getPraisenum());
					}
				}
				if(comments.size()>0){
					noneLinearLayout.setVisibility(View.GONE);
					noneTextView.setVisibility(View.GONE);
					allcommentsListView.setVisibility(View.VISIBLE);
					adapter = new CommentsAdapter(context, R.layout.comment_item, comments,allcommentsListView);
					allcommentsListView.setAdapter(adapter);
					allcommentsListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context,CommentsActivity2.class);
							intent.putExtra("comment", comments.get(position));
							context.startActivity(intent);
						}
						
					});
				}else{
					noneLinearLayout.setVisibility(View.VISIBLE);
					noneTextView.setVisibility(View.VISIBLE);
					allcommentsListView.setVisibility(View.GONE);
					noneLinearLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context,SendCommentActivity.class);
							startActivity(intent);
						}
					});
				}
			}
			
		}.execute(url);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityCollector.removeActivity(CommentsActivity1.this);
		CommentsActivity1.this.finish();
	}
}
