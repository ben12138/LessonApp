package com.lesson.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.AccessController;

import com.example.lesson.R;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.fragment.MyInfFragment;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;
import com.lesson.util.ServerUtils;
import com.lesson.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

public class ChangeInfActivity1 extends Activity{
	
	private Context context;
	private ImageView headimageImageView;
	private TextView nicknameTextView;
	private LinearLayout myInfLinearLayout;
	private LinearLayout headimageLinearLayout;
	private Button backButton;
	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	private static final int CROP_SMALL_PICTURE = 2;
	private Uri imageUri;
	private File headImageFile;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private boolean uploadImageSuccess = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_inf1_layout);
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
		headimageImageView = (ImageView) findViewById(R.id.headimage);
		headimageImageView.setImageBitmap(Utils.getBitmap(GOLBALVALUE.user.getHeadImage()));
		headimageLinearLayout = (LinearLayout) findViewById(R.id.headimage_layout);
		headimageLinearLayout.setOnClickListener(headimageLayoutOnClickListener);
		nicknameTextView = (TextView) findViewById(R.id.nickname);
		nicknameTextView.setText(GOLBALVALUE.user.getNickname());
		myInfLinearLayout = (LinearLayout) findViewById(R.id.myInf);
		myInfLinearLayout.setOnClickListener(myInfLayoutOnClickListener);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
	}
	private OnClickListener headimageLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.choose_photo_pop_window,null,false);
			Button cameraButton = (Button) view.findViewById(R.id.camera);
			Button xiangceButton = (Button) view.findViewById(R.id.xiangce);
			Button cancelButton = (Button) view.findViewById(R.id.cancel);
			final PopupWindow popup = new PopupWindow(view,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			popup.setBackgroundDrawable(dw);
			popup.setFocusable(true);
			popup.showAtLocation(findViewById(R.id.myInf), Gravity.BOTTOM, 0, 0);
			popup.setAnimationStyle(R.style.mypopwindow_anim_style);
			popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popup.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					popup.dismiss();
				}
			});
			cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					popup.dismiss();
				}
			});
			cameraButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent openCameraIntent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					headImageFile = new File(Environment
							.getExternalStorageDirectory(), GOLBALVALUE.user.getEmail()+".jpg");
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
					popup.dismiss();
				}
			});
			xiangceButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
					openAlbumIntent.setType("image/*");
					startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
					popup.dismiss();
				}
			});
		}
	};
	private OnClickListener myInfLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,ChangeInfActivity2.class);
			startActivity(intent);
		}
	};
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(ChangeInfActivity1.this);
			ChangeInfActivity1.this.finish();
		}
	};
	public void onBackPressed() {
		ActivityCollector.removeActivity(ChangeInfActivity1.this);
		ChangeInfActivity1.this.finish();
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
			Bitmap headImage = extras.getParcelable("data");
			headImage = Utils.toRoundBitmap(headImage, imageUri);
			headimageImageView.setImageBitmap(headImage);
			headImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] image = baos.toByteArray();
			headImageFile = new File(Utils.savePhoto(headImage, Environment.getExternalStorageDirectory()+ "/Lesson/headImage/", GOLBALVALUE.user.getEmail()));
			uploadFile();
			Message msg = new Message();
			msg.obj = headImage;
			msg.what = MainActivity.CHANGESTATE;
			MainActivity.handler.sendMessage(msg);
			Message msg1 = new Message();
			msg1.obj = headImage;
			msg1.what = MyInfFragment.CHANGESTATE;
			MyInfFragment.handler.sendMessage(msg1);
		}
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
