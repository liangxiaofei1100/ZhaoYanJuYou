package com.zhaoyan.communication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.util.AppUtil;
import com.dreamlink.communication.lib.util.Notice;
import com.zhaoyan.common.file.APKFile;
import com.zhaoyan.common.file.FileManager;
import com.zhaoyan.common.file.SingleMediaScanner;
import com.zhaoyan.common.util.Log;
import com.zhaoyan.communication.FileReceiver.OnReceiveListener;
import com.zhaoyan.communication.FileSender.OnFileSendListener;
import com.zhaoyan.communication.SocketCommunicationManager.OnFileTransportListener;
import com.zhaoyan.communication.protocol.FileTransferInfo;
import com.zhaoyan.juyou.R;
import com.zhaoyan.juyou.common.FileInfoManager;
import com.zhaoyan.juyou.common.HistoryInfo;
import com.zhaoyan.juyou.common.HistoryManager;
import com.zhaoyan.juyou.common.ZYConstant;
import com.zhaoyan.juyou.common.ZYConstant.Extra;
import com.zhaoyan.juyou.provider.JuyouData;

/**
 * 2013年9月16日 该service目前做以下几件事情 1.接受发送文件的广播
 * 2.收到广播后，根据广播中的内容，开始发送文件，然后根据发送的回调更新数据库 3.文件接收：注册文件接收监听器，边接收，边更新数据库
 */
