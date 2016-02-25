package com.fei435;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;



public class Splash extends Activity {
	
	private ImageButton startMain;
	private ImageButton config;
	
    private Drawable startDrawable;
    private Drawable configDrawable;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题（应用的名字必须要写在setContentView之前，否则会有异常）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        
        startMain = (ImageButton)findViewById(R.id.btnStartMain);
        config = (ImageButton)findViewById(R.id.btnConfig);
        
        startMain.setOnClickListener(
			new ImageButton.OnClickListener(){
				public void onClick(View v) {
					Intent setIntent = new Intent();
					setIntent.setClass(Splash.this, Main.class);
					startActivity(setIntent);
					
					finish();  
		            System.exit(0);
				}
			});
        
        config.setOnClickListener(
        		new ImageButton.OnClickListener(){
        			public void onClick(View v) {
        				Intent setIntent = new Intent();
        				setIntent.setClass(Splash.this, WifiCarSettings.class);
        				startActivity(setIntent);
					}
        			
        		});
    }
}
