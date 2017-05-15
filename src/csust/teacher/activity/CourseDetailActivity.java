package csust.teacher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.teacher.adapter.MyStudentSignRateAdapter;
import csust.teacher.chartView.MyArc;
import csust.teacher.chartView.MyColumnar;
import csust.teacher.chartView.Score;
import csust.teacher.info.CourseInfo;
import csust.teacher.info.StudentSignRate;
import csust.teacher.model.Model;
import csust.teacher.net.ThreadPoolUtils;
import csust.teacher.refresh.PullToRefreshLayout;
import csust.teacher.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.teacher.refresh.view.PullableListView;
import csust.teacher.thread.HttpGetThread;
import csust.teacher.utils.MyJson;

/**
 * 用于展示课程详细信息
 * 
 * @author anLA7856
 *
 */
public class CourseDetailActivity extends Activity implements OnClickListener {
	// 定义相应的控件。
	private ImageView mClose;
	private LinearLayout mProgross, mLinearLayout, mLinearChart, mArc;
	private TextView HomeNoValue;
	// 用于存储展示柱型框。
	private RelativeLayout pillars;

	// 用于存url，获得课程效率百分率
	private String percentUrl = null;
	// 该门课所有次签到。
	private String allSignUrl = null;
	// 用于获得list的，并且以listview的形式展示
	private String allStudentListUrl = null;

	// 用来保存上一个页面的courseinfo
	private CourseInfo courseInfo = null;

	// 定义myjson解析类
	private MyJson myJson = new MyJson();

	// adapter，用于展示该门课的每一个学生的签到状况
	private MyStudentSignRateAdapter myStudentSignRateAdapter = null;

	// list列表
	private List<StudentSignRate> list = new ArrayList<StudentSignRate>();

	private PullableListView listView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	// 定义start
	private int mStart = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_sign_detail_info);
		// 获得上一个页面传来的courseinfo
		courseInfo = (CourseInfo) getIntent()
				.getSerializableExtra("courseInfo");

		// 初始化界面
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 根据id获得相应控件
		mClose = (ImageView) findViewById(R.id.close);
		mProgross = (LinearLayout) findViewById(R.id.load_progressBar);
		mLinearLayout = (LinearLayout) findViewById(R.id.HomeGroup);
		HomeNoValue = (TextView) findViewById(R.id.HomeNoValue);
		mLinearChart = (LinearLayout) findViewById(R.id.sign_detail_chart);
		mArc = (LinearLayout) findViewById(R.id.arc);
		pillars = (RelativeLayout) findViewById(R.id.pillars);
		// FrameLayout.LayoutParams hint_page_params = new
		// FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,myStudentSignRateAdapter.getMyLength()*mylist.size());

		((PullToRefreshLayout) findViewById(R.id.refresh_view))
				.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView) findViewById(R.id.content_view);

		mClose.setOnClickListener(this);

		myStudentSignRateAdapter = new MyStudentSignRateAdapter(
				CourseDetailActivity.this, list, mLinearLayout);

		// coursename就是课程号。拼接url
		percentUrl = Model.GETCOURSESIGNINFOCOUNT + "course_id="
				+ courseInfo.getCourseName();
		// 通过另一个线程来获得数据交互
		ThreadPoolUtils.execute(new HttpGetThread(hand1, percentUrl));

		// 用于获得list，该门课所有次签到。

		allSignUrl = Model.GETALLSIGNLISTOFCOURSE + "course_id="
				+ courseInfo.getCourseName();
		ThreadPoolUtils.execute(new HttpGetThread(hand2, allSignUrl));

		// 用于去初始化listview，并显示
		allStudentListUrl = Model.GETSTUDENTLISTCOURSERATE + "course_id="
				+ courseInfo.getCourseName() + "&start=" + mStart + "&count="
				+ Model.INIT_COUNT;
		ThreadPoolUtils.execute(new HttpGetThread(hand3, allStudentListUrl));
		// 加入到adapter.

		listView.setAdapter(myStudentSignRateAdapter);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.close:
			// 关闭页面
			finish();
			break;

		default:
			break;
		}
	}

	// 用于获得课程签到百分率的handler
	Handler hand1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				// 404页面
				Toast.makeText(CourseDetailActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				// 传输失败
				Toast.makeText(CourseDetailActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				// 正确的处理逻辑
				String result = (String) msg.obj;

				// 把进度框消失
				mProgross.setVisibility(View.GONE);

				int myPass = Integer.parseInt(result);
				mLinearChart.setVisibility(View.VISIBLE);
				mArc.addView(new MyArc(CourseDetailActivity.this, myPass));

			}
		};
	};

	/**
	 * 用于显示柱型条形列表的handler.
	 */
	Handler hand2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				// 404页面
				Toast.makeText(CourseDetailActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				// 传输失败
				Toast.makeText(CourseDetailActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				// 正确的处理逻辑
				String result = (String) msg.obj;

				List<Score> myScore = myJson.getSignNameRateCountList(result);
				for (int i = 0; i < myScore.size(); i++) {

					myScore.get(i).setTimes("第" + (i + 1) + "次");

				}
				pillars.addView(new MyColumnar(CourseDetailActivity.this,
						myScore));

			}

		};

	};

	/**
	 * 用于去初始化listview，并显示的handler
	 */
	Handler hand3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				// 404页面
				Toast.makeText(CourseDetailActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				// 传输失败
				Toast.makeText(CourseDetailActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
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
				List<StudentSignRate> newList = myJson
						.getOneStudentSignRateOfOneCourse(result);
				if (newList.size() != 0) {

					for (StudentSignRate t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(CourseDetailActivity.this, "已经没有了。。", 1)
							.show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有签到记录情况");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);

					}
				}

				myStudentSignRateAdapter.notifyDataSetChanged();

			}
			myStudentSignRateAdapter.notifyDataSetChanged();
		};
	};

	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			CourseDetailActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			allStudentListUrl = Model.GETSTUDENTLISTCOURSERATE + "course_id="
					+ courseInfo.getCourseName() + "&start=" + mStart
					+ "&count" + Model.INIT_COUNT;
			ThreadPoolUtils
					.execute(new HttpGetThread(hand3, allStudentListUrl));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			CourseDetailActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			allStudentListUrl = Model.GETSTUDENTLISTCOURSERATE + "course_id="
					+ courseInfo.getCourseName() + "&start=" + mStart
					+ "&count" + 5;
			ThreadPoolUtils
					.execute(new HttpGetThread(hand3, allStudentListUrl));
		}

	}

}
