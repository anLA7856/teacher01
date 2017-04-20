package csust.teacher.adapter;

import java.util.List;

import csust.teacher.activity.R;
import csust.teacher.info.SignInfo;
import csust.teacher.model.Model;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 签到记录的适配器
 * @author U-anLA
 *
 */
public class MySignListAdapter extends BaseAdapter {

	private List<SignInfo> list;
	private Context ctx;

	public MySignListAdapter(Context ctx, List<SignInfo> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SignHolder hold;

		if (convertView == null) {
			hold = new SignHolder();
			convertView = View.inflate(ctx, R.layout.mysignlistview_item, null);

			hold.signDate = (TextView) convertView
					.findViewById(R.id.itemSignDate);
			hold.courseNum = (TextView) convertView
					.findViewById(R.id.itemSignCourseNum);
			hold.courseName = (TextView) convertView
					.findViewById(R.id.itemSignCourseName);
			hold.teacherName = (TextView) convertView
					.findViewById(R.id.itemSignTeacherName);
			hold.mLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.linearMySignlistAll);
			convertView.setTag(hold);
		} else {
			hold = (SignHolder) convertView.getTag();
		}

		hold.courseNum.setText(list.get(position).getSign_courseNum());
		hold.signDate.setText(list.get(position).getSign_date());
		hold.teacherName.setText(list.get(position).getSign_teacherName());
		hold.courseName.setText(list.get(position).getSign_courseName());
		// 设置监听
		// 在beginsignfragment中设置监听。
		// hold.mLinearLayout.setOnClickListener(new View.OnClickListener() {
		//
		// @SuppressLint("ShowToast")
		// @Override
		// public void onClick(View v) {
		// //用于点击进入进行实际的签到。
		// if(Model.MYUSERINFO != null){
		// //已登录
		// }else{
		// Toast.makeText(ctx, "请先登录才能进行签到喔", 1).show();
		// }
		// }
		// });

		return convertView;

	}

	static class SignHolder {
		TextView signDate;
		TextView courseNum;
		TextView courseName;
		TextView teacherName;
		LinearLayout mLinearLayout;
	}

}
