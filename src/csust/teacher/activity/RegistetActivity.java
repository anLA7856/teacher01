package csust.teacher.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
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
import csust.teacher.utils.FaceUtil;
import csust.teacher.utils.WifiAdmin;

/**
 * 注册页面，教师端的注册
 * 
 * @author anLA7856
 *
 */
public class RegistetActivity extends Activity implements OnClickListener {

	// 常量保存结果，选择图片，还是照相。
	private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	// 定义关闭的imageview
	private ImageView mClose;

	// 下一步，也就是登录
	private RelativeLayout mNext;
	// 用于注册时的各种需要的信息
	private EditText mName, mUsername, mPassword;
	// 自己的头像的图片
	private ImageView mPic;
	// 用string来保存相应的信息。
	private String username, password, url, value, name, sex = "", age = "",
			stuNum = "";
	// 登录时保存图片地址的file
	private File mPictureFile = null;

	// 进度对话框
	private ProgressDialog mProDialog;

	// 提示的toast
	private Toast mToast;

	// 位图，用来将图片数据注册并且上传。
	private Bitmap mImage = null;

	// 图片路径。
	private String imgUrl = null;

	// 用于获得和存储wifimac地址
	private String wifiMac = null;

	// 控制并且获得本机wifimac的费用
	private WifiAdmin myAdmin;

