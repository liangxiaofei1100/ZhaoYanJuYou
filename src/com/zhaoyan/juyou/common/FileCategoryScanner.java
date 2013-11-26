package com.zhaoyan.juyou.common;

import java.io.File;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zhaoyan.common.util.LogFile;
import com.zhaoyan.juyou.activity.FileCategoryActivity;

import android.content.Context;

/**
 * Scan directory to get specified types files. The scan is implements by
 * multiple threads.
 * 
 */
public class FileCategoryScanner {
	private String[] mFilterType;
	private int mType = -1;
	private File mRootDir;
	private Context mContext;
	private FileCategoryScanListener mListener;
	private FileInfoManager mFileInfoManager;
	private Vector<FileInfo> mFileInfos;
	private int mScanningDirCount = 0;
	private ExecutorService mExecutorService;

	private boolean mIsScanning = false;

	/**
	 * 
	 * @param context
	 *            ApplicationContext
	 * @param rootDir
	 *            the directory to scan. must be a directory or
	 *            IllegalArgumentException will be throwed.
	 * @param filterType
	 *            null means all type.
	 */
	public FileCategoryScanner(Context context, File rootDir,
			String[] filterType, int type) {
		if (!rootDir.isDirectory()) {
			throw new IllegalArgumentException(
					"FileCategoryScanner, rootDir must be a directory.");
		}
		mContext = context.getApplicationContext();
		mFileInfoManager = new FileInfoManager();
		mFileInfos = new Vector<FileInfo>();
		mRootDir = rootDir;
		mFilterType = filterType;
		mType = type;
	}

	public void startScan() {
		if (mIsScanning) {
			return;
		}
		if (mListener != null) {
			mListener.onScanStart();
		}
		mScanningDirCount = 0;
		mExecutorService = Executors.newCachedThreadPool();
		scanDir(mRootDir);
	}

	public boolean isScanning() {
		return mIsScanning;
	}

	public void cancelScan() {
		if (mIsScanning) {
			mExecutorService.shutdown();
			if (mListener != null) {
				mListener.onScanCancel();
			}
			mIsScanning = false;
		}
	}

	public void setScanListener(FileCategoryScanListener listener) {
		mListener = listener;
	}

	private class ScanRunnable implements Runnable {
		private File mDir;

		public ScanRunnable(File dir) {
			if (!dir.isDirectory()) {
				throw new IllegalArgumentException(
						"ScanThread, dir must be a directory.");
			}
			mDir = dir;
		}

		@Override
		public void run() {
			File[] files = mDir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						//do not load juyou log file
						if (!LogFile.LOG_FOLDER_NAME.equals(file.getAbsolutePath())) {
							scanDir(file);
						}
					} else {
						CategoryFile(file);
					}
				}
			}
			scanDirFinish();
		}
	}

	private synchronized void scanDir(File dir) {
		mScanningDirCount++;
		mExecutorService.execute(new ScanRunnable(dir));
	}

	private void CategoryFile(File file) {
		String name = file.getName();
		boolean isMatched = false;
		if (mFilterType != null) {
			for (String filter : mFilterType) {
				if (name.endsWith(filter)) {
					isMatched = true;
					break;
				}
			}
		} else {
			isMatched = true;
		}

		if (isMatched) {
			FileInfo fileInfo = mFileInfoManager.getFileInfo(mContext, file);
			mFileInfos.add(fileInfo);
		}
	}

	private synchronized void scanDirFinish() {
		mScanningDirCount--;
		if (mScanningDirCount == 0) {
			scanDirAllFinish();
		}
	}

	private void scanDirAllFinish() {
		mIsScanning = false;
		try {
			if (FileCategoryActivity.TYPE_APK == mType) {
				Collections.sort(mFileInfos, FileInfo.getNameComparator());
			}else {
				Collections.sort(mFileInfos, FileInfo.getDateComparator());
			}
		} catch (Exception e) {
		}
		if (mListener != null) {
			mListener.onScanComplete(mFileInfos);
		}
		mExecutorService.shutdown();
	}

	public interface FileCategoryScanListener {
		/**
		 * Scan is started. Notice, this method runs in the same thread with the
		 * invoker.
		 */
		void onScanStart();

		/**
		 * Scan is complete.Notice, this method runs in the different thread
		 * with the invoker.
		 * 
		 * @param fileInfos
		 */
		void onScanComplete(Vector<FileInfo> fileInfos);

		/**
		 * Scan is canceled when scanning. this method runs in the same thread
		 * with the invoker.
		 */
		void onScanCancel();
	}
}
