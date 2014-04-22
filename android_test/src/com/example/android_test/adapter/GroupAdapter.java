package com.example.android_test.adapter;

import java.util.List;

import com.example.android_test.R;
import com.example.android_test.bean.ImageBean;
import com.example.android_test.view.MyImageView;
import com.example.android_test.view.MyImageView.OnMeasureListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private List<ImageBean> list = null;
	private Point point = new Point(0,0);//用来封装ImageView的宽和高的对象  
	private GridView gridView;
	
	protected LayoutInflater inflater;
	
	public GroupAdapter(Context context, List<ImageBean> list, GridView gridView){
		this.list = list;
		this.gridView = gridView;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean imageBean = list.get(position);
		String path = imageBean.getTopImagePath();
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.album_item, null);
			
			viewHolder.myImageView = (MyImageView) convertView.findViewById(R.id.group_images);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
			
			viewHolder.myImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					
					point.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			
			viewHolder.myImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		viewHolder.mTextViewTitle.setText(imageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(imageBean.getImageCounts());
		viewHolder.myImageView.setTag(path);
		
		Bitmap bmap = null;
		
		if(bmap != null){
			viewHolder.myImageView.setImageBitmap(bmap);
		}else{
			viewHolder.myImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		return convertView;
	}

	
	public static class ViewHolder{
		public MyImageView myImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}
}
