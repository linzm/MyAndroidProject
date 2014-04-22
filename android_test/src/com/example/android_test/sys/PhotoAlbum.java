package com.example.android_test.sys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.android_test.R;
import com.example.android_test.adapter.GroupAdapter;
import com.example.android_test.bean.ImageBean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PhotoAlbum extends Activity {

	private HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private final static int SCAN_OK = 1;
	private ProgressDialog progressDialog;
	private GroupAdapter adapter;
	private GridView mGridView;
	
	private Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				progressDialog.dismiss();
				
				//创建适配器
				adapter = new GroupAdapter(PhotoAlbum.this, 
						list = subGroupOfImage(mGroupMap), mGridView);
				mGridView.setAdapter(adapter);
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_main);
		
		mGridView = (GridView) findViewById(R.id.main_grid);
		
		getImages();
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), 
						"1", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//扫描图片
	private void getImages(){
		progressDialog = ProgressDialog.show(this, "title", "加载中……");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver resolver = PhotoAlbum.this.getContentResolver();
				
				String selection = MediaStore.Images.Media.MIME_TYPE + "=? or "+
						MediaStore.Images.Media.MIME_TYPE + "=?";
				Cursor mcursor = resolver.query(imageUri, null, selection, 
						new String[]{"image/jpeg","image/png"}, 
						MediaStore.Images.Media.DATE_MODIFIED);
				
				if(mcursor == null)
					return;
				
				while (mcursor.moveToNext()) {
					//获取图片路径
					String path = mcursor.getString(mcursor.
							getColumnIndex(MediaStore.Images.Media.DATA	));
					//父路径名
					String parentName = new File(path).getParentFile().getName();
					
					//根据父路径名将图片放入到mGruopMap中 
					if(!mGroupMap.containsKey(parentName)){
						List<String> childList = new ArrayList<String>();
						childList.add(path);
						mGroupMap.put(parentName, childList);
					}else{
						mGroupMap.get(parentName).add(path);
					}
				}
				
				//通知Handler扫描图片完成 
				mHandler.sendEmptyMessage(SCAN_OK);
				mcursor.close();
				
			}
		}).start();
		
	}

	private List<ImageBean> subGroupOfImage(HashMap<String,List<String>> mGroupMap){
		
		if(mGroupMap.size() == 0){
			return null;
		}
		
		List<ImageBean> list = new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean imageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			
			imageBean.setFolderName(key);
			imageBean.setImageCounts(value.size());
			imageBean.setTopImagePath(value.get(0));
			
			list.add(imageBean);
		}
		
		return list;
		
	}
}
