package Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cz.R;

public class MainAdapter extends BaseAdapter{

	Context context;
	String[] data;
	LayoutInflater inflater;
	ViewHodler hodler;
	private int imgs[] = null;
	public MainAdapter(Context context,String[] data) {
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imgs = new int[17];
		imgs[0] = R.drawable.body_item1;
		imgs[1] = R.drawable.body_item2;
		imgs[2] = R.drawable.body_item3;
		imgs[3] = R.drawable.body_item4;
		imgs[4] = R.drawable.body_item5;
		imgs[5] = R.drawable.body_item6;
		imgs[6] = R.drawable.body_item7;
		imgs[7] = R.drawable.body_item8;
		imgs[8] = R.drawable.body_item9;
		imgs[9] = R.drawable.body_item10;
		imgs[10] = R.drawable.body_item11;
		imgs[11] = R.drawable.body_item12;
		imgs[12] = R.drawable.body_item13;
		imgs[13] = R.drawable.body_item14;
		imgs[14] = R.drawable.body_item15;
		imgs[15] = R.drawable.body_item16;
		imgs[16] = R.drawable.body_item17;
	}
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return data.length;
	}

	@Override
	public String getItem(int arg0) {
		// TODO 自动生成的方法存根
		return data[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			System.out.println(arg0);
			arg1 = inflater.inflate(R.layout.body_item,null);
			hodler = new ViewHodler();
			hodler.title = (TextView) arg1.findViewById(R.id.body2);
			hodler.lable = (ImageView) arg1.findViewById(R.id.body1);
			arg1.setTag(hodler);
		}else {
			hodler = (ViewHodler) arg1.getTag();
		}
		hodler.title.setText(data[arg0]);
		hodler.lable.setBackgroundResource(imgs[arg0]);
		return arg1;
	}
	public void refresh(String[] data){
		this.data = data;
		notifyDataSetChanged();
	}
	class ViewHodler{
		TextView title;
		ImageView lable;
	};
}
