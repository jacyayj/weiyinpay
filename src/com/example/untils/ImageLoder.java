package com.example.untils;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.widget.ImageView;

public class ImageLoder {

	ImageView im;
	String url;
	Context context;

	/**
	 * 
	 * ��ȡͼƬ��ַ�б�
	 * 
	 * @return list
	 */
	@SuppressWarnings("unused")
	private ArrayList<String> getImgPathList() {
		ArrayList<String> list = new ArrayList<String>();
		// Cursor cursor = getContentResolver().query(
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// new String[] { "_id", "_data" }, null, null, null);
		// while (cursor.moveToNext()) {
		// list.add(cursor.getString(1));// ��ͼƬ·����ӵ�list��
		// }
		// cursor.close();
		return list;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param im
	 * @param url
	 * @param context
	 */
	public static void loadimage(ImageView im, String[] url, Context context,
			int postion) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		imageLoader.displayImage(url[postion], im);
	}

}
