package com.lesson.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lesson.R;
import com.lesson.adapter.CLassesAdapter;
import com.lesson.adapter.ClassInfAdapter;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;

@SuppressLint("NewApi")
public class ClassificationFragment extends Fragment{
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	
	private ListView classesListView;
	private HttpClient client = null;
	private Context context;
	private CLassesAdapter classesAdapter;
	private List<String> classesList;
	private ClassInfAdapter  classInfAdapter;
	private ListView classesInfListView;
	
	private View view ;
	
	public static Handler handler;
	
	private int selected = -1;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}
	public ClassificationFragment(int selected) {
		// TODO Auto-generated constructor stub
		this.selected = selected;
	}
	public ClassificationFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.classification_layout, container, false);
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
		}
		return view;
	}
		
	private void isNotConnectedInit(final View view) {
		reConnectLinearLayout = (LinearLayout) view
				.findViewById(R.id.reconnect);
		reConnectLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConnectivityManager manager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = manager.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 联网状态
					isNotConnectedLinearLayout.setVisibility(View.GONE);
					isConnectedLinearLayout.setVisibility(View.VISIBLE);
					init(view);
				} else {
					// 断网状态
					isNotConnectedLinearLayout.setVisibility(View.VISIBLE);
					isConnectedLinearLayout.setVisibility(View.GONE);
					isNotConnectedInit(view);
				}
			}
		});
	}
	
	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return view;
	}
	
	private void init(View view){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
			}
		};
		classesInfListView = (ListView) view.findViewById(R.id.classesInf);
		classesList = new ArrayList<String>();
		getCourseType();
		classesListView = (ListView) view.findViewById(R.id.classes);
	}
	private void getCourseType(){
		String url = NetConnectionDate.url+"GetCourseInf";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				client = new DefaultHttpClient();
				String urlString = arg0[0];
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("type", "phone"));
				list.add(new BasicNameValuePair("getInf", "allcoursename"));
				try {
					post.setEntity(new UrlEncodedFormEntity(list));
					HttpResponse response;
					response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					return result;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				String coursetype1 = null;
				String coursetype2 = null;
				for(int i=0;i<jsonarray.length();i++){
					JSONObject json = jsonarray.getJSONObject(i);
					if(json.has("coursetype1")){
						coursetype1 = json.getString("coursetype1");
						GOLBALVALUE.map.put(coursetype1, new HashMap<String,HashMap<String,String>>());
					}else if(json.has("coursetype2")){
						coursetype2 = json.getString("coursetype2");
						if(!GOLBALVALUE.map.get(coursetype1).containsKey(coursetype2)){
							GOLBALVALUE.map.get(coursetype1).put(coursetype2, new HashMap<String,String>());
						}
					}else{
						GOLBALVALUE.map.get(coursetype1).get(coursetype2).put(json.getString("id"), (String) json.get("coursetype3"));
					}
				}
				for(String key:GOLBALVALUE.map.keySet()){
					classesList.add(key);
					for(String key1:GOLBALVALUE.map.get(key).keySet()){
						for(String key2:GOLBALVALUE.map.get(key).get(key1).keySet()){
						}
					}
				}
				if(selected == 0|| selected == -1){
					classesAdapter = new CLassesAdapter(context,
							R.layout.classes_item, classesList, 0);
					classesListView.setAdapter(classesAdapter);
					classesListView.setOnItemClickListener(classnameItemListener);
					classInfAdapter = new ClassInfAdapter(context,
							R.layout.classesinf_item,
							GOLBALVALUE.map.get(classesList.get(0)));
					GOLBALVALUE.coursetype1 = classesList.get(0);
					classesInfListView.setAdapter(classInfAdapter);
				}else{
					classesAdapter = new CLassesAdapter(context,
							R.layout.classes_item, classesList, selected-1);
					classesListView.setAdapter(classesAdapter);
					classesListView.setOnItemClickListener(classnameItemListener);
					classInfAdapter = new ClassInfAdapter(context,
							R.layout.classesinf_item,
							GOLBALVALUE.map.get(classesList.get(selected-1)));
					GOLBALVALUE.coursetype1 = classesList.get(selected-1);
					classesInfListView.setAdapter(classInfAdapter);
				}
			}
			
		}.execute(url,"phone","allcoursename");
	}
	private OnItemClickListener classnameItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			classesAdapter.upDateItem(position, classesListView,classesList);
			classInfAdapter = new ClassInfAdapter(context, R.layout.classesinf_item, GOLBALVALUE.map.get(classesList.get(position)));
			classesInfListView.setAdapter(classInfAdapter);
			GOLBALVALUE.coursetype1 = classesList.get(position);
		}
	};
}
