package com.lesson.bean;

import java.util.HashMap;
import java.util.Map;

import com.lesson.view.MyVideoView;

import android.media.AudioManager;

public class GOLBALVALUE {
	public static UserInf user = new UserInf();
	public static Map<String, HashMap<String, HashMap<String,String>>> map = new HashMap<String, HashMap<String, HashMap<String,String>>>();
	public static String coursetype1;
	public static int HOME_TYPE = 1;
	public static String headimageUrl;
	public static final int ADDCOMMENT = 3;
	public static CourseInf course;
	public static boolean isStartActivity = false;
}
