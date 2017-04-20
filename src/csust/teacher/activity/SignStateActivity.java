package csust.teacher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.AvoidXfermode.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.adapter.MySignInfoListAdapter;
import csust.teacher.info.SignInfo;
import csust.teacher.info.SignNameInfo;
import csust.teacher.info.StudentInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.refresh.PullToRefreshLayout;
import csust.teacher.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.teacher.refresh.view.PullableListView;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.CharacterUtil;
import csust.teacher.utils.MyJson;

/**
 * 签到状态，也就是当前有多少人签到，一共有多少签到的。
 * 
 * @author U-anLA
 *
 */
public class SignStateActivity extends Activity implements OnClickListener {

	private ImageView mClose;
	private LinearLayout load_progressBar, mLinearLayout, mSignState;
	private TextView HomeNoValue, areadySign, notSign;
	private MyJson myJson = new MyJson();
	private List<SignNameInfo> list = new ArrayList<SignNameInfo>();
	private MySignInfoListAdapter mySignInfoListAdapter = null;
	// 用于获得signinfolist的url各个学生单独条目的
	private String url = null;
	// 用于获得总数已签到和未签到的数目。
	private String urlCount = null;

	// 用于得到前一个页面获得的signinfo
	private SignInfo signInfo = null;
	// 用于检测是否正在刷新
	private boolean loadflag = false;
	// 用于在警告提示框的radiobutton
	private RadioGroup alertRadioGroup;
	private RadioButton mRadioLeave, mRadioNotin, mRadioHaveSigned;

	// 一个alertdialog
	private AlertDialog changeState;

	// 存储某一次签到的完整信息
	private SignNameInfo mySignNameInfo;

	// 用来保存上一次radiobutton的选择
	private int lastChoice;

	private int mStart = 0;

	private PullableListView listView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	private int target = 1;

	// 加入新的签到状态
	private ImageView addSignState;

	// 用于选择未签到的学生
	private Spinner mSpinnerStudent;

	// 用于选择签到的状态
	private Spinner mSpinnerState;

	// 用于获取所有未签到的学生的url
	private String getStudentsUrl = null;

	// 用于添加新的签到状态的url；
	private String uploadNewStateUrl = null;

	// 用于正在加载的提示框
	private ProgressDialog mProDialog;

	// 用于添加到spinner的adapter的student列表
	private ArrayAdapter<String> adapterStudent = null;

	// 用于在四种操作选择的adapter里面。
	private ArrayAdapter<String> adapterState = null;

	// 签到状态
	private String[] myStrStates = { "已签到", "请假", "他人代签" };

