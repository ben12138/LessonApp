package com.lesson.databasehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBasesHelper extends SQLiteOpenHelper{
	
	private final String CREATE_TABLE_SQL = "create table user(_id integer primary key autoincrement,username,password,nickname,headimage,email,sex,school,birthday,introduction)";
	
	public MyDataBasesHelper(Context context,String name,int version) {
		// TODO Auto-generated constructor stub
		super(context, name, null, version);
	}
	
	public void onCreate(android.database.sqlite.SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
	};
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
