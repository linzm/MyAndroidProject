package com.example.android_test.sys;

import java.util.List;

import com.example.android_test.R;
import com.example.android_test.adapter.ChildAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

public class ShowImageActivity extends Activity {

	private GridView gridView;
	private List<String> list;
	private ChildAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_main);
		
		gridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		
		adapter = new ChildAdapter(this, list, gridView);
		
		gridView.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();  
        super.onBackPressed();
	}
}
