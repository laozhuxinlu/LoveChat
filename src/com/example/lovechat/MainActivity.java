package com.example.lovechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button login;
	private Button register;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		login = (Button)findViewById(R.id.login);
		register = (Button)findViewById(R.id.register);
		
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Log.i("find","begin1");
				Intent tTntent = new Intent();
				tTntent.setClass(MainActivity.this,ChatActivity.class);
				startActivity(tTntent);	
				
			}
		});
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//showDialog(1); 
				Intent tTntent = new Intent();
				tTntent.setClass(MainActivity.this,LoveChat2.class);
				startActivity(tTntent);	
				//关闭当前页面
                android.os.Process  
                        .killProcess(android.os.Process  
                                .myPid());  
                finish(); 
			}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
