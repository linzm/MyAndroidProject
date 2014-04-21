package com.example.android_test.sys;

import java.io.InputStream;
import java.util.ArrayList;

import com.example.android_test.R;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Contact extends ListActivity {

	Context mContext = null;
	
	private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  
	
	/**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**电话号码**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    /**头像ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**联系人的ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  
    
    /**联系人名称**/  
    private ArrayList<String> mContactsName = new ArrayList<String>();  
      
    /**联系人头像**/  
    private ArrayList<String> mContactsNumber = new ArrayList<String>();  
  
    /**联系人头像**/  
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		getPhoneContact();
		MyListAdapter myAdapter = new MyListAdapter(this);
		
		setListAdapter(myAdapter);
		
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//调起打电话
				Intent intent = new Intent(Intent.ACTION_CALL, 
						Uri.parse("tel:"+mContactsNumber.get(position)));
				
				startActivity(intent);
			}
		});
		
	}
	
	private void getPhoneContact(){
		ContentResolver resolver = this.getContentResolver();
		
		Cursor cursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, 
				null, null, null);
		
		if(cursor != null){
			while(cursor.moveToNext()){
				
				String number = cursor.getString(PHONES_NUMBER_INDEX);
				if(TextUtils.isEmpty(number))
					continue;
				
				String name = cursor.getString(PHONES_DISPLAY_NAME_INDEX);
				Long contactId = cursor.getLong(PHONES_CONTACT_ID_INDEX);
				Long photoId = cursor.getLong(PHONES_PHOTO_ID_INDEX);
				
				Bitmap bmap = null;
				
				if(photoId > 0){
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
							contactId);
					InputStream is = ContactsContract.Contacts.
							openContactPhotoInputStream(resolver, uri);
					bmap = BitmapFactory.decodeStream(is);
				}else{
					bmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				}
						
				mContactsName.add(name);  
		        mContactsNumber.add(number);  
		        mContactsPhonto.add(bmap);  		
			}
			
			cursor.close();
		}
	}
	
	class MyListAdapter extends BaseAdapter{

		public MyListAdapter(Context context){
			mContext = context;
		}
		
		@Override
		public int getCount() {
			return mContactsName.size(); 
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView iamge = null;  
	        TextView name = null;  
	        TextView number = null; 
	        Button button = null;
	        if (convertView == null || position < mContactsNumber.size()) {  
	        convertView = LayoutInflater.from(mContext).inflate(  
	            R.layout.alist, null);  
		        iamge = (ImageView) convertView.findViewById(R.id.contact_photo);  
		        name = (TextView) convertView.findViewById(R.id.contact_name);  
		        number = (TextView) convertView.findViewById(R.id.contact_number); 
		        button = (Button) convertView.findViewById(R.id.sms_btn);
	        }  
	        //绘制联系人名称  
	        name.setText(mContactsName.get(position));  
	        //绘制联系人号码  
	        number.setText(mContactsNumber.get(position));  
	        //绘制联系人头像  
	        iamge.setImageBitmap(mContactsPhonto.get(position));  
	        
	        button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//跳转到发短信界面
					Intent intent = new Intent(Intent.ACTION_SENDTO, 
							Uri.parse("smsto:" + mContactsNumber.get(position)));
					intent.putExtra("sms_body", "带上内容");
					startActivity(intent);
				}
			});
	        
	        return convertView; 
		}
		
	}
}
