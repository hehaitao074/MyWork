package com.storm.fliplayout.bean;

import java.io.Serializable;

public class PicWen implements Serializable{
	
	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String title;
	private String imgUrl;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	

}
