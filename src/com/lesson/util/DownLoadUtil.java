package com.lesson.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownLoadUtil {

	private String path;
	private String targetFile;
	private int threadNum;
	private DownThread[] threads;
	private int fileSize;
	
	public DownLoadUtil(String path,String targetFile,int threadNum) {
		// TODO Auto-generated constructor stub
		this.path = path;
		this.threadNum = threadNum;
		threads = new DownThread[threadNum];
		this.targetFile = targetFile;
	}
	
	public void download(){
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg," +
					"application/x-shockwave-flash,application/xaml+xml," +
					"application/vnd.ms-xpsdocument,application/x-ms-xbap," +
					"application/x-ms-application,application/vnd.ms-excel," +
					"application/vnd.ms-powerpoint,application/msword,*/*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			fileSize = conn.getContentLength();
			conn.disconnect();
			int currentPartSize = fileSize / threadNum + 1;
			RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
			file.setLength(fileSize);
			file.close();
			for(int i=0;i<threadNum;i++){
				int startPos = i * currentPartSize;
				RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
				currentPart.seek(startPos);
				threads[i] = new DownThread(startPos, currentPartSize, currentPart);
				threads[i].start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//获得下载完成的百分比
	public double getCompleteRate(){
		int sumSize = 0;
		for(int i = 0;i < threadNum ;i++){
			sumSize += threads[i].length;
		}
		return sumSize * 1.0 / fileSize;
	}
	
	private class DownThread extends Thread{
		private int startPos;//当前下载位置
		private int currentPartSize;//定义当前线程负责下载得文件大小
		private RandomAccessFile currentPart;//定义当前线程需要下载的文件块
		public int length;
		public DownThread(int startPos,int currentPartSize,RandomAccessFile currentPart) {
			// TODO Auto-generated constructor stub
			this.startPos = startPos;
			this.currentPartSize = currentPartSize;
			this.currentPart = currentPart;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5*1000);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg," +
						"application/x-shockwave-flash,application/xaml+xml," +
						"application/vnd.ms-xpsdocument,application/x-ms-xbap," +
						"application/x-ms-application,application/vnd.ms-excel," +
						"application/vnd.ms-powerpoint,application/msword,*/*");
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Charset", "UTF-8");
				InputStream inStream = conn.getInputStream();
				//跳过startPOS个字节，表明线程只下载自己负责的那部分文件
				skipFully(inStream,this.startPos);
				byte[] buffer = new byte[1024];
				int hasRead = 0;
				while(length < currentPartSize && (hasRead = inStream.read(buffer)) > 0){
					currentPart.write(buffer,0,hasRead);
					length += hasRead;
				}
				currentPart.close();
				inStream.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void skipFully(InputStream in,long bytes){
		long remaining = bytes;
		long len = 0;
		while(remaining > 0){
			try {
				len = in.skip(remaining);
				remaining -= len;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
