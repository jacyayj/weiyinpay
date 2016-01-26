package com.example.modle;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {
    @Override
	public void onCreate() {
    	super.onCreate();
    	@SuppressWarnings("deprecation")
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)  
        .memoryCacheExtraOptions(480, 800)          // default = device screen dimensions  
        .threadPoolSize(3)                          // default  
        .threadPriority(Thread.NORM_PRIORITY - 1)   // default  
        .tasksProcessingOrder(QueueProcessingType.FIFO) // default  
        .denyCacheImageMultipleSizesInMemory()  
        .memoryCacheSize(2 * 1024 * 1024)  
        .memoryCacheSizePercentage(13)              // default  
        .discCacheSize(50 * 1024 * 1024)        // 缓冲大小  
        .discCacheFileCount(100)                // 缓冲文件数目  
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default  
        .writeDebugLogs()  
        .build();  
    	ImageLoader.getInstance().init(config);
    }
}
