package com.fei435;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import com.fei435.Constant;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class WiFiCarController{    //WiFiCar连接的封装类

    private boolean mThreadFlag = false;//这是网络连接线程的flag
    private int mSocketStatus = Constant.STATUS_INIT;
    private boolean bReaddyToSendCmd = true;
    private SocketClient mtcpSocket;   //此为socket的状态，getWiFiStatus获取到的是WiFi网络的状态
    private Handler mHandler;
    private TextView mLogText;
    private Context mContext;
    
    private ControlThread mThreadClient = null;
    
    
    public WiFiCarController (Handler mHandler, TextView mLogText, Context mContext) {
    	this.mHandler = mHandler;
    	this.mLogText = mLogText;
    	this.mContext = mContext;
    	getWifiStatus();  //获取WiFi状态保存到Constant中
	}
    
    /** 
     * bytes转换成十六进制字符串 
     * @param byte[] b byte数组 
     * @return String 每个Byte值之间空格分隔 
     */
    private String byte2HexStr(byte[] b){
        String stmp="";  
        StringBuilder sb = new StringBuilder("");  
        for (int n=0;n<b.length;n++)  
        {  
            stmp = Integer.toHexString(b[n] & 0xFF);  
            sb.append((stmp.length()==1)? "0"+stmp : stmp);  
            sb.append(" ");  
        }  
        return sb.toString().toUpperCase().trim();  
    }
    
    private String byte2IntStr(byte[] b){
        String stmp="";  
        StringBuilder sb = new StringBuilder("");  
        for (int n=0;n<b.length;n++)  
        {  
            stmp = Integer.toString(b[n] & 0xFF);  
            sb.append((stmp.length()==1)? "0"+stmp : stmp);  
            sb.append(" ");  
        }  
        return sb.toString().toUpperCase().trim();  
    }
    
    private void initWifiConnection() {       //在此函数中已经尝试打开socket，并做错误判断
        mSocketStatus = Constant.STATUS_INIT;
        Log.i("Socket", "initWifiConnection");
        try {
            if (mtcpSocket != null) {
                mtcpSocket.closeSocket();
            }
            String clientUrl = Constant.ROUTER_CONTROL_URL;
            int clientPort = Constant.ROUTER_CONTROL_PORT;
            if (Constant.m4test) {
            	clientUrl = Constant.ROUTER_CONTROL_URL_TEST;
                clientPort = Constant.ROUTER_CONTROL_PORT_TEST;
            }
            
            try {
            	mtcpSocket = new SocketClient(clientUrl, clientPort);
            	mSocketStatus = Constant.STATUS_CONNECTED;
            	Log.i("socket", "Wifi Connect created ip=" + clientUrl + " port=" + clientPort);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				Log.e("socket", "creating socket error UnknownHostException:"+e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("socket", "creating socket error IOException:"+e.toString());
			}
        } catch (Exception e) {
            Log.d("socket", "initWifiConnection exception:"+e.toString());
        }
        
        Message msg = new Message();
        if (mSocketStatus != Constant.STATUS_CONNECTED || null == mtcpSocket) {          
            msg.what = Constant.MSG_ID_ERR_CONN;
        } else {
            msg.what = Constant.MSG_ID_CON_SUCCESS;
        }
        mHandler.sendMessage(msg);
    }
    
    //发送消息给mLogText,参数String是mLogText将要显示的内容
    private void setUiInfo(String str){
    	Message msg = new Message();
    	msg.what = Constant.MSG_ID_SET_UI_INFO;
    	msg.obj = str;
    	mHandler.sendMessage(msg);
    }
        
    public class ControlThread extends Thread{    //接受数据包的线程
    	
    	public void run()
        {   
            Log.i("socket thread", "mThreadClient 已经开始");
            BufferedInputStream is = null;
            
            try {
                Log.i("socket", "WiFiConnection init complete");       
                //取得输入、输出流
                //mBufferedReaderClient = new BufferedReader(new InputStreamReader(mtcpSocket.getInputStream()));//这个是字符流，没用
                is = new BufferedInputStream(mtcpSocket.getInputStream());
                
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = Constant.MSG_ID_ERR_INIT_READ;
                mHandler.sendMessage(msg);
                return;
            }

            byte[] buffer = new byte[1024];
            long lastTicket = System.currentTimeMillis();
            byte[] command = {0,0,0,0,0};
            int commandLength = 0;
            int i = 0;
            while (mThreadFlag)
            {
            	if(mSocketStatus == Constant.STATUS_CONNECTED && 
            			getWifiStatus() == Constant.WIFI_STATE_CONNECTED){
            		try {
            			Log.i("socket thread", "mThreadClient work 1s");
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            		
//            		try
//            		{
//            		    //Log.i("socket thread","mThreadFlag:" + mThreadFlag+System.currentTimeMillis());
//            		    int ret = is.read(buffer);
//            		    Log.i("socket thread","is.read(buffer) ret="+ret);
//            		    if (ret > 0) {
//            		        
//            		        printRecBuffer("receive buffer", buffer, ret);
//            		        
//            		        if(ret > 0 && ret <= Constant.COMMAND_LENGTH ) {
//            		            long newTicket = System.currentTimeMillis();
//            		            long ticketInterval = newTicket - lastTicket;
//            		            Log.d("Socket", "time ticket interval =" + ticketInterval);
//            		            
//            		            //距离上次接收小于1000ms才组包，否则大于1000ms就算是接收完了或者丢弃
//            		            if (ticketInterval < Constant.MIN_COMMAND_REC_INTERVAL) {  //小车端发了一半命令，但是没有发完，然后1s之内又发过来，在buffer中取ret大的数据追加到command中，最多追加commandLenth
//            		                if (commandLength > 0) {
//            		                    commandLength = appendBuffer(buffer, ret, command, commandLength);//
//            		                } else {
//            		                    Log.d("Socket", "not recognized command_1");       //若1s之内没有下文了，则丢弃包
//            		                }
//            		            } else {
//            		                if (buffer[0] == Constant.COMMAND_PERFIX ) {     		//新收到的包
//            		                    for (i = 0; i < ret; i++) {
//            		                        command[i] = buffer[i];
//            		                    }
//            		                    commandLength = ret;
//            		                } else {
//            		                    Log.d("Socket", "not recognized command_2");
//            		                    commandLength = 0;
//            		                }
//            		            }
//            		            
//            		            lastTicket = newTicket;    //更新时间戳
//            		            printRecBuffer ("print command", command, commandLength);
//            		            
//            		            if (commandLength >= Constant.COMMAND_LENGTH) {   //判断是否已经接受完一条命令  实际上等于就够了
//            		                Message msg = new Message();
//            		                msg.what = Constant.MSG_ID_CON_READ;
//            		                msg.obj = command;
//            		                mHandler.sendMessage(msg);
//            		                commandLength = 0; 
//            		            }
//            		        }
//            		    }
//            		} catch (Exception e) {
//            		    Message msg = new Message();
//            		    Log.i("socket thread", e.toString());
//            		    msg.what = Constant.MSG_ID_ERR_RECEIVE;
//            		    mHandler.sendMessage(msg);
//            		}
            		
            	} else{
            		try {
            			Log.i("socket thread", "WiFi或者socket连接未就绪,sleep(100)");
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            	}
            }
            Log.i("socket thread", "mThreadClient 已经终止");
        }
    }


    public void sendCommand(byte[] data) {   //发送命令的函数
        if ( mSocketStatus != Constant.STATUS_CONNECTED || null == mtcpSocket) {
        	setUiInfo("状态异常, 无法发送命令 " +  byte2IntStr(data));
        	Log.i("socket command","状态异常, 无法发送命令 " +  byte2HexStr(data));
            return;
        }
         
        if (!bReaddyToSendCmd) {
            setUiInfo("please wait 1 second to send msg ....");
         	Log.i("socket","not ready to send command,wait 1s pls");
         	return;
        }
        //调试命令发送时使用
        //tag:(mlogtext|socket|settingclick|SurfaceStatus|heart|inspect|MjpegView|ScreenCapture|filelock|speed)
        //tag:(MjpegView|ScreenCapture|filelock)
        //来在logcat做filter
        try {
            mtcpSocket.sendMsg(data);
            setUiInfo("发送命令" + byte2IntStr(data) + "到WiFiCar成功");
            Log.i("socket command","发送命令" + byte2HexStr(data) + "到WiFiCar成功");
        } catch (Exception e) {
            Log.i("Socket", e.getMessage() != null ? e.getMessage().toString() : "sendCommand error!");
            Log.i("socket", e.toString());
            setUiInfo("发送命令" + byte2IntStr(data) + "到WiFiCar失败，请检查连接");
            Log.i("socket command","发送命令" + byte2HexStr(data) + "到WiFiCar失败，请检查连接");
        }
    }
    
    //此函数获取WiFi连接状态
    private int getWifiStatus () {
        int status = Constant.WIFI_STATE_UNKNOW;
        ConnectivityManager conMan = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager mWifiMng = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        
        switch (mWifiMng.getWifiState()) {
        case WifiManager.WIFI_STATE_DISABLED:
        case WifiManager.WIFI_STATE_DISABLING:    
        case WifiManager.WIFI_STATE_ENABLING:
        case WifiManager.WIFI_STATE_UNKNOWN:
            status = Constant.WIFI_STATE_DISABLED;
            break;
        case WifiManager.WIFI_STATE_ENABLED:
            status = Constant.WIFI_STATE_NOT_CONNECTED;
            State wifiState = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (State.CONNECTED == wifiState) {
                WifiInfo info = mWifiMng.getConnectionInfo();
                if (null != info) {
                    String bSSID = info.getBSSID();
                    String SSID = info.getSSID();
                    Log.i("socket", "getWifiStatus bssid=" + bSSID + " ssid=" + SSID);
                    if (null != SSID && SSID.length() > 0) {
                        //if (SSID.toLowerCase().contains(Constant.WIFI_SSID_PERFIX)) {
                			status = Constant.WIFI_STATE_CONNECTED;
                        //}
                    }
                }
            }
            break;
        default:
            break;
        }
        Constant.CURRENT_WIFI_STATE = status;
        return status;
    }
    
    
    public void connectToRouter() {
        int status = getWifiStatus();    //获取WiFi连接状态
        
        if (Constant.WIFI_STATE_CONNECTED == status || Constant.m4test) {
        	//连接服务器
            initWifiConnection();
        	if (mSocketStatus == Constant.STATUS_CONNECTED){
        		if(!mThreadFlag){
                	mThreadFlag = true;
                    //网络连接线程            
                    try {
                    	mThreadClient = new ControlThread();
                    	mThreadClient.start();
                    } catch (IllegalThreadStateException e) {
                    	Log.e("socket", "mThreadClient 启动失败" + e.getMessage());
                    }
                }
        	} else {
        		setUiInfo("连接到WiFiCar失败，控制地址有误！");
        		Log.i("socket","连接到WiFiCar失败，控制地址有误！");
			}
        } else if (Constant.WIFI_STATE_NOT_CONNECTED == status) {
        	setUiInfo("初始化连接路由器失败，wifi未连接！");
            Log.i("socket","初始化连接路由器失败，wifi未连接！");
        } else {
        	setUiInfo("初始化连接路由器失败，wifi未开启！");
            Log.i("socket","初始化连接路由器失败，wifi未开启！");
        }
    }

    public void disconnFromRouter() {
    	int status = getWifiStatus();
    	if (Constant.WIFI_STATE_CONNECTED == status && mThreadFlag) {
    		
			Log.i("socket thread", "mThreadClient status:try join");
			mThreadFlag = false;
			boolean retry = true;
	        while (retry) {
	            try {
	                mThreadClient.join();
	                Log.i("socket thread", "mThreadClient status:join");
	                retry = false;
	            } catch (InterruptedException e) {
	            	Log.i("socket", "关闭mThreadClient失败:"+e.toString());
					e.printStackTrace();
	            }
	        }
    	}
    	//关闭socket
    	if(null != mtcpSocket) {                
            try {
            	Log.i("socket", "关闭mtcpSocket..");
                mtcpSocket.closeSocket();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("socket", "error closing socket:"+e.toString());
            }
        }
        if (null != mHandler) {
        	int i;
        	for (i = Constant.MSG_ID_LOOP_START + 1; i < Constant.MSG_ID_LOOP_END; i++ ) {
        		mHandler.removeMessages(i);
        	}
        }
	}

    private int appendBuffer (byte[] buffer, int len, byte[] dstBuffer, int dstLen) {
    	int j = 0;
    	int i = dstLen;
    	for (i = dstLen; i < Constant.COMMAND_LENGTH && j < len; i++) {
    		dstBuffer[i] = buffer[j];
    		j++;
    	}
    	return i;
    }

    //打印接收到的数据包
    void printRecBuffer(String tag, byte[] buffer, int len) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(tag);
    	sb.append(" len = ");
    	sb.append(len);
    	sb.append(" :");
    	for (int i =0 ;i < len; i++) {
    		sb.append(buffer[i]);
    		sb.append(", ");
    	}
    	Log.i("socket printRecBuffer", sb.toString());
    }
    
    public void selfcheck() {
        sendCommand(Constant.COMM_SELF_CHECK);
    }
}
