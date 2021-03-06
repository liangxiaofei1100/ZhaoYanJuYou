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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler.Callback;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.util.AppUtil;
import com.dreamlink.communication.lib.util.Notice;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.file.FileManager;
import com.zhaoyan.common.file.SingleMediaScanner;
import com.zhaoyan.common.util.BitmapUtilities;
import com.zhaoyan.common.util.Log;
import com.zhaoyan.communication.FileReceiver.OnReceiveListener;
import com.zhaoyan.communication.FileSender.OnFileSendListener;
import com.zhaoyan.communication.ProtocolCommunication.OnFileTransportListener;
import com.zhaoyan.juyou.R;
import com.zhaoyan.juyou.common.AppInfo;
import com.zhaoyan.juyou.common.AppManager;
import com.zhaoyan.juyou.common.FileInfoManager;
import com.zhaoyan.juyou.common.HistoryInfo;
import com.zhaoyan.juyou.common.HistoryManager;
import com.zhaoyan.juyou.common.ZYConstant;
import com.zhaoyan.juyou.common.ZYConstant.Extra;
import com.zhaoyan.juyou.provider.AppData;
import com.zhaoyan.juyou.provider.JuyouData;
import com.zhaoyan.communication.FileSender;
import com.zhaoyan.communication.FileReceiver;

/**
 * 2013年9月16日 该service目前做以下几件事情 1.接受发送文件的广播
 * 2.收到广播后，根据广播中的内容，开始发送文件，然后根据发送的回调更新数据库 3.文件接收：注册文件接收监听器，边接收，边更新数据库
 */
