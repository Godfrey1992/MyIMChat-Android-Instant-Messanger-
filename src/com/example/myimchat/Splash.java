package com.example.myimchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_up);
		
		
		Thread timer = new Thread(){
			@Override
			public void run(){
				try{
					sleep(5000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					//Starts a new activity
					Intent openMain = new Intent(Splash.this, Logi.class);
					startActivity(openMain);
				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		//ourSong.release();
		finish();
	}
	
	
}
