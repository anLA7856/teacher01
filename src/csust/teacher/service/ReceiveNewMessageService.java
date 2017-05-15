package csust.teacher.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import csust.teacher.application.SignTeacherApp;
import csust.teacher.database.MessageDB;
import csust.teacher.info.ChatMessage;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.MyJson;
import csust.teacher.utils.NetUtil;

/**
 * 用于从服务器拉取消息
 * 
 * @author anLA7856
 *
 */
public class ReceiveNewMessageService extends Service {

	private volatile boolean isRun = false;
	private String url = null;
	private MyJson myJson = new MyJson();

	private SignTeacherApp mApplication;

	private MessageDB mMsgDB;// 保存消息的数据库

	private Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Log.d("tt", "Receiveservice 连接失败");
			} else if (msg.what == 100) {
				Log.d("tt", "Receiveservice 连接失败");
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// 请求后的处理界面。，主要是将结果插入到数据库，如果有结果是当前用户正在聊天的，则要进行实时的界面更新操作。
				List<ChatMessage> list = myJson.getChatMessageList(result);
				for (int i = 0; i < list.size(); i++) {
					ChatMessage cm = list.get(i);
					if (Model.MYCHATACTIVITY == null) {

					} else {
						if (cm.getSenderId() == Integer
								.parseInt(Model.MYCHATACTIVITY.getStudentId()
										.toString())
								&& cm.getReceiveId() == Model.MYUSERINFO
										.getTeacher_id()) {
							// 说明是当前对话窗口的，从大管家那里获得引用，并更新界面,不确定是否可行
							Model.MYCHATACTIVITY.getAdapter().upDateMsg(cm);
						}
					}

					// 存到数据库
					mMsgDB.saveMsg(Model.MYUSERINFO.getTeacher_id() + "", cm);
				}

			}
			// 改变标志位
			isRun = false;
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = SignTeacherApp.getInstance();
		mMsgDB = mApplication.getMessageDB();// 发送数据库
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Log.d("service", "I am a service");
					// 一旦启动，就一直轮询下去。，这里就不适用sleep来进行睡眠操作，因为可能一次和服务器通讯，就需要点时间。
					if (!isRun) {
						// 说明一次轮询已经完成，此时可以进行下一次轮询操作。
						if (NetUtil.isNetConnected(getApplicationContext()) == false) {
							// 网络不通畅，下一次；
							continue;
						}
						if (Model.MYUSERINFO == null) {
							continue;
						}
						// 可以用学生端的这个servlet。
						url = Model.STUGETNEWCHATMESSAGE + "studentId="
								+ Model.MYUSERINFO.getTeacher_id();
						Log.i("service", url);
						ThreadPoolUtils.execute(new HttpGetThread(hand, url));
						isRun = true;
					} else {
						// 过了这一次，不轮询
						continue;
					}
				}

			}
		}).start();

	}
}
