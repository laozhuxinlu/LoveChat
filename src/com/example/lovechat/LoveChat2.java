package com.example.lovechat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.lovechat.LoveChats.ThreadShow1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LoveChat2 extends Activity {

	private TextView state11,state22,state33;
	private ListView mListView;
	private EditText message;
	private Button sendmessage,back2,call2;
	
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
	private ServerThread startServerThread = null;
	private BluetoothServerSocket mserverSocket = null; 
	 private readThread mreadThread = null;
	 
	 private List<String> msgList=new ArrayList<String>();
	 private ArrayAdapter<String> mAdapter; 
	 
	 private Boolean connectState;
	 
	 private Boolean flagtime;
	 
	 static final int NOTIFICATION_ID1 = 0x123;
	 NotificationManager nm;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lovechat2);
		
		connectState = false;
		flagtime = true;
		
		state11 = (TextView)findViewById(R.id.state112);
		state22 = (TextView)findViewById(R.id.state222);
		state33 = (TextView)findViewById(R.id.state332);
		message = (EditText)findViewById(R.id.ET12);
		sendmessage = (Button)findViewById(R.id.sendMessage2);
		back2 = (Button)findViewById(R.id.Back2);
		call2 = (Button)findViewById(R.id.call2);
		
		 mAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);  
         mListView = (ListView) findViewById(R.id.LV22);  
         mListView.setAdapter(mAdapter);  
         mListView.setFastScrollEnabled(true); 
         
		
		state11.setText("从设备");
		state22.setText("未连接");
		state33.setText("蓝牙已连接，请建立通讯");
		
		  shutdownServer(); 
		  startServerThread = new ServerThread();  
          startServerThread.start(); 
		
		sendmessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub			
				String msgText =message.getText().toString();  
                if (msgText.length()>0) {  
                    sendMessageHandle(msgText);
                    message.setText("");  
                    message.clearFocus();  
                    //close InputMethodManager  
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0);  
                }else  
                Toast.makeText(LoveChat2.this, "发送内容不能为空！", Toast.LENGTH_SHORT).show();  
			}
		});
		
		back2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sendMessageHandle("010111001010");
                message.setText("");  
                message.clearFocus();  
                //close InputMethodManager  
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
                imm.hideSoftInputFromWindow(message.getWindowToken(), 0); 
                
                 state22.setText("未连接");
                 shutdownServer(); 
     			 startServerThread = new ServerThread();  
                 startServerThread.start();  
			}
		});
		 	
		call2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 sendMessageHandle("100101100001");
	             //close InputMethodManager  
	             InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
	             imm.hideSoftInputFromWindow(message.getWindowToken(), 0); 
			}
		});
	
	}
	
	//开启服务器
