package com.fei435;

import android.R.integer;
import android.util.Log;

public class Constant {
	public static final String PREF_KEY_ROUTER_URL = "pref_key_router_url";
	public static final String PREF_KEY_CAMERA_URL = "pref_key_camera_url";
	
	public static final String PREF_KEY_TEST_MODE_ENABLED = "pref_key_test_enabled";
	public static final String PREF_KEY_ROUTER_URL_TEST = "pref_key_router_url_test";
	public static final String PREF_KEY_CAMERA_URL_TEST = "pref_key_camera_url_test";
	
	public static final String PREF_KEY_LEN_ON = "pref_key_len_on";
	public static final String PREF_KEY_LEN_OFF = "pref_key_len_off";
	
	//第一次安装应用程序后，还没有启动过，没有创建sharedPreference
	//这些值就是 sharedPreference的默认值
	//TODO:项目完成后更改这些值
	public static final String DEFAULT_VALUE_CAMERA_URL = "http://192.168.128.135:8080/?action=stream";
	public static final String DEFAULT_VALUE_ROUTER_URL = "192.168.128.135:2001";
	public static final String DEFAULT_VALUE_CAMERA_URL_TEST = "http://192.168.128.135:8080/?action=stream";
	public static final String DEFAULT_VALUE_ROUTER_URL_TEST = "192.168.128.135:2001";
	
	public static final String DEFAULT_VALUE_LEN_ON = "FF040100FF";
	public static final String DEFAULT_VALUE_LEN_OFF = "FF040000FF";
	
	public static final int COMMAND_LENGTH = 5;
	public static final int COMMAND_RADIOX = 16;
	public static final int MIN_COMMAND_REC_INTERVAL = 1000;//ms
	
    public static final String ACTION_TAKE_PICTURE_DONE = "fei435.take_picture_done";
    public static final String ACTION_RECORDING_START = "fei435.recording_start";
    public static final String ACTION_RECORDING_STOP = "fei435.recording_stop";
    public static final String EXTRA_RES = "res";
    public static final String EXTRA_PATH = "path";
   
    public final static int CAM_RES_OK = 6;
    public final static int CAM_RES_FAIL_FILE_WRITE_ERROR = 7;
    public final static int CAM_RES_FAIL_FILE_NAME_ERROR = 8;
    public final static int CAM_RES_FAIL_NO_SPACE_LEFT = 9;
    public final static int CAM_RES_FAIL_BITMAP_ERROR = 10;
    public final static int CAM_RES_FAIL_UNKNOW = 20;
    
    public final static int RECORDER_STOP_OK = 21;
    public final static int RECORDER_STOP_FAILED = 22;
    public final static int RECORDER_START_OK = 23;
    public final static int RECORDER_START_FAILED = 23;
    
    public final static int CONNECTION_TIMEOUT_INT = 3*1000;  //http连接超时时间
    public final static int SO_TIMEOUT_INT = 3*1000;		  //http等待data时间
    public final static int SOCKET_TIMEOUT = 3*1000;
    
    //////////////////////////////////////////////////////////
    public static boolean m4test = false;
    public static String CAMERA_VIDEO_URL = "";
    public static String CAMERA_VIDEO_URL_TEST = "";
    public static String ROUTER_CONTROL_URL = "";
    public static String ROUTER_CONTROL_URL_TEST = "";
    public static int ROUTER_CONTROL_PORT = 2001;
    public static int ROUTER_CONTROL_PORT_TEST = 2001;
    
    public final static int STATUS_INIT = 0x2001;
    //public final static int STATUS_CONNECTING = 0x2002;
    public final static int STATUS_CONNECTED = 0x2003;
    
    public final static int WIFI_STATE_UNKNOW = 0x3000;
    public final static int WIFI_STATE_DISABLED = 0x3001;
    public final static int WIFI_STATE_NOT_CONNECTED = 0x3002;
    public final static int WIFI_STATE_CONNECTED = 0x3003;
    public static int CURRENT_WIFI_STATE = 0x3000;
    
    public final static String WIFI_SSID_PERFIX = "";
    
    public final static int MSG_ID_ERR_CONN = 1001;
    //public final int MSG_ID_ERR_SEND = 1002;
    public final static int MSG_ID_ERR_RECEIVE = 1003;
    public final static int MSG_ID_CON_READ = 1004;
    public final static int MSG_ID_CON_SUCCESS = 1005;    
    public final static int MSG_ID_START_CHECK = 1006;
    public final static int MSG_ID_ERR_INIT_READ = 1007;
    public final static int MSG_ID_CLEAR_QUIT_FLAG = 1008;
    public final static int MSG_ID_SET_UI_INFO = 1009;
    
    public final static int MSG_ID_LOOP_START = 1010;
    public final static int MSG_ID_HEART_BREAK_RECEIVE = 1011;
    public final static int MSG_ID_HEART_BREAK_SEND = 1012;
    public final static int MSG_ID_LOOP_END = 1013;
    public final static int MSG_ID_SET_SPEED = 1014;
    
