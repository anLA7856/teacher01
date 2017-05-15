package csust.teacher.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.database.MessageDB;
import csust.teacher.info.ChatMessage;
import csust.teacher.info.UserInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.thread.HttpPostThread;
import csust.teacher.utils.MyJson;
import csust.teacher.utils.WifiAdmin;

/**
 * 登录界面
 * 
 * @author anLA7856
 *
 */
public class LoginActivity extends Activity implements OnClickListener {

	// 定义相应控件的引用
	private ImageView mClose;
	private RelativeLayout mWeibo, mQQ;
	private Button mLogin;
	private EditText mName, mPassword;
	private TextView mRegister;
	// 定义值，用于存储用户名密码
	private String NameValue = null;
	private String PasswordValue = null;
	// 用于与服务器通信的url
	private String url = null;
	private String value = null;
	// 自己的myjson解析类
	private MyJson myJson = new MyJson();

	// wifiAdmin操作类，
	private WifiAdmin myWifiAdmin;
	// 存储新登录收的wifimac
	private String wifiMac;

	// 定义进度匡
	private ProgressDialog mProDialog;

	private MessageDB messageDB = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		messageDB = new MessageDB(getApplicationContext());
		initView();
	}

	/**
	 * 初始化界面用
	 */
	private void initView() {
		// 防止开机一次都没开过wifi而娶不到mac地址
		WifiAdmin myAdmin = new WifiAdmin(this);
		int state = myAdmin.checkState();
		if (state != WifiManager.WIFI_STATE_ENABLED
				&& state != WifiManager.WIFI_STATE_ENABLING) {
			myAdmin.openWifi();

		}

		mProDialog = new ProgressDialog(this);

		// 用于与activity_login的控件一一对应
		mClose = (ImageView) findViewById(R.id.loginClose);
		mLogin = (Button) findViewById(R.id.Ledit_login);
		mWeibo = (RelativeLayout) findViewById(R.id.button_weibo);
		mQQ = (RelativeLayout) findViewById(R.id.buton_qq);
		mName = (EditText) findViewById(R.id.Ledit_name);
		mPassword = (EditText) findViewById(R.id.Ledit_password);
		mRegister = (TextView) findViewById(R.id.register);
		// 添加相应事件。
		myWifiAdmin = new WifiAdmin(this);
		wifiMac = myWifiAdmin.getMacAddress();

		mClose.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		mWeibo.setOnClickListener(this);
		mQQ.setOnClickListener(this);
		mRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int mId = v.getId();
		switch (mId) {
		case R.id.loginClose:
			// 关闭页面
			// finish();
			break;
		case R.id.Ledit_login:
			// 登录按钮，获得并存储相应的值
			NameValue = mName.getText().toString();
			PasswordValue = mPassword.getText().toString();
			// 相应检验
			if (NameValue.equalsIgnoreCase(null)
					|| PasswordValue.equalsIgnoreCase(null)
					|| NameValue.equals("") || PasswordValue.equals("")) {
				Toast.makeText(LoginActivity.this, "账号密码不能为空", 1).show();
				return;
			} else if (!NameValue.matches("[a-zA-Z0-9]{5,12}")) {
				Toast.makeText(LoginActivity.this, "用户名填写错误", 1).show();
				return;
			} else if (!PasswordValue.matches("[a-zA-Z0-9]{6,12}")) {
				Toast.makeText(LoginActivity.this, "密码填写错误", 1).show();
				return;
			} else {
				// 登录接口

				login();
			}
			break;
		case R.id.button_weibo:
			Toast.makeText(LoginActivity.this, "(暂时无法使用)正在与Sina公司沟通中...", 1)
					.show();
			break;
		case R.id.buton_qq:
			Toast.makeText(LoginActivity.this, "(暂时无法使用)正在与Tencent公司沟通中...", 1)
					.show();
			break;
		case R.id.register:
			Intent intent = new Intent(LoginActivity.this,
					RegistetActivity.class);
			// startActivity(intent);
			startActivityForResult(intent, 1);

		}
	}

	/**
	 * 登录，以post的登录的login
	 */
	private void login() {
		mProDialog.setTitle("登录中。。。");
		mProDialog.show();
		url = Model.LOGIN;
		value = "{\"uname\":\"" + NameValue + "\",\"wifiMac\":\"" + wifiMac
				+ "\",\"upassword\":\"" + PasswordValue + "\"}";
		// Log.e("qianpengyu", value);
		// 异步传输验证
		ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
	}

	/**
	 * 用于处理登录之后的handler
	 */
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {

			super.handleMessage(msg);
			mProDialog.dismiss();
			if (msg.what == 404) {
				// 请求页面不存在
				Toast.makeText(LoginActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				// 请求失败
				Toast.makeText(LoginActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				// 请求成功，
				String result = (String) msg.obj;
				if (result.equalsIgnoreCase("NOUSER")) {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "用户名不存在", 1).show();
					return;
				} else if (result.equalsIgnoreCase("NOPASS")) {
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "密码错误", 1).show();
					return;
				} else if (result != null) {
					Toast.makeText(LoginActivity.this, "登录成功", 1).show();
					List<UserInfo> newList = myJson.getUserInfoList(result);
					// Log.i("newList", newList.toArray().toString());
					if (newList != null) {
						Model.MYUSERINFO = newList.get(0);
					}

					// 获取聊天数据
					String url1 = Model.STUGETALLCHATMESSAGE + "studentId="
							+ Model.MYUSERINFO.getTeacher_id();

					ThreadPoolUtils.execute(new HttpGetThread(hand1, url1));
					//

					Intent intent = new Intent(LoginActivity.this,
							UserInfoActivity.class);
					Bundle bund = new Bundle();
					bund.putSerializable("UserInfo", Model.MYUSERINFO);
					intent.putExtra("value", bund);
					startActivity(intent);
					SharedPreferences sp = LoginActivity.this
							.getSharedPreferences("UserInfo", MODE_PRIVATE);
					Log.e("SharedPreferencesOld",
							sp.getString("UserInfoJson", "none"));
					SharedPreferences.Editor mSettinsEd = sp.edit();
					mSettinsEd.putString("UserInfoJson", result);
					// 提交保存
					mSettinsEd.commit();

					// Log.e("SharedPreferencesNew",
					// sp.getString("UserInfoJson", "none"));
					finish();
				}
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 请求结果后的页面，如果不存在，记得把先前填写的用户名给加上去。
		if (requestCode == 1 && resultCode == 2 && data != null) {
			NameValue = data.getStringExtra("NameValue");
			mName.setText(NameValue);
		}

	};

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		registerReceiver(mXmppreceiver, intentFilter);
	};

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mXmppreceiver);
	}

	private BroadcastReceiver mXmppreceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// if (action.equals(KFMainService.ACTION_XMPP_CONNECTION_CHANGED))
			// {
			// updateStatus(intent.getIntExtra("new_state", 0));
			// }

		}
	};

	private void updateStatus(int status) {

		// switch (status) {
		// case KFXmppManager.CONNECTED:
		// KFSLog.d("登录成功");
		// break;
		// case KFXmppManager.DISCONNECTED:
		// KFSLog.d("未登录");
		// break;
		// case KFXmppManager.CONNECTING:
		// KFSLog.d("登录中");
		// break;
		// case KFXmppManager.DISCONNECTING:
		// KFSLog.d("登出中");
		// break;
		// case KFXmppManager.WAITING_TO_CONNECT:
		// case KFXmppManager.WAITING_FOR_NETWORK:
		// KFSLog.d("waiting to connect");
		// break;
		// default:
		// throw new IllegalStateException();
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 如果pause，就直接destory
		onDestroy();
	}

	Handler hand1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 进度匡消失,这里才消失
			mProDialog.dismiss();

			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(LoginActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(LoginActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// Log.e("loginInfo", result);
				// 从服务器把所有获得了的聊天信息都保存下来。
				List<ChatMessage> list = myJson.getChatMessageList(result);
				for (int i = 0; i < list.size(); i++) {
					messageDB.saveMsg(Model.MYUSERINFO.getTeacher_id() + "",
							list.get(i));
				}
			}
		};
	};

}
