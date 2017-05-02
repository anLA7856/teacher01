package csust.teacher.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.activity.R;
import csust.teacher.info.CourseStudentListInfo;

/**
 * mylistview的适配器
 * @author U-anLA
 *
 */
public class MyCourseChatListAdapter extends BaseExpandableListAdapter{

	public List<CourseStudentListInfo> list = null;

	private Context context;

	public MyCourseChatListAdapter(List<CourseStudentListInfo> list,
			Context context) {  //初始化数据

		this.context = context;
		this.list = list;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).getList().get(childPosition);   //获取父类下面的每一个子类项
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;  //子类位置
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) { //显示子类数据的iew
		View view = null;
		view = LayoutInflater.from(context).inflate(
				R.layout.mylistview_chat_student_item, null);
		TextView textView = (TextView) view
				.findViewById(R.id.itemStudentName);
		textView.setText(list.get(groupPosition).getList().get(childPosition).getStudent_name().toString());
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return list.get(groupPosition).getList().size();  //子类item的总数
	}

	@Override
	public Object getGroup(int groupPosition) {   //父类数据
		return list.get(groupPosition).getCourse();
	}

	@Override
	public int getGroupCount() {
		return list.size();  ////父类item总数
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;   //父类位置
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.mycoursechatcistview_item, null);
		TextView textView = (TextView) view.findViewById(R.id.itemCourseName);
		textView.setText(list.get(groupPosition).getCourse().getCourse_name().toString());
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {  //点击子类触发事件
		Toast.makeText(context,
				"第" + groupPosition + "大项，第" + childPosition + "小项被点击了",
				Toast.LENGTH_LONG).show();
		return true;

	}
	
}













