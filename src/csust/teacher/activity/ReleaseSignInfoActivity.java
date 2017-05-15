package csust.teacher.activity;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import csust.teacher.info.CourseInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.MyJson;

/**
 * 释放，也就是新开启签到信息的activity。
 * 
 * @author anLA7856
 *
 */
public class ReleaseSignInfoActivity extends Activity implements
		OnClickListener {

	// 下拉框，用于选择课程。
	private Spinner mSpinner;
	// 时间选择器。
	private TimePicker mTimePicker;
	// 登录的button。
	private Button mButton;
	// 获得course列表的url
	private String url = null;
	// 用于上传数据的url
	private String urlUpdate = null;
	// 用于存储courseid和signdate。
	private String courseId = null, signDate = null, signTime = null;

	// myjson的解析类
	private MyJson myJson = new MyJson();
	// 用于添加到spinner的adapter
	private ArrayAdapter<String> adapter = null;

	private TextView mTextView;

	//
	private ProgressDialog proDialog = null;

	private Date nowDate = new Date();

	private java.sql.Date passDate = null;

	// close
	private ImageView mClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_release_sign);
		initView();
	}

	private void initView() {

		mSpinner = (Spinner) findViewById(R.id.release_sign_Spinner_item);
		mTimePicker = (TimePicker) findViewById(R.id.release_sign_Picker_date);
		mButton = (Button) findViewById(R.id.release_sign_submit);
		mTextView = (TextView) findViewById(R.id.release_sign_data);
		mClose = (ImageView) findViewById(R.id.Close);
		mClose.setOnClickListener(this);
		// 给mbutton添加事件
		mButton.setOnClickListener(this);
		url = Model.GETTEACOURSE + "username="
				+ Model.MYUSERINFO.getTeacher_id();
		// 用于获得该教师所有课程
		ThreadPoolUtils.execute(new HttpGetThread(hand1, url));
		// 用于设置现在的日期
		mTextView.setText("选择时间：" + new Date());
		// 用于设置为24小时制
		mTimePicker.setIs24HourView(true);
		// 设置processdialog
		proDialog = new ProgressDialog(this);
		proDialog = new ProgressDialog(this);
		proDialog.setCancelable(true);
		proDialog.setTitle("请稍后");
		// 首先提示
		proDialog.setMessage("正在获取数据");
		proDialog.show();

		// 初始化signdate
		signDate = nowDate.getHours() + ":" + nowDate.getMinutes() + ":"
				+ nowDate.getSeconds();
		passDate = new java.sql.Date(nowDate.getTime());
		signDate = passDate.toString().trim();
		signTime = nowDate.getHours() + ":" + nowDate.getMinutes() + ":"
				+ nowDate.getSeconds();

		mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				signTime = hourOfDay + ":" + minute + ":00";
				// passDate = new java.sql.Date(new Date(new
				// Date().getYear(),new Date().getMonth(),new
				// Date().getDay(),hourOfDay,minute).getTime());
				signDate = passDate.toString().trim();
				// signTime= sign
			}
		});

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.Close:
			finish();
			break;

		case R.id.release_sign_submit:
			// 用于提交数据
			// 给出友好提示
			proDialog.setMessage("添加中。。。");
			proDialog.show();
			// 提交数据
			courseId = mSpinner.getSelectedItem().toString().split(":")[2]
					.trim();
			urlUpdate = Model.UPDATESIGNINFO + "courseId=" + courseId
					+ "&date=" + signDate + "&date2=" + signTime;
			ThreadPoolUtils.execute(new HttpGetThread(hand2, urlUpdate));
			break;

		default:
			break;
		}
	}

	// 用于处理获得课程并显示到spinner的hander
	Handler hand1 = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 404) {
				Toast.makeText(ReleaseSignInfoActivity.this, "请求失败，服务器故障", 1)
						.show();
			} else if (msg.what == 100) {
				Toast.makeText(ReleaseSignInfoActivity.this, "服务器无响应", 1)
						.show();
			} else if (msg.what == 200) {
				// 正确处理逻辑
				String result = (String) msg.obj;
				List<CourseInfo> newList = myJson.getCourseInfoList(result);
				// 初始化spinner
				if (newList.size() == 0) {
					Toast.makeText(ReleaseSignInfoActivity.this, "请先至少添加一门课程",
							1).show();
					finish();
				}
				initSpinnerText(newList);
				ReleaseSignInfoActivity.this.proDialog.dismiss();
			}
		}

		private void initSpinnerText(List<CourseInfo> newList) {
			String[] strs = new String[newList.size()];
			for (int i = 0; i < strs.length; i++) {
				// teacherName就是courseName,teacherNum就是courseNum，而courseName就是courseid
				strs[i] = newList.get(i).getTeacherNum() + ":  "
						+ newList.get(i).getTeacherName() + ":  "
						+ newList.get(i).getCourseName();
			}

			if (strs != null) {

				adapter = new ArrayAdapter<String>(
						ReleaseSignInfoActivity.this,
						android.R.layout.simple_spinner_item, strs);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpinner.setAdapter(adapter);
			}
		};
	};

	/**
	 * 用于处理提交信息的handler
	 */
	Handler hand2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 404) {
				Toast.makeText(ReleaseSignInfoActivity.this, "请求失败，服务器故障", 1)
						.show();
			} else if (msg.what == 100) {
				Toast.makeText(ReleaseSignInfoActivity.this, "服务器无响应", 1)
						.show();
			} else if (msg.what == 200) {
				// 成功的返回！
				proDialog.dismiss();
				String result = (String) msg.obj;
				if (result.trim().equals("[1]")) {
					Toast.makeText(ReleaseSignInfoActivity.this, "添加成功！", 1)
							.show();
				} else if (result.trim().equals("[0]")) {
					Toast.makeText(ReleaseSignInfoActivity.this, "抱歉，添加失败！", 1)
							.show();
				}

				ReleaseSignInfoActivity.this.finish();

			}
		};
	};

}
