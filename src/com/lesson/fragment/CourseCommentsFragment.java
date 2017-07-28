package com.lesson.fragment;

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
import com.lesson.adapter.CommentsAdapter;
import com.lesson.bean.Comments;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class CourseCommentsFragment extends Fragment{
	
	private Context context;
	private ListView commentsListView;
	private List<Comments> comments;
	private CommentsAdapter adapter;
	private LinearLayout noneLinearLayout;
	
	private HttpClient client;
	public static Handler handler;
	
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
		View view = inflater.inflate(R.layout.course_comments_layout, container,false);
		init(view);
		return view;
	}
	
	private void init(View view){
		commentsListView = (ListView) view.findViewById(R.id.comments);
		noneLinearLayout = (LinearLayout) view.findViewById(R.id.none);
		noneLinearLayout.setVisibility(View.GONE);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Comments comment = (Comments) msg.obj;
				if(comments != null){
					comments.add(0,comment);
					CommentsAdapter.URLS.add(comment.getSenderheadImage());
					noneLinearLayout.setVisibility(View.GONE);
					commentsListView.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				}else{
					comments = new ArrayList<Comments>();
					comments.add(comment);
					noneLinearLayout.setVisibility(View.GONE);
					commentsListView.setVisibility(View.VISIBLE);
					adapter = new CommentsAdapter(context, R.layout.comment_item, comments,commentsListView);
					commentsListView.setAdapter(adapter);
				}
				
			}
		};
		getcomments();
	}
	
	private void getcomments(){
		String url = NetConnectionDate.url+"/GetAllCommentsServlet";
		new AsyncTask<String, Void, String>() {
			
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				try {
					List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
					list.add(new BasicNameValuePair("courseinfid", GOLBALVALUE.course.getId()+""));
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					HttpResponse response = client.execute(post);
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
					commentsListView.setVisibility(View.VISIBLE);
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
					if(comments.size()>0){
						noneLinearLayout.setVisibility(View.GONE);
						commentsListView.setVisibility(View.VISIBLE);
						adapter = new CommentsAdapter(context, R.layout.comment_item, comments,commentsListView);
						commentsListView.setAdapter(adapter);
					}
				}else{
					noneLinearLayout.setVisibility(View.VISIBLE);
					commentsListView.setVisibility(View.GONE);
				}
			}
			
		}.execute(url);
	}
	
}
