package com.fei435;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
//文件锁
//import java.nio.channels.FileLock;
//import java.nio.channels.FileChannel;
//import java.io.RandomAccessFile;

import com.fei435.Constant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//显示视频的类
public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {
    public final static int POSITION_UPPER_LEFT = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD = 1;
    public final static int SIZE_BEST_FIT = 4;
    public final static int SIZE_FULLSCREEN = 8;

    private MjpegViewThread thread;
    private MjpegInputStream mIn = null;
    private String mInUrl = null;
    private boolean showFps = true;
    private boolean mRun = false;
    private boolean surfaceDone = false;
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;
    private static int mScreenWidth; 
    private static int mScreenHeight;
    private boolean resume = false;  //TODO:这是什么flag？init()函数中第一次启动才初始化，以后resume不需要初始化
    private boolean mtakePic = false;//flag for take a picture
    private String mFileName = null;//file name to save picture
    //added by feifei435
    private boolean bIsCapturing = false;
    private String mVideoName;
    
    private Context context;
    private Handler mHandler;

   //构造函数
    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public MjpegView(Context context) {
        super(context);
        init(context);
    }
    
    //发送消息给mLogText,参数String是mLogText将要显示的内容
    private void setUiInfo(String str){
    	Message msg = new Message();
    	msg.what = Constant.MSG_ID_SET_UI_INFO;
    	msg.obj = str;
    	mHandler.sendMessage(msg);
    }
    
    public class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private Bitmap ovl;

        public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) {
            mSurfaceHolder = surfaceHolder;
        }

        private Rect destRect(int bmw, int bmh) {
            int tempx;
            int tempy;
            if (displayMode == MjpegView.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_FULLSCREEN)
                return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() - 2;
            int bheight = b.height() - 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left + 1,
                    (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }
        //此类最重要的方法
        public void run() {
        	Log.i("MjpegView", "playback thread started! time:" + start);
            
        	start = System.currentTimeMillis();
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps = "";
            //输出Bitmap到图片，以便javacv recorder采集
            OutputStream os = null;
            
            //设置绘图及playback模式
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
            setDisplayMode(MjpegView.SIZE_STANDARD);
            
            while (mRun) {  //在这里面解码视频
            	//Log.i("surfaceDone","surfaceDone"+surfaceDone);
                if (surfaceDone) {
                    try {
                    	if (false)
                			Log.i("MjpegView", "thread run once++++");
                        c = mSurfaceHolder.lockCanvas();
                        synchronized (mSurfaceHolder) {
                            try {
                            	if (mIn == null && mInUrl != null) {
                            		
                            		try{
                            			mIn = MjpegInputStream.read(mInUrl);
                            		}catch (ClientProtocolException e) {
                            			mRun = false;  //退出线程
                            			Log.i("MjpegView error", "MjpegView error"+e.toString());
                            			setUiInfo("获取视视频流失败");
                            			break;  //跳出while循环
							        } catch (IOException e) {
							        	mRun = false;  //退出线程
                            			Log.i("MjpegView error", "MjpegView error"+e.toString());
                            			setUiInfo("获取视视频流失败");
                            			break;  //跳出while循环
							        }
                            	}
                            	
                                bm = mIn.readMjpegFrame();   //读取一帧视频图像
                                
                                //是否拍照
                                if (mtakePic) {
                                	Log.i("ScreenCapture", "thread run start to take picture");
                                	String fName = new FileUtils().generateFileName("CAM_");
                                	Log.i("MjpegView", "mtakePic  " + fName);
                                	int res = ScreenCapture.saveBitmapToFile(bm, fName);
                                	BroardCastResult(Constant.ACTION_TAKE_PICTURE_DONE, res, fName);
                                	mFileName = fName;
                                	mtakePic = false;
                                }
                                
                                destRect = destRect(mScreenWidth, mScreenHeight);
                                c.drawColor(Color.BLACK);
                                c.drawBitmap(bm, null, destRect, p);   //绘制读取到的一帧图像
                                if (showFps) {
                                    p.setXfermode(mode);
                                    if (ovl != null) {
                                        height = ((ovlPos & 1) == 1) ? destRect.top
                                                : destRect.bottom
                                                        - ovl.getHeight();
                                        width = ((ovlPos & 8) == 8) ? destRect.left
                                                : destRect.right
                                                        - ovl.getWidth();
                                        c.drawBitmap(ovl, width, height, null);
                                    }
                                    p.setXfermode(null);
                                    frameCounter++;
                                    if ((System.currentTimeMillis() - start) >= 1000) {
                                        fps = String.valueOf(frameCounter)
                                                + "fps";
                                        frameCounter = 0;
                                        start = System.currentTimeMillis();
                                        ovl = makeFpsOverlay(overlayPaint, fps);
                                    }
                                }
                                
                                if(bIsCapturing){
                                	//保存Bitmap为jpeg
                                	try {
										File file = null;
										if(!new FileUtils().isFileExist(FileUtils.TMP_FRAME_NAME, FileUtils.FILE_PATH)){					
											file = new FileUtils().createFileInSDCard(FileUtils.TMP_FRAME_NAME, FileUtils.FILE_PATH);
										}else{
											file = new FileUtils().getFile(FileUtils.TMP_FRAME_NAME, FileUtils.FILE_PATH);
										}
										
										if(!FileUtils.frameFileLocked){
											FileUtils.frameFileLocked = true;
						                	Log.i("filelock", "MjpegView:已将"+FileUtils.TMP_FRAME_NAME+"加锁");
						                	os = new FileOutputStream(file);
											bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
											os.flush();
											os.close();
											bm.recycle();
											bm = null;
											Log.i("ScreenCapture", System.currentTimeMillis()+"已将图像帧保存为jpeg供recorder录像");
											
											FileUtils.frameFileLocked = false;
								            Log.i("filelock", "MjpegView:已将"+FileUtils.TMP_FRAME_NAME+"解锁");
										} else{
											//recorder线程正在读取tmpframe.jpg，放弃写
											Log.i("ScreenCapture", "MjpegView:recorder线程正在读"+FileUtils.TMP_FRAME_NAME+",放弃保存图像");
										}	
										
									} catch(FileNotFoundException e){
										e.printStackTrace();
									}
									catch(IOException e){
										e.printStackTrace();
									}
                                }
                            } catch (Exception e) {}
                        }
                    } finally {
                        if (c != null)
                            mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    private void init(Context context) {

        this.context = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new MjpegViewThread(holder, context);
        setFocusable(true);
        if (!resume) {
            resume = true;
            overlayPaint = new Paint();
            overlayPaint.setTextAlign(Paint.Align.LEFT);
            overlayPaint.setTextSize(12);
            overlayPaint.setTypeface(Typeface.DEFAULT);
            overlayTextColor = Color.WHITE;
            overlayBackgroundColor = Color.BLACK;
            ovlPos = MjpegView.POSITION_LOWER_RIGHT;
            displayMode = MjpegView.SIZE_STANDARD;
            dispWidth = getWidth();
            dispHeight = getHeight();

            Log.i("SurfaceStatus Playback", "init successfully!");
        }
        setOverlayTextColor(Color.GREEN);
        DisplayMetrics dm = getResources().getDisplayMetrics(); 
        mScreenWidth = dm.widthPixels; 
        mScreenHeight = dm.heightPixels;
        setKeepScreenOn(true);
        
        Log.i("SurfaceStatus", "MjpegView init complete!");
    }
    public void setHandler(Handler ha) {
    	this.mHandler = ha;
	}
    //开始播放视频
    public void startPlayback() {
        if (mIn != null || mInUrl != null) {
            mRun = true;
            try {
            	thread.start();
            	Log.i("SurfaceStatus Playback", "startPlayback");
            } catch (IllegalThreadStateException e) {
            	Log.e("SurfaceStatus Playback", "startPlayback ERROR! " + e.getMessage());
            }
        }
    }

    public void resumePlayback() {
    	if(!mRun){
    		mRun = true;
            init(context);
            thread.start();
            Log.i("SurfaceStatus Playback", "resumePlayback");
    	}else {
    		Log.i("SurfaceStatus Playback", "PlayBack已开始，无需resumePlayback");
		}
    }
    //停止播放视频
    public void stopPlayback() {
    	Log.i("SurfaceStatus Playback", "stopPlayback");
        if(mRun){   //运行的时候才停止
        	mRun = false;
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        thread.setSurfaceSize(w, h);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        Log.i("SurfaceStatus", "surfaceDestroyed");
        stopPlayback();  //停止播放视频
    }


    public void surfaceCreated(SurfaceHolder holder) {
    	Log.i("SurfaceStatus", "surfaceCreated");
        surfaceDone = true;
    }

    public void showFps(boolean b) {
        showFps = b;
    }

    public void setSource(MjpegInputStream source) {
        mIn = source;
        mInUrl = null; 
        //startPlayback();
    }
    public void setSource(String url) {
    	mInUrl = url;
    	mIn = null;
        //startPlayback();//这里没必要playback
    }
    
    public void setOverlayPaint(Paint p) {
        overlayPaint = p;
    }

    public void setOverlayTextColor(int c) {
        overlayTextColor = c;
    }

    public void setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }

    public void setOverlayPosition(int p) {
        ovlPos = p;
    }

    public void setDisplayMode(int s) {
        displayMode = s;
    }
    
    public void saveBitmap () {
    	if(mRun) {
            Log.i("MjpegView", "saveBitmap start!" );
    		mtakePic = true;
    	} else {
    		Log.i("MjpegView", "saveBitmap error, not running!" );
    	}
    }
    public void toggleVideoCapture (){
    	if(mRun){
    		if(bIsCapturing){
    			bIsCapturing = false;
    			String video_path = ScreenCapture.stop();
    			Log.i("ScreenCapture Intent", "video capture stopped!" );
    			BroardCastResult(Constant.ACTION_RECORDING_STOP, 0, "已保存录像:" + video_path);
    		}else{
    			bIsCapturing = true;
    			ScreenCapture.start();
    			Log.i("ScreenCapture Intent", "video capture start!" );
    			BroardCastResult(Constant.ACTION_RECORDING_START, 0, "开始录像..再次点击结束录像    ");
    		}
    	} else {
    		Log.e("MjpegView", "video capture error, not running!" );
		}
    }
    
    //action为Constant中ACTION_开头的定义
    //res为相应操作的结果
    //fName可以认为是附加消息，不一定非是路径
    private void BroardCastResult (String action, int res, String text) {
    	Log.i("ScreenCapture Intent", "BroardCastResult res: " + res);
    	Intent intent = new Intent(action);
    	intent.putExtra(Constant.EXTRA_RES, res);
    	intent.putExtra(Constant.EXTRA_PATH, text);
    	context.sendBroadcast(intent);
    }
}