//	  @Override  
//	    protected void onResume() {
//		  			//shutdownServer();       
//	                startServerThread = new ServerThread();  
//	                startServerThread.start();  
//	                //BluetoothMsg_isOpen = true;  
//	        super.onResume();  
//	    } 
	  
	  //建立服务器  
	    private class ServerThread extends Thread {   
	        @Override  
	        public void run() {  
	                      
	            try {  
	                /* 创建一个蓝牙服务器  
	                 * 参数分别：服务器名称、UUID   */   
	                mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Clay_Server",  
	                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));         
	                  
	                Log.i("server", "wait cilent connect...");  
	                  
	                Message msg1 = new Message();  
	                msg1.obj = "请稍候，正在等待客户端的连接...";  
	                msg1.what = 1;  
	                LinkDetectedHandler.sendMessage(msg1);
	                
	                Message msg3 = new Message();  
	                msg3.obj = "通讯已建立，请建立客户端";  
	                msg3.what = 5;  
	                LinkDetectedHandler.sendMessage(msg3);  
	                  
	                /* 接受客户端的连接请求 */ 
	            
	                socket = mserverSocket.accept();  
	                Log.i("server", "accept success !");  
	                
	                Message msg2 = new Message();  
	                String info2 = "已连接";  
	                msg2.obj = info2;  
	                msg2.what = 1;  
	                LinkDetectedHandler.sendMessage(msg2);
	                
	                Message msg = new Message();  
	                String info = "客户端已经连接上！可以发送信息。";  
	                msg.obj = info;  
	                msg.what = 1;  
	                LinkDetectedHandler.sendMessage(msg); 
	        		//开启定时器
	        		new Thread(new ThreadShow1()).start();
	                //启动接受数据  
	                mreadThread = new readThread();  
	                mreadThread.start();  
	            } catch (IOException e) { 
	            	 Message msg = new Message();  
		             msg.obj = "客户端连接异常！断开连接重新试一试。";  
		             msg.what = 1;  
		             LinkDetectedHandler.sendMessage(msg); 
		             
		             Message msg3 = new Message();  
		                msg3.obj = "通讯建立失败，请重建服务器";  
		                msg3.what = 5;  
		                LinkDetectedHandler.sendMessage(msg3); 
		                
	                e.printStackTrace();  
	            }  
	        }  
	    }; 

	    
	    //发送数据  
	    private void sendMessageHandle(String msg)   
	    {         
	        if (socket == null)   
	        {  
	            Toast.makeText(LoveChat2.this, "没有连接", Toast.LENGTH_SHORT).show();  
	            return;  
	        }  
	        try {                 
	            OutputStream os = socket.getOutputStream();   
	            os.write(msg.getBytes());  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }             
	        if(msg.equals("100101100001")){
	        	msgList.add("正在呼叫对方……");  
	 	        mAdapter.notifyDataSetChanged();  
	 	        mListView.setSelection(msgList.size() - 1); 
	        	
	        }else
		        if(msg.equals("010111001010")){
		        	msgList.add("已重建");  
		 	        mAdapter.notifyDataSetChanged();  
		 	        mListView.setSelection(msgList.size() - 1);
		        }else{
	        	msgList.add("我："+msg);  
	 	        mAdapter.notifyDataSetChanged();  
	 	        mListView.setSelection(msgList.size() - 1); 
	        }
	    }  
	    
	    //读取数据  
	    private class readThread extends Thread {   
	        @Override  
	        public void run() {  
	              
	            byte[] buffer = new byte[1024];  
	            int bytes;  
	            InputStream mmInStream = null;  
	              
	            try {  
	                mmInStream = socket.getInputStream();  
	            } catch (IOException e1) {  
	                // TODO Auto-generated catch block  
	                e1.printStackTrace();  
	            }     
	            while (true) {  
	                try {  
	                    // Read from the InputStream  
	                    if( (bytes = mmInStream.read(buffer)) > 0 )  
	                    {  
	                        byte[] buf_data = new byte[bytes];  
	                        for(int i=0; i<bytes; i++)  
	                        {  
	                            buf_data[i] = buffer[i];  
	                        }  
	                        String s = new String(buf_data); 
	                        if(s.equals("100101100001")){
	                        	
	                        	Message msg = new Message();  
	        	                String info = "对方正在呼叫您……";  
	        	                msg.obj = info;  
	        	                msg.what = 0;  
	        	                LinkDetectedHandler.sendMessage(msg);
	        	                
	                        	 Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			                     Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			                     r.play();
	                        }else 
	                        	if(s.equals("010111001010")){
	                        		 //关闭定时器(终结其他线程的方法)实现错误
	                        		 //关闭定时器
//	                        		flagtime = false;
//		        	                new Thread(new ThreadShow1()).destroy();
	                        		Message msg = new Message();  
		        	                String info = "已断开";  
		        	                msg.obj = info;  
		        	                msg.what = 1;  
		        	                LinkDetectedHandler.sendMessage(msg);	        	             
	                        		 shutdownServer(); 
	                     			 startServerThread = new ServerThread();  
	                                 startServerThread.start(); 
	                        	}else
	                        		if(s.equals("101011001001")){		                        			
	                        			//关闭定时器
//	                        			flagtime = false;
//			        	                new Thread(new ThreadShow1()).destroy();
			        	                Message msg1 = new Message();  		        	               
			        	                msg1.obj = "客户端已退出，请重建客户端";  
			        	                msg1.what = 5; 
			        	                LinkDetectedHandler.sendMessage(msg1);
			        	                shutdownServer(); 
		                     			startServerThread = new ServerThread();  
		                                startServerThread.start(); 
			        	             
	                        		}
	                        else
	                        {
//	                        	notifi(s);  //在状态栏点击有bug
	                        	Message msg = new Message();  
		                        msg.obj = "他："+s;  
		                        msg.what = 0;  
		                        LinkDetectedHandler.sendMessage(msg);  
		                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		                        r.play();
	                        }                        
	                    }  
	                } catch (IOException e) {  
	                    try {  
	                        mmInStream.close();  
	                    } catch (IOException e1) {  
	                        // TODO Auto-generated catch block  
	                        e1.printStackTrace();  
	                    }  
	                    break;  
	                }  
	            }  
	        }  
	    };
	    
	    private Handler LinkDetectedHandler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {  

	        	if(msg.what==0){
	        		msgList.add((String)msg.obj);  
	 	            mAdapter.notifyDataSetChanged();  
	 	            mListView.setSelection(msgList.size() - 1);
	        	}
	        	if(msg.what==1){        		
	        		state22.setText((String)msg.obj);
	        	} 
	        	if(msg.what==2){   
	        		state22.setText("已断开");
	        		msgList.add((String)msg.obj);  
	 	            mAdapter.notifyDataSetChanged();  
	 	            mListView.setSelection(msgList.size() - 1);
	 	           shutdownServer();
	 	           startServerThread = new ServerThread();  
	               startServerThread.start(); 
	        	} 
	        	if(msg.what==3){        		
	        		//蓝牙断开处理
	        			state22.setText("已断开");
	        			state33.setText("蓝牙已断开，请重新建立连接并开启服务器");
	        	} 
	        	if(msg.what==4){        		
	        		//蓝牙连接处理
	        			state33.setText("通讯已连接");
	        	}  
	        	if(msg.what==5){        		
	        		//蓝牙连接处理
	        			state33.setText((String)msg.obj);
	        	} 
	        }
	    };
	    	    
	    /* 停止服务器 */  
	    private void shutdownServer() {  
	        new Thread() {  
	            @Override  
	            public void run() {  
	                if(startServerThread != null)  
	                {  
	                    startServerThread.interrupt();  
	                    startServerThread = null;  
	                }  
	                if(mreadThread != null)  
	                {  
	                    mreadThread.interrupt();  
	                    mreadThread = null;  
	                }                 
	                try {                     
	                    if(socket != null)  
	                    {  
	                        socket.close();  
	                        socket = null;  
	                    }  
	                    if (mserverSocket != null)  
	                    {  
	                        mserverSocket.close();/* 关闭服务器 */  
	                        mserverSocket = null;  
	                    }  
	                } catch (IOException e) {  
	                    Log.e("server", "mserverSocket.close()", e);  
	                }  
	            };  
	        }.start();  
	    }  

	 	 //Back改写
	 	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	 	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
	 	        	 sendMessageHandle("101011001001");
	                    message.setText("");  
	                    message.clearFocus();  
	                    //close InputMethodManager  
	                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
	                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0); 
	 	        	super.onBackPressed();   //注释掉这行,back键不退出activity
	 	        	//具体操作添加
					Intent tTntent = new Intent();
					tTntent.setClass(LoveChat2.this,MainActivity.class);
					startActivity(tTntent);	
					//关闭当前页面
	                android.os.Process  
	                        .killProcess(android.os.Process  
	                                .myPid());  
	                finish(); 
	 	            return true;  
	 	        } else  
	 	            return super.onKeyDown(keyCode, event);  
	 	    } 

	 	//定时器线程判断蓝牙是否断开
	 // 线程类  
	 	
	    class ThreadShow1 implements Runnable {  
	  	    	
	        @Override  
	        public void run() {  
	            // TODO Auto-generated method stub  
	            while (flagtime) {  
	                try {  
	                    Thread.sleep(4000); 
	                    //获取蓝牙状态并赋给msg.obj	                    
	                    Message msg = new Message();                    
	                    Boolean connectState = socket.isConnected();
	                    if(connectState){
	                    	msg.what = 4;  
	                    }else{
	                    	msg.what = 3;  
	                    }	                    
	                    LinkDetectedHandler.sendMessage(msg);  
	                } catch (Exception e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();   
	                }  
	            }  
	        }  
	    }  
	    
	    //notification
	    private void notifi(String s){
	    	
	    	// 创建一个启动其他Activity的Intent
			Intent intent = new Intent(this
				, LoveChats.class);
			PendingIntent pi = PendingIntent.getActivity(
				this, 0, intent, 0);
			Notification notify = new Notification.Builder(this)
				// 设置打开该通知，该通知自动消失
				.setAutoCancel(true)
				// 设置显示在状态栏的通知提示信息
				.setTicker("有新消息")
				// 设置通知的图标
				.setSmallIcon(R.drawable.lovechat)
				// 设置通知内容的标题
				.setContentTitle("一条新通知")
				// 设置通知内容
				.setContentText(s)
				// // 设置使用系统默认的声音、默认LED灯
//				 .setDefaults(Notification.DEFAULT_SOUND
//				 |Notification.DEFAULT_LIGHTS)
				// 设置通知的自定义声音
				.setSound(Uri.parse("android.resource://org.crazyit.ui/"
					+ R.raw.msg))
				.setWhen(System.currentTimeMillis())
				// 设改通知将要启动程序的Intent
				.setContentIntent(pi)
				.build();
			// 发送通知
			nm.notify(NOTIFICATION_ID1, notify);
	    	
	    }
}