	// 用来标记是否拍照了
	private boolean isPhoto = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		myAdmin = new WifiAdmin(RegistetActivity.this);
		// 初始化窗体。
		initView();
	}

	/**
	 * 用于初始化各个窗体控件。
	 */
	private void initView() {
		// 获得各种相应的控件。
		mClose = (ImageView) findViewById(R.id.RegiRegisterClose);
		mName = (EditText) findViewById(R.id.RegiMyName);
		mNext = (RelativeLayout) findViewById(R.id.next);

		mUsername = (EditText) findViewById(R.id.RegiMyUsername);
		mPassword = (EditText) findViewById(R.id.RegiMyPassword);
		mPic = (ImageView) findViewById(R.id.RegiCamera);

		// 添加各种listener事件。
		mClose.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mPic.setOnClickListener(this);

		// 初始化进度窗口
		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(true);
		mProDialog.setTitle("注册账号中...");

		// 获得本机的wifimac地址。
		wifiMac = myAdmin.getMac();

		/**
		 * 添加cancel事件。
		 */
		mProDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// cancel进度框时,取消正在进行的操作

			}
		});

	}

	@Override
	public void onClick(View v) {
		int mId = v.getId();
		switch (mId) {

		case R.id.RegiCamera:
			// 照相的事件。
			String str = Model.LOCALSTORAGE + "signPic"
					+ System.currentTimeMillis() / 1000 + ".jpg";
			mPictureFile = new File(str);

			Intent mIntent = new Intent();
			// 将目的设为拍照。
			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			// 把目录传到extra来传过去。
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mPictureFile));
			// mIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			// 把orientation传进去。
			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			// 启动intent并且在后面的forresult来获知处理的结果。
			startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
			break;

		case R.id.RegiRegisterClose:
			// 退出。
			finish();
			break;
		case R.id.next:
			// 登录
			username = mUsername.getText().toString();
			password = mPassword.getText().toString();
			name = mName.getText().toString();

			if (username.equals("")) {
				Toast.makeText(RegistetActivity.this, "用户名不能为空！", 1).show();
				return;
			}
			if (password.equals("")) {
				Toast.makeText(RegistetActivity.this, "用户名不能为空！", 1).show();
				return;
			}
			if (name.equals("")) {
				Toast.makeText(RegistetActivity.this, "姓名不能为空！", 1).show();
				return;
			}

			// 中文英语都可以判定在8位内
			if (name.length() > 8) {
				Toast.makeText(RegistetActivity.this, "姓名长度超过8位", 1).show();
				return;
			}

			if (!username.matches("[a-zA-Z0-9]{5,12}")) {
				// 判定5~12位
				Toast.makeText(RegistetActivity.this, "用户名必须是5~12位的字母数字", 1)
						.show();
				return;
			}

			if (!password.matches("[a-zA-Z0-9]{6,12}")) {
				// 判定5~12位
				Toast.makeText(RegistetActivity.this, "密码必须是6~12位的字母数字", 1)
						.show();
				return;
			}

			if (isPhoto == false) {
				// 判断是否有上传自己的头像。
				new AlertDialog.Builder(this)
						.setTitle("提示框")
						.setMessage("确认不上传自己的头像？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@SuppressLint("ShowToast")
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										//
										myRegister();
									}
								}).setNegativeButton("取消", null).show();
			} else {
				myRegister();

			}

			break;

		}
	}

	/**
	 * post方式传送
	 */
	private void myRegister() {
		mProDialog.setMessage("注册账号中...");
		mProDialog.show();
		url = Model.REGISTET;
		value = "{\"username\":\"" + username + "\",\"password\":\"" + password
				+ "\",\"wifiMac\":\"" + wifiMac + "\",\"name\":\"" + name
				+ "\",\"sex\":\"" + sex + "\",\"age\":\"" + age
				+ "\",\"stuNum\":\"" + stuNum + "\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand, url, value, username
				+ ".jpg", imgUrl));
	}

	/**
	 * 用来处理登录后的handler。
	 */
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			// 进度匡消失
			mProDialog.dismiss();
			if (msg.what == 404) {
				Toast.makeText(RegistetActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(RegistetActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// Log.e("anla", "result:" + result);
				if (result.equals("ok")) {
					Toast.makeText(RegistetActivity.this, "用户名注册成功,请登陆", 1)
							.show();
					Intent intent = new Intent();
					intent.putExtra("NameValue", username);
					intent.putExtra("PasswordValue", password);
					setResult(2, intent);
					finish();
				} else if (result.trim().equals("no")) {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(RegistetActivity.this, "用户名以存在,请重新注册", 1)
							.show();
					return;
				} else {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(RegistetActivity.this, "服务器原因注册失败", 1)
							.show();
					return;
				}

			}

			// mProDialog.dismiss();
		};
	};

	/**
	 * 来展示tip
	 * 
	 * @param str
	 */
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 重写获得result的页面。
		if (resultCode != RESULT_OK) {
			return;
		}

		String fileSrc = null;
		if (requestCode == REQUEST_CAMERA_IMAGE) {

			if (null == mPictureFile) {
				showTip("拍照失败，请重试");
				return;
			}

			fileSrc = mPictureFile.getAbsolutePath();
			// fileSrc = urlPath;
			imgUrl = fileSrc;
			updateGallery(fileSrc);
			Uri u = Uri.fromFile(new File(fileSrc));
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this, Uri.fromFile(new File(fileSrc)));
		} else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// 获取返回数据
			Bitmap bmp = data.getParcelableExtra("data");
			// 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
			if (null != bmp) {
				FaceUtil.saveBitmapToFile(RegistetActivity.this, bmp);
			}
			// 获取图片保存路径
			fileSrc = FaceUtil.getImagePath(RegistetActivity.this);
			// 获取图片的宽和高
			Options options = new Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			// 压缩图片
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			// 若mImageBitmap为空则图片信息不能正常获取
			if (null == mImage) {
				showTip("图片信息无法正常获取！");
				return;
			}

			// 部分手机会对图片做旋转，这里检测旋转角度
			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0) {
				// 把图片旋转为正的方向
				mImage = FaceUtil.rotateImage(degree, mImage);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// 可根据流量及网络状况对图片进行压缩
			mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);

			((ImageView) findViewById(R.id.RegiCamera)).setImageBitmap(mImage);
			isPhoto = true;
		}

	}

	/**
	 * 更新view的图片
	 * 
	 * @param filename
	 */
	private void updateGallery(String filename) {
		MediaScannerConnection.scanFile(this, new String[] { filename }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}

	@Override
	public void finish() {
		// 重写finish()
		if (null != mProDialog) {
			mProDialog.dismiss();
		}
		super.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		/**
		 * 来防止像三星这样的或转动屏幕的手机。
		 */
		Log.i("UserInfoActivity", "onConfigurationChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("UserInfoActivity", "横屏");
			Configuration o = newConfig;
			o.orientation = Configuration.ORIENTATION_PORTRAIT;
			newConfig.setTo(o);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("UserInfoActivity", "竖屏");
		}
		super.onConfigurationChanged(newConfig);
	}

}
