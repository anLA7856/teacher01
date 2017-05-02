package csust.teacher.adapter;

import java.util.List;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.DbException;

import csust.teacher.activity.R;
import csust.teacher.download.DownloadManager;
import csust.teacher.download.DownloadRequestCallBack;
import csust.teacher.download.DownloadService;
import csust.teacher.info.CourseInfo;
import csust.teacher.info.StudentInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.LoadImg;
import csust.teacher.utils.LoadImg.ImageDownloadCallBack;
import csust.teacher.utils.MyJson;

/**
 * mylistview的适配器
 * @author U-anLA
 *
 */
public class MyCourseChatListAdapter extends BaseAdapter{

	private DownloadManager downloadManager ;
	
	private List<CourseInfo> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;
	private boolean upFlag = false;
	private boolean downFlag = false;
	private ProgressDialog mProDialog;
	private MyJson myJson = new MyJson();
	private MyStudentAdapter myStudentAdapter = null;
	
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	
	private int mExpandedMenuPos = -1;
	
	Holder tth = null;
	//获得课程列表下的listview的句柄
	private ListView myHolderListView = null;
	
	//获得学生列表所在的layout
	private LinearLayout myLinearLayout = null;
	
	

	public int getmExpandedMenuPos() {
		return mExpandedMenuPos;
	}
	public void setmExpandedMenuPos(int mExpandedMenuPos) {
		this.mExpandedMenuPos = mExpandedMenuPos;
	}
	public MyCourseChatListAdapter(NotificationManager mNotificationManager,Context ctx, List<CourseInfo> list) {
		mProDialog = new ProgressDialog(ctx);
		mProDialog.setCancelable(true);
		this.list = list;
		this.ctx = ctx;
		//加载图像
		loadImgHeadImg = new LoadImg(ctx);
		
		this.mNotificationManager = mNotificationManager;
		downloadManager = DownloadService.getDownloadManager(ctx);
	}
	public MyCourseChatListAdapter(Context ctx, List<CourseInfo> list) {
		mProDialog = new ProgressDialog(ctx);
		mProDialog.setCancelable(true);
		this.list = list;
		this.ctx = ctx;
		//加载图像
		loadImgHeadImg = new LoadImg(ctx);
		this.mNotificationManager = mNotificationManager;
		downloadManager = DownloadService.getDownloadManager(ctx);
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
		final Holder hold;
		
		
		if(convertView == null){
			hold = new Holder();
			convertView = View.inflate(ctx, R.layout.mycoursechatcistview_item, null);
			hold.teacherPic = (ImageView) convertView.findViewById(R.id.itemTeacherPic);
			hold.teacherNum = (TextView) convertView.findViewById(R.id.itemTeacherNum);
			hold.courseName = (TextView) convertView.findViewById(R.id.itemCourseName);
			hold.teacherName = (TextView) convertView.findViewById(R.id.itemTeacherName);
			hold.myListView = (ListView) convertView.findViewById(R.id.listview_menu_list);
			hold.linearMylistAll = (LinearLayout) convertView.findViewById(R.id.linearMylistAll);
			hold.listview_menu_item_menu = (LinearLayout) convertView.findViewById(R.id.listview_menu_item_menu);
			convertView.setTag(hold);
		}else{
			hold = (Holder) convertView.getTag();
		}
		
		Object b = convertView.getTag();

		hold.teacherNum.setText(list.get(position).getTeacherNum());
		
		hold.teacherName.setText(list.get(position).getTeacherName());
		hold.courseName.setText(list.get(position).getCourseName());
		//设置下拉列表显示
		//hold.myListView.setVisibility(position == mExpandedMenuPos ? View.VISIBLE : View.GONE);

		//设置监听
		hold.teacherPic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//用于和教师会话~，后期实现。
				if(Model.MYUSERINFO != null){
					//已登录
					Toast.makeText(ctx, "嘿嘿嘿", 1).show();
				}else{
					Toast.makeText(ctx, "请先登录才能发送消息哦", 1).show();
				}
			}
		});
		
		hold.linearMylistAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//用于和教师会话~，后期实现。
				//用于展开课程下学生列表
				//首先向服务器发送器你去查询
				//点击的时候保存句柄。
				myHolderListView = hold.myListView;
				tth = hold;
				myLinearLayout = hold.listview_menu_item_menu;
				if(myLinearLayout.getVisibility() == ListView.VISIBLE){
					myLinearLayout.setVisibility(ListView.INVISIBLE);
					
					MyCourseChatListAdapter.this.notifyDataSetChanged();
					
					return;
				}
				if (position == mExpandedMenuPos) {
	                mExpandedMenuPos = -1;
	            } else {
	                mExpandedMenuPos = position;
	            }
	            
				String t = tth.teacherNum.getText().toString();
				
				String getStudentUrl = Model.GETCOURSESTUDENTLIST+"courseId=" + hold.courseName.getText().toString();
				ThreadPoolUtils.execute(new HttpGetThread(hand2, getStudentUrl));
			}
		});
		

		
		
		hold.teacherPic.setImageResource(R.drawable.default_users_avatar);
		if(list.get(position).getTeacherNum().equalsIgnoreCase("")){
			hold.teacherPic.setImageResource(R.drawable.default_users_avatar);
		}else{
			hold.teacherPic.setTag(Model.USERHEADURL + list.get(position).getTeacherNum());
			Bitmap bitTeacher = loadImgHeadImg.loadImage(hold.teacherPic, Model.USERHEADURL+list.get(position).getTeacherNum(), new ImageDownloadCallBack() {
				@Override
				public void onImageDownload(ImageView imageView, Bitmap bitmap) {
					if(position >= list.size()){
						if(hold.teacherPic.getTag().equals(Model.USERHEADURL+list.get(position-1).getTeacherNum())){
							hold.teacherPic.setImageBitmap(bitmap);
						}
					}else{
						if(hold.teacherPic.getTag().equals(Model.USERHEADURL+list.get(position).getTeacherNum())){
							hold.teacherPic.setImageBitmap(bitmap);
						}
					}
				}
			});
			if(bitTeacher != null){
				hold.teacherPic.setImageBitmap(bitTeacher);
			}
		}
		
		return convertView;
		
	}
	
	static class Holder{
		ImageView teacherPic;
		TextView teacherNum;
		TextView courseName;
		TextView teacherName;
		ListView myListView;
		LinearLayout linearMylistAll;
		LinearLayout listview_menu_item_menu;
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
			final StudentHolder hold;
			
			
			if(convertView == null){
				hold = new StudentHolder();
				convertView = View.inflate(ctx, R.layout.mylistview_chat_student_item, null);
				hold.studentPic = (ImageView) convertView.findViewById(R.id.itemStudentPic);
				hold.studentName = (TextView) convertView.findViewById(R.id.itemStudentName);
				
				convertView.setTag(hold);
			}else{
				hold = (StudentHolder) convertView.getTag();
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
			return convertView;
		}
	}
	static class StudentHolder{
		ImageView studentPic;
		TextView studentName;
		
	}
	
	
	/**
	 * 用于获取一门课程下的所有学生列表
	 */
	Handler hand2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
			} else if (msg.what == 200) {
				// 正确的处理逻辑
				String result = (String) msg.obj;
				//获取的应该是一样的，所以myjson可以共用
				List<StudentInfo> list = myJson.getUnsignedStudentsInfo(result);
				//s
				myStudentAdapter = new MyStudentAdapter(list, ctx);
				
				myHolderListView.setAdapter(myStudentAdapter);
				myLinearLayout.setVisibility(ListView.VISIBLE);
				myStudentAdapter.notifyDataSetChanged();
				//课程的adapter更新。
				notifyDataSetChanged();
				
				

			}
		};
	};
	
}