    public final static byte COMMAND_PERFIX = -1;
    public final static int HEART_BREAK_CHECK_INTERVAL = 8000;//ms
    public final static int QUIT_BUTTON_PRESS_INTERVAL = 2500;//ms
    public final static int HEART_BREAK_SEND_INTERVAL = 2500; //ms
    
    public static byte[] COMM_FORWARD =  {(byte) 0xFF, (byte)0x00, (byte)0x01, (byte)0x00, (byte) 0xFF};
    public static byte[] COMM_BACKWARD = {(byte) 0xFF, (byte)0x00, (byte)0x02, (byte)0x00, (byte) 0xFF};
    public static byte[] COMM_STOP =     {(byte) 0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte) 0xFF};
    public static byte[] COMM_LEFT =     {(byte) 0xFF, (byte)0x00, (byte)0x03, (byte)0x00, (byte) 0xFF};
    public static byte[] COMM_RIGHT =    {(byte) 0xFF, (byte)0x00, (byte)0x04, (byte)0x00, (byte) 0xFF};

    public static byte[] COMM_LEN_ON =   {(byte) 0xFF, (byte)0x04, (byte)0x03, (byte)0x00, (byte) 0xFF};
    public static byte[] COMM_LEN_OFF =  {(byte) 0xFF, (byte)0x04, (byte)0x02, (byte)0x00, (byte) 0xFF};

    public static byte[] COMM_GEAR_CONTROL_1 =   {(byte) 0xFF, (byte)0x01, (byte)0x01, (byte)0x00, (byte) 0xFF};
    public static byte[] COMM_GEAR_CONTROL_2 =   {(byte) 0xFF, (byte)0x01, (byte)0x02, (byte)0x00, (byte) 0xFF};
    
    //这个的值是一打开app就要发送的初始速度分别是506  16
    public static byte[] COMM_SPEED_VALUE_1 =   {(byte) 0xFF, (byte)0x04, (byte)0x01, (byte)0xFA, (byte) 0xFF};
    public static byte[] COMM_SPEED_VALUE_2 =   {(byte) 0xFF, (byte)0x05, (byte)0x00, (byte)0x10, (byte) 0xFF};

    public static byte[] COMM_SELF_CHECK =     {(byte) 0xFF, (byte)0xEE, (byte)0xEE, 0x00, (byte) 0xFF};
    public static byte[] COMM_SELF_CHECK_ALL = {(byte) 0xFF, (byte)0xEE, (byte)0xE0, 0x00, (byte) 0xFF};

    public static byte[] COMM_HEART_BREAK = {(byte) 0xFF, (byte)0xEE, (byte)0xE1, 0x00, (byte) 0xFF};
    /////////////////////////////////////////////////////////
    
	public static class CommandArray {
		
		public byte mCmd1 = 0;
		public byte mCmd2 = 0;
		public byte mCmd3 = 0;
		public CommandArray (int cmd1, int cmd2, int cmd3) {
			mCmd1 = (byte)cmd1;
			mCmd2 = (byte)cmd2;
			mCmd3 = (byte)cmd3;
		}
		
		public CommandArray (String cmdLine) {
	        int icmd1 = -1;
			int icmd2 = -1;
			int icmd3 = -1;
			
			if (cmdLine != null 
	    			&& (cmdLine.startsWith("FF") || cmdLine.startsWith("ff"))
	    			&& (cmdLine.endsWith("FF") || cmdLine.endsWith("ff"))
	    			&& cmdLine.length() == COMMAND_LENGTH*2 ) {
	    		String cmd1 = cmdLine.substring(2, 4);
	    		String cmd2 = cmdLine.substring(4, 6);
	    		String cmd3 = cmdLine.substring(6, 8);
	    		
	    		try {
	    			icmd1 = Integer.parseInt(cmd1, COMMAND_RADIOX);
	    			icmd2 = Integer.parseInt(cmd2, COMMAND_RADIOX);
	    			icmd3 = Integer.parseInt(cmd3, COMMAND_RADIOX);
	    		} catch (Exception e) {
	    			icmd1 = icmd2 = icmd3 = -1;
	    		}
	    		
	    		if (icmd1 >= 0 && icmd2 >= 0 && icmd3 >= 0) {
    				mCmd1 = (byte)icmd1;
    				mCmd2 = (byte)icmd2;
    				mCmd3 = (byte)icmd3;
	    	
	    		} else {
	    			Log.i("Constant", "uncorrect command:" + cmdLine 
	    					+ " cmd1=" + icmd1
	    					+ " cmd2=" + icmd2
	    					+ " cmd3=" + icmd3);
	    		}
	    	} else {
	    		Log.i("Constant", "error format command:" + cmdLine 
    					+ " cmd1=" + icmd1
    					+ " cmd2=" + icmd2
    					+ " cmd3=" + icmd3);
	    	}
		}
		
		public boolean isValid() {
			if (mCmd1 != 0 || mCmd2 != 0 || mCmd3 != 0) {
    			return true;
    		} else {
    			return false;
    		}
		}
	}

}
