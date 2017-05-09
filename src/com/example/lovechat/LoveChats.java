package com.example.lovechat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
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

public class LoveChats extends Activity {
	
	private TextView state11,state22,state33;
	private ListView mListView;
	private EditText message;
	private Button sendmessage,back,call;
	
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
	private clientThread clientConnectThread = null;
	private readThread mreadThread = null;
	 
	 private List<String> msgList=new ArrayList<String>();
	 private ArrayAdapter<String> mAdapter; 
	 
	 private String address;
	 
	 static final int NOTIFICATION_ID1 = 0x123;
	 NotificationManager nm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lovechats);
		
		// 获取系统的NotificationManager服务
				nm = (NotificationManager) 
					getSystemService(NOTIFICATION_SERVICE);
				
		state11 = (TextView)findViewById(R.id.state11);
		state22 = (TextView)findViewById(R.id.state22);
		state33 = (TextView)findViewById(R.id.state33);
		message = (EditText)findViewById(R.id.ET1);
		sendmessage = (Button)findViewById(R.id.sendMessage);
		back = (Button)findViewById(R.id.Back);
		call = (Button)findViewById(R.id.call);
		
		 mAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);  
         mListView = (ListView) findViewById(R.id.LV2);  
         mListView.setAdapter(mAdapter);  
         mListView.setFastScrollEnabled(true); 
         
         state22.setText("未连接");      		
		state11.setText("主设备||连接设备："+ChatActivity.ConnectBTname);
		state33.setText("蓝牙已连接，请建立通讯");
		
		address = ChatActivity.connectBTaddress; 
		device = mBluetoothAdapter.getRemoteDevice(address); 
		shutdownClient();
		//打开客户端确认
		showDialog(1);
		
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
                Toast.makeText(LoveChats.this, "发送内容不能为空！", Toast.LENGTH_SHORT).show();  
			
				
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			
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
                shutdownClient();
				showDialog(1);	
			}
		});
	
		call.setOnClickListener(new OnClickListener() {
		
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
	
	//开启客户端
	
//	   @Override  
//	    protected void onResume() {            	          
// 	                
//		   			//shutdownClient();
//	                if(!address.equals("null"))  
//	                {  	
//	                    device = mBluetoothAdapter.getRemoteDevice(address);          
//	                    clientConnectThread = new clientThread();  
//	                    clientConnectThread.start();  
//	              //      BluetoothMsg_isOpen = true;  
//	                }  
//	                else  
//	                {  
//	                    Toast.makeText(LoveChats.this, "address is null !", Toast.LENGTH_SHORT).show();  
//	                }   
//	        super.onResume();  
//	    } 
	   
	    //建立客户端  

	    private class clientThread extends Thread {           
	        @Override  
	        public void run() {  
	            try {  
	                //创建一个Socket连接：只需要服务器在注册时的UUID号  	                
	                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));  
	                //连接  
	                Message msg1 = new Message();  
	                msg1.obj = "请稍候，正在连接服务器:";  
	                msg1.what = 1;  
	                LinkDetectedHandler.sendMessage(msg1);  
	                  
	                socket.connect(); 
	                
	                Message msg2 = new Message();  
	                msg2.obj = "已连接";
//	                + BluetoothMsg.BlueToothAddress;  
	                msg2.what = 1;  
	                LinkDetectedHandler.sendMessage(msg2);
	                
	                Message msg = new Message();  
	                msg.obj = "已经连接上服务端！可以发送信息。";  
	                msg.what = 1;  
	                LinkDetectedHandler.sendMessage(msg); 	
	        		//开启定时器
	        		new Thread(new ThreadShow1()).start();
	                //启动接受数据  
	                mreadThread = new readThread();  
	                mreadThread.start();
//	                sendMessageHandle("对方已上线"); 
//	                //close InputMethodManager  
//	                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
//	                imm.hideSoftInputFromWindow(message.getWindowToken(), 0); 
	            }   
	            catch (IOException e)   
	            {  
	                Log.e("connect", "", e);  
	                Message msg = new Message();  
	                msg.obj = "连接服务端异常！断开连接重新试一试。";  
	                msg.what = 1;  
	                LinkDetectedHandler.sendMessage(msg);  
	                
	                Message msg1 = new Message();  
	                msg1.obj = "服务器建立失败，请确认服务器已建立后重建客户端";  
	                msg1.what = 1;  
	                LinkDetectedHandler.sendMessage(msg1);  
	            }   
	        }  
	    };    
	
	    //发送数据  
	    private void sendMessageHandle(String msg)   
	    {         
	        if (socket == null)   
	        {  
	            Toast.makeText(LoveChats.this, "没有连接", Toast.LENGTH_SHORT).show();   
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
	        }else
	        {
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
	                        		
		        	                shutdownClient();
		        	                Message msg = new Message();  		        	               
		        	                msg.obj = "已断开";  
		        	                msg.what = 2; 
		        	                LinkDetectedHandler.sendMessage(msg);
		        	                
		        	                //关闭定时器(终结其他线程的方法)实现错误
//		        	                new Thread(new ThreadShow1()).stop();
		        	                 
	                        	}else
	                        		if(s.equals("101011001001")){			        	                
			        	                Message msg1 = new Message();  		        	               
			        	                msg1.obj = "服务器已退出，请先建立服务器";  
			        	                msg1.what = 5; 
			        	                LinkDetectedHandler.sendMessage(msg1);
			        	                
			        	              //关闭定时器
//			        	                new Thread(new ThreadShow1()).stop();
	 
	                        		}else
	                        {
//	                        	notifi(s);    //在状态栏点击有bug
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
	        		
	        		shutdownClient();
	        		state22.setText((String)msg.obj);
	        		showDialog(1);
	        	}
	        	if(msg.what==3){
	        		
	        		state22.setText("已断开");
	        		msgList.add((String)msg.obj);  
	 	            mAdapter.notifyDataSetChanged();  
	 	            mListView.setSelection(msgList.size() - 1);
	 	            shutdownClient();
	        	}
	        	if(msg.what==4){        		
	        		//蓝牙断开处理
	        			state22.setText("已断开");
	        			state33.setText("蓝牙已断开，请重新建立连接并开启服务器");
//	        			shutdownClient();
	        	} 
	        	if(msg.what==5){        		
	        		//蓝牙连接处理
	        			state33.setText("通讯已连接");
	        	} 
	             
	        }  
	    };
	    
	    /* 停止客户端连接 */  
	    private void shutdownClient() {  
	        new Thread() {  
	            @Override  
	            public void run() {  
	                if(clientConnectThread!=null)  
	                {  
	                    clientConnectThread.interrupt();  
	                    clientConnectThread= null;  
	                }  
	                if(mreadThread != null)  
	                {  
	                    mreadThread.interrupt();  
	                    mreadThread = null;  
	                }  
	                if (socket != null) {  
	                    try {  
	                        socket.close();  
	                    } catch (IOException e) {  
	                        // TODO Auto-generated catch block  
	                        e.printStackTrace();  
	                    }  
	                    socket = null;  
	                }  
	            };  
	        }.start();  
	    }
	    
	    
	  	//自定义弹框形式
	 	 protected Dialog onCreateDialog(int id) { 
 
	 		 	//简单提示，并提供Button供用户执行
	 	        if (id == 1) {  
	 	            return new AlertDialog.Builder(LoveChats.this)  
	 	            		//设置弹框的内容
	 	                    .setMessage("请确定对象设备已进入Salve界面")  
	 	                    //设置弹框的标题
	 	                    .setTitle("请确认！") 
	 	                    //设置确定按键
	 	                    .setPositiveButton("确定",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//以下是“确定”按键的操作
	 	                                	//打开客户端	 	                                   
	 	                                   device = mBluetoothAdapter.getRemoteDevice(address);          
	 	                   				   clientConnectThread = new clientThread();  
	 	                                   clientConnectThread.start();                                   
	 	                                    dialog.dismiss();  	                                     	  
	 	                                }  
	 	                            })  
	 	                     //设置取消按键
	 	                    .setNegativeButton("取消",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//以下是“取消”按键的操作
//	 	                                	back.setEnabled(true);
	 	                                    dialog.dismiss();  //去掉弹框
	 	  
	 	                                }  
	 	                            }).create();          	
	 	        }  
	 	        
	 		 	//简单提示，并提供Button供用户执行
	 	        if (id == 2) {  
	 	            return new AlertDialog.Builder(LoveChats.this)  
	 	            		//设置弹框的内容
	 	                    .setMessage("请确定对象设备已进入Salve界面")  
	 	                    //设置弹框的标题
	 	                    .setTitle("请确认！") 
	 	                    //设置确定按键
	 	                    .setPositiveButton("确定",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//以下是“确定”按键的操作
	 	                                	//打开客户端	 	                                   
	 	                               //    device = mBluetoothAdapter.getRemoteDevice(address);          
//	 	                   				   clientConnectThread = new clientThread();  
//	 	                                   clientConnectThread.start();                                   
	 	                                    dialog.dismiss();  	                                     	  
	 	                                }  
	 	                            })  
	 	                     //设置取消按键
	 	                    .setNegativeButton("取消",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//以下是“取消”按键的操作
//	 	                                	back.setEnabled(true);
	 	                                    dialog.dismiss();  //去掉弹框
	 	  
	 	                                }  
	 	                            }).create();          	
	 	        }  
	 	      	 	        
	 	        return null;  
	 	  
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
//					Intent tTntent = new Intent();
//					tTntent.setClass(LoveChats.this,ChatActivity.class);
//					startActivity(tTntent);	
//					//关闭当前页面
//	                android.os.Process  
//	                        .killProcess(android.os.Process  
//	                                .myPid());  
//	                finish(); 
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
	            while (true) {  
	                try {  
	                    Thread.sleep(4000); 
	                    //获取蓝牙状态并赋给msg.obj
	                    Message msg = new Message();
                        Boolean connectState = socket.isConnected();
	                    if(connectState){
	                    	msg.what = 5;  
	                    }else{
	                    	msg.what = 4;  
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
