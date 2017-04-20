package csust.teacher.adapter;

import java.util.List;

import csust.teacher.activity.R;
import csust.teacher.adapter.MySignListAdapter.SignHolder;
import csust.teacher.info.SignNameInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用于展示签到结果列表的list列表
 * 
 * @author U-anLA
 *
 */
public class MySignInfoListAdapter extends BaseAdapter {

	private List<SignNameInfo> list;
	private Context ctx;

	public MySignInfoListAdapter(List<SignNameInfo> list, Context ctx) {
		super();
		this.list = list;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 用于获得view
		final SignNameInfoHolder hold;
		if (convertView == null) {
			hold = new SignNameInfoHolder();
			convertView = View.inflate(ctx, R.layout.mylistview_item_have_sign,
					null);

			hold.userImage = (ImageView) convertView
					.findViewById(R.id.sign_student_Pic);

			hold.signDate = (TextView) convertView
					.findViewById(R.id.havasign_signdate);

			hold.studentName = (TextView) convertView
					.findViewById(R.id.havasign_studentName);
			
			hold.signState = (TextView) convertView.findViewById(R.id.havasign_state);

			convertView.setTag(hold);
		} else {
			hold = (SignNameInfoHolder) convertView.getTag();
		}

		// 设置签到日期
		hold.signDate.setText(list.get(position).getSign_time());
		// 设置姓名
		hold.studentName.setText(list.get(position).getStudentName());
		//设置签到状态
		hold.signState.setText(list.get(position).getSign_state());
		
		// 设置图片，这里暂时没有设置
		

		return convertView;
	}

	/**
	 * 用于保存签到名字的item，list
	 * 
	 * @author U-anLA
	 *
	 */
	static class SignNameInfoHolder {
		TextView studentName;
		TextView signDate;

		ImageView userImage;
		TextView signState;
	}

}
