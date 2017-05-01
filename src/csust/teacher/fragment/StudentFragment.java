package csust.teacher.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.activity.CourseDetailActivity;
import csust.teacher.activity.R;
import csust.teacher.adapter.MyCourseChatListAdapter;
import csust.teacher.adapter.MyListAdapter;
import csust.teacher.info.CourseInfo;
import csust.teacher.info.StudentInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.refresh.PullToRefreshLayout;
import csust.teacher.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.teacher.refresh.view.PullableListView;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.MyJson;
import csust.teacher.utils.LoadImg.ImageDownloadCallBack;

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
	private List<CourseInfo> list = new ArrayList<CourseInfo>();
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

	private PullableListView listView;
	
	//用于显示一门课的学生，
	private ListView studentListView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	private NotificationManager mNotificationManager;
	
	public StudentFragment(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_course, null);
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
		mLinearLayout = (LinearLayout) view.findViewById(R.id.HomeGroup);

		mTopImg = (ImageView) view.findViewById(R.id.Menu);
		mSendAshamed = (ImageView) view.findViewById(R.id.SendAshamed);
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);

		((PullToRefreshLayout) view.findViewById(R.id.refresh_view))
				.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView) view.findViewById(R.id.content_view);

		mTopImg.setOnClickListener(this);
		mSendAshamed.setOnClickListener(this);
		HomeNoValue.setVisibility(View.GONE);
		mAdapter = new MyCourseChatListAdapter(mNotificationManager,ctx, list);

		if (Model.MYUSERINFO != null) {
			isFirst = true;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getTeacher_id() + "&start=" + mStart
					+ "&count=" + Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}

		listView.setAdapter(mAdapter);
//		listView.setOnItemClickListener(new MainListOnItemClickListener());
//		listView.setOnItemLongClickListener(new MainListOnItemClickListener());

	}

	Handler hand = new Handler() {
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
				if (pullToRefreshLayout != null) {
					pullToRefreshLayout
							.refreshFinish(PullToRefreshLayout.SUCCEED);
				}
				String result = (String) msg.obj;
				if (isFirst == true) {
					// 清空
					if (list != null) {
						list.removeAll(list);
					}
				}
				List<CourseInfo> newList = myJson.getCourseInfoList(result);
				if (newList.size() != 0) {

					for (CourseInfo t : newList) {
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

//	private class MainListOnItemClickListener implements OnItemClickListener,
//			OnItemLongClickListener {
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			Toast.makeText(ctx, "嘿嘿嘿", 1).show();
//			//用于点击事件，点一下，就展开下拉列表，是本门课程的学生。
//			//首先向服务器发送器你去查询
//			String getStudentUrl = Model.GETCOURSESTUDENTLIST+"courseId=" + list.get(arg2).getCourseName();
//			ThreadPoolUtils.execute(new HttpGetThread(hand2, getStudentUrl));
//			//			Intent intent = new Intent(ctx, CourseDetailActivity.class);
////			Bundle bund = new Bundle();
////			bund.putSerializable("courseInfo", list.get(arg2));
////			// intent.putExtra("value", bund);
////			intent.putExtras(bund);
////			startActivity(intent);
//
//		}
//
//		@Override
//		public boolean onItemLongClick(AdapterView<?> parent, View view,
//				int position, long id) {
//
//			final int myPosition = position;
//			new AlertDialog.Builder(ctx)
//					.setTitle("删除提示框")
//					.setMessage("确认删除本门课程(相关的课程记录和签到记录均会删除！！)")
//					.setPositiveButton("确定",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// 用于删除某一门课程！courseName就是course_id
//									String url1 = Model.TEADELETECOURSE
//											+ "course_id="
//											+ list.get(myPosition)
//													.getCourseName();
//
//									ThreadPoolUtils.execute(new HttpGetThread(
//											hand1, url1));
//
//								}
//							}).setNegativeButton("取消", null).show();
//			// 注意这里是防止再次出发单词点击实际，如果是false，就会出发单词短点击事件
//			return true;
//		}
//	}

	/**
	 * 用于删除某一门课程
	 */
	Handler hand1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 200) {
				// 正确的处理逻辑
				String result = (String) msg.obj;

				if (result.equals("[1]")) {
					// 说明删除成功！
					Toast.makeText(ctx, "删除成功", 1).show();
					// 这里还需要刷新
					list.removeAll(list);
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
				} else {
					Toast.makeText(ctx, "删除失败！！！", 1).show();
					// 说明删除失败！
				}

			}
		};
	};

	/**
	 * 用于获取一门课程下的所有学生列表
	 */
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
				// 正确的处理逻辑
				String result = (String) msg.obj;
				//获取的应该是一样的，所以myjson可以共用
				List<StudentInfo> list = myJson.getUnsignedStudentsInfo(result);
				//s
				

			}
		};
	};
	
	
	
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
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getTeacher_id() + "&start=" + mStart
					+ "&count=" + Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
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

	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			StudentFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getTeacher_id() + "&start=" + mStart
					+ "&count=" + Model.INIT_COUNT;

			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

		
		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			StudentFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getTeacher_id() + "&start=" + mStart
					+ "&count=" + 5;

			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

	}
	
	/**
	 * 用于适配课程下面的学生列表
	 * @author U-ANLA
	 *
	 */
	private class MyStudentAdapter extends BaseAdapter{

		private List<StudentInfo> list;
		private Context ctx;
		public MyStudentAdapter(List<StudentInfo> list,Context ctx) {
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
			final StudentFragment.Holder hold;
			
			
			if(convertView == null){
				hold = new Holder();
				convertView = View.inflate(ctx, R.layout.mylistview_chat_student_item, null);
				hold.studentPic = (ImageView) convertView.findViewById(R.id.itemStudentPic);
				hold.studentName = (TextView) convertView.findViewById(R.id.itemStudentName);
				
				convertView.setTag(hold);
			}else{
				hold = (Holder) convertView.getTag();
			}
			Object b = convertView.getTag();
			//头像暂时没有设置
			hold.studentName.setText(list.get(position).getStudent_name());
			
			
			//设置监听
			hold.studentPic.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//用于和教师会话~，后期实现。
					Toast.makeText(ctx, "查看大图后期实现", 1).show();
				}
			});
			

			
			//学生各自头像。
		//	hold.teacherPic.setImageResource(R.drawable.default_users_avatar);
//			if(list.get(position).getTeacherNum().equalsIgnoreCase("")){
//				hold.teacherPic.setImageResource(R.drawable.default_users_avatar);
//			}else{
//				hold.teacherPic.setTag(Model.USERHEADURL + list.get(position).getTeacherNum());
//				Bitmap bitTeacher = loadImgHeadImg.loadImage(hold.teacherPic, Model.USERHEADURL+list.get(position).getTeacherNum(), new ImageDownloadCallBack() {
//					@Override
//					public void onImageDownload(ImageView imageView, Bitmap bitmap) {
//						if(position >= list.size()){
//							if(hold.teacherPic.getTag().equals(Model.USERHEADURL+list.get(position-1).getTeacherNum())){
//								hold.teacherPic.setImageBitmap(bitmap);
//							}
//						}else{
//							if(hold.teacherPic.getTag().equals(Model.USERHEADURL+list.get(position).getTeacherNum())){
//								hold.teacherPic.setImageBitmap(bitmap);
//							}
//						}
//					}
//				});
//				if(bitTeacher != null){
//					hold.teacherPic.setImageBitmap(bitTeacher);
//				}
//			}
			
			return convertView;
		}
		
		
		
	}
	static class Holder{
		ImageView studentPic;
		TextView studentName;
		
	}

}
