package Adapter;

import java.util.ArrayList;

import com.example.cz.R;
import com.example.modle.Bank;
import com.example.untils.AllUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

public class BankAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Bank> data;
	private LayoutInflater inflater;
	private ViewHodler hodler;
	private String[] banks;
	private final int BANKIMG[] = new int[]{R.drawable.chengduban,R.drawable.gongsahng,R.drawable.faxia,R.drawable.guangda,
			R.drawable.guangdingfazhan,R.drawable.jianshe,R.drawable.jiaotong,R.drawable.mingshen,
			R.drawable.nonghang,R.drawable.nongcunxinyongshe,R.drawable.pinan,R.drawable.shanghaipudong,
			R.drawable.shanghaiyh,R.drawable.shenzhenfazhan,R.drawable.zhaoshang,R.drawable.zhongguoyh,
			R.drawable.zhongguoyouzhen,R.drawable.zhongxin,R.drawable.xinye};
	public BankAdapter(Context context) {
		this.context = context;
		data = new ArrayList<Bank>();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		banks = context.getResources().getStringArray(R.array.bankarray);
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Bank getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.addaccount,null);
			hodler = new ViewHodler();
			hodler.bank1 = (TextView) arg1.findViewById(R.id.bank_tv1);
			hodler.bank2 = (TextView) arg1.findViewById(R.id.bank_tv2);
			hodler.user = (TextView) arg1.findViewById(R.id.bank_tv3);
			hodler.img = (ImageView) arg1.findViewById(R.id.bank_iv);
			@SuppressWarnings("deprecation")
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,(int) (((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()*0.09));
			arg1.setLayoutParams(layoutParams);
			arg1.setTag(hodler);
		}else {
			hodler = (ViewHodler) arg1.getTag();
		}
		hodler.bank1.setText(data.get(arg0).getPname());
		hodler.bank2.setText("Î²ºÅ"+data.get(arg0).getPnumber().substring(data.get(arg0).getPnumber().length()-4)+"´¢Ðî¿¨");	
		hodler.user.setText(AllUtils.hideName(data.get(arg0).getUsername()));
		for (int i = 0; i < banks.length; i++) {
			if (banks[i].equals(data.get(arg0).getPname())) {
				hodler.img.setImageResource(BANKIMG[i]);
			}
		}
		return arg1;
	}
	public void refresh(ArrayList<Bank> data){
		this.data = data;
		notifyDataSetChanged();
	}
	class ViewHodler{
		TextView bank1;
		TextView bank2;
		TextView user;
		ImageView img;
	}
}
