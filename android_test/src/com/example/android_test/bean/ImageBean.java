package com.example.android_test.bean;

public class ImageBean {

	//文件夹名
	private String folderName;
	//文件夹中的一张图片路径
	private String topImagePath;
	//图片数
	private int imageCounts;
	
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getTopImagePath() {
		return topImagePath;
	}
	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}
	public int getImageCounts() {
		return imageCounts;
	}
	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}
	
}