	// 定义一个浮动的alertview
	private AlertDialog mAddState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sign_state);
		// 吧mylistview撞进来

		// 获得前一个页面的signinfo
		signInfo = (SignInfo) getIntent().getSerializableExtra("courseInfo");
		// signInfo = (SignInfo)
		// savedInstanceState.getSerializable("courseInfo");
		initView();

	}

	/**
	 * 用于初始化界面。
	 */
	private void initView() {
		// 绑定相应控件。
		mProDialog = new ProgressDialog(SignStateActivity.this);
		mProDialog.setCancelable(true);
		load_progressBar = (LinearLayout) findViewById(R.id.load_progressBar);
		mLinearLayout = (LinearLayout) findViewById(R.id.HomeGroup);
		mSignState = (LinearLayout) findViewById(R.id.SignState);
		HomeNoValue = (TextView) findViewById(R.id.HomeNoValue);
		areadySign = (TextView) findViewById(R.id.areadySign);
		notSign = (TextView) findViewById(R.id.notSign);
		mClose = (ImageView) findViewById(R.id.sign_close);
		addSignState = (ImageView) findViewById(R.id.addSignState);

		((PullToRefreshLayout) findViewById(R.id.refresh_view))
				.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView) findViewById(R.id.content_view);

		addSignState.setOnClickListener(this);

		// 说明有值。
		HomeNoValue.setVisibility(View.GONE);
		mySignInfoListAdapter = new MySignInfoListAdapter(list,
				SignStateActivity.this);
		// 设置adapter

		// 用于获得每位同学的签到状况
		url = Model.GETSIGNSTUDENTINFO + "allow_sign_id="
				+ signInfo.getAllow_sign_id() + "&start=" + mStart + "&count="
				+ Model.INIT_COUNT;
		// 用于传入hand1来处理并且来获得每位同学签到状况
		ThreadPoolUtils.execute(new HttpGetThread(hand1, url));

		// 用于获得总的签到状况，总的数目
		urlCount = Model.GETSIGNCOUNT + "allow_sign_id="
				+ signInfo.getAllow_sign_id();
		ThreadPoolUtils.execute(new HttpGetThread(hand2, urlCount));

		mClose.setOnClickListener(this);

		listView.setAdapter(mySignInfoListAdapter);
		listView.setOnItemLongClickListener(new MainClickListener());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.sign_close:
			finish();
			break;

		case R.id.addSignState:
			// 添加新的学生签到状态的imageview
			// 点击后就先获取所有未签到的数据。
			mProDialog.setTitle("加载中。。。");
			mProDialog.show();
			getStudentsUrl = Model.GETUNSIGNEDSTUDENTS + "allow_sign_id="
					+ signInfo.getAllow_sign_id();
			ThreadPoolUtils.execute(new HttpGetThread(hand4, getStudentsUrl));

			break;

		default:
			break;
		}
	}

	/**
	 * 封装的内部类。 实现了onitemlongclicklistener
	 * 
	 * @author U-anLA
	 *
	 */
	private class MainClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			mySignNameInfo = list.get(position);

			// 动态加载一个view
			LayoutInflater layoutInflater = LayoutInflater
					.from(SignStateActivity.this);
			View changeStateView = layoutInflater.inflate(
					R.layout.change_sign_state, null);

			// 绑定相应的view的控件。
			alertRadioGroup = (RadioGroup) changeStateView
					.findViewById(R.id.radioGroup);
			mRadioHaveSigned = (RadioButton) changeStateView
					.findViewById(R.id.state_radio_havesign);
			mRadioLeave = (RadioButton) changeStateView
					.findViewById(R.id.state_radio_leave);
			mRadioNotin = (RadioButton) changeStateView
					.findViewById(R.id.state_radio_notsign);
			// 初始化设定
			if (mySignNameInfo.getSign_state().equals("已签到")) {
				mRadioHaveSigned.setChecked(true);
				lastChoice = R.id.state_radio_havesign;
			} else if (mySignNameInfo.getSign_state().equals("请假")) {
				mRadioLeave.setChecked(true);
				lastChoice = R.id.state_radio_leave;
			} else {
				mRadioNotin.setChecked(true);
				lastChoice = R.id.state_radio_notsign;
			}

			// 添加一个事件
			alertRadioGroup
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							// 获取变更后的选中项的ID
							int radioButtonId = group.getCheckedRadioButtonId();

							// 更新签到状态。
							String mymyresult = null;
							switch (radioButtonId) {
							case R.id.state_radio_havesign:
								mymyresult = "已签到";
								break;

							case R.id.state_radio_leave:
								mymyresult = "请假";
								break;
							case R.id.state_radio_notsign:
								mymyresult = "他人代签";
								break;
							default:
								break;
							}
							if (radioButtonId != lastChoice) {
								// 如果未签到，则不更改状态
								String urlChange = Model.CHANGESIGNSTATE
										+ "allow_sign_id="
										+ signInfo.getAllow_sign_id()
										+ "&student_id="
										+ mySignNameInfo.getStudent_id()
										+ "&signState=" + mymyresult;
								ThreadPoolUtils.execute(new HttpGetThread(
										hand3, urlChange));

							} else {
								// 为原来的状态，点一下还是要消失。
								changeState.dismiss();
							}

						}
					});
			changeState = new AlertDialog.Builder(SignStateActivity.this)
					.setTitle("请选择状态").setView(changeStateView).create();

			changeState.show();

			return true;
		}

	}

	/**
	 * 用于加载listview的hand
	 */
	Handler hand1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(SignStateActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(SignStateActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {

				if (list != null) {
					list.removeAll(list);
					mySignInfoListAdapter.notifyDataSetChanged();
				}

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
				List<SignNameInfo> newList = myJson
						.getRealSignNameInfoList(result);
				if (newList.size() != 0) {

					for (SignNameInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(SignStateActivity.this, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有签到信息记录情况");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);

					}
				}

				mySignInfoListAdapter.notifyDataSetChanged();

			}
			mySignInfoListAdapter.notifyDataSetChanged();
		};
	};

	/**
	 * 获取签到状况的handler
	 */
	Handler hand2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(SignStateActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(SignStateActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				// 处理成功的页面
				String result = (String) msg.obj;
				// 前一个是已经签到的，后面是总人数
				String[] str = result.split(",");

				// 设置签到状况为可见
				SignStateActivity.this.mSignState.setVisibility(View.VISIBLE);
				// 设置相对应的值
				areadySign.setText(str[0]);
				notSign.setText(str[1]);

			}
		}
	};

	/**
	 * 用于更新状态。
	 */
	Handler hand3 = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(SignStateActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(SignStateActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				// 处理成功的页面
				String result = (String) msg.obj;

				if (result.equals("[1]")) {
					// 更新成功！
					changeState.dismiss();

					Toast.makeText(SignStateActivity.this, "更新成功！", 1).show();

					// 刷新！！！
					// 用于获得每位同学的签到状况

					url = Model.GETSIGNSTUDENTINFO + "allow_sign_id="
							+ signInfo.getAllow_sign_id() + "&start=" + mStart
							+ "&count=" + Model.INIT_COUNT;

					// 用于传入hand1来处理并且来获得每位同学签到状况
					ThreadPoolUtils.execute(new HttpGetThread(hand1, url));

					// 用于获得总的签到状况，总的数目
					urlCount = Model.GETSIGNCOUNT + "allow_sign_id="
							+ signInfo.getAllow_sign_id();
					ThreadPoolUtils.execute(new HttpGetThread(hand2, urlCount));

				} else {
					// 更新失败！！
					changeState.dismiss();
					Toast.makeText(SignStateActivity.this, "更新失败，失败！", 1)
							.show();
				}
			}
		};
	};

	/**
	 * 用于获得未签到学生的信息
	 */
	Handler hand4 = new Handler() {
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(SignStateActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(SignStateActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				mProDialog.dismiss();
				// 处理成功的页面
				String result = (String) msg.obj;
				if (result.equals("[]")) {
					// 如果没有可处理的学生，则直接退出

					Toast.makeText(SignStateActivity.this, "所有学生都已经签到了。。", 1)
							.show();
					return;
				}
				List<StudentInfo> myNewList = myJson
						.getUnsignedStudentsInfo(result);
				// 动态加载一个view
				LayoutInflater layoutInflater = LayoutInflater
						.from(SignStateActivity.this);
				View add_new_student = layoutInflater.inflate(
						R.layout.add_new_student_state, null);

				// 绑定相应的view的控件。
				mSpinnerStudent = (Spinner) add_new_student
						.findViewById(R.id.add_state_student);
				mSpinnerState = (Spinner) add_new_student
						.findViewById(R.id.add_stata_state);
				// 获取完控件后，即可插入数据了
				insertData(myNewList);
				mAddState = new AlertDialog.Builder(SignStateActivity.this)
						.setTitle("添加")
						.setView(add_new_student)
						.setPositiveButton(
								"确定",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										String strTmp = mSpinnerStudent
												.getSelectedItem().toString()
												.split("；")[0];
										String my_student_id = strTmp
												.substring(4, strTmp.length());
										String my_student_state = mSpinnerState
												.getSelectedItem().toString();

										String myAddStateUrl = Model.ADDNEWSTUDENTSIGNSTATE
												+ "student_username="
												+ my_student_id
												+ "&sign_state="
												+ my_student_state
												+ "&course_id="
												+ signInfo.getSign_courseNum()
												+ "&allow_sign_id="
												+ signInfo.getAllow_sign_id();
										ThreadPoolUtils
												.execute(new HttpGetThread(
														hand5, myAddStateUrl));

										mProDialog.setTitle("添加中，请勿取消");
										mProDialog.show();
										// Toast.makeText(SignStateActivity.this,
										// "daole", 1).show();
									}
								}).setNegativeButton("取消", null).create();

				mAddState.show();

			}
		}

		/**
		 * 用于插入数据到student的spinner中。
		 * 
		 * @param myNewList
		 */
		private void insertData(List<StudentInfo> newList) {
			String[] strs = new String[newList.size()];
			for (int i = 0; i < strs.length; i++) {
				// teacherName就是courseName,teacherNum就是courseNum，而courseName就是courseid
				strs[i] = "用户名：" + newList.get(i).getStudent_username()
						+ "；姓名：  " + newList.get(i).getStudent_name();
			}

			if (strs != null) {

				adapterStudent = new ArrayAdapter<String>(
						SignStateActivity.this,
						android.R.layout.simple_spinner_item, strs);
				adapterStudent
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				if (mSpinnerStudent == null) {
					System.out.println();
				}
				if (adapterStudent == null) {
					System.out.println();
				}
				mSpinnerStudent.setAdapter(adapterStudent);

				// 更改state数组
				adapterState = new ArrayAdapter<String>(SignStateActivity.this,
						android.R.layout.simple_spinner_item, myStrStates);
				adapterState
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpinnerState.setAdapter(adapterState);
			}

		};
	};

	/**
	 * 用于添加新的签到状态的handler
	 */
	Handler hand5 = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(SignStateActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(SignStateActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				mProDialog.dismiss();
				// 处理成功的页面
				String result = (String) msg.obj;
				if (result == null) {
					return;
				}
				if (result.equals("[1]")) {

					Toast.makeText(SignStateActivity.this, "添加信息成功！", 1).show();
					// 刷新！！！
					// 用于获得每位同学的签到状况

					url = Model.GETSIGNSTUDENTINFO + "allow_sign_id="
							+ signInfo.getAllow_sign_id() + "&start=" + mStart
							+ "&count=" + Model.INIT_COUNT;

					// 用于传入hand1来处理并且来获得每位同学签到状况
					ThreadPoolUtils.execute(new HttpGetThread(hand1, url));

					// 用于获得总的签到状况，总的数目
					urlCount = Model.GETSIGNCOUNT + "allow_sign_id="
							+ signInfo.getAllow_sign_id();
					ThreadPoolUtils.execute(new HttpGetThread(hand2, urlCount));
				} else if (result.equals("[0]")) {
					Toast.makeText(SignStateActivity.this, "抱歉添加信息失败！", 1)
							.show();

				}
			}
		};
	};

	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			SignStateActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETSIGNSTUDENTINFO + "allow_sign_id="
					+ signInfo.getAllow_sign_id() + "&start=" + mStart
					+ "&count=" + Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand1, url));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			SignStateActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETSIGNSTUDENTINFO + "allow_sign_id="
					+ signInfo.getAllow_sign_id() + "&start=" + mStart
					+ "&count=" + 5;
			ThreadPoolUtils.execute(new HttpGetThread(hand1, url));
		}

	}

}
