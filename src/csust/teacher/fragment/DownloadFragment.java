package csust.teacher.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;

import csust.teacher.activity.LoginActivity;
import csust.teacher.activity.R;
import csust.teacher.activity.ReleaseSignInfoActivity;
import csust.teacher.adapter.MySignListAdapter;
import csust.teacher.download.BaseDownloadHolder;
import csust.teacher.download.DownloadInfo;
import csust.teacher.download.DownloadManager;
import csust.teacher.download.DownloadRequestCallBack;
import csust.teacher.download.DownloadService;
import csust.teacher.model.Model;
import csust.teacher.utils.MyJson;

/**
 * 教师端的展示正在下载的签到报表。
 * 
 * @author U-anLA
 *
 */

public class DownloadFragment extends Fragment implements OnClickListener {

	private static final int KEY_1 = 0;
	private static final int KEY_2 = 1;
	private static final int KEY_3 = 2;
	private ListView act_download_list;
	private DownloadManager downloadManager;
	private List<DownloadInfo> downloadFinishInfos;// 完成下载的列表
	private List<DownloadInfo> downloadingInfos;// 未完成下载的列表
	private List<DownloadInfo> downloadInfoList;// 下载列表中的所有下载对象
	private MyAdapter myAdapter;// 适配器

	private View view;
	private ImageView mTopImg, mAddSignInfo;

