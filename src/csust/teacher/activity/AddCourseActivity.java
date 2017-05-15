package csust.teacher.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.thread.HttpPostThread;
import csust.teacher.utils.CharacterUtil;

/**
 * 添加课程的activity，实现了触摸监听事件
 * 
 * @author anLA7856
 *
 */
public class AddCourseActivity extends Activity implements OnClickListener {

	// 关闭按钮
	private ImageView mClose;
	// 上传按钮
	private RelativeLayout mUpload;
	// 课程名和课程描述
	private EditText mCourseName, mCourseDespription;

	// 用于上传课程信息的url
	private String url = null;

	// 用于保存coursename和description
	private String courseName, description;

	// 用于定义记录框
	private ProgressDialog mProDialog;

	// value，用来存post的json格式的字符串。
	private String value = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除activity首部的一条
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 加载界面
		setContentView(R.layout.activity_upload);
		initView();
	}

	/**
	 * 用于初始化界面
	 */
	private void initView() {
		mProDialog = new ProgressDialog(this);
		// 获得一一对应的值
		mClose = (ImageView) findViewById(R.id.upload_close);
		mUpload = (RelativeLayout) findViewById(R.id.upload_upload);
		mCourseName = (EditText) findViewById(R.id.upload_coursename);
		mCourseDespription = (EditText) findViewById(R.id.upload_description);
		// 为关闭和上传添加事件
		mClose.setOnClickListener(this);
		mUpload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.upload_close:
			// 关闭就直接关闭
			finish();
			break;
		case R.id.upload_upload:
			// 上传事件
			// 获得课程名和课程描述
			courseName = mCourseName.getText().toString().trim();
			description = mCourseDespription.getText().toString().trim();
			// 进行相应的判断，以及检测
			if (description.length() > 20) {
				Toast.makeText(AddCourseActivity.this, "描述不能超过20个字", 1).show();
				break;
			}
			if (courseName.length() > 10) {
				Toast.makeText(AddCourseActivity.this, "课程名字不能超过10个字", 1)
						.show();
				break;
			}
			if (courseName.trim().equals("")) {
				Toast.makeText(AddCourseActivity.this, "课程名字不能为空", 1).show();
				break;
			}

			if (!new CharacterUtil().checkString(courseName.trim())) {
				Toast.makeText(AddCourseActivity.this, "课程名字不能包含非法字符", 1)
						.show();
				break;
			}

			mProDialog.setTitle("创建课程中。。。");
			mProDialog.show();
			// 拼接url
			value = "{\"teacher_id\":\"" + Model.MYUSERINFO.getTeacher_id()
					+ "\",\"course_name\":\"" + courseName
					+ "\",\"description\":\"" + description + "\"}";

			// url = Model.UPLOADNEWCOURSE + "teacher_id="
			// + Model.MYUSERINFO.getTeacher_id() + "&course_name="
			// + courseName + "&description=" + description;
			// 改为post方式。
			ThreadPoolUtils.execute(new HttpPostThread(hand,
					Model.UPLOADNEWCOURSE, value));
			// ThreadPoolUtils.execute(new HttpGetThread(hand, url));
			break;
		default:
			break;
		}
	}

	// 用于处理上传后成功的handler
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProDialog.dismiss();
			super.handleMessage(msg);
			if (msg.what == 404) {
				// 请求失败的逻辑
				Toast.makeText(AddCourseActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				// 服务器问题
				Toast.makeText(AddCourseActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				// 处理成功的逻辑
				String result = msg.obj.toString();
				if (result.equals("[1]")) {
					// 添加成功！
					Toast.makeText(AddCourseActivity.this, "添加课程信息成功！", 1)
							.show();
					finish();
				} else {
					// 添加失败！
					Toast.makeText(AddCourseActivity.this, "添加失败！", 1).show();
				}

			}
		};
	};
}
