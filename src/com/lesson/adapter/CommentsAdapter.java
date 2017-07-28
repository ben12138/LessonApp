package com.lesson.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.lesson.R;
import com.lesson.activity.CommentsActivity2;
import com.lesson.activity.SendCommentActivity;
import com.lesson.bean.Comments;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.LoadImage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentsAdapter extends ArrayAdapter<Comments> implements OnScrollListener{
	
	private int resourceId;
	private Context context;
	private Bitmap bitmap;
	private HttpClient client;
	private LoadImage loadImage;
	private int mStart;
	private int mEnd;
	public static List<String> URLS;
	private boolean mFirstIn;
	private List<Integer> praiselist = new ArrayList<>();
	
	private final static int CHANGEHEADIMAGE = 0;
	
	public CommentsAdapter(Context context,int textViewResourceId,List<Comments> comments ,ListView listView) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, comments);
		resourceId = textViewResourceId;
		this.context = context;
		loadImage = new LoadImage(listView);
		URLS = new ArrayList<String>();
		for(int i=0;i<comments.size();i++){
			URLS.add(comments.get(i).getSenderheadImage());
		}
		listView.setOnScrollListener(this);
		mFirstIn = true;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		final ViewHolder viewHolder;
		final Comments comment = getItem(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
			viewHolder.senderHeadImageImageView = (ImageView) view.findViewById(R.id.sender_headimage);
			viewHolder.senderNickNameTextView = (TextView) view.findViewById(R.id.sender_nickname);
			viewHolder.contentTextView = (TextView) view.findViewById(R.id.content);
			viewHolder.sendTimeTextView = (TextView) view.findViewById(R.id.send_time);
			viewHolder.praiseImageImageView = (ImageView) view.findViewById(R.id.praise_image);
			viewHolder.praiseNumTextView = (TextView) view.findViewById(R.id.praise_num);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.senderNickNameTextView.setText(comment.getSenderNickName());
		viewHolder.contentTextView.setText(comment.getContent());
		viewHolder.sendTimeTextView.setText(comment.getSendtime());
		viewHolder.praiseImageImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!praiselist.contains(position)) {
					viewHolder.praiseImageImageView
							.setImageResource(R.drawable.praise_pressed);
					viewHolder.praiseNumTextView.setText(comment
							.getPraisenum() + 1 + "");
					submit(comment, position, viewHolder);
					viewHolder.imageid = R.drawable.praise_pressed;
				}else{
					Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
				}
			}
		});
		if(!praiselist.contains(position)){
			viewHolder.praiseImageImageView.setImageResource(R.drawable.praise_normal);
			viewHolder.imageid = R.drawable.praise_normal;
			viewHolder.praiseNumTextView.setText(comment.getPraisenum()+"");
		}else{
			viewHolder.praiseImageImageView.setImageResource(R.drawable.praise_pressed);
			viewHolder.imageid = R.drawable.praise_pressed;
			viewHolder.praiseNumTextView.setText(comment.getPraisenum()+1+"");
		}
		loadImage.showImageByAsyncTask(viewHolder.senderHeadImageImageView, comment.getSenderheadImage());
		return view;
	}
	
	private class ViewHolder{
		ImageView senderHeadImageImageView;
		TextView senderNickNameTextView;
		TextView contentTextView;
		TextView sendTimeTextView;
		ImageView praiseImageImageView;
		TextView praiseNumTextView;
		boolean isClicked = false;
		int imageid;
	}
	
	private void submit(Comments comment,final int position,final ViewHolder viewHolder){
		String url = NetConnectionDate.url+"GetAllCommentsServlet?type=praise&id="+comment.getId();
		new AsyncTask<String, Void, Integer>() {
			@Override
			protected Integer doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String utlString = arg0[0];
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(utlString);
				try {
					HttpResponse response = client.execute(get);
					String result = EntityUtils.toString(response.getEntity());
					if(result.equals("success") ){
						return 1;
					}else{
						return 0;
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
					Toast.makeText(context, "抱歉，出错了", Toast.LENGTH_SHORT).show();
				}else{
					praiselist.add(position);
					viewHolder.isClicked = true;
				}
			}
		}.execute(url);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItem) {
		// TODO Auto-generated method stub
		mStart = firstVisibleItem;
		mEnd = firstVisibleItem + visibleItemCount;//可见的起始项
		//第一次显示调用
		if(mFirstIn && visibleItemCount > 0){
			loadImage.loadCommentsImages(mStart, mEnd);
			mFirstIn = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(scrollState == SCROLL_STATE_IDLE){
			//当前状态处于停止状态，加载可见项
			loadImage.loadCommentsImages(mStart, mEnd);
		}else{
			//停止任务
			loadImage.cancelAllTasks();
		}
	}
}
