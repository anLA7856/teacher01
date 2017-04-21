package csust.teacher.activity;


import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.download.DownloadService1;

public class DownloadActivity extends Activity {

	public NotificationManager mNotificationManager;
	public static final String ACTION_DOWNLOAD_PROGRESS = "my_download_progress";
	public static final String ACTION_DOWNLOAD_SUCCESS = "my_download_success";
	public static final String ACTION_DOWNLOAD_FAIL = "my_download_fail";

	String url = null;

	ProgressBar progBar;
	MyReceiver receiver;

	TextView textView;
	Button btnOpen;

	NotificationCompat.Builder mBuilder;

	/** true:为不确定样式的 false:确定样式 */
	public Boolean indeterminate = false;

	/** Notification的ID */
	int notifyId = 102;
	/** Notification的进度条数值 */
	int progress = 0;

	
	
	
	/** 初始化通知栏 */
	private void initNotify() {
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setContentIntent(getDefalutIntent(0))
				// .setNumber(number)//显示数量
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);
	}

	/** 显示带进度条通知栏 */
	public void showProgressNotify(String fileName) {
		mBuilder.setContentTitle("下载成功！").setContentText(fileName)
				.setTicker("下载成功！");// 通知首次出现在通知栏，带上升动画效果的

		mNotificationManager.notify(notifyId, mBuilder.build());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initService();
		url = (String) getIntent()
				.getSerializableExtra("url");
		setContentView(R.layout.activity_download);
		progBar = (ProgressBar) findViewById(R.id.progressBar1);
		textView = (TextView) findViewById(R.id.text_desc);
		btnOpen = (Button) findViewById(R.id.btn_open);

		if (DownloadService1.getInstance() != null) {
			progBar.setProgress(DownloadService1.getInstance().getProgress());
		}

		receiver = new MyReceiver();
		initNotify();
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DOWNLOAD_PROGRESS);
		filter.addAction(ACTION_DOWNLOAD_SUCCESS);
		filter.addAction(ACTION_DOWNLOAD_FAIL);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_down:
			startDownloadService();
			break;

		case R.id.button_pause:
			pauseDownloadService();
			break;

		case R.id.button_cancel:
			stopDownloadService();
			break;

		default:
			break;
		}
	}

	void startDownloadService() {
		if (DownloadService1.getInstance() != null
				&& DownloadService1.getInstance().getFlag() != DownloadService1.Flag_Init) {
			Toast.makeText(this, "已经在下载", 0).show();
			return;
		}
		Intent it = new Intent(this, DownloadService1.class);
		it.putExtra("flag", "start");
		it.putExtra("url", url);
		startService(it);
	}

	void pauseDownloadService() {
		String flag = null;
		int f = DownloadService1.getInstance().getFlag();
		if (DownloadService1.getInstance() != null) {
			// 如果当前已经暂停，则恢复
			if (f == DownloadService1.Flag_Pause) {
				flag = "resume";
			} else if (f == DownloadService1.Flag_Down) {
				flag = "pause";
			} else {
				return;
			}
		}
		Intent it = new Intent(this, DownloadService1.class);
		it.putExtra("flag", flag);
		startService(it);
	}

	void stopDownloadService() {
		Intent it = new Intent(this, DownloadService1.class);
		it.putExtra("flag", "stop");
		startService(it);
		progBar.setProgress(0);
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_DOWNLOAD_PROGRESS)) {
				int pro = intent.getExtras().getInt("progress");
				progBar.setProgress(pro);
			} else if (action.equals(ACTION_DOWNLOAD_SUCCESS)) {

				Toast.makeText(DownloadActivity.this, "下载成功",
						Toast.LENGTH_SHORT).show();
				final File f = (File) intent.getExtras()
						.getSerializable("file");
				DownloadActivity.this.showProgressNotify(f.getAbsolutePath());
				btnOpen.setVisibility(View.VISIBLE);
				textView.setText("文件已保存在：" + f.getAbsolutePath());
				btnOpen.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						openFile(f);
					}
				});
			} else if (action.equals(ACTION_DOWNLOAD_FAIL)) {
				// textView.setText("文件下载失败");
				Toast.makeText(DownloadActivity.this, "下载失败",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void openFile(File f) {
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//		String type = "audio";
//		intent.setDataAndType(Uri.fromFile(f), type);
//		startActivity(intent);
		//专用打开xls文件。
	     Intent intent = new Intent("android.intent.action.VIEW");  	     
	     intent.addCategory("android.intent.category.DEFAULT");  
	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	     intent.setDataAndType(Uri.fromFile(f), "application/vnd.ms-excel");  
	     startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		final int f = DownloadService1.getInstance().getFlag();
		// XXX:暂停状态下退出？？？
		if (f == DownloadService1.Flag_Down || f == DownloadService1.Flag_Pause) {
			new AlertDialog.Builder(this)
					.setTitle("确定退出程序？")
					.setMessage("你有未完成的下载任务")
					.setNegativeButton("取消下载",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									stopDownloadService();
									DownloadActivity.super.onBackPressed();
								}
							})
					.setPositiveButton("后台下载",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (f == DownloadService1.Flag_Pause) {
										Intent it = new Intent(
												DownloadActivity.this,
												DownloadService1.class);
										it.putExtra("flag", "resume");
										startService(it);
									}

									DownloadActivity.super.onBackPressed();
								}
							}).create().show();
			return;
		}

		if (DownloadService1.getInstance() != null)
			DownloadService1.getInstance().stopSelf();
		super.onBackPressed();
	}

	/**
	 * 初始化要用到的系统服务
	 */
	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	/** 
	 * 清除当前创建的通知栏 
	 */
	public void clearNotify(int notifyId){
		mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//		mNotification.cancel(getResources().getString(R.string.app_name));
	}
	
	/**
	 * 清除所有通知栏
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// 删除你发的所有通知
	}
	
	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:  
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT  
	 * 点击去除： Notification.FLAG_AUTO_CANCEL 
	 */
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}
}