public class FileTransferService extends Service implements
		OnFileTransportListener, OnFileSendListener, OnReceiveListener {
	private static final String TAG = "FileTransferService";

	public static final String ACTION_SEND_FILE = "com.zhaoyan.communication.FileTransferService.ACTION_SEND_FILE";

	private Notice mNotice;
	private SocketCommunicationManager mSocketMgr;
	private FileInfoManager mFileInfoManager = null;
	private HistoryManager mHistoryManager = null;
	private UserManager mUserManager = null;
	private PackageManager pm = null;

	private Map<Object, Uri> mTransferMap = new ConcurrentHashMap<Object, Uri>();
	private int mAppId = -1;

	private Map<Object, SendFileThread> mSendingFileThreadMap = new ConcurrentHashMap<Object, SendFileThread>();
	private Queue<SendFileThread> mSendQueue = new ConcurrentLinkedQueue<SendFileThread>();

	/** Thread pool */
	private ExecutorService mExecutorService = Executors.newCachedThreadPool();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	BroadcastReceiver transferReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "onReceive.action:" + action);
			if (ACTION_SEND_FILE.equals(action)) {
				handleSendFileRequest(intent);
			}
		}
	};

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		// register broadcast
		IntentFilter filter = new IntentFilter(ACTION_SEND_FILE);
		registerReceiver(transferReceiver, filter);

		mNotice = new Notice(this);
		mFileInfoManager = new FileInfoManager(this);
		mHistoryManager = new HistoryManager(this);
		mSocketMgr = SocketCommunicationManager.getInstance(this);
		mAppId = AppUtil.getAppID(this);
		Log.d(TAG, "mappid=" + mAppId);
		mSocketMgr.registerOnFileTransportListener(this, mAppId);

		mUserManager = UserManager.getInstance();
		pm = getPackageManager();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "FileTransferService.onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	public void handleSendFileRequest(Intent intent) {
		Bundle bundle;
		bundle = intent.getExtras();
		if (null != bundle) {
			List<String> pathLists = bundle
					.getStringArrayList(Extra.SEND_FILES);
			List<User> userList = bundle
					.getParcelableArrayList(Extra.SEND_USERS);
			for (String path : pathLists) {
				sendFile(path, userList);
			}

			SendFileThread thread = mSendQueue.peek();
			if (thread != null && !thread.isSendSending()) {
				mExecutorService.execute(thread);
			}
		}
	}

	public void sendFile(String path, List<User> list) {
		File file = new File(path);
		sendFile(file, list);
	}

	public void sendFile(File file, List<User> list) {
		Log.d(TAG, "sendFile.name:" + file.getName());
		for (int i = 0; i < list.size(); i++) {
			User receiverUser = list.get(i);
			Log.d(TAG, "receiverUser[" + i + "]=" + receiverUser.getUserName());
			SendFileThread sendFileThread = new SendFileThread(file,
					receiverUser);
			mSendQueue.offer(sendFileThread);
		}
	}

	class SendFileThread extends Thread {
		private File file = null;
		private User receiveUser = null;
		private Object key = null;
		private boolean isFileSending = false;

		SendFileThread(File file, User receiveUser) {
			this.file = file;
			this.receiveUser = receiveUser;
			addToSendQueue();
		}

		@Override
		public void run() {
			isFileSending = true;
			mSendingFileThreadMap.put(key, this);
			startSendFile();
		}

		private void addToSendQueue() {
			HistoryInfo historyInfo = new HistoryInfo();
			historyInfo.setFile(file);
			historyInfo.setFileSize(file.length());
			historyInfo.setReceiveUser(receiveUser);
			historyInfo.setSendUserName(getResources().getString(R.string.me));
			historyInfo.setMsgType(HistoryManager.TYPE_SEND);
			historyInfo.setDate(System.currentTimeMillis());
			historyInfo.setStatus(HistoryManager.STATUS_PRE_SEND);
			historyInfo.setFileType(FileManager.getFileType(
					getApplicationContext(), file));
			if (FileManager.TYPE_APK == FileManager.getFileType(
					getApplicationContext(), file)) {
				byte[] fileIcon = FileTransferInfo
						.bitmapToByteArray(FileTransferInfo
								.drawableToBitmap(APKFile.getApkIcon(
										getApplicationContext(),
										file.getAbsolutePath())));
				historyInfo.setIcon(fileIcon);
			}

			ContentValues values = mHistoryManager.getInsertValues(historyInfo);
			Uri uri = getContentResolver().insert(
					JuyouData.History.CONTENT_URI, values);
			key = new Object();
			// save file & uri map
			mTransferMap.put(key, uri);
		}

		private void startSendFile() {
			// 当有一个文件要发送的时候，先将其插入到数据库表中
			ContentValues values = new ContentValues();
			values.put(JuyouData.History.STATUS, HistoryManager.STATUS_PRE_SEND);
			getContentResolver().update(getFileUri(key), values, null, null);
			mSocketMgr.sendFile(file, FileTransferService.this, receiveUser,
					mAppId, key);
		}

		public boolean isSendSending() {
			return isFileSending;
		}

		public void setSendFinished() {
			isFileSending = false;
		}
	}

	@Override
	public void onReceiveFile(FileReceiver fileReceiver) {
		String fileName = fileReceiver.getFileTransferInfo().getFileName();
		String sendUserName = fileReceiver.getSendUser().getUserName();
		File file = null;
		long fileSize = fileReceiver.getFileTransferInfo().getFileSize();
		byte[] fileIcon = fileReceiver.getFileTransferInfo().getFileIcon();

		Log.d(TAG, "onReceiveFile:" + fileName + "," + sendUserName + ",size="
				+ fileSize);
		// define a file to save the receive file
		File fileDir = new File(ZYConstant.DEFAULT_RECEIVE_FILE_FOLDER);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		String filePath = ZYConstant.DEFAULT_RECEIVE_FILE_FOLDER
				+ File.separator + fileName;
		file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "create file error:" + e.toString());
				mNotice.showToast("can not create the file:" + fileName);
				return;
			}
		} else {
			// if file is exist,auto rename
			fileName = FileInfoManager.autoRename(fileName);
			while (new File(ZYConstant.DEFAULT_RECEIVE_FILE_FOLDER
					+ File.separator + fileName).exists()) {
				fileName = FileInfoManager.autoRename(fileName);
			}
			filePath = ZYConstant.DEFAULT_RECEIVE_FILE_FOLDER + File.separator
					+ fileName;
			file = new File(filePath);
		}

		HistoryInfo historyInfo = new HistoryInfo();
		historyInfo.setFile(file);
		historyInfo.setFileSize(fileSize);
		historyInfo.setSendUserName(sendUserName);
		historyInfo.setReceiveUser(mUserManager.getLocalUser());
		historyInfo.setMsgType(HistoryManager.TYPE_RECEIVE);
		historyInfo.setDate(System.currentTimeMillis());
		historyInfo.setStatus(HistoryManager.STATUS_PRE_RECEIVE);
		historyInfo.setFileType(FileManager.getFileType(
				getApplicationContext(), file));
		if (fileIcon == null || fileIcon.length == 0) {
			Log.d(TAG, "onReceiveFile file icon is null.");
		} else {
			historyInfo.setIcon(fileIcon);
			Log.d(TAG, "onReceiveFile file icon data size = " + fileIcon.length);
		}

		ContentValues values = mHistoryManager.getInsertValues(historyInfo);
		Uri uri = getContentResolver().insert(JuyouData.History.CONTENT_URI,
				values);
		Object key = new Object();
		mTransferMap.put(key, uri);
		Log.d(TAG, "onReceiveFile.mTransferMap.size=" + mTransferMap.size());

		fileReceiver.receiveFile(file, FileTransferService.this, key);
	}

	@Override
	public void onSendProgress(long sentBytes, File file, Object key) {
		ContentValues values = new ContentValues();
		values.put(JuyouData.History.STATUS, HistoryManager.STATUS_SENDING);
		values.put(JuyouData.History.PROGRESS, sentBytes);

		getContentResolver().update(getFileUri(key), values, null, null);
	}

	@Override
	public void onSendFinished(boolean success, File file, Object key) {
		int status;
		if (success) {
			status = HistoryManager.STATUS_SEND_SUCCESS;
		} else {
			status = HistoryManager.STATUS_SEND_FAIL;
		}

		ContentValues values = new ContentValues();
		values.put(JuyouData.History.STATUS, status);
		getContentResolver().update(getFileUri(key), values, null, null);

		SendFileThread thread = mSendingFileThreadMap.get(key);
		if (thread != null) {
			thread.setSendFinished();
			mSendingFileThreadMap.remove(key);
			mSendQueue.remove();
		}

		thread = mSendQueue.peek();
		if (thread != null) {
			mExecutorService.execute(thread);
		}
	}

	/***
	 * get the transfer file'uri that in the db
	 * 
	 * @return
	 */
	public Uri getFileUri(Object key) {
		return mTransferMap.get(key);
	}

	@Override
	public void onReceiveProgress(long receivedBytes, File file, Object key) {
		ContentValues values = new ContentValues();
		values.put(JuyouData.History.STATUS, HistoryManager.STATUS_SENDING);
		values.put(JuyouData.History.PROGRESS, receivedBytes);
		getContentResolver().update(getFileUri(key), values, null, null);
	}

	@Override
	public void onReceiveFinished(boolean success, File file, Object key) {
		int status;
		if (success) {
			status = HistoryManager.STATUS_SEND_SUCCESS;
			new SingleMediaScanner(getApplicationContext(), file);
		} else {
			status = HistoryManager.STATUS_SEND_FAIL;
			if (file.exists()) {
				file.delete();
			}
		}

		ContentValues values = new ContentValues();
		values.put(JuyouData.History.STATUS, status);
		getContentResolver().update(getFileUri(key), values, null, null);

		mTransferMap.remove(key);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		mSocketMgr.unregisterOnFileTransportListener(this);
		unregisterReceiver(transferReceiver);

		mSendingFileThreadMap.clear();
		mSendQueue.clear();
	}

}