package com.lesson.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lesson.R;
import com.lesson.activity.RegisterActivity3.SetDateDialog;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.fragment.MyInfFragment;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;

public class ChangeInfActivity2 extends Activity{
	
	private Context context;
	
	private Button backButton;
	private TextView cancelTextView;
	private TextView editTextView;
	private TextView okTextView;
	private EditText nicknameEditText;
	private TextView sexTextView;
	private TextView birthdayTextView;
	private EditText schoolEditText;
	private TextView emailTextView;
	private EditText introductionEditText;
	
	private String nickname;
	private String birthday;
	private int sex;
	private String school;
	private String introduction;
	//有没有正在修改的状态，如果正在修改，则back键效果等于取消，否则等于back，0表示被修改中，1表示没有修改
	private static boolean change = false;
	
	private HttpClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_inf2_layout);
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
		ActivityCollector.addActivity(this);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtononClickListener);
		cancelTextView = (TextView) findViewById(R.id.cancel);
		cancelTextView.setOnClickListener(cancelButtonOnClickListener);
		cancelTextView.setVisibility(View.GONE);
		editTextView = (TextView) findViewById(R.id.edit);
		editTextView.setOnClickListener(editButtonOnClickListener);
		okTextView = (TextView) findViewById(R.id.ok);
		okTextView.setOnClickListener(okButtonOnClickListener);
		okTextView.setVisibility(View.GONE);
		nicknameEditText = (EditText) findViewById(R.id.nickname);
		nicknameEditText.setEnabled(false);
		nicknameEditText.setText(GOLBALVALUE.user.getNickname());
		sexTextView = (TextView) findViewById(R.id.sex);
		sexTextView.setOnClickListener(sexTextViewOnClickListener);
		sexTextView.setClickable(false);
		if(GOLBALVALUE.user.getSex() == 0){
			sexTextView.setText("男");
		}else if(GOLBALVALUE.user.getSex() == 1){
			sexTextView.setText("女");
		}
		birthdayTextView = (TextView) findViewById(R.id.birthday);
		birthdayTextView.setOnClickListener(birthdayTextViewOnClickListener);
		birthdayTextView.setClickable(false);
//		System.out.println(GOLBALVALUE.user.getBirthday());
		birthdayTextView.setText(GOLBALVALUE.user.getBirthday());
