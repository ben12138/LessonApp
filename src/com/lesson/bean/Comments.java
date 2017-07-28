package com.lesson.bean;

import java.io.Serializable;
import java.util.Date;

public class Comments implements Serializable{
	private int id;
	private String sender;
	private String senderheadImage;
	private String senderNickName;
	private String content;
	private String sendtime;
	private int praisenum;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSenderheadImage() {
		return senderheadImage;
	}
	public void setSenderheadImage(String senderheadImage) {
		this.senderheadImage = senderheadImage;
	}
	public String getSenderNickName() {
		return senderNickName;
	}
	public void setSenderNickName(String senderNickName) {
		this.senderNickName = senderNickName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendtime() {
		return sendtime;
	}
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}
	public int getPraisenum() {
		return praisenum;
	}
	public void setPraisenum(int praisenum) {
		this.praisenum = praisenum;
	}
	@Override
	public String toString() {
		return "Comments [id=" + id + ", sender=" + sender
				+ ", senderheadImage=" + senderheadImage + ", senderNickName="
				+ senderNickName + ", content=" + content + ", sendtime="
				+ sendtime + ", praisenum=" + praisenum + "]";
	}
	
}
