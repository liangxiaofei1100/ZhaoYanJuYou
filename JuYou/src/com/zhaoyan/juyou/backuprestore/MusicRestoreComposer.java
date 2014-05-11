package com.zhaoyan.juyou.backuprestore;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.zhaoyan.common.util.Log;
import com.zhaoyan.juyou.backuprestore.Constants.ModulePath;

/**
 * Describe class <code>MusicRestoreComposer</code> here.
 * 
 * @author
 * @version 1.0
 */
public class MusicRestoreComposer extends Composer {
    private static final String TAG = "MusicRestoreComposer";
    private int mIndex;
    private File[] mFileList;
    private ArrayList<String> mExistFileList = null;
    private static final String[] mProjection = new String[] { Audio.Media._ID, Audio.Media.DATA };


    /**
     * Creates a new <code>MusicRestoreComposer</code> instance.
     *
     * @param context a <code>Context</code> value
     */
    public MusicRestoreComposer(Context context) {
        super(context);
    }

    /**
     * Describe <code>init</code> method here.
     * 
     * @return a <code>boolean</code> value
     */
    public final boolean init() {
        boolean result = false;
        String path = mParentFolderPath + File.separator + ModulePath.FOLDER_MUSIC;
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            mFileList = folder.listFiles();
            mExistFileList = getExistFileList(path);
            result = true;
        }

        Log.d(TAG, "init():" + result + ",count:" + getCount());
        return result;
    }

    /**
     * Describe <code>getModuleType</code> method here.
     * 
     * @return an <code>int</code> value
     */
    public final int getModuleType() {
        return ModuleType.TYPE_MUSIC;
    }

    /**
     * Describe <code>getCount</code> method here.
     * 
     * @return an <code>int</code> value
     */
    public final int getCount() {
        int count = 0;
        if (mFileList != null) {
            count = mFileList.length;
        }

        Log.d(TAG, "getCount():" + count);
        return count;
    }

    /**
     * Describe <code>isAfterLast</code> method here.
     * 
     * @return a <code>boolean</code> value
     */
    public final boolean isAfterLast() {
        boolean result = true;
        if (mFileList != null) {
            result = (mIndex >= mFileList.length) ? true : false;
        }

        Log.d(TAG, "isAfterLast():" + result);
        return result;
    }

    /**
     * Describe <code>implementComposeOneEntity</code> method here.
     * 
     * @return a <code>boolean</code> value
     */
    public final boolean implementComposeOneEntity() {
        boolean result = false;
        if (mFileList != null && mIndex < mFileList.length) {
            File file = mFileList[mIndex++];

            if (!mExistFileList.contains(file.getAbsolutePath())) {
                String name = file.getAbsolutePath();
                Uri data = Uri.parse("file://" + name);
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));

                //ContentValues cv = new ContentValues();
                //cv.put(Audio.Media.IS_MUSIC, 1);
                //cv.put(Audio.Media.DATA, file.getAbsolutePath());
                //cv.put(Audio.Media.TITLE, file.getName());
                //mContext.getContentResolver().insert(Audio.Media.EXTERNAL_CONTENT_URI, cv);

                Log.d(TAG, file.getAbsolutePath());
            } else {
                // mContext.getContentResolver().update(Audio.Media.EXTERNAL_CONTENT_URI,
                //                                      cv,
                //                                      Audio.Media.DATA + "=?",
                //                                      new String[] {file.getAbsolutePath()});
                Log.d(TAG, "already exist");
            }

            result = true;
        }

        return result;
    }


    /**
     * Describe <code>onStart</code> method here.
     *
     */
    public void onStart() {
        super.onStart();
    }


    /**
     * Describe <code>onEnd</code> method here.
     *
     */
    public void onEnd() {
        super.onEnd();
    }

    // private void deleteFolder(File file) {
    //     if (file.exists()) {
    //         if (file.isFile()) {
    //             int count = mContext.getContentResolver().delete(Audio.Media.EXTERNAL_CONTENT_URI,
    //                     Audio.Media.DATA + " like ?", new String[] { file.getAbsolutePath() });
    //             Log.d(TAG, "deleteFolder():" + count + ":" + file.getAbsolutePath());
    //             file.delete();
    //         } else if (file.isDirectory()) {
    //             File files[] = file.listFiles();
    //             for (int i = 0; i < files.length; ++i) {
    //                 this.deleteFolder(files[i]);
    //             }
    //         }

    //         file.delete();
    //     }
    // }

    private ArrayList<String> getExistFileList(String path) {
        ArrayList<String> fileList = new ArrayList<String>();
        int len = mFileList.length;
        if(len > 0) {
            HashMap<String, Boolean> map = new HashMap<String, Boolean>();
            for(File file : mFileList) {
                map.put(file.getAbsolutePath(), true);
            }

            Cursor cur = mContext.getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI,
                                                             mProjection,
                                                             Audio.Media.DATA + " like ?",
                                                             new String[] { "%" + path + "%" },
                                                             null);
            if (cur != null) {
                if(cur.moveToFirst()) {
                    while(!cur.isAfterLast()) {
                        int dataColumn = cur.getColumnIndexOrThrow(Audio.Media.DATA);
                        String data = cur.getString(dataColumn);
                        if ((data != null) && map.get(data)) {
                            fileList.add(data);
                            Log.d(TAG, "getExistFileList:" + data);
                        }
                        cur.moveToNext();
                    }
                }
            
                cur.close();
            }
        }

        return fileList;
    }

}