//		System.out.println(GOLBALVALUE.user.getBirthday());
		emailTextView = (TextView) findViewById(R.id.email);
		emailTextView.setEnabled(false);
		emailTextView.setText(GOLBALVALUE.user.getEmail());
		introductionEditText = (EditText) findViewById(R.id.introduction);
		introductionEditText.setEnabled(false);
		introductionEditText.setText(GOLBALVALUE.user.getIntroduction());
		schoolEditText = (EditText) findViewById(R.id.school);
		schoolEditText.setEnabled(false);
		schoolEditText.setText(GOLBALVALUE.user.getSchool());
	}
	private OnClickListener backButtononClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(ChangeInfActivity2.this);
			ChangeInfActivity2.this.finish();
		}
	};
	private OnClickListener cancelButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			backButton.setVisibility(View.VISIBLE);
			cancelTextView.setVisibility(View.GONE);
			editTextView.setVisibility(View.VISIBLE);
			okTextView.setVisibility(View.GONE);
			if(GOLBALVALUE.user.getSex() == 0){
				sexTextView.setText("男");
			}else if(GOLBALVALUE.user.getSex() == 1){
				sexTextView.setText("女");
			}
			sexTextView.setClickable(false);
			nicknameEditText.setText(GOLBALVALUE.user.getNickname());
			nicknameEditText.setEnabled(false);
			birthdayTextView.setText(GOLBALVALUE.user.getBirthday());
			birthdayTextView.setClickable(false);
			introductionEditText.setText(GOLBALVALUE.user.getIntroduction());
			introductionEditText.setEnabled(false);
			schoolEditText.setText(GOLBALVALUE.user.getSchool());
			schoolEditText.setEnabled(false);
		}
	};
	private OnClickListener okButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			cancelTextView.setVisibility(View.GONE);
			backButton.setVisibility(View.VISIBLE);
			okTextView.setVisibility(View.GONE);
			editTextView.setVisibility(View.VISIBLE);
			schoolEditText.setEnabled(false);
			introductionEditText.setEnabled(false);
			birthdayTextView.setClickable(false);
			sexTextView.setClickable(false);
			nicknameEditText.setEnabled(false);
			change = false;
			submit();
		}
	};
	private OnClickListener editButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			backButton.setVisibility(View.GONE);
			cancelTextView.setVisibility(View.VISIBLE);
			editTextView.setVisibility(View.GONE);
			okTextView.setVisibility(View.VISIBLE);
			nicknameEditText.setEnabled(true);
			sexTextView.setClickable(true);
			birthdayTextView.setClickable(true);
			schoolEditText.setEnabled(true);
			introductionEditText.setEnabled(true);
			change = true;
		}
	};
	private OnClickListener sexTextViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("性别");
			final String str[] = { "男", "女" };
			builder.setItems(str, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case 0:
						sex = 0;
						sexTextView.setText("男");
						break;
					case 1:
						sex = 1;
						sexTextView.setText("女");
						break;
					}
				}
			});
			builder.show();
		}
	};
	private OnClickListener birthdayTextViewOnClickListener = new OnClickListener() {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SetDateDialog s = new SetDateDialog();
			s.show(getFragmentManager(), "选择日期");
		}
	};
	@SuppressLint("NewApi")
	class SetDateDialog extends DialogFragment implements OnDateSetListener {

		@SuppressLint("NewApi")
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,
					year, month, day);
			return dpd;
		}

		@Override
		public void onDateSet(DatePicker v, int year, int month, int day) {
			// TODO Auto-generated method stub
			birthdayTextView
					.setText(year + "年" + (month + 1) + "月" + day + "日");
			birthday = year + "年" + (month + 1) + "月" + day + "日";
		}

	}
	private void submit(){
		String url = NetConnectionDate.url+"UpDateUserInfServlet";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				
//				post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
				   
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("nickname", nicknameEditText.getText().toString().trim()));
				list.add(new BasicNameValuePair("sex", sex+""));
				list.add(new BasicNameValuePair("birthday", GOLBALVALUE.user.getBirthday()));
				list.add(new BasicNameValuePair("school", schoolEditText.getText().toString().trim()));
				list.add(new BasicNameValuePair("introduction", introductionEditText.getText().toString().trim()));
				list.add(new BasicNameValuePair("email", emailTextView.getText().toString().trim()));
				
				try {
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					HttpResponse response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					return result.trim();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "failure";
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "failure";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "failure";
				}
			}
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				System.out.println(result);
				if(result.equals("success")){
					upDateUset();
				}else if(result.equals("failure")){
					Toast.makeText(context, "糟糕，出错了", Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(url);
	}
	private void upDateUset(){
		nickname = nicknameEditText.getText().toString().trim();
		school = schoolEditText.getText().toString().trim();
		introduction = introductionEditText.getText().toString().trim();
		GOLBALVALUE.user.setNickname(nickname);
		GOLBALVALUE.user.setSex(sex);
		GOLBALVALUE.user.setBirthday(birthday);
		GOLBALVALUE.user.setSchool(school);
		GOLBALVALUE.user.setIntroduction(introduction);
		Message msg = new Message();
		msg.what = MainActivity.CHANGEUSER;
		MainActivity.handler.sendMessage(msg);
		Message msg1 = new Message();
		msg1.what = MyInfFragment.CHANGEUSER;
		MyInfFragment.handler.sendMessage(msg1);
	}
	public void onBackPressed() {
		if(change){
			backButton.setVisibility(View.VISIBLE);
			cancelTextView.setVisibility(View.GONE);
			editTextView.setVisibility(View.VISIBLE);
			okTextView.setVisibility(View.GONE);
			if(GOLBALVALUE.user.getSex() == 0){
				sexTextView.setText("男");
			}else if(GOLBALVALUE.user.getSex() == 1){
				sexTextView.setText("女");
			}
			sexTextView.setClickable(false);
			nicknameEditText.setText(GOLBALVALUE.user.getNickname());
			nicknameEditText.setEnabled(false);
			birthdayTextView.setText(GOLBALVALUE.user.getBirthday());
			birthdayTextView.setClickable(false);
			introductionEditText.setText(GOLBALVALUE.user.getIntroduction());
			introductionEditText.setEnabled(false);
			schoolEditText.setText(GOLBALVALUE.user.getSchool());
			schoolEditText.setEnabled(false);
			change = false;
		}else{
			ActivityCollector.removeActivity(ChangeInfActivity2.this);
			ChangeInfActivity2.this.finish();
		}
	};
}
