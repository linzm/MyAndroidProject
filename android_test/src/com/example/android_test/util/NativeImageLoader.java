package com.example.android_test.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

public class NativeImageLoader {

	private LruCache<String, Bitmap> lruCache;
	private static NativeImageLoader instance = new NativeImageLoader();
	private ExecutorService imageThreadPool = Executors.newFixedThreadPool(1);
	
	
	private NativeImageLoader() {
		//获取应用程序的最大内存
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
		
		//用最大内存的1/4来存储图片
		final int cacheSize = maxMemory/4;
		lruCache = new LruCache<String, Bitmap>(cacheSize){
			
			//获取每张图片的大小 
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes()*bitmap.getHeight() / 1024;
			}
		};
	}
	
	/** 
     * 通过此方法来获取NativeImageLoader的实例 
     */ 
	public static NativeImageLoader getInstance(){
		return instance;
	}
	
	/**
	 * 加载本地图片，对图片不进行剪裁 
	 */
	public Bitmap loadNativeImage(final String path,final NativeImageCallBack callBack){
		return null;
	}
	
	/** 
     * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap 
     * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack callBack)来加载 
     */  
	public Bitmap loadNativeImage(final String path, final Point mPoint, 
			final NativeImageCallBack callBack){
		//先获取内存中的Bitmap
		Bitmap bitmap = getBitmapFromMemoryCache(path);
		
		final Handler handler = new Handler(){
			
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				callBack.onImageLoad((Bitmap)msg.obj, path);
			}
		};
		
		if(bitmap == null){
			imageThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					//先获取图片的缩略图 
					Bitmap bitmap = decodeThumbBitmapForFile(path, 
							mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y);
					
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					handler.sendMessage(msg);
					
					//将图片加入到内存缓存  
					lruCache.put(path, bitmap);
				}
			});
		}
		
		return bitmap;
	}
	
	 /** 
     * 往内存缓存中添加Bitmap 
     */ 
	private void addBitmapToMemoryCache(String key,Bitmap bitmap){
		if(getBitmapFromMemoryCache(key) == null && bitmap != null){
			lruCache.put(key, bitmap);
		}
	}
	
	/** 
     * 根据key来获取内存中的图片 
     */ 
	private Bitmap getBitmapFromMemoryCache(String key){
		return lruCache.get(key);
	}
	
	/** 
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图 
     */  
	private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(path, options);
		
	}
	
	/** 
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放 
     */ 
	private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		
		int inSampleSize = 1;
		if(viewWidth == 0 || viewHeight == 0){
			return inSampleSize;
		}
		
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		//假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例  
		if(bitmapWidth > viewWidth || bitmapHeight > viewHeight){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewHeight);
			
			//为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
			
		return inSampleSize;
	}
	
	/** 
     * 加载本地图片的回调接口 
     */  
	public interface NativeImageCallBack{
		/** 
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中 
         */ 
		public void onImageLoad(Bitmap bitmap, String path);
	}
}
