package com.fei435;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.RandomAccessFile;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FileUtils {
	
	//目录相关常量
    public final static String FILE_PATH = "WifiCarClient";
	public final static String FILE_NAME = "video.mp4";
	public final static String TMP_FRAME_NAME = "tmpframe.jpg";
	
	public static boolean frameFileLocked = false;
	
	private String SDCardRoot;
	private static boolean isCardExist;
	
	public FileUtils() throws NoSdcardException {
		getSDCardRoot();
	}
	
	public String getSDCardRoot() throws NoSdcardException{
		if(isCardExist()){
			SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		}else{
			throw new NoSdcardException();
		}
		return SDCardRoot;
	}
	
	public static boolean isCardExist(){
		isCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)?true:false;
		return isCardExist;	
	}
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		if(!file.exists()){	
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	//在SD卡中创建目录
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		dirFile.mkdirs();

		return dirFile;
	}
	
	public boolean filterFileExist(String path, String filter) {
		File file = new File(SDCardRoot + path + File.separator);
		if (file.exists() && file.isDirectory()) {

			String[] fileNames = file.list(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".png");
				}
			});
			if (fileNames.length > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}
	public File getFile(String fileName,String path){
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file;
	}
	public void deleteFile(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		boolean result = file.delete();
	}
	
	public void closeInputStream(InputStream inputStream){
		if(inputStream!=null){
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.e("error", "close failed");
				e.printStackTrace();
			}
		}
	}
	public class NoSdcardException extends Exception{
		//没有SD卡，不能录像或拍照！
		public NoSdcardException(){
			Log.i("FileUtils","没有SD卡，不能录像或拍照！");
		}
	}
	
    public static String generateFileName(String prefix) {
    	
    	String suffix = null;
    	if(prefix.equals("CAM_")){
    		suffix = ".png";
    	} else if (prefix.equals("VID_")) {
    		suffix = ".mp4";
		} else {
			//do nothing
		}
    	
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    	Date curDate = new Date(System.currentTimeMillis());//get current time
	    	String str = formatter.format(curDate);
			String imgName = prefix + str + suffix;
	    	
			File file;
			if(!new FileUtils().isFileExist(imgName, FILE_PATH)){
				file = new FileUtils().createFileInSDCard(imgName, FILE_PATH);
			} else {
				//如果在一秒钟内点了两次截图，再在文件名中加个后缀
				imgName = prefix + str + System.currentTimeMillis() + suffix;
				file = new FileUtils().createFileInSDCard(imgName, FILE_PATH);
			}
			return file.getAbsolutePath();
		} catch (NoSdcardException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
}
