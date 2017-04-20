package csust.teacher.adapter;

import java.util.List;

import csust.teacher.activity.R;
import csust.teacher.adapter.MyListAdapter.Holder;
import csust.teacher.info.CourseInfo;
import csust.teacher.info.StudentSignRate;
import csust.teacher.model.Model;
import csust.teacher.utils.LoadImg;
import csust.teacher.utils.LoadImg.ImageDownloadCallBack;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于展示在每一门课的签到结果中，的每个学生的listview
 * 
 * @author U-anLA
 *
 */
public class MyStudentSignRateAdapter extends BaseAdapter {

	private List<StudentSignRate> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;
	private boolean upFlag = false;
	private boolean downFlag = false;
	
	//保存listviewItem的长度.
	private int myLength;
	
	//用于存储页面传来的引用。
	private LinearLayout mLinearLayout;

	public int getMyLength() {
		return myLength;
	}

	public void setMyLength(int myLength) {
		this.myLength = myLength;
	}

	public MyStudentSignRateAdapter(Context ctx, List<StudentSignRate> list,LinearLayout mLinearLayout) {
		this.list = list;
		this.ctx = ctx;
		// 加载图像
		loadImgHeadImg = new LoadImg(ctx);
		this.mLinearLayout = mLinearLayout;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder hold;

		if (convertView == null) {
			hold = new Holder();
			
			convertView = View.inflate(ctx,
					R.layout.mylistview_item_student_sign_rate, null);
			hold.studentPic = (ImageView) convertView
					.findViewById(R.id.signRateList_student_Pic);
			hold.haveSign = (TextView) convertView
					.findViewById(R.id.signRateList_haveSign);
			hold.allSignCount = (TextView) convertView
					.findViewById(R.id.signRateList_allCountSign);
			hold.signRate = (TextView) convertView
					.findViewById(R.id.signRateList_signRate);
			hold.studentName = (TextView) convertView
					.findViewById(R.id.signRateList_studentName);
			//下面这种方法获得view的高度。
			convertView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),  
	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));  
			myLength = convertView.getMeasuredHeight();  
			//通过前一个引用传来的linearlayout来改变
			LinearLayout.LayoutParams  hint_page_params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					getMyLength()*list.size());
			
			

			mLinearLayout.setLayoutParams(hint_page_params);

			convertView.setTag(hold);
		} else {
			hold = (Holder) convertView.getTag();
		}

		
		Log.i("myLength", myLength+"");
		hold.allSignCount.setText(list.get(position).getAllSignCount()+"");
		hold.haveSign.setText(list.get(position).getHave_sign()+"");
		hold.signRate.setText(list.get(position).getRate() + "%");
		hold.studentName.setText(list.get(position).getStudent_name()+"");

		// 设置监听
		hold.studentPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 用于和教师会话~，后期实现。
				if (Model.MYUSERINFO != null) {
					// 已登录
				} else {
					Toast.makeText(ctx, "可以点击哦...", 1).show();
				}
			}
		});

		hold.studentPic.setImageResource(R.drawable.default_users_avatar);
		if (list.get(position).getStudent_username().equalsIgnoreCase("")) {
			hold.studentPic.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.studentPic.setTag(Model.USERHEADURL
					+ list.get(position).getStudent_username());
			Bitmap bitTeacher = loadImgHeadImg.loadImage(hold.studentPic,
					Model.USERHEADURL
							+ list.get(position).getStudent_username(),
					new ImageDownloadCallBack() {
						@Override
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							if (position >= list.size()) {
								if (hold.studentPic.getTag().equals(
										Model.USERHEADURL
												+ list.get(position - 1)
														.getStudent_username())) {
									hold.studentPic.setImageBitmap(bitmap);
								}
							} else {
								if (hold.studentPic.getTag().equals(
										Model.USERHEADURL
												+ list.get(position)
														.getStudent_username())) {
									hold.studentPic.setImageBitmap(bitmap);
								}
							}
						}
					});
			if (bitTeacher != null) {
				hold.studentPic.setImageBitmap(bitTeacher);
			}
		}

		return convertView;

	}

	static class Holder {
		ImageView studentPic;
		TextView studentName;
		TextView haveSign;
		TextView allSignCount;
		TextView signRate;
	}

}
