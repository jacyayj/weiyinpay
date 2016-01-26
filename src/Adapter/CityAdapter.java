package Adapter;

import java.util.ArrayList;

import com.example.cz.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityAdapter extends BaseAdapter{
	Context context;
	ArrayList<String> data;
	ViewHodler hodler;
	public CityAdapter(Context context,ArrayList<String> data) {
		this.context = context;
		this.data = data;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.dialog_list_item, null);
			hodler = new ViewHodler();
			hodler.name = (TextView) convertView.findViewById(R.id.dialog_list_tv);
			convertView.setTag(hodler);
		}else {
			hodler = (ViewHodler) convertView.getTag();
		}
			hodler.name.setText(data.get(position));
		return convertView;
	}
	public void refresh(ArrayList<String> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	class ViewHodler{
		TextView name;
	}
	@Override
	public int getCount() {
		return data.size();
	}
	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
