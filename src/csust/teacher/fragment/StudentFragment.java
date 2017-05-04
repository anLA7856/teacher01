package csust.teacher.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.activity.ChatActivity;
import csust.teacher.activity.R;
import csust.teacher.adapter.MyCourseChatListAdapter;
import csust.teacher.info.CourseStudentListInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.MyJson;

/**
 * 查看的课程列表的fragment
 * 
 * @author U-anLA
 *
 */

public class StudentFragment extends Fragment implements OnClickListener {

	private View view;
	private ImageView mTopImg;
	private ImageView mSendAshamed;
	private TextView mTopMenuOne;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private StudentFragmentCallBack mStudentFragmentCallBack;
	private MyJson myJson = new MyJson();
	private List<CourseStudentListInfo> list = new ArrayList<CourseStudentListInfo>();
	private MyCourseChatListAdapter mAdapter = null;
	private int mStart = 0;
	private int mEnd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottomFlag = true;
	private Context ctx;

	// 设置onpause时间标志
	private boolean isPause = false;

	private ExpandableListView listView; //控件
	
	//用于显示一门课的学生，
	private ListView studentListView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;


	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_student, null);
		ctx = view.getContext();
		// 这是鸡肋，可能需要改！！！！！！
		if (list != null) {
			list.removeAll(list);
		}
		initView();
		return view;
	}

	private void initView() {
		load_progressBar = (LinearLayout) view
				.findViewById(R.id.load_progressBar);
		//主界面
		mLinearLayout = (LinearLayout) view.findViewById(R.id.HomeGroup);

		mTopImg = (ImageView) view.findViewById(R.id.Menu);
		mSendAshamed = (ImageView) view.findViewById(R.id.SendAshamed);
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
		listView = (ExpandableListView) view.findViewById(R.id.all);



		mTopImg.setOnClickListener(this);
		mSendAshamed.setOnClickListener(this);
		HomeNoValue.setVisibility(View.GONE);
		mAdapter = new MyCourseChatListAdapter(list,ctx);

		if (Model.MYUSERINFO != null) {
			isFirst = true;
			//通过教师号，查询到所有课程以及课程下
			String getStudentUrl = Model.GETCOURSESTUDENTLIST+"teacherId=" + Model.MYUSERINFO.getTeacher_id();
			ThreadPoolUtils.execute(new HttpGetThread(hand2, getStudentUrl));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}

		listView.setAdapter(mAdapter);
		
		listView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//点了我一下,然后这里就从这里进入聊天界面，应该可以从v里面获得courseid的，我估计，明天写
				
				Intent intent = new Intent(ctx, ChatActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("teacherId", Model.LIST.get(groupPosition).getList().get(childPosition).getStudent_id());

				// 这句暂时不嫩共。
				intent.putExtra("value", bund);
				startActivity(intent);
				Toast.makeText(ctx, "点了我一下"+groupPosition+":"+childPosition, 1).show();
				return true;
			}
		});

	}

	Handler hand2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 200) {
				load_progressBar.setVisibility(View.GONE);
				
				String result = (String) msg.obj;
				if (isFirst == true) {
					// 清空
					if (list != null) {
						list.removeAll(list);
					}
				}
				List<CourseStudentListInfo> newList = myJson.getCourseStudentListInfo(result);
				//存起来
				Model.LIST = null;
				Model.LIST = newList;
				if (newList.size() != 0) {

					for (CourseStudentListInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(ctx, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有教师信息记录情况");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);
					}
				}

				mAdapter.notifyDataSetChanged();

			}
			mAdapter.notifyDataSetChanged();
		};
	};

	public void setCallBack(StudentFragmentCallBack mStudentFragmentCallBack) {
		this.mStudentFragmentCallBack = mStudentFragmentCallBack;
	}

	public interface StudentFragmentCallBack {
		public void callback(int flag);
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.Menu:
			mStudentFragmentCallBack.callback(R.id.Menu);
			break;
		case R.id.SendAshamed:
			mStudentFragmentCallBack.callback(R.id.SendAshamed);
			break;
		default:
			break;
		}
	}



	
	
	
	@Override
	public void onResume() {
		super.onResume();
		if (isPause == false) {
			return;
		}

		// 每次onresum时候，就要把homenovalue设为false
		mStart = 0;
		HomeNoValue.setVisibility(View.GONE);

		if (list.size() != 0) {
			list.removeAll(list);
		}
		if (Model.MYUSERINFO != null) {
			isFirst = true;
			//改成和上面一样，不要下拉刷新之类的了
			String getStudentUrl = Model.GETCOURSESTUDENTLIST+"teacherId=" + Model.MYUSERINFO.getTeacher_id();
			ThreadPoolUtils.execute(new HttpGetThread(hand2, getStudentUrl));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	

}
