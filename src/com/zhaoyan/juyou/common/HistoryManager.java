package com.zhaoyan.juyou.common;

import java.text.NumberFormat;
import java.util.Comparator;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.zhaoyan.juyou.provider.JuyouData;

public class HistoryManager {
		//status
		public static final int STATUS_DEFAULT = 0;
		public static final int STATUS_PRE_SEND = 10;
		public static final int STATUS_SENDING = 11;
		public static final int STATUS_SEND_SUCCESS = 12;
		public static final int STATUS_SEND_FAIL = 13;
		
		public static final int STATUS_PRE_RECEIVE = 21;
		public static final int STATUS_RECEIVING = 22;
		public static final int STATUS_RECEIVE_SUCCESS = 23;
		public static final int STATUS_RECEIVE_FAIL = 24;
		
		public static final NumberFormat nf = NumberFormat.getPercentInstance();
		
		public static final int TYPE_SEND = 0;
		public static final int TYPE_RECEIVE = 1;
		
		public static final String ME = "ME";
		
		private Context mContext;
		
		/**
	     * Perform alphabetical comparison of application entry objects.
	     */
	    public static final Comparator<HistoryInfo> DATE_COMPARATOR = new Comparator<HistoryInfo>() {
	        @Override
	        public int compare(HistoryInfo object1, HistoryInfo object2) {
	        	long date1 = object1.getDate();
				long date2 = object2.getDate();
				if (date1 > date2) {
					return -1;
				} else if (date1 == date2) {
					return 0;
				} else {
					return 1;
				}
	        }
	    };
	    
	    public HistoryManager(Context context){
	    	mContext = context;
	    }
	    
	    public  synchronized void insertToDb(HistoryInfo historyInfo){
	    	InsertThread insertThread = new InsertThread(historyInfo);
	    	insertThread.start();
	    }
	    
	    public void deleteItemFromDb(int id){
	    	Uri uri = Uri.parse(JuyouData.History.CONTENT_URI + "/" + id);
			mContext.getContentResolver().delete(uri, null, null);
	    }
	    
	    class InsertThread extends Thread{
	    	HistoryInfo historyInfo = null;
	    	
	    	InsertThread(HistoryInfo historyInfo){
	    		this.historyInfo = historyInfo;
	    	}
	    	
	    	@Override
	    	public void run() {
	    		ContentValues values = new ContentValues();
	    		values.put(JuyouData.History.FILE_PATH, historyInfo.getFile().getAbsolutePath());
	    		values.put(JuyouData.History.FILE_NAME, historyInfo.getFile().getName());
	    		values.put(JuyouData.History.FILE_SIZE, historyInfo.getFile().length());
	    		values.put(JuyouData.History.SEND_USERNAME, historyInfo.getSendUserName());
	    		values.put(JuyouData.History.RECEIVE_USERNAME, historyInfo.getReceiveUser().getUserName());
	    		values.put(JuyouData.History.PROGRESS, historyInfo.getProgress());
	    		values.put(JuyouData.History.DATE, historyInfo.getDate());
	    		values.put(JuyouData.History.STATUS, historyInfo.getStatus());
	    		values.put(JuyouData.History.MSG_TYPE, historyInfo.getMsgType());
	    		
	    		mContext.getContentResolver().insert(JuyouData.History.CONTENT_URI, values);
	    	}
	    }
	    
	    public ContentValues getInsertValues(HistoryInfo historyInfo){
	    	ContentValues values = new ContentValues();
	    	values.put(JuyouData.History.FILE_PATH, historyInfo.getFile().getAbsolutePath());
    		values.put(JuyouData.History.FILE_NAME, historyInfo.getFile().getName());
    		values.put(JuyouData.History.FILE_SIZE, historyInfo.getFileSize());
    		values.put(JuyouData.History.SEND_USERNAME, historyInfo.getSendUserName());
    		values.put(JuyouData.History.RECEIVE_USERNAME, historyInfo.getReceiveUser().getUserName());
    		values.put(JuyouData.History.PROGRESS, historyInfo.getProgress());
    		values.put(JuyouData.History.DATE, historyInfo.getDate());
    		values.put(JuyouData.History.STATUS, historyInfo.getStatus());
    		values.put(JuyouData.History.MSG_TYPE, historyInfo.getMsgType());
    		values.put(JuyouData.History.FILE_TYPE, historyInfo.getFileType());
    		
    		return values;
	    }
}
