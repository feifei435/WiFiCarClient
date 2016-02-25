package com.fei435;

import com.fei435.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HanryReceiver extends BroadcastReceiver 
{ 
    @Override 
    public void onReceive(Context context, Intent intent) 
    { 
    	Log.i("ScreenCapture Intent", "onReceive intent = " + intent.getAction());
    	
        if (Constant.ACTION_TAKE_PICTURE_DONE.equals(intent.getAction())) 
        { 
            Bundle bundle = intent.getExtras(); 
            if (bundle != null) 
            { 
                int res = bundle.getInt(Constant.EXTRA_RES); 
                String text = bundle.getString(Constant.EXTRA_PATH);
                
                Log.i("Intent", "onReceive intent, res= " + res + " path=" + text);
                
                switch (res) {
                case Constant.CAM_RES_OK:
                	Toast.makeText(context, "≥…π¶±£¥Ê’’∆¨£∫" + text, Toast.LENGTH_LONG).show();
                	break;
                case Constant.CAM_RES_FAIL_BITMAP_ERROR:
                case Constant.CAM_RES_FAIL_FILE_NAME_ERROR:
                case Constant.CAM_RES_FAIL_FILE_WRITE_ERROR:
                case Constant.CAM_RES_FAIL_NO_SPACE_LEFT:
                case Constant.CAM_RES_FAIL_UNKNOW:
                	Toast.makeText(context, "±£¥Ê’’∆¨ ß∞‹£∫Error = " + res, Toast.LENGTH_LONG).show();
                	break;
                default:
                	break;
                }
            } 
        }
        else if (Constant.ACTION_RECORDING_START.equals(intent.getAction()) || 
        		Constant.ACTION_RECORDING_STOP.equals(intent.getAction()) ){
        	Log.i("ScreenCapture Intent", "onReceive intent = " + intent.getAction());
        	Bundle bundle = intent.getExtras();
        	if (bundle != null){
        		int res = bundle.getInt(Constant.EXTRA_RES);
	        	String text = bundle.getString(Constant.EXTRA_PATH);
	        	
	        	Log.i("ScreenCapture Intent", "onReceive intent, res= " + res + " path=" + text);
	        	
	        	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        	}
        }
    }
} 