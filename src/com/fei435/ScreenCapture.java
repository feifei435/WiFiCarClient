package com.fei435;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import com.fei435.FileUtils;
import com.fei435.FileUtils.NoSdcardException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//文件锁
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core;
/**
 * convertFromBitmaptoVideo
 * @author yanjiaqi  qq:985202568
 * modified by feifei435
 */

public class ScreenCapture {
	private static int switcher = 0;//录像键
	private static boolean isPaused = false;//暂停键
	private static double RECORD_FPS = 10f;
	
	private static String video_path_name = null;
	
	
	//截取视频一帧并保存  注意 bitName为路径+文件名
    public static int saveBitmapToFile(Bitmap mBitmap, String bitName){
    	FileOutputStream fOut = null;
    	Log.i("ScreenCapture", "saveBitmapToFile enter");
    	if (null == bitName || bitName.length() <= 4) {
    		return Constant.CAM_RES_FAIL_FILE_NAME_ERROR;
    	}
    	
    	File f = new File(bitName);
    	Log.i("ScreenCapture", "saveBitmapToFile, fname =" + f);
    	try {
	    	f.createNewFile();
	    	Log.i("ScreenCapture", "saveBitmapToFile, createNewFile success, f=" + f);
	    	fOut = new FileOutputStream(f);
	    	Log.i("ScreenCapture", "saveBitmapToFile, FileOutputStream success, fOut=" + fOut);
    	} catch (IOException e) {
    		Log.i("ScreenCapture", "exception, err=" + e.getMessage());
    		return Constant.CAM_RES_FAIL_FILE_WRITE_ERROR;
    	}
    	
    	mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
    	
    	try {
    		fOut.flush();
    		fOut.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    		return Constant.CAM_RES_FAIL_BITMAP_ERROR;
    	}
    	
    	return Constant.CAM_RES_OK;
    }
	

	public static void start(){
		
		video_path_name = FileUtils.generateFileName("VID_");
		switcher = 1;
		
		new Thread(){
			public void run(){
				Log.i("ScreenCapture", "ScreenCapture线程已启动");
				try {
					new FileUtils().creatSDDir(FileUtils.FILE_PATH);
				
					//TODO:可选的方案：把这里的640 480改为先读取assets中的示例图片的宽高，也可以确定录像参数
					FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
							video_path_name, 640, 480);
					Log.i("ScreenCapture", "recorder已创建，"+"width:"+recorder.getImageHeight()+"height:"+recorder.getImageHeight());
			
					recorder.setFormat("mp4");
					recorder.setFrameRate(RECORD_FPS);//录像帧率
					recorder.start();
				
					while(switcher!=0){
						if(!isPaused){
							
							//TODO:判断是否跟上一帧重复  保证帧率
							//虽然这里由cvLoadImage直接根据路径读取图像，但是为了使用java中文件锁保持互斥，还是要定义一个File对象
							
						    if(!FileUtils.frameFileLocked) {
						        if(new FileUtils().isFileExist(FileUtils.TMP_FRAME_NAME, FileUtils.FILE_PATH)){
						        	FileUtils.frameFileLocked = true;//加锁
							        Log.i("filelock", "recorder:已将"+FileUtils.TMP_FRAME_NAME+"加锁");
							        
						        	FileUtils.frameFileLocked = true;//加锁
							        Log.i("filelock", "recorder:已将"+FileUtils.TMP_FRAME_NAME+"加锁");
						        	
							        opencv_core.IplImage image = cvLoadImage(new FileUtils().getSDCardRoot()+ FileUtils.FILE_PATH + File.separator+FileUtils.TMP_FRAME_NAME);
									Log.i("ScreenCapture", "recorder正在将帧"+System.currentTimeMillis()+"保存到MP4文件");
									recorder.record(image);
									
									//解锁文件
							        FileUtils.frameFileLocked = false;
							        Log.i("filelock", "recorder:已将"+FileUtils.TMP_FRAME_NAME+"解锁");
							        
							        try {//录完一帧休息一下
										sleep(200);
										Log.i("filelock", "recorder:sleep some time");
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
							        
						        } else {
						        	Log.i("ScreenCapture", "等待tmpframe.jpg");
						        	try {
										sleep(200);
										Log.i("filelock", "recorder:sleep some time");
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
						    } else {
						        //MjpegView线程正在写jpg，放弃读
						    	Log.i("ScreenCapture", "MjpegView线程正在写"+FileUtils.TMP_FRAME_NAME+",放弃保存图像");
						    	try {
									sleep(200);
									Log.i("filelock", "recorder:sleep some time");
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
						    }
						}
					}
					recorder.stop();
					
					Log.i("ScreenCapture", "recorder已停止");
				}catch(FileUtils.NoSdcardException e){
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	//返回值为视频路径
	public static String stop(){
		switcher = 0;
		isPaused = false;
		return video_path_name;
	}
	public static void pause(){
		if(switcher==1){
			isPaused = true;
		}
	}
	public static void restart(){
		if(switcher==1){
			isPaused = false;
		}
	}
	public static boolean isStarted(){
		if(switcher==1){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isPaused(){
		return isPaused;
	}
		
	private static Bitmap getImageFromFile(String filename){
		Bitmap image = null;
		try{
			image = BitmapFactory.decodeFile(
					new FileUtils().getSDCardRoot() + 
					FileUtils.FILE_PATH + File.separator + filename
					);
		}catch (NoSdcardException e) {
			e.printStackTrace();
		}
		return image;
	}
}