	private TextView mTopMenuOne;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private DownloadFragmentCallBack mDownloadFragmentCallBack;
	private MyJson myJson = new MyJson();

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

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_download, null);
		ctx = view.getContext();
		// 这是鸡肋，可能需要改！！！！！！
		if (downloadManager == null) {
            downloadManager = DownloadService.getDownloadManager(ctx);
        }
		initView();
		return view;
	}

	private void initView() {
		downloadFinishInfos = new ArrayList<DownloadInfo>();
	    downloadingInfos = new ArrayList<DownloadInfo>();
		load_progressBar = (LinearLayout) view
				.findViewById(R.id.load_progressBar);
		mLinearLayout = (LinearLayout) view.findViewById(R.id.HomeGroup);

		mTopImg = (ImageView) view.findViewById(R.id.Menu);
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
		mAddSignInfo = (ImageView) view.findViewById(R.id.AddSignInfo);
		act_download_list = (ListView) view.findViewById(R.id.act_download_list);
		mTopImg.setOnClickListener(this);
		mAddSignInfo.setOnClickListener(this);
		HomeNoValue.setVisibility(View.GONE);

		myAdapter = new MyAdapter();
        act_download_list.setAdapter(myAdapter);
        
		if (Model.MYUSERINFO != null) {
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.VISIBLE);
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}

	}

	public void setCallBack(DownloadFragmentCallBack mDownloadFragmentCallBack) {
		this.mDownloadFragmentCallBack = mDownloadFragmentCallBack;
	}

	public interface DownloadFragmentCallBack {
		public void callback(int flag);
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.Menu:
			mDownloadFragmentCallBack.callback(R.id.Menu);
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
								}
							}).setNegativeButton("取消", null).show();
			// 注意这里是防止再次出发单词点击实际，如果是false，就会出发单词短点击事件
			return true;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次onresum时候，就要把homenovalue设为false
		mStart = 0;
		HomeNoValue.setVisibility(View.GONE);
		if (isPause == false) {
			return;
		}

		if (Model.MYUSERINFO != null) {
			isFirst = true;
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.VISIBLE);
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
	
	//三个大内部类
	 private class MyAdapter extends BaseAdapter {

	        public MyAdapter() {
	        	Log.d("ee", "ee");
	            downloadInfoList = downloadManager.getDownloadInfoList();
	            initData();
	        }

	        /**
	         * 初始化下载完成/正在下载集合中的数据
	         */
	        public void initData() {
	            downloadFinishInfos.clear();
	            downloadingInfos.clear();
	            for (DownloadInfo downloadInfo : downloadInfoList) {
	                if (downloadInfo.getState() == HttpHandler.State.SUCCESS) {
	                    downloadFinishInfos.add(0, downloadInfo);
	                } else {
	                    downloadingInfos.add(0, downloadInfo);
	                }
	            }
	        }

	        @Override
	        public int getViewTypeCount() {
	            return 3;
	        }

	        @Override
	        public int getItemViewType(int position) {
	            if (position == 0) {
	                return KEY_1;
	            } else if (position == downloadingInfos.size() + 1) {
	                return KEY_2;
	            } else {
	                return KEY_3;
	            }
	        }

	        @Override
	        public int getCount() {
	            return downloadManager.getDownloadInfoListCount() + 2;
	        }

	        @Override
	        public Object getItem(int position) {
	            return null;
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            int itemViewType = getItemViewType(position);
	            BaseDownloadHolder holder = null;
	            if (convertView == null) {
	                switch (itemViewType) {
	                    case KEY_1:
	                        convertView = LayoutInflater.from(ctx).inflate(R.layout.activity_downloadlist_downloadingtab, null);
	                        holder = new Hodler1();
	                        ((Hodler1) holder).act_downloadlist_tv_ing = (TextView) convertView.findViewById(R.id.act_downloadlist_tv_ing);
	                        break;
	                    case KEY_2:
	                        convertView = LayoutInflater.from(ctx).inflate(R.layout.activity_downloadlist_downloadfinishtab, null);
	                        holder = new Hodler2();
	                        ((Hodler2) holder).act_download_tv_finish = (TextView) convertView.findViewById(R.id.act_download_tv_finish);
	                        ((Hodler2) holder).act_download_tv_clean = (TextView) convertView.findViewById(R.id.act_download_tv_clean);
	                        //清空记录操作
	                        ((Hodler2) holder).act_download_tv_clean.setOnClickListener(new View.OnClickListener() {
	                            @Override
	                            public void onClick(View v) {
	                                int size = downloadFinishInfos.size();
	                                for (int i = size-1; i >= 0; i-- ) {
	                                    DownloadInfo delInfo = downloadFinishInfos.get(i);
	                                    String fileSavePath = delInfo.getFileSavePath();
	                                    File file = new File(fileSavePath);
	                                    //文件存在删除文件
	                                    if(file.exists()){
	                                        file.delete();
	                                    }
	                                    //清空数据库记录
	                                    try {
	                                        downloadManager.removeDownload(delInfo);
	                                    } catch (DbException e) {
	                                        e.printStackTrace();
	                                    }

	                                    initData();
	                                    notifyDataSetChanged();
	                                }
	                            }
	                        });
	                        break;
	                    case KEY_3:
	                        convertView = View.inflate(ctx, R.layout.activity_list_view_item, null);
	                        holder = new Hodler3();
	                        ((Hodler3) holder).download_label = (TextView) convertView.findViewById(R.id.download_label);
	                        ((Hodler3) holder).download_state = (TextView) convertView.findViewById(R.id.download_state);
	                        ((Hodler3) holder).download_pb = (ProgressBar) convertView.findViewById(R.id.download_pb);
	                        ((Hodler3) holder).download_remove_btn = (Button) convertView.findViewById(R.id.download_remove_btn);
	                        ((Hodler3) holder).download_stop_btn = (Button) convertView.findViewById(R.id.download_stop_btn);
	                        break;
	                }
	                convertView.setTag(holder);
	            } else {
	                switch (itemViewType) {
	                    case KEY_1:
	                        holder = (Hodler1) convertView.getTag();
	                        break;
	                    case KEY_2:
	                        holder = (Hodler2) convertView.getTag();
	                        break;
	                    case KEY_3:
	                        holder = (Hodler3) convertView.getTag();
	                        break;
	                }
	            }
	            //填充数据
	            switch (itemViewType) {
	                case KEY_1:
	                    ((Hodler1) holder).act_downloadlist_tv_ing.setText("进行中(" + downloadingInfos.size() + ")");
	                    break;
	                case KEY_2:
	                    ((Hodler2) holder).act_download_tv_finish.setText("已完成(" + downloadFinishInfos.size() + ")");

	                    break;
	                case KEY_3:
	                    //=======================获取对应位置的holder设置数据===============================
	                    if (position < downloadingInfos.size() + 1) {//下载中
	                        // 因为显示进行中数量的布局占用了一个位置，在集合中对应位置必须-1
	                        final DownloadInfo downloadInfo = downloadingInfos.get(position - 1);
	                        holder.update(downloadInfo);
	                        setHolderData((Hodler3) holder, downloadInfo, downloadManager);
	                    } else if (position >= downloadingInfos.size() + 1 + 1) {//下载完成
	                        // 因为显示进行中和下载完成数量的布局占用了二个位置，在集合中对应位置必须减去下载中集合的总数再-1-1
	                        final DownloadInfo downloadInfo = downloadFinishInfos.get(position - downloadingInfos.size() - 1 - 1);
	                        holder.update(downloadInfo);
	                        setHolderData((Hodler3) holder, downloadInfo, downloadManager);
	                    }
	                    break;
	            }
	            return convertView;
	        }

	        /**
	         * 为holder设置相关参数
	         *
	         * @param holder
	         * @param downloadInfo
	         */
	        private void setHolderData(final Hodler3 holder, final DownloadInfo downloadInfo, final DownloadManager downloadManager) {
	            if (downloadInfo != null) {
	                holder.download_label.setText(downloadInfo.getFileName());
	                holder.download_state.setText(downloadInfo.getState() + "");
	                //暂停、继续、重试按钮点击事件
	                holder.download_stop_btn.setOnClickListener(new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                        try {
	                            if(downloadInfo.getState() == HttpHandler.State.CANCELLED || downloadInfo.getState() == HttpHandler.State.FAILURE){
	                                downloadManager.resumeDownload(downloadInfo,new DownloadRequestCallBack());
	                            }else if(downloadInfo.getState() == HttpHandler.State.LOADING){
	                                downloadManager.stopDownload(downloadInfo);
	                            }
	                            //更新Listview数据
	                            initData();
	                            notifyDataSetChanged();
	                        } catch (DbException e) {
	                            e.printStackTrace();
	                        }
	                    }
	                });
	                //取消按钮点击事件
	                holder.download_remove_btn.setOnClickListener(new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                        try {
	                            downloadManager.removeDownload(downloadInfo);
	                            File file = new File(downloadInfo.getFileSavePath());
	                            if(file.exists()){
	                                file.delete();
	                            }
	                            //更新Listview数据
	                            initData();
	                            notifyDataSetChanged();
	                        } catch (DbException e) {
	                            e.printStackTrace();
	                        }
	                    }
	                });
	            }

	            //*****************设置更新进度回调**********************
	            HttpHandler<File> handler = downloadInfo.getHandler();
	            if (handler != null) {
	                DownloadManager.ManagerCallBack requestCallBack = (DownloadManager.ManagerCallBack) handler.getRequestCallBack();
	                if (requestCallBack.getBaseCallBack() == null) {
	                    requestCallBack.setBaseCallBack(new DownloadRequestCallBack());
	                }
	                requestCallBack.setUserTag(new WeakReference<Hodler3>(holder));
	            }
	        }

	    }
	    //正在下载hodler
	    private class Hodler1 extends BaseDownloadHolder {
	        TextView act_downloadlist_tv_ing;//正在下载

	        @Override
	        public void refresh() {

	        }

	    }
	    //下载完成hodler
	    private class Hodler2 extends BaseDownloadHolder {
	        TextView act_download_tv_finish;//下载完成标签
	        TextView act_download_tv_clean;//清楚记录

	        @Override
	        public void refresh() {

	        }

	    }
	    //下载itme hodler
	    private class Hodler3 extends BaseDownloadHolder {
	        public TextView download_label;//应用名称
	        public TextView download_state;//状态
	        public ProgressBar download_pb;//进度条
	        public Button download_remove_btn;//取消按钮
	        public Button download_stop_btn;//暂停继续重试按钮

	        @Override
	        public void refresh() {
	            //更新下载进度
	            if (downloadInfo != null) {
	                if (downloadInfo.getFileLength() > 0) {
	                    //根据当前下载大小和APP大小计算出的进度百分比
	                    int prosress = (int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength());
	                    if (prosress >= 100) {
	                        download_pb.setProgress(100);
	                        myAdapter.initData();
	                        myAdapter.notifyDataSetChanged();
	                    }else{
	                        download_pb.setProgress(prosress);
	                        download_state.setText(downloadInfo.getState()+"");
	                    }
	                } else {
	                    //APP大小为0时（一般不会遇到除非数据出错）
	                    download_pb.setProgress(0);
	                }
	            }
	        }

	    }
	

	
	
	
}
