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
         
		
		state11.setText("���豸");
		state22.setText("δ����");
		state33.setText("���������ӣ��뽨��ͨѶ");
		
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
                Toast.makeText(LoveChat2.this, "�������ݲ���Ϊ�գ�", Toast.LENGTH_SHORT).show();  
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
                
                 state22.setText("δ����");
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
	
	//����������
//	  @Override  
//	    protected void onResume() {
//		  			//shutdownServer();       
//	                startServerThread = new ServerThread();  
//	                startServerThread.start();  
//	                //BluetoothMsg_isOpen = true;  
//	        super.onResume();  
//	    } 
	  
	  //����������  
	    private class ServerThread extends Thread {   
	        @Override  
	        public void run() {  
	                      
	            try {  
	                /* ����һ������������  
	                 * �����ֱ𣺷��������ơ�UUID   */   
	                mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Clay_Server",  
	                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));         
	                  
	                Log.i("server", "wait cilent connect...");  
	                  
	                Message msg1 = new Message();  
	                msg1.obj = "���Ժ����ڵȴ��ͻ��˵�����...";  
	                msg1.what = 1;  
	                LinkDetectedHandler.sendMessage(msg1);
	                
	                Message msg3 = new Message();  
	                msg3.obj = "ͨѶ�ѽ������뽨���ͻ���";  
	                msg3.what = 5;  
	                LinkDetectedHandler.sendMessage(msg3);  
	                  
	                /* ���ܿͻ��˵��������� */ 
	            
	                socket = mserverSocket.accept();  
	                Log.i("server", "accept success !");  
	                
	                Message msg2 = new Message();  
	                String info2 = "������";  
	                msg2.obj = info2;  
	                msg2.what = 1;  
	                LinkDetectedHandler.sendMessage(msg2);
	                
	                Message msg = new Message();  
	                String info = "�ͻ����Ѿ������ϣ����Է�����Ϣ��";  
	                msg.obj = info;  
	                msg.what = 1;  
	                LinkDetectedHandler.sendMessage(msg); 
	        		//������ʱ��
	        		new Thread(new ThreadShow1()).start();
	                //������������  
	                mreadThread = new readThread();  
	                mreadThread.start();  
	            } catch (IOException e) { 
	            	 Message msg = new Message();  
		             msg.obj = "�ͻ��������쳣���Ͽ�����������һ�ԡ�";  
		             msg.what = 1;  
		             LinkDetectedHandler.sendMessage(msg); 
		             
		             Message msg3 = new Message();  
		                msg3.obj = "ͨѶ����ʧ�ܣ����ؽ�������";  
		                msg3.what = 5;  
		                LinkDetectedHandler.sendMessage(msg3); 
		                
	                e.printStackTrace();  
	            }  
	        }  
	    }; 

	    
	    //��������  
	    private void sendMessageHandle(String msg)   
	    {         
	        if (socket == null)   
	        {  
	            Toast.makeText(LoveChat2.this, "û������", Toast.LENGTH_SHORT).show();  
	            return;  
	        }  
	        try {                 
	            OutputStream os = socket.getOutputStream();   
	            os.write(msg.getBytes());  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }             
	        if(msg.equals("100101100001")){
	        	msgList.add("���ں��жԷ�����");  
	 	        mAdapter.notifyDataSetChanged();  
	 	        mListView.setSelection(msgList.size() - 1); 
	        	
	        }else
		        if(msg.equals("010111001010")){
		        	msgList.add("���ؽ�");  
		 	        mAdapter.notifyDataSetChanged();  
		 	        mListView.setSelection(msgList.size() - 1);
		        }else{
	        	msgList.add("�ң�"+msg);  
	 	        mAdapter.notifyDataSetChanged();  
	 	        mListView.setSelection(msgList.size() - 1); 
	        }
	    }  
	    
	    //��ȡ����  
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
	        	                String info = "�Է����ں���������";  
	        	                msg.obj = info;  
	        	                msg.what = 0;  
	        	                LinkDetectedHandler.sendMessage(msg);
	        	                
	                        	 Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			                     Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			                     r.play();
	                        }else 
	                        	if(s.equals("010111001010")){
	                        		 //�رն�ʱ��(�ս������̵߳ķ���)ʵ�ִ���
	                        		 //�رն�ʱ��
//	                        		flagtime = false;
//		        	                new Thread(new ThreadShow1()).destroy();
	                        		Message msg = new Message();  
		        	                String info = "�ѶϿ�";  
		        	                msg.obj = info;  
		        	                msg.what = 1;  
		        	                LinkDetectedHandler.sendMessage(msg);	        	             
	                        		 shutdownServer(); 
	                     			 startServerThread = new ServerThread();  
	                                 startServerThread.start(); 
	                        	}else
	                        		if(s.equals("101011001001")){		                        			
	                        			//�رն�ʱ��
//	                        			flagtime = false;
//			        	                new Thread(new ThreadShow1()).destroy();
			        	                Message msg1 = new Message();  		        	               
			        	                msg1.obj = "�ͻ������˳������ؽ��ͻ���";  
			        	                msg1.what = 5; 
			        	                LinkDetectedHandler.sendMessage(msg1);
			        	                shutdownServer(); 
		                     			startServerThread = new ServerThread();  
		                                startServerThread.start(); 
			        	             
	                        		}
	                        else
	                        {
//	                        	notifi(s);  //��״̬�������bug
	                        	Message msg = new Message();  
		                        msg.obj = "����"+s;  
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
	        		state22.setText("�ѶϿ�");
	        		msgList.add((String)msg.obj);  
	 	            mAdapter.notifyDataSetChanged();  
	 	            mListView.setSelection(msgList.size() - 1);
	 	           shutdownServer();
	 	           startServerThread = new ServerThread();  
	               startServerThread.start(); 
	        	} 
	        	if(msg.what==3){        		
	        		//�����Ͽ�����
	        			state22.setText("�ѶϿ�");
	        			state33.setText("�����ѶϿ��������½������Ӳ�����������");
	        	} 
	        	if(msg.what==4){        		
	        		//�������Ӵ���
	        			state33.setText("ͨѶ������");
	        	}  
	        	if(msg.what==5){        		
	        		//�������Ӵ���
	        			state33.setText((String)msg.obj);
	        	} 
	        }
	    };
	    	    
	    /* ֹͣ������ */  
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
	                        mserverSocket.close();/* �رշ����� */  
	                        mserverSocket = null;  
	                    }  
	                } catch (IOException e) {  
	                    Log.e("server", "mserverSocket.close()", e);  
	                }  
	            };  
	        }.start();  
	    }  

	 	 //Back��д
	 	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	 	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
	 	        	 sendMessageHandle("101011001001");
	                    message.setText("");  
	                    message.clearFocus();  
	                    //close InputMethodManager  
	                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
	                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0); 
	 	        	super.onBackPressed();   //ע�͵�����,back�����˳�activity
	 	        	//����������
					Intent tTntent = new Intent();
					tTntent.setClass(LoveChat2.this,MainActivity.class);
					startActivity(tTntent);	
					//�رյ�ǰҳ��
	                android.os.Process  
	                        .killProcess(android.os.Process  
	                                .myPid());  
	                finish(); 
	 	            return true;  
	 	        } else  
	 	            return super.onKeyDown(keyCode, event);  
	 	    } 

	 	//��ʱ���߳��ж������Ƿ�Ͽ�
	 // �߳���  
	 	
	    class ThreadShow1 implements Runnable {  
	  	    	
	        @Override  
	        public void run() {  
	            // TODO Auto-generated method stub  
	            while (flagtime) {  
	                try {  
	                    Thread.sleep(4000); 
	                    //��ȡ����״̬������msg.obj	                    
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
	    	
	    	// ����һ����������Activity��Intent
			Intent intent = new Intent(this
				, LoveChats.class);
			PendingIntent pi = PendingIntent.getActivity(
				this, 0, intent, 0);
			Notification notify = new Notification.Builder(this)
				// ���ô򿪸�֪ͨ����֪ͨ�Զ���ʧ
				.setAutoCancel(true)
				// ������ʾ��״̬����֪ͨ��ʾ��Ϣ
				.setTicker("������Ϣ")
				// ����֪ͨ��ͼ��
				.setSmallIcon(R.drawable.lovechat)
				// ����֪ͨ���ݵı���
				.setContentTitle("һ����֪ͨ")
				// ����֪ͨ����
				.setContentText(s)
				// // ����ʹ��ϵͳĬ�ϵ�������Ĭ��LED��
//				 .setDefaults(Notification.DEFAULT_SOUND
//				 |Notification.DEFAULT_LIGHTS)
				// ����֪ͨ���Զ�������
				.setSound(Uri.parse("android.resource://org.crazyit.ui/"
					+ R.raw.msg))
				.setWhen(System.currentTimeMillis())
				// ���֪ͨ��Ҫ���������Intent
				.setContentIntent(pi)
				.build();
			// ����֪ͨ
			nm.notify(NOTIFICATION_ID1, notify);
	    	
	    }
}
