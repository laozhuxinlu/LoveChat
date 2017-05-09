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
		
		// ��ȡϵͳ��NotificationManager����
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
         
         state22.setText("δ����");      		
		state11.setText("���豸||�����豸��"+ChatActivity.ConnectBTname);
		state33.setText("���������ӣ��뽨��ͨѶ");
		
		address = ChatActivity.connectBTaddress; 
		device = mBluetoothAdapter.getRemoteDevice(address); 
		shutdownClient();
		//�򿪿ͻ���ȷ��
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
                Toast.makeText(LoveChats.this, "�������ݲ���Ϊ�գ�", Toast.LENGTH_SHORT).show();  
			
				
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
                state22.setText("δ����");
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
	
	//�����ͻ���
	
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
	   
	    //�����ͻ���  

	    private class clientThread extends Thread {           
	        @Override  
	        public void run() {  
	            try {  
	                //����һ��Socket���ӣ�ֻ��Ҫ��������ע��ʱ��UUID��  	                
	                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));  
	                //����  
	                Message msg1 = new Message();  
	                msg1.obj = "���Ժ��������ӷ�����:";  
	                msg1.what = 1;  
	                LinkDetectedHandler.sendMessage(msg1);  
	                  
	                socket.connect(); 
	                
	                Message msg2 = new Message();  
	                msg2.obj = "������";
//	                + BluetoothMsg.BlueToothAddress;  
	                msg2.what = 1;  
	                LinkDetectedHandler.sendMessage(msg2);
	                
	                Message msg = new Message();  
	                msg.obj = "�Ѿ������Ϸ���ˣ����Է�����Ϣ��";  
	                msg.what = 1;  
	                LinkDetectedHandler.sendMessage(msg); 	
	        		//������ʱ��
	        		new Thread(new ThreadShow1()).start();
	                //������������  
	                mreadThread = new readThread();  
	                mreadThread.start();
//	                sendMessageHandle("�Է�������"); 
//	                //close InputMethodManager  
//	                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
//	                imm.hideSoftInputFromWindow(message.getWindowToken(), 0); 
	            }   
	            catch (IOException e)   
	            {  
	                Log.e("connect", "", e);  
	                Message msg = new Message();  
	                msg.obj = "���ӷ�����쳣���Ͽ�����������һ�ԡ�";  
	                msg.what = 1;  
	                LinkDetectedHandler.sendMessage(msg);  
	                
	                Message msg1 = new Message();  
	                msg1.obj = "����������ʧ�ܣ���ȷ�Ϸ������ѽ������ؽ��ͻ���";  
	                msg1.what = 1;  
	                LinkDetectedHandler.sendMessage(msg1);  
	            }   
	        }  
	    };    
	
	    //��������  
	    private void sendMessageHandle(String msg)   
	    {         
	        if (socket == null)   
	        {  
	            Toast.makeText(LoveChats.this, "û������", Toast.LENGTH_SHORT).show();   
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
	        }else
	        {
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
	                        		
		        	                shutdownClient();
		        	                Message msg = new Message();  		        	               
		        	                msg.obj = "�ѶϿ�";  
		        	                msg.what = 2; 
		        	                LinkDetectedHandler.sendMessage(msg);
		        	                
		        	                //�رն�ʱ��(�ս������̵߳ķ���)ʵ�ִ���
//		        	                new Thread(new ThreadShow1()).stop();
		        	                 
	                        	}else
	                        		if(s.equals("101011001001")){			        	                
			        	                Message msg1 = new Message();  		        	               
			        	                msg1.obj = "���������˳������Ƚ���������";  
			        	                msg1.what = 5; 
			        	                LinkDetectedHandler.sendMessage(msg1);
			        	                
			        	              //�رն�ʱ��
//			        	                new Thread(new ThreadShow1()).stop();
	 
	                        		}else
	                        {
//	                        	notifi(s);    //��״̬�������bug
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
	        		
	        		shutdownClient();
	        		state22.setText((String)msg.obj);
	        		showDialog(1);
	        	}
	        	if(msg.what==3){
	        		
	        		state22.setText("�ѶϿ�");
	        		msgList.add((String)msg.obj);  
	 	            mAdapter.notifyDataSetChanged();  
	 	            mListView.setSelection(msgList.size() - 1);
	 	            shutdownClient();
	        	}
	        	if(msg.what==4){        		
	        		//�����Ͽ�����
	        			state22.setText("�ѶϿ�");
	        			state33.setText("�����ѶϿ��������½������Ӳ�����������");
//	        			shutdownClient();
	        	} 
	        	if(msg.what==5){        		
	        		//�������Ӵ���
	        			state33.setText("ͨѶ������");
	        	} 
	             
	        }  
	    };
	    
	    /* ֹͣ�ͻ������� */  
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
	    
	    
	  	//�Զ��嵯����ʽ
	 	 protected Dialog onCreateDialog(int id) { 
 
	 		 	//����ʾ�����ṩButton���û�ִ��
	 	        if (id == 1) {  
	 	            return new AlertDialog.Builder(LoveChats.this)  
	 	            		//���õ��������
	 	                    .setMessage("��ȷ�������豸�ѽ���Salve����")  
	 	                    //���õ���ı���
	 	                    .setTitle("��ȷ�ϣ�") 
	 	                    //����ȷ������
	 	                    .setPositiveButton("ȷ��",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//�����ǡ�ȷ���������Ĳ���
	 	                                	//�򿪿ͻ���	 	                                   
	 	                                   device = mBluetoothAdapter.getRemoteDevice(address);          
	 	                   				   clientConnectThread = new clientThread();  
	 	                                   clientConnectThread.start();                                   
	 	                                    dialog.dismiss();  	                                     	  
	 	                                }  
	 	                            })  
	 	                     //����ȡ������
	 	                    .setNegativeButton("ȡ��",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//�����ǡ�ȡ���������Ĳ���
//	 	                                	back.setEnabled(true);
	 	                                    dialog.dismiss();  //ȥ������
	 	  
	 	                                }  
	 	                            }).create();          	
	 	        }  
	 	        
	 		 	//����ʾ�����ṩButton���û�ִ��
	 	        if (id == 2) {  
	 	            return new AlertDialog.Builder(LoveChats.this)  
	 	            		//���õ��������
	 	                    .setMessage("��ȷ�������豸�ѽ���Salve����")  
	 	                    //���õ���ı���
	 	                    .setTitle("��ȷ�ϣ�") 
	 	                    //����ȷ������
	 	                    .setPositiveButton("ȷ��",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//�����ǡ�ȷ���������Ĳ���
	 	                                	//�򿪿ͻ���	 	                                   
	 	                               //    device = mBluetoothAdapter.getRemoteDevice(address);          
//	 	                   				   clientConnectThread = new clientThread();  
//	 	                                   clientConnectThread.start();                                   
	 	                                    dialog.dismiss();  	                                     	  
	 	                                }  
	 	                            })  
	 	                     //����ȡ������
	 	                    .setNegativeButton("ȡ��",  
	 	                            new DialogInterface.OnClickListener() {  
	 	  
	 	                                public void onClick(DialogInterface dialog,  
	 	                                        int which) { 
	 	                                	//�����ǡ�ȡ���������Ĳ���
//	 	                                	back.setEnabled(true);
	 	                                    dialog.dismiss();  //ȥ������
	 	  
	 	                                }  
	 	                            }).create();          	
	 	        }  
	 	      	 	        
	 	        return null;  
	 	  
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
//					Intent tTntent = new Intent();
//					tTntent.setClass(LoveChats.this,ChatActivity.class);
//					startActivity(tTntent);	
//					//�رյ�ǰҳ��
//	                android.os.Process  
//	                        .killProcess(android.os.Process  
//	                                .myPid());  
//	                finish(); 
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
	            while (true) {  
	                try {  
	                    Thread.sleep(4000); 
	                    //��ȡ����״̬������msg.obj
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
