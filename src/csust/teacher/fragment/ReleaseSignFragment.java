package csust.teacher.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.activity.LoginActivity;
import csust.teacher.activity.R;
import csust.teacher.activity.ReleaseSignInfoActivity;
import csust.teacher.activity.SignStateActivity;
import csust.teacher.adapter.MySignListAdapter;
import csust.teacher.info.SignInfo;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.refresh.PullToRefreshLayout;
import csust.teacher.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.teacher.refresh.view.PullableListView;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.MyJson;

/**
 * 教师端的展示自己正在签到的条项
 * 
 * @author U-anLA
 *
 */

public class ReleaseSignFragment extends Fragment implements OnClickListener {

	private View view;
	private ImageView mTopImg, mAddSignInfo;

	private TextView mTopMenuOne;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private BeginSignFragmentCallBack mBeginSignFragmentCallBack;
	private MyJson myJson = new MyJson();
	private List<SignInfo> list = new ArrayList<SignInfo>();
	private MySignListAdapter mSignAdapter = null;
	private int mStart = 0;
	private int mEnd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottomFlag = true;
	private Context ctx;

	// 设置onpause标志变量
	private boolean isPause = false;

	private PullableListView listView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_sign, null);
		ctx = view.getContext();
		// 这是鸡肋，可能需要改！！！！！！
		if (list != null) {
			list.removeAll(list);
		}
		initView();
		return view;
	}

	private void initView() {
		load_progressBar = (LinearLayout) view
				.findViewById(R.id.load_progressBar);
		mLinearLayout = (LinearLayout) view.findViewById(R.id.HomeGroup);

		mTopImg = (ImageView) view.findViewById(R.id.Menu);
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
		mAddSignInfo = (ImageView) view.findViewById(R.id.AddSignInfo);

		((PullToRefreshLayout) view.findViewById(R.id.refresh_view))
				.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView) view.findViewById(R.id.content_view);

		mTopImg.setOnClickListener(this);
		mAddSignInfo.setOnClickListener(this);
		HomeNoValue.setVisibility(View.GONE);
		mSignAdapter = new MySignListAdapter(ctx, list);

		listView.setAdapter(mSignAdapter);

		if (Model.MYUSERINFO != null) {
			isFirst = true;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.TEAGETMYSIGNINGINFO + "startCount=" + mStart
					+ "&student_id=" + Model.MYUSERINFO.getTeacher_id()
					+ "&count=" + Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}

		listView.setOnItemClickListener(new MainListOnItemClickListener());
		listView.setOnItemLongClickListener(new MainListOnItemClickListener());
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 200) {
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
				List<SignInfo> newList = myJson.getNotSignInfoList(result);
				if (newList.size() != 0) {

					for (SignInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(ctx, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有签到信息情况");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);

					}
				}

				mSignAdapter.notifyDataSetChanged();

			}
			mSignAdapter.notifyDataSetChanged();
		};
	};

	public void setCallBack(BeginSignFragmentCallBack mBeginSignFragmentCallBack) {
		this.mBeginSignFragmentCallBack = mBeginSignFragmentCallBack;
	}

	public interface BeginSignFragmentCallBack {
		public void callback(int flag);
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.Menu:
			mBeginSignFragmentCallBack.callback(R.id.Menu);
			break;
		case R.id.AddSignInfo:
			if (Model.MYUSERINFO != null) {
				Intent intent = new Intent(ctx, ReleaseSignInfoActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(ctx, "请先登录", 1).show();
				Intent intent = new Intent(ctx, LoginActivity.class);
				startActivity(intent);
			}

			break;
		default:
			break;
		}
	}

	private class MainListOnItemClickListener implements OnItemClickListener,
			OnItemLongClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Intent intent = new Intent(ctx, SignStateActivity.class);

			Bundle bund = new Bundle();
			bund.putSerializable("courseInfo", list.get(arg2));
			// intent.putExtra("value", bund);
			intent.putExtras(bund);
			startActivity(intent);

		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final int myPosition = position;
			new AlertDialog.Builder(ctx)
					.setTitle("关闭提示框")
					.setMessage("确认关闭本次签到(课程同学将不能继续签到！！)")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 用于删除某一门课程！courseName就是course_id

									String url1 = Model.CLOSESIGN
											+ "allow_sign_id="
											+ list.get(myPosition)
													.getAllow_sign_id();

									ThreadPoolUtils.execute(new HttpGetThread(
											hand1, url1));

								}
							}).setNegativeButton("取消", null).show();
			// 注意这里是防止再次出发单词点击实际，如果是false，就会出发单词短点击事件
			return true;
		}

	}

	Handler hand1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;

				if (result.equals("[1]")) {
					// 成功！并且刷新下，也就是重新读一下。
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					Toast.makeText(ctx, "关闭本门签到成功！", 1).show();

				} else {
					// 失败！
					Toast.makeText(ctx, "关闭本门课程失败！！！", 1).show();
				}

			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();

		// 每次onresum时候，就要把homenovalue设为false
		mStart = 0;
		HomeNoValue.setVisibility(View.GONE);
		if (isPause == false) {
			return;
		}

		if (list.size() != 0) {
			list.removeAll(list);
		}
		if (Model.MYUSERINFO != null) {
			isFirst = true;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.TEAGETMYSIGNINGINFO + "startCount=" + mStart
					+ "&student_id=" + Model.MYUSERINFO.getTeacher_id()
					+ "&count=" + Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			ReleaseSignFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.TEAGETMYSIGNINGINFO + "startCount=" + mStart
					+ "&student_id=" + Model.MYUSERINFO.getTeacher_id()
					+ "&count=" + Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			ReleaseSignFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			url = Model.TEAGETMYSIGNINGINFO + "startCount=" + mStart
					+ "&student_id=" + Model.MYUSERINFO.getTeacher_id()
					+ "&count=" + 5;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

	}

}
