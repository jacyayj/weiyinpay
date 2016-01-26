package Adapter;

import java.util.ArrayList;

import com.example.cz.R;
import com.example.modle.NEWS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter{
	Context context;
	ArrayList<NEWS> data;
	LayoutInflater inflater;
	ViewHodler hodler;
	public NewsAdapter(Context context) {
		this.context = context;
		data = new ArrayList<NEWS>();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public NEWS getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			System.out.println(arg0);
			arg1 = inflater.inflate(R.layout.news_item,null);
			hodler = new ViewHodler();
			hodler.state = (ImageView) arg1.findViewById(R.id.news_item1);
			hodler.title = (TextView) arg1.findViewById(R.id.news_item2);
			hodler.time = (TextView) arg1.findViewById(R.id.news_item3);
			hodler.content = (TextView) arg1.findViewById(R.id.news_item4);
			@SuppressWarnings("deprecation")
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,(int) (((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()*0.09));
			arg1.setLayoutParams(layoutParams);
			arg1.setTag(hodler);
		}else {
			hodler = (ViewHodler) arg1.getTag();
		}
		hodler.title.setText(data.get(arg0).getFtitle());
		hodler.time.setText(data.get(arg0).getFdate());
		try {
			hodler.content.setText((data.get(arg0).getFcontent()).substring(0,12));
		} catch (StringIndexOutOfBoundsException e) {
			hodler.content.setText(data.get(arg0).getFcontent());
		}
		if ("1".equals(data.get(arg0).getFstatus())) {
			hodler.state.setVisibility(View.VISIBLE);
		}else {
			hodler.state.setVisibility(View.INVISIBLE);
		}
		return arg1;
	}
	public void refresh(ArrayList<NEWS> data){
		this.data = data;
		notifyDataSetChanged();
	}
	class ViewHodler{
		TextView title;
		TextView time;
		TextView content;
		ImageView state;
	};
}
