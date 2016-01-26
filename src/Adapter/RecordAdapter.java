package Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import com.example.cz.R;
import com.example.modle.RecordModle;

public class RecordAdapter extends BaseAdapter{

	Context context;
	ArrayList<RecordModle> data;
	LayoutInflater inflater;
	ViewHodler hodler;
	public RecordAdapter(Context context) {
		this.context = context;
		data = new ArrayList<RecordModle>();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return data.size();
	}

	@Override
	public RecordModle getItem(int arg0) {
		// TODO 自动生成的方法存根
		return data.get(arg0);
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
			arg1 = inflater.inflate(R.layout.record_item,null);
			hodler = new ViewHodler();
			hodler.type = (TextView) arg1.findViewById(R.id.record_item1);
			hodler.money = (TextView) arg1.findViewById(R.id.record_item2);
			hodler.state = (TextView) arg1.findViewById(R.id.record_item3);
			hodler.time = (TextView) arg1.findViewById(R.id.record_item4);
			@SuppressWarnings("deprecation")
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,(int) (((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()*0.09));
			arg1.setLayoutParams(layoutParams);
			arg1.setTag(hodler);
		}else {
			hodler = (ViewHodler) arg1.getTag();
		}
		hodler.type.setText(data.get(arg0).getTranstype());
		hodler.money.setText(data.get(arg0).getAmount());
		hodler.state.setText(data.get(arg0).getTransstat());
		hodler.time.setText(data.get(arg0).getDatetime());
		return arg1;
	}
	public void refresh(ArrayList<RecordModle> data){
		this.data = data;
		notifyDataSetChanged();
	}
	class ViewHodler{
		TextView type;
		TextView money;
		TextView state;
		TextView time;
	};

}
