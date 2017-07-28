package com.lesson.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.lesson.bean.GOLBALVALUE;
import com.lesson.databasehelper.MyDataBasesHelper;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ServerUtils;
import com.lesson.util.Utils;
import com.example.lesson.R;
import com.lesson.util.ActivityCollector;
import com.mysql.fabric.xmlrpc.base.Array;

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
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RegisterActivity3 extends Activity{
	
	private Context context;
	private LinearLayout cameraLinearLayout;
	private LinearLayout xiangceLinearLayout;
	private ImageView headImageImageView;
	private EditText passwordEditText;
	private EditText confirmPasswordEdutEditText;
	private EditText nicknameEditText;
	private TextView birthdayTextView;
	private String birthday;
	private TextView sexTextView;
	private int sex = 0;
	private EditText schoolEditText;
	private EditText personalIntroductionEditText;
	private Button submitButton;
	private Button backButton;
	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	private static final int CROP_SMALL_PICTURE = 2;
	private Uri imageUri;
	private byte[] image = null;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private Bitmap headImage;
	private File headImageFile;
	private String email;
	private boolean uploadImageSuccess = false;
	private boolean uploadInfSuccess = false;
	private Handler handler = null;
	private int submitSuccess = 0;
	private HttpClient client;
	
	private MyDataBasesHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registe3_layout);
		init();
	}
	
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		ActivityCollector.addActivity(this);
		context = this;
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		cameraLinearLayout = (LinearLayout) findViewById(R.id.camera);
		cameraLinearLayout.setOnClickListener(cameraOnClikListener);
		xiangceLinearLayout = (LinearLayout) findViewById(R.id.xiangce);
		xiangceLinearLayout.setOnClickListener(xiangceOnClickListener);
		headImageImageView = (ImageView) findViewById(R.id.head_image);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPasswordEdutEditText = (EditText) findViewById(R.id.confirm_password);
		nicknameEditText = (EditText) findViewById(R.id.nickname);
		birthdayTextView = (TextView) findViewById(R.id.birthday);
		birthdayTextView.setOnClickListener(birthdayTextViewOnClickListener);
		sexTextView = (TextView) findViewById(R.id.sex);
		sexTextView.setOnClickListener(sexTextViewOnCLickListener);
		schoolEditText = (EditText) findViewById(R.id.school);
		personalIntroductionEditText = (EditText) findViewById(R.id.personal_introduction);
		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(submitButtonOnClickListener);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == submitSuccess){
					Intent intent = new Intent(context,MainActivity.class);
					startActivity(intent);
				}
			}
		};
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityCollector.removeActivity(this);
		this.finish();
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(RegisterActivity3.this);
			RegisterActivity3.this.finish();
		}
	};
	
	private OnClickListener submitButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(isRight()){
				submitInf();
			}else{
				Toast.makeText(context, "请输入正确信息", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener birthdayTextViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SetDateDialog s = new SetDateDialog();
			s.show(getFragmentManager(), "选择日期");
		}
	};
	
	private OnClickListener sexTextViewOnCLickListener = new OnClickListener() {
		
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
	
	private OnClickListener xiangceOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
			openAlbumIntent.setType("image/*");
			startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
		}
	};
	
	private OnClickListener cameraOnClikListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			headImageFile = new File(Environment
					.getExternalStorageDirectory(), email+".jpg");
			if(headImageFile.exists()){
				headImageFile.delete();
			}
			try {
				headImageFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageUri = Uri.fromFile(headImageFile);
			
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			startPhotoZoom(imageUri);
			break;
		case CHOOSE_PICTURE:
			if(data!=null){
				startPhotoZoom(data.getData());
			}
			break;
		case CROP_SMALL_PICTURE:
			if (data != null) {
				setImageToView(data);
			}
			break;
		default:
			break;

		}
	};
	
	protected void startPhotoZoom(Uri uri) {
		if (uri == null) {
			Log.i("tag", "The uri is not exist.");
		}
		imageUri = uri;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_SMALL_PICTURE);
	}
	
	protected void setImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			headImage = extras.getParcelable("data");
			headImage = Utils.toRoundBitmap(headImage, imageUri);
			headImageImageView.setImageBitmap(headImage);
			headImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
			image = baos.toByteArray();
			headImageFile = new File(Utils.savePhoto(headImage, Environment.getExternalStorageDirectory()+ "/Lesson/headImage/", email));
		}
	}
	
	class SetDateDialog extends DialogFragment implements OnDateSetListener {

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
	
	public boolean isRight(){
		if(headImage == null || 
				passwordEditText.getText().toString() == null ||
				!passwordEditText.getText().toString().equals(confirmPasswordEdutEditText.getText().toString()) ||
				birthday == null || sex == -1){
			return false;
		}
		return true;
		
	}
	
	private void submitInf(){
		uploadFile();
		uploadInf();
		new Thread(new Runnable() {
			private boolean isRunning = true;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isRunning){
					if(uploadImageSuccess&&uploadInfSuccess){
						Message msg = new Message();
						msg.what = submitSuccess;
						handler.sendMessage(msg);
						isRunning = false;
					}
				}
			}
		}).start();
	}
	
	private void uploadInf(){
		String url = NetConnectionDate.url+"RegisterServlet";
		new AsyncTask<String , Void, Integer>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				Toast.makeText(context, "正在提交，请稍后", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			protected Integer doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("email", email));
				list.add(new BasicNameValuePair("name", email));
				list.add(new BasicNameValuePair("password", passwordEditText.getText().toString()));
				list.add(new BasicNameValuePair("nickname", nicknameEditText.getText().toString()));
				list.add(new BasicNameValuePair("birthday", birthday));
				list.add(new BasicNameValuePair("sex", sex+""));
				list.add(new BasicNameValuePair("school", schoolEditText.getText().toString()));
				list.add(new BasicNameValuePair("introduction", personalIntroductionEditText.getText().toString()));
				list.add(new BasicNameValuePair("onlinedevice", "phone"));
				try {
					HttpEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);//设置编码，防止中午乱码
					post.setEntity(entity);
					post.setEntity(new UrlEncodedFormEntity(list));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					HttpResponse response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					System.out.println(result.equals("success"));
					if(result.equals("success")){
						return 1;
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
				return 0;
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result == 0){
					Toast.makeText(context, "糟糕，网络开小差了", Toast.LENGTH_SHORT).show();
				}else if(result == 1){
					uploadInfSuccess = true;
					GOLBALVALUE.user.setBirthday(birthday);
					GOLBALVALUE.user.setEmail(email);
					GOLBALVALUE.user.setHeadImage(Environment.getExternalStorageDirectory()+ "/Lesson/headImage/"+email+".png");
					GOLBALVALUE.headimageUrl = NetConnectionDate.url+"headImage/"+email+".png";
					GOLBALVALUE.user.setIntroduction(personalIntroductionEditText.getText().toString().trim());
					GOLBALVALUE.user.setNickname(nicknameEditText.getText().toString().trim());
					GOLBALVALUE.user.setSchool(schoolEditText.getText().toString().trim());
					GOLBALVALUE.user.setSex(sex);
					GOLBALVALUE.user.setUsername(email);
					insertUser();
					
				}
			}
		}.execute(url,email);
	}
	
	private void insertUser(){
		dbHelper = new MyDataBasesHelper(context, "user.db3", 1);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.execSQL("insert into user values(null,?,?,?,?,?,?,?,?,?)", new String[]{
				GOLBALVALUE.user.getUsername(),
				GOLBALVALUE.user.getPassword(),
				GOLBALVALUE.user.getNickname(),
				GOLBALVALUE.user.getHeadImage(),
				GOLBALVALUE.user.getEmail(),
				GOLBALVALUE.user.getSex()+"",
				GOLBALVALUE.user.getSchool(),
				GOLBALVALUE.user.getBirthday(),
				GOLBALVALUE.user.getIntroduction()
		});
		Utils.savePhoto(headImage, Environment.getExternalStorageDirectory()+ "/Lesson/headImage/", email);
	}
	
	private void uploadFile(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = ServerUtils.formUpload(NetConnectionDate.url+"UpLoadHeadImageServlet", headImageFile);
				if(result.trim().equals("success")){
					uploadImageSuccess = true;
					System.out.println("uploadFileok");
				}
			}
		}).start();
      
    }
}
