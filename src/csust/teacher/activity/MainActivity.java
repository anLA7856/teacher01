package csust.teacher.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import csust.teacher.fragment.CourseFragment;
import csust.teacher.fragment.CourseFragment.CourseFragmentCallBack;
import csust.teacher.fragment.DownloadFragment;
import csust.teacher.fragment.DownloadFragment.DownloadFragmentCallBack;
import csust.teacher.fragment.ReleaseSignFragment;
import csust.teacher.fragment.ReleaseSignFragment.BeginSignFragmentCallBack;
import csust.teacher.fragment.StudentFragment;
import csust.teacher.fragment.StudentFragment.StudentFragmentCallBack;
import csust.teacher.info.UserInfo;
import csust.teacher.model.Model;
import csust.teacher.utils.MyJson;

/**
 * 主界面的mainactivity，包括使用第三方组件slidingfragmentactivity
 * 
 * @author anLA7856
 *
 */
public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener {
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	// private KFSettingsManager mSettingsMgr;
	// 左边的抽屉类
	private View mLeftView;
	// 第三方抽屉菜单管理工具类
	private SlidingMenu mSlidingMenu;
	// 开始签到的碎片
	private ReleaseSignFragment mBeginSignFragment;
	// 课程管理的碎片
	private CourseFragment mCourseFragment;
	// 正在下载的碎片
	private DownloadFragment mDownloadFragment;
	// 学生，即联系人的碎片
	private StudentFragment mStudentFragment;
	// 定义fragment管理器：
	private FragmentManager mFragmentManager;
	// 获取fragment栈
	private android.support.v4.app.FragmentTransaction mFragmentTransaction;
	private List<Fragment> myFragmentList = new ArrayList<Fragment>();
	// leftView里面的控件
	private LinearLayout mLoginThisApp;// 用户登录用户
	private TextView myUserName;
	private ImageView mSettingBtn; // 设置按钮
	// leftview中下面的按钮
	private RelativeLayout mLeftSign, mLeftCourse, mLeftDownload, mLeftStudent;
	private int fragmentFlag = 0;
	// json解析类
	private MyJson myjson = new MyJson();

	// 判断是否退出
	private boolean isExist;

	// 用来存储myuserinfo
	private UserInfo myOldUserInfo;

	private static int target = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initService();
		// 用于初始化界面
		initView();
		login();
		// 从本地数据库中得到用户信息。
		SharedPreferences sp = MainActivity.this.getSharedPreferences(
				"UserInfo", MODE_PRIVATE);
		String result = sp.getString("UserInfoJson", "none");
		Log.e("SharedPreferencesOld", result);
		// 得到后，将其从在一个全局变量中
		File file = new File(Model.LOCALSTORAGE);
		if (!file.exists()) {
			// 建立自己的根目录
			file.mkdir();
		}
		File file2 = new File(Model.LOCALSTORAGE + "download/");
		if (!file2.exists()) {
			file2.mkdir();
		}
		if (!result.equals("none")) {
			// 如果有值，就用myjson来解析并存储
			List<UserInfo> newList = myjson.getUserInfoList(result);
			if (newList != null) {
				Model.MYUSERINFO = newList.get(0);
				myOldUserInfo = Model.MYUSERINFO;
				myUserName.setText(Model.MYUSERINFO.getTeacher_username());

			}
		}

	}

	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	// @SuppressLint("NewApi")
	// @Override
	// protected void onResume() {
	// // 用于从登录界面出来后的状况的时间
	// //如果回来后，没有用户名密码了，就必须重新登录。
	// super.onResume();
	//
	// if(target==0){
	// return;
	// }
	//
	// if(Model.MYUSERINFO == null){
	// //直接注销登录，后返回界面
	// //需要清除界面痕迹
	// System.out.println();
	// mLeftSign.callOnClick();
	// mLeftCourse.callOnClick();
	// }else if(Model.MYUSERINFO != null && myOldUserInfo != null){
	// //没有注销
	// if(!(myOldUserInfo.getTeacher_username().equals(Model.MYUSERINFO.getTeacher_username()))){
	// //登陆了不同的账号了，和老账号不同。
	// //需要清除痕迹
	// mLeftSign.callOnClick();
	// mLeftCourse.callOnClick();
	// }
	//
	// }
	//
	// }
	//
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	// target=1;
	// }
	//

	private void login() {
		// 检查 用户名/密码 是否已经设置,如果已经设置，则登录

	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 获取相应的控件
		// 获得左边的view
		mLeftView = View.inflate(MainActivity.this, R.layout.leftview, null);
		// 初始化左边view控件
		mLoginThisApp = (LinearLayout) mLeftView
				.findViewById(R.id.LoginThisAPP);
		mSettingBtn = (ImageView) mLeftView.findViewById(R.id.SettingBtn);
		mLeftCourse = (RelativeLayout) mLeftView.findViewById(R.id.LeftCourse);
		mLeftStudent = (RelativeLayout) mLeftView
				.findViewById(R.id.LeftStudent);
		mLeftSign = (RelativeLayout) mLeftView.findViewById(R.id.LeftSign);
		mLeftDownload = (RelativeLayout) mLeftView
				.findViewById(R.id.LeftDownload);
		myUserName = (TextView) mLeftView.findViewById(R.id.myUserName);

		// 给左边的view添加相应监听事件。
		mLoginThisApp.setOnClickListener(MainActivity.this);
		mSettingBtn.setOnClickListener(MainActivity.this);
		mLeftCourse.setOnClickListener(MainActivity.this);
		mLeftStudent.setOnClickListener(MainActivity.this);
		mLeftSign.setOnClickListener(MainActivity.this);
		mLeftDownload.setOnClickListener(MainActivity.this);

		// 添加背景被点击的事件。
		mLeftCourse
				.setBackgroundResource(R.drawable.side_menu_background_active);
		// 课程的fragment
		mCourseFragment = new CourseFragment(mNotificationManager);
		//
		mStudentFragment = new StudentFragment();
		// 把fragment添加到list中
		myFragmentList.add(mCourseFragment);

		// 签到的fragment
		mBeginSignFragment = new ReleaseSignFragment();
		// 下载列表的fragment
		mDownloadFragment = new DownloadFragment();

		// 把fragment添加到list中
		myFragmentList.add(mBeginSignFragment);
		myFragmentList.add(mDownloadFragment);
		myFragmentList.add(mStudentFragment);
		// 用开源的第三方组件来构造效果
		// com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
		mSlidingMenu = this.getSlidingMenu();

		// 设置在后方的view，为mleftview
		this.setBehindContentView(mLeftView);

		// 设置相应的slidingmenu的相应参数。
		mSlidingMenu.setShadowDrawable(R.drawable.drawer_shadow);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		// 设置mfragmentManager
		mFragmentManager = MainActivity.this.getSupportFragmentManager();
		// 开启mfragmentManager的事务。
		mFragmentTransaction = mFragmentManager.beginTransaction();
		// 供跳转
		createFragment(2);
		createFragment(1);

		// fragmenttransaction
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		mFragmentTransaction.replace(R.id.main, mCourseFragment);
		mFragmentTransaction.commit();

	}

	/**
	 * 设置加载，而产生的fragment
	 * 
	 * @param flag
	 */
	private void createFragment(int flag) {
		// 如果正在加载的fragment是传过来的，那么就不操作，否则去加载
		MainActivity.this.toggle();
		if (fragmentFlag != flag) {
			switch (flag) {
			case 1: // 课程管理的fragment
				mCourseFragmentCallBack();
				break;
			case 2: // 签到的fragment
				mBeginSignFragmentCallBack();
				break;
			case 3: // 下载的fragment
				mDownloadFragmentCallBack();
				break;
			case 4:
				mStudentFragmentCallBack();
				break;
			}

			if (fragmentFlag != 0) {
				mFragmentTransaction.remove(myFragmentList
						.get(fragmentFlag - 1));
			}
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.replace(R.id.main,
					myFragmentList.get(flag - 1));
			// 提交保存替换或者添加fragment
			mFragmentTransaction.commit();
			fragmentFlag = flag;
		}
	}

	/**
	 * 从mcoursefragment里面毁掉回来的事件监听设置方法
	 */
	private void mCourseFragmentCallBack() {
		mCourseFragment.setCallBack(new MyCourseFragmentCallBack());
	}

	private void mStudentFragmentCallBack() {
		mStudentFragment.setCallBack(new MyStudentFragmentCallBack());
	}

	/**
	 * 从mbeginsignfragment里面回调回来的事件监听设置方法
	 */
	private void mBeginSignFragmentCallBack() {
		mBeginSignFragment.setCallBack(new MyBeginSignFragmentCallBack());
	}

	/**
	 * 从mdownloadfragment里面回调回来的事件监听设置方法
	 */
	private void mDownloadFragmentCallBack() {
		mDownloadFragment.setCallBack(new MyDownloadFragmentCallBack());
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.LoginThisAPP:
			if (Model.MYUSERINFO != null) {
				Intent intent = new Intent(MainActivity.this,
						UserInfoActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("UserInfo", Model.MYUSERINFO);
				intent.putExtra("value", bund);
				startActivity(intent);
			} else {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.SettingBtn:
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.LeftCourse:
			createleftviewbg();
			mLeftCourse
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFragment(1);
			break;
		case R.id.LeftSign:
			createleftviewbg();
			mLeftSign
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFragment(2);
			break;

		case R.id.LeftDownload:
			createleftviewbg();
			mLeftDownload
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFragment(3);
			break;
		case R.id.LeftStudent:
			createleftviewbg();
			mLeftStudent
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFragment(4);
			break;
		default:
			break;
		}
	}

	// 设置leftview控件的默认背景色
	private void createleftviewbg() {
		mLeftCourse.setBackgroundResource(R.drawable.leftview_list_bg);
		mLeftSign.setBackgroundResource(R.drawable.leftview_list_bg);
		mLeftDownload.setBackgroundResource(R.drawable.leftview_list_bg);
		mLeftStudent.setBackgroundResource(R.drawable.leftview_list_bg);
	}

	@Override
	protected void onStart() {
		if (Model.MYUSERINFO != null) {
			myUserName.setText(Model.MYUSERINFO.getTeacher_username());
			// 这里没写
			// KFIMInterfaces.setVCardField(MainActivity.this, "NICKNAME",
			// Model.MYUSERINFO.getUname());

		} else {
			myUserName.setText("登录益签到  教师端");
		}
		super.onStart();
	}

	/**
	 * 实现coursefragment接口类
	 * 
	 * @author U-anLA
	 *
	 */
	private class MyCourseFragmentCallBack implements CourseFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;

			case R.id.SendAshamed:
				if (Model.MYUSERINFO != null) {
					Intent intent = new Intent(MainActivity.this,
							AddCourseActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 实现studentFragment接口类
	 * 
	 * @author U-anLA
	 *
	 */
	private class MyStudentFragmentCallBack implements StudentFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;

			case R.id.SendAshamed:
				if (Model.MYUSERINFO != null) {
					Intent intent = new Intent(MainActivity.this,
							AddCourseActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 实现接口子类。
	 * 
	 * @author U-anLA
	 *
	 */
	private class MyBeginSignFragmentCallBack implements
			BeginSignFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;
			// 这里就没有上一个的那个加入新的东西的模块
			default:
				break;
			}
		}

	}

	private class MyDownloadFragmentCallBack implements
			DownloadFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;
			// 这里就没有上一个的那个加入新的东西的模块
			default:
				break;
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void exit() {
		if (!isExist) {
			isExist = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			isExist = false;
		};
	};

}
