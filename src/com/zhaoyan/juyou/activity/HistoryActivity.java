package com.zhaoyan.juyou.activity;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dreamlink.communication.lib.util.Notice;
import com.zhaoyan.common.util.Log;
import com.zhaoyan.common.util.ZYUtils;
import com.zhaoyan.juyou.R;
import com.zhaoyan.juyou.adapter.HistoryCursorAdapter;
import com.zhaoyan.juyou.provider.JuyouData;

public class HistoryActivity extends BaseActivity implements OnScrollListener,
		OnItemClickListener {
	private static final String TAG = "HistoryActivityTest";
	private Context mContext;

	// view
	private TextView mStorageTV;
	private ListView mHistoryMsgLV;
	private ProgressBar mLoadingBar;

	// adapter
	private HistoryCursorAdapter mAdapter;

	private Notice mNotice;

	// msg
	private static final int MSG_UPDATE_UI = 1;

	private QueryHandler queryHandler = null;

	private static final String[] PROJECTION = { JuyouData.History._ID,
			JuyouData.History.FILE_PATH, JuyouData.History.FILE_NAME,
			JuyouData.History.FILE_SIZE, JuyouData.History.SEND_USERNAME,
			JuyouData.History.RECEIVE_USERNAME, JuyouData.History.PROGRESS,
			JuyouData.History.DATE, JuyouData.History.STATUS,
			JuyouData.History.MSG_TYPE, JuyouData.History.FILE_TYPE,
			JuyouData.History.FILE_ICON };

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_UI:
				int num = msg.arg1;
				updateTitleNum(num);
				break;

			default:
				break;
			}
		};
	};

	class HistoryContent extends ContentObserver {
		public HistoryContent(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			int count = mAdapter.getCount();
			updateUI(count);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.history);
		mContext = this;
		mNotice = new Notice(mContext);

		initTitle(R.string.history);
		mTitleNumView.setVisibility(View.VISIBLE);

		initView();
		queryHandler = new QueryHandler(getContentResolver());

		HistoryContent historyContent = new HistoryContent(new Handler());
		getContentResolver().registerContentObserver(
				JuyouData.History.CONTENT_URI, true, historyContent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		query();
	}

	@Override
	protected void onDestroy() {
		if (mAdapter != null) {
			Cursor cursor = mAdapter.getCursor();
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		super.onDestroy();
	}

	public void query() {
		queryHandler.startQuery(11, null, JuyouData.History.CONTENT_URI,
				PROJECTION, null, null, JuyouData.History.SORT_ORDER_DEFAULT);
	}

	@SuppressLint("NewApi")
	private void initView() {
		mStorageTV = (TextView) findViewById(R.id.tv_storage);
		String space = getResources().getString(
				R.string.storage_space,
				ZYUtils.getFormatSize(Environment.getExternalStorageDirectory()
						.getTotalSpace()),
				ZYUtils.getFormatSize(Environment.getExternalStorageDirectory()
						.getFreeSpace()));
		mStorageTV.setText(space);
		mLoadingBar = (ProgressBar) findViewById(R.id.pb_history_loading);
		mHistoryMsgLV = (ListView) findViewById(R.id.lv_history_msg);
		mHistoryMsgLV.setOnScrollListener(this);
		mHistoryMsgLV.setOnItemClickListener(this);

		mAdapter = new HistoryCursorAdapter(mContext, mHistoryMsgLV);
		mHistoryMsgLV.setAdapter(mAdapter);
		mHistoryMsgLV.setSelection(0);
	}

	// query db
	private class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			Log.d(TAG, "onQueryComplete");
			mLoadingBar.setVisibility(View.INVISIBLE);
			int num = 0;
			if (null != cursor) {
				Log.d(TAG, "onQueryComplete.count=" + cursor.getCount());
				mAdapter.swapCursor(cursor);
				num = cursor.getCount();
			}

			updateUI(num);
		}

	}

	private void updateUI(int num) {
		Message message = mHandler.obtainMessage();
		message.arg1 = num;
		message.what = MSG_UPDATE_UI;
		message.sendToTarget();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mNotice.showToast(position + "" + ",view = " + view);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			mAdapter.setIdleFlag(false);
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			mAdapter.setIdleFlag(true);
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			mAdapter.setIdleFlag(false);
			break;

		default:
			break;
		}
		mAdapter.notifyDataSetChanged();
	}
}