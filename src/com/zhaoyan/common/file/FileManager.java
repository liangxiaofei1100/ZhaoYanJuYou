package com.zhaoyan.common.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.R.integer;
import android.content.Context;

import com.zhaoyan.common.util.Log;
import com.zhaoyan.juyou.R;

public class FileManager {
	private static final String TAG = "FileManager";
	public static final int EBOOK = 0x01;
	public static final int HTML = 0x02;
	public static final int WORD = 0x03;
	public static final int EXCEL = 0x04;
	public static final int PPT = 0x05;
	public static final int PDF = 0x06;
	public static final int AUDIO = 0x07;
	public static final int VIDEO = 0x08;
	public static final int CHM = 0x09;
	public static final int APK = 0x10;
	public static final int ARCHIVE = 0x11;
	public static final int IMAGE = 0x12;
	public static final int UNKNOW = 0x20;

	public static int getFileType(Context context, String filepath) {
		return getFileType(context, new File(filepath));
	}

	public static int getFileType(Context context, File file) {
		int fileType = fileFilter(context, file.getName());
		return fileType;
	}
	
	public static int getFileTypeByName(Context context, String fileName){
		return fileFilter(context, fileName);
	}

	private static int fileFilter(Context context, String fileName) {
		int ret;

		if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_ebook))) {
			// text
			ret = EBOOK;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_image))) {
			// Images
			ret = IMAGE;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_audio))) {
			// audios
			ret = AUDIO;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_video))) {
			// videos
			ret = VIDEO;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_apk))) {
			// apk
			ret = APK;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_word))) {
			// word
			ret = WORD;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_ppt))) {
			// ppt
			ret = PPT;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_excel))) {
			// excel
			ret = EXCEL;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_archive))) {
			// packages
			ret = ARCHIVE;
		} else if (checkEndsWithInStringArray(fileName, context.getResources()
				.getStringArray(R.array.ext_pdf))) {
			// pdf
			ret = PDF;
		} else {
			ret = UNKNOW;
		}

		return ret;
	}

	/**
	 * Check the given file name whether match the file suffix name array.
	 * 
	 * @param checkItsEnd
	 * @param fileEndings
	 * @return
	 */
	private static boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		String str = checkItsEnd.toLowerCase();
		for (String aEnd : fileEndings) {
			if (str.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }
	
	/**
	 * copy single file
	 * 
	 * @param srcPath
	 *           src file path
	 * @param desPath
	 *           des file path
	 * @return
	 * @throws Exception
	 */
	public static boolean copyFile(String srcPath, String desPath){
		Log.d(TAG, "copyFile.srcPath:" + srcPath);
		Log.d(TAG, "copyFile.desPath:" + desPath);
		if (new File(srcPath).isDirectory()) {
			Log.d(TAG, "copyFile error:" + srcPath + " is a directory.");
			return false;
		}
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			File srcFile = new File(srcPath);
			if (srcFile.exists()) {
				inputStream = new FileInputStream(srcPath);
				outputStream = new FileOutputStream(desPath);
				byte[] buffer = new byte[1024 * 10];
				int byteread = 0;
				while ((byteread = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, byteread);
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			}else {
				Log.e(TAG, srcPath + " is not exist");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * folder copy
	 * @param srcPath source folder path
	 * @param desPath destination folder path
	 * @return
	 */
	public static boolean copyFolder(String srcPath, String desPath) {
		Log.d(TAG, "copyFolder.srcPath:" + srcPath);
		Log.d(TAG, "copyFolder.desPath:" + desPath);
		File srcFile = new File(srcPath);
		String[] srcFileNameList = srcFile.list();
		if (srcFileNameList.length == 0) {
			//copy a empty folder
			return new File(desPath).mkdirs();
		}
		
		// create desPath folder
		if (!new File(desPath).mkdirs()) {
			return false;
		}
		
		File tempFile = null;
		for (String name : srcFileNameList) {
			if (srcPath.endsWith(File.separator)) {
				tempFile = new File(srcFile + name);
			} else {
				tempFile = new File(srcFile + File.separator + name);
			}

			if (tempFile.isFile()) {
				copyFile(tempFile.getAbsolutePath(), desPath + File.separator
						+ name);
			} else if (tempFile.isDirectory()) {
				// is a child folder
				copyFolder(srcPath + File.separator + name, desPath
						+ File.separator + name);
			}
		}
		
		return true;
	}
	
	public static boolean moveFile(String srcPath, String destPath) {
		Log.d(TAG, "moveFile >>> " + srcPath + "," + destPath);

		if (srcPath == null || destPath == null) {
			Log.e(TAG, "MoveFile: null parameter");
			return false;
		}

		File file = new File(srcPath);
		try {
			return file.renameTo(new File(destPath));
		} catch (SecurityException e) {
			Log.e(TAG, "Fail to move file," + e.toString());
		}
		return false;
	}
	 
	public static String makePath(String path, String name) {
		if (path.endsWith(File.separator))
			return path + name;

		return path + File.separator + name;
	}
	
	public static boolean containsPath(String path1, String path2) {
	    String path = path2;
	    while (path != null) {
	        if (path.equalsIgnoreCase(path1))
	            return true;

	        if (path.equals("/mnt"))
	            break;
	        path = new File(path).getParent();
	    }

	    return false;
	}

}
