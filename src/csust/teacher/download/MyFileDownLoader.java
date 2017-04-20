package csust.teacher.download;


import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import csust.teacher.activity.R;

/**
 * 专用下载器，用于下载报表
 * @author U-ANLA
 *
 */
public class MyFileDownLoader {
	
	public static final String ACTION_DOWNLOAD_PROGRESS = "my_download_progress";
	public static final String ACTION_DOWNLOAD_SUCCESS = "my_download_success";
	public static final String ACTION_DOWNLOAD_FAIL = "my_download_fail";
	
	
	private Context ctx;
	private NotificationCompat.Builder mBuilder;
	/** Notification管理 */
	private NotificationManager mNotificationManager;
	
	private  String downloadUrl;
	
	/** Notification的ID */
	private int notifyId = 102;
	/** Notification的进度条数值 */
	private int progress = 0;
	
	private MyReceiver receiver; 
	
	/**
	 * 传入一个ctx，一个notificationmanager，而不用传入notificationcompat
	 * @param ctx
	 * @param mNotificationManager
	 */
	public MyFileDownLoader(Context ctx,NotificationManager mNotificationManager,String downloadUrl){
		this.ctx = ctx;
		this.mNotificationManager = mNotificationManager;
		this.downloadUrl = downloadUrl;
		initNotify();
		init();
	}
	


	private void init() {
		if (DownloadService.getInstance() != null) {
			mBuilder.setProgress(100, DownloadService.getInstance().getProgress(), false);
		}

		receiver = new MyReceiver();
		mNotificationManager.notify(notifyId, mBuilder.build());
		
		//不知是否可行
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DOWNLOAD_PROGRESS);
		filter.addAction(ACTION_DOWNLOAD_SUCCESS);
		filter.addAction(ACTION_DOWNLOAD_FAIL);
		ctx.registerReceiver(receiver, filter);
		
	}



	/** 初始化通知栏 */
	private void initNotify() {
		mBuilder = new NotificationCompat.Builder(ctx);
		mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setContentIntent(getDefalutIntent(0))
				// .setNumber(number)//显示数量
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(true)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);
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
		PendingIntent pendingIntent= PendingIntent.getActivity(ctx, 1, new Intent(), flags);
		return pendingIntent;
	}
	
	/**
	 * 显示下载成功的时候。
	 * @param fileName
	 */
	public void showProgressNotify(String fileName) {
		mBuilder.setContentTitle("下载成功！").setContentText(fileName)
				.setTicker("下载成功！");// 通知首次出现在通知栏，带上升动画效果的
		mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条  设置为true就是不确定的那种进度条效果
		mNotificationManager.notify(notifyId, mBuilder.build());
	}
	
	public void showBeginDownload(){
		mBuilder.setContentTitle("正在下载").setContentText("成绩表")
		.setTicker("开始下载");// 通知首次出现在通知栏，带上升动画效果的
		mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条  设置为true就是不确定的那种进度条效果
		mNotificationManager.notify(notifyId, mBuilder.build());
	}
	
	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_DOWNLOAD_PROGRESS)) {
				int pro = intent.getExtras().getInt("progress");
				mBuilder.setProgress(100, pro, false);
				mNotificationManager.notify(notifyId, mBuilder.build());
			} else if (action.equals(ACTION_DOWNLOAD_SUCCESS)) {

				Toast.makeText(ctx, "下载成功", Toast.LENGTH_SHORT)
						.show();
				final File f = (File) intent.getExtras()
						.getSerializable("file");
				showProgressNotify(f.getAbsolutePath());

			} else if (action.equals(ACTION_DOWNLOAD_FAIL)) {
				// textView.setText("文件下载失败");
				Toast.makeText(ctx, "下载失败", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}
	
	public void startDownloadService() {
		showBeginDownload();
		

		if (DownloadService.getInstance() != null
				&& DownloadService.getInstance().getFlag() != DownloadService.Flag_Init) {

			Toast.makeText(ctx, "已经在下载", 0).show();
			return;
		}
		Intent it = new Intent(ctx, DownloadService.class);
		it.putExtra("flag", "start");
		it.putExtra("url", downloadUrl);
		ctx.startService(it);
	}
	
}