public class FileTransferService extends Service implements
		OnFileTransportListener, OnFileSendListener, OnReceiveListener {
	private static final String TAG = "FileTransferService";

	public static final String ACTION_SEND_FILE = "com.zhaoyan.communication.FileTransferService.ACTION_SEND_FILE";
	public static final String ACTION_NOTIFY_SEND_OR_RECEIVE = "com.zhaoyan.communication.FileTransferService.ACTION_NOTIFY_SEND_OR_RECEIVE";
	public static final String ACTION_CANCEL_SEND = "com.zhaoyan.communication.FileTransferService.ACTION_CANCEL_SEND";
	public static final String ACTION_CANCEL_RECEIVE = "com.zhaoyan.communication.FileTransferService.ACTION_CANCEL_RECEIVE";

	// show badgeview or not
	public static final String EXTRA_BADGEVIEW_SHOW = "badgeview_show";

	private Notice mNotice;
	private ProtocolCommunication mProtocolCommunication;
	private HistoryManager mHistoryManager = null;
	private UserManager mUserManager = null;

	// uri string <==> key object
	private Map<String, Object> mUriMap = new ConcurrentHashMap<String, Object>();
	// key object <==> FileReceiver
	private Map<Object, FileReceiver> mFileReceiverMap = new ConcurrentHashMap<Object, FileReceiver>();

	private Map<Object, Uri> mTransferMap = new ConcurrentHashMap<Object, Uri>();
	private int mAppId = -1;

	private Map<Object, SendFileThread> mSendingFileThreadMap = new ConcurrentHashMap<Object, SendFileThread>();
	private Queue<SendFileThread> mSendQueue = new ConcurrentLinkedQueue<SendFileThread>();

	/** Thread pool */
	private ExecutorService mExecutorService = Executors.newCachedThreadPool();

	FileSender mFileSender = null;

	private static final int MSG_SEND_FILE_REQUEST = 1;
	private ProcessCommandThread mHandlerThread;
	private Handler mHandler;

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
			} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				// get install or uninstall app package name
				String packageName = intent.getData().getSchemeSpecificPart();

				// get installed app
				AppInfo appInfo = null;
				try {
					ApplicationInfo info = getPackageManager()
							.getApplicationInfo(packageName, 0);
					appInfo = new AppInfo(getApplicationContext(), info);
					appInfo.setPackageName(packageName);
					appInfo.setAppIcon(info.loadIcon(getPackageManager()));
					appInfo.loadLabel();
					appInfo.loadVersion();
					if (AppManager.isGameApp(context, packageName)) {
						appInfo.setType(AppManager.GAME_APP);
					} else {
						appInfo.setType(AppManager.NORMAL_APP);
					}
					ContentValues values = AppManager
							.getValuesByAppInfo(appInfo);
					getContentResolver()
							.insert(AppData.App.CONTENT_URI, values);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				// get install or uninstall app package name
				String packageName = intent.getData().getSchemeSpecificPart();
				Uri uri = Uri
						.parse(AppData.App.CONTENT_URI + "/" + packageName);
				getContentResolver().delete(uri, null, null);
			} else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				Log.d(TAG, "onReceive:ACTION_MEDIA_MOUNTED");
				mNotice.showToast("SD卡可用");
			} else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_REMOVED.equals(action)
					|| Intent.ACTION_MEDIA_SHARED.equals(action)) {
				Log.d(TAG, "onReceive:ACTION_MEDIA_UNMOUNTED");
				mNotice.showToast("SD卡已拔出");
			}
		}
	};

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		mHandlerThread = new ProcessCommandThread(
				"HandlerThread-FileTransferService");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread);

		// register broadcast
		IntentFilter filter = new IntentFilter(ACTION_SEND_FILE);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_SHARED);
		registerReceiver(transferReceiver, filter);

		// register appliaction install/remove
		IntentFilter appFilter = new IntentFilter();
		appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		appFilter.addDataScheme("package");
		registerReceiver(transferReceiver, appFilter);

		mNotice = new Notice(this);
		mHistoryManager = new HistoryManager(getApplicationContext());
		mAppId = AppUtil.getAppID(this);
		Log.d(TAG, "mappid=" + mAppId);
		mProtocolCommunication = ProtocolCommunication.getInstance();
		mProtocolCommunication.registerOnFileTransportListener(this, mAppId);

		mUserManager = UserManager.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null != intent) {
			String action = intent.getAction();
			Log.d(TAG, "onStartCommand: action = " + action);

			if (ACTION_CANCEL_SEND.equals(action)) {
				handleCancelSendRequest(intent);
			} else if (ACTION_CANCEL_RECEIVE.equals(action)) {
				handleCancelReceiveRequest(intent);
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Cancel receiving data.
	 * 
	 * @param intent
	 *            Include the storage identifier.
	 * 
	 * @return void
	 */
	private void handleCancelReceiveRequest(Intent intent) {
		Bundle bundle = intent.getExtras();
		String uri = bundle.getString(HistoryManager.HISTORY_URI);
		Log.d(TAG, "handleCancelReceiveRequest: uri = " + uri);

		// 由于系统调用延迟，调用到这里时，数据传输可能刚好完成，此时的key就为null
		Object key = mUriMap.get(uri);
		if (key == null) {
			Log.d(TAG, "handleCancelReceiveRequest: key is null!");
			return;
		}
		FileReceiver currFileReceiver = mFileReceiverMap.get(key);

		if (currFileReceiver != null) {
			// Send Message to Client
			User sendUser = currFileReceiver.getSendUser();
			mProtocolCommunication.cancelReceiveFile(sendUser, mAppId);

			// Close receive thread
			currFileReceiver.cancelReceiveFile();
		} else {
			Log.d(TAG, "currFileReceiver is null!");
		}

	}

	/**
	 * Cancel sending data.
	 * 
	 * @param intent
	 *            Include the storage identifier.
	 * 
	 * @return void
	 */
	private void handleCancelSendRequest(Intent intent) {
		Bundle bundle = intent.getExtras();
		String uri = bundle.getString(HistoryManager.HISTORY_URI);
		Log.d(TAG, "handleCancelSendRequest: uri = " + uri);

		// 由于系统调用延迟，调用到这里时，数据传输可能刚好完成，此时的key就为null
		Object key = mUriMap.get(uri);
		if (key == null) {
			Log.d(TAG, "handleCancelSendRequest: key is null!");
			return;
		}
		SendFileThread currExeSendThread = mSendingFileThreadMap.get(key);
		if (currExeSendThread != null) {
			// Send Message to Client
			User receiverUser = currExeSendThread.getReceiveUser();
			mProtocolCommunication.cancelSendFile(receiverUser, mAppId);

			// Close send thread
			mFileSender.cancelSendFile();
		} else {
			Log.d(TAG, "currExeSendThread is null!");
		}
	}

	public void handleSendFileRequest(Intent intent) {
		Bundle bundle = intent.getExtras();
		Message message = mHandler.obtainMessage(MSG_SEND_FILE_REQUEST);
		message.setData(bundle);
		message.sendToTarget();
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
			// set user head
			historyInfo.setSendUserHeadId(0);
			historyInfo.setSendUserIcon(null);
			//
			historyInfo.setMsgType(HistoryManager.TYPE_SEND);
			historyInfo.setDate(System.currentTimeMillis());
			historyInfo.setStatus(HistoryManager.STATUS_PRE_SEND);
			historyInfo.setFileType(FileManager.getFileType(
					getApplicationContext(), file));
			if (FileManager.APK == FileManager.getFileType(
					getApplicationContext(), file)) {
				byte[] fileIcon = BitmapUtilities
						.bitmapToByteArray(BitmapUtilities
								.drawableToBitmap(APKUtil.getApkIcon2(
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
			// save uri string & key
			mUriMap.put(uri.toString(), key);

			Log.d(TAG, "addToSendQueue: URI = " + uri.toString());
		}

		private void startSendFile() {
			// 当有一个文件要发送的时候，先将其插入到数据库表中
			ContentValues values = new ContentValues();
			values.put(JuyouData.History.STATUS, HistoryManager.STATUS_PRE_SEND);
			getContentResolver().update(getFileUri(key), values, null, null);
			mFileSender = mProtocolCommunication.sendFile(file,
					FileTransferService.this, receiveUser, mAppId, key);
			// when send a file,notify othes that need to know
			sendBroadcastForNotify(true);
		}

		public User getReceiveUser() {
			return receiveUser;
		}

		public boolean isSendSending() {
			return isFileSending;
		}

		public void setSendFinished() {
			isFileSending = false;
		}
	}

	public void sendBroadcastForNotify(boolean show) {
		Intent intent = new Intent(ACTION_NOTIFY_SEND_OR_RECEIVE);
		intent.putExtra(EXTRA_BADGEVIEW_SHOW, show);
		sendBroadcast(intent);
	}

	@Override
	public void onReceiveFile(FileReceiver fileReceiver) {
		String currentRevFolder = "";
		UserInfo userInfo = null;
		String fileName = fileReceiver.getFileTransferInfo().getFileName();
		String sendUserName = fileReceiver.getSendUser().getUserName();
		File file = null;
		long fileSize = fileReceiver.getFileTransferInfo().getFileSize();
		byte[] fileIcon = fileReceiver.getFileTransferInfo().getFileIcon();
		userInfo = UserHelper.getUserInfo(getApplicationContext(),
				fileReceiver.getSendUser());
		int sendUserHeadId = userInfo.getHeadId();
		byte[] sendUserIcon = null;
		Log.d(TAG, "onReceiveFile:" + fileName + "," + sendUserName + ",size="
				+ fileSize + ",sendUserHeadId=" + sendUserHeadId);
		HistoryInfo historyInfo = new HistoryInfo();
		historyInfo.setFileSize(fileSize);
		historyInfo.setSendUserName(sendUserName);
		historyInfo.setSendUserHeadId(sendUserHeadId);
		if (UserInfo.HEAD_ID_NOT_PRE_INSTALL == sendUserHeadId) {
			sendUserIcon = userInfo.getHeadBitmapData();
		}
		historyInfo.setSendUserIcon(sendUserIcon);
		historyInfo.setReceiveUser(mUserManager.getLocalUser());
		historyInfo.setMsgType(HistoryManager.TYPE_RECEIVE);
		historyInfo.setDate(System.currentTimeMillis());
		historyInfo.setStatus(HistoryManager.STATUS_PRE_RECEIVE);

		int fileType = FileManager.getFileTypeByName(getApplicationContext(),
				fileName);
		switch (fileType) {
		case FileManager.APK:
			currentRevFolder = ZYConstant.JUYOU_APP_FOLDER;
			break;
		case FileManager.AUDIO:
			currentRevFolder = ZYConstant.JUYOU_MUSIC_FOLDER;
			break;
		case FileManager.VIDEO:
			currentRevFolder = ZYConstant.JUYOU_VIDEO_FOLDER;
			break;
		case FileManager.IMAGE:
			currentRevFolder = ZYConstant.JUYOU_IMAGE_FOLDER;
			break;
		default:
			currentRevFolder = ZYConstant.JUYOU_OTHER_FOLDER;
			break;
		}
		// define a file to save the receive file
		File fileDir = new File(currentRevFolder);
		if (!fileDir.exists()) {
			boolean ret = fileDir.mkdirs();
			if (!ret) {
				Log.e(TAG, "can not create folder:" + fileDir.getAbsolutePath());
				historyInfo.setStatus(HistoryManager.STATUS_RECEIVE_FAIL);
			}
		}

		String filePath = currentRevFolder + File.separator + fileName;
		file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "create file error:" + e.toString());
				mNotice.showToast("can not create the file:" + fileName);
				historyInfo.setStatus(HistoryManager.STATUS_RECEIVE_FAIL);
			}
		} else {
			// if file is exist,auto rename
			fileName = FileInfoManager.autoRename(fileName);
			while (new File(currentRevFolder + File.separator + fileName)
					.exists()) {
				fileName = FileInfoManager.autoRename(fileName);
			}
			filePath = currentRevFolder + File.separator + fileName;
			file = new File(filePath);
		}

		historyInfo.setFile(file);
		historyInfo.setFileType(FileManager.getFileType(
				getApplicationContext(), filePath));
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
		mUriMap.put(uri.toString(), key);
		mFileReceiverMap.put(key, fileReceiver);
		Log.d(TAG, "onReceiveFile.mTransferMap.size=" + mTransferMap.size());
		Log.d(TAG, "onReceiveFile: URI = " + uri.toString());

		fileReceiver.receiveFile(file, FileTransferService.this, key);
		// when receive a file,notify othes that need to know
		sendBroadcastForNotify(true);
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
		values.put(JuyouData.History.STATUS, HistoryManager.STATUS_RECEIVING);
		values.put(JuyouData.History.PROGRESS, receivedBytes);
		getContentResolver().update(getFileUri(key), values, null, null);
	}

	@Override
	public void onReceiveFinished(boolean success, File file, Object key) {
		int status;
		if (success) {
			status = HistoryManager.STATUS_RECEIVE_SUCCESS;
			new SingleMediaScanner(getApplicationContext(), file);
		} else {
			status = HistoryManager.STATUS_RECEIVE_FAIL;
			if (file.exists()) {
				file.delete();
			}
		}

		ContentValues values = new ContentValues();
		values.put(JuyouData.History.STATUS, status);
		getContentResolver().update(getFileUri(key), values, null, null);

		mUriMap.remove(getFileUri(key).toString());
		mTransferMap.remove(key);
		mFileReceiverMap.remove(key);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		mProtocolCommunication.unregisterOnFileTransportListener(this);
		unregisterReceiver(transferReceiver);

		mSendingFileThreadMap.clear();
		mSendQueue.clear();

		mHandlerThread.quit();
	}

	private class ProcessCommandThread extends HandlerThread implements
			Callback {

		public ProcessCommandThread(String name) {
			super(name);
		}

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SEND_FILE_REQUEST:
				Bundle bundle = msg.getData();
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
				break;
			default:
				break;
			}
			return true;
		}
	}
}
