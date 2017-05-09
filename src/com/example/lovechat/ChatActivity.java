package com.example.lovechat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	
	private Button open,close,connected,scan,visable;
	private Button enterchat;
	private ListView LV1;
	private TextView PsText;
	
	 private BluetoothAdapter BA;
	 private Set<BluetoothDevice>pairedDevices;
	 private int connectState;
	 public static String ConnectBT,ConnectBTname,connectBTaddress;
	 private String[] spilt_values;
	 
	 ArrayList connected_list = new ArrayList();
	 ArrayList discover_list = new ArrayList();
	 
	 private ProgressDialog pd;            //定义一个弹框正在加载蓝牙设备
	 
	 private boolean ifconnected;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		
		BA = BluetoothAdapter.getDefaultAdapter();
		
		open = (Button)findViewById(R.id.open);
		close = (Button)findViewById(R.id.close);
		connected = (Button)findViewById(R.id.connected);
		scan = (Button)findViewById(R.id.scan);
		visable = (Button)findViewById(R.id.visable);
		LV1 = (ListView)findViewById(R.id.LV1);	
		PsText = (TextView)findViewById(R.id.Ps);
		
		enterchat = (Button)findViewById(R.id.EnterChat);
		
		ifconnected = false;
							
		//发现设备
		 //注册，当一个设备被发现时调用onReceive 
	   IntentFilter filter_dis = new IntentFilter(BluetoothDevice.ACTION_FOUND);  
      		this.registerReceiver(mReceiver, filter_dis);
       
     //当搜索结束后调用onReceive  
       filter_dis = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  
       	this.registerReceiver(mReceiver, filter_dis); 
       	
    	LV1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ConnectBT = "Null"+"/n"+"Null";
				if((discover_list.size()==0)&(connected_list.size()!=0)){				
					ConnectBT = (String) connected_list.get(arg2);
				}
				if((discover_list.size()!=0)&(connected_list.size()==0)){				
					ConnectBT = (String) discover_list.get(arg2);
					}	
				spilt_values = ConnectBT.split("\n");
				ConnectBTname = spilt_values[0];
				connectBTaddress = spilt_values[1];
				
				if(ifconnected==true){
					
					PsText.setText("您当前选中连接的设备为："+ ConnectBTname);
				}
				if(ifconnected==false){
				//调取连接BT确认
				showDialog(2); 
				}
				
								
			}
		});
				
	}
	
	public void openBT(View view){

	      if (!BA.isEnabled()) {
	    	  
	         Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	         
	         startActivityForResult(turnOn, 1);
	         Toast.makeText(getApplicationContext(),"Turned on" 
	         ,Toast.LENGTH_LONG).show();
	      }
	      else{
	         Toast.makeText(getApplicationContext(),"Already on",
	         Toast.LENGTH_LONG).show();
	         }
	   
	}
	
	public void closeBT(View view){

	      BA.disable();
	      Toast.makeText(getApplicationContext(),"Turned off" ,
	      Toast.LENGTH_LONG).show();
	   
		
	}
	
	public void showcennected(View view){
		
		 discover_list.clear();
		 connected_list.clear();
	      pairedDevices = BA.getBondedDevices();
	      for(BluetoothDevice bt : pairedDevices)
	    	  connected_list.add(bt.getName() + "\n" + bt.getAddress());
	      Toast.makeText(getApplicationContext(),"Showing Paired Devices",
	      Toast.LENGTH_SHORT).show();
	      
	      final ArrayAdapter adapter1 = new ArrayAdapter
	      (this,android.R.layout.simple_list_item_1, connected_list);
	      LV1.setAdapter(adapter1);	
	      ifconnected = true;
	}
	
	public void scanBT(View view){
		
		discover_list.clear();
		connected_list.clear();
		pd = ProgressDialog.show(ChatActivity.this, "正在搜索周边蓝牙设备", "加载中，请稍后……",true,true); 		
		Toast.makeText(getApplicationContext(),"discover Devices",
			      Toast.LENGTH_SHORT).show(); 
		BA.startDiscovery();
	}
	
	public void BeVisable(View view){
		
		 Intent getVisible = new Intent(BluetoothAdapter.
			      ACTION_REQUEST_DISCOVERABLE);
			      startActivityForResult(getVisible, 0);
	}
	
	   //发现设备
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {            	
      	@Override
      	public void onReceive(Context context, Intent intent) {            		
      		Log.i("find","进入查找");            		
      		String action = intent.getAction(); 
      		if(BluetoothDevice.ACTION_FOUND.equals(action)){
      			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
      			 // 已经配对的则跳过  
      			 Log.i("find","查找设备");
                  if (device.getBondState() != BluetoothDevice.BOND_BONDED) {  
                 	 Log.i("find","添加设备");
                 	discover_list.add(device.getName() + "\n" + device.getAddress());                       	
                       }              
      		}
      		else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {  //搜索结束  
                  if (discover_list == null) { 
                 	 Log.i("find","没查找到设备");
                  } 
                  Log.i("find","显示设备");
                  final ArrayAdapter adapter1 = new ArrayAdapter
            		      (ChatActivity.this,android.R.layout.simple_list_item_1, discover_list);
            		      LV1.setAdapter(adapter1);	           		      
//            		   }
            		      pd.dismiss();// 关闭ProgressDialog  
              }              		
      	}
      };
      	
    private void connectBT(){
    	
   	 if(BA.isDiscovering()){
   		 BA.cancelDiscovery();
   	 }
   	 BluetoothDevice btDev = BA.getRemoteDevice(connectBTaddress);  	 
   	 connectState = btDev.getBondState(); 
   	 
   	 switch (connectState){
   	 	   //正在配对情况
   	 case BluetoothDevice.BOND_BONDING:   
   		Toast.makeText(getApplicationContext(),"Be Bonding",
   		      Toast.LENGTH_LONG).show();
           // 未配对情况      
        case BluetoothDevice.BOND_NONE:     
            // 配对      
            try {     
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");     
                createBondMethod.invoke(btDev);
                PsText.setText("您当前选中连接的设备为："+ ConnectBTname);
            } catch (Exception e) {      
                e.printStackTrace();     
            }     
            break;     
        // 已配对情况      
        case BluetoothDevice.BOND_BONDED:     
            try {     
                // 连接      
            	PsText.setText("您当前选中连接的设备为："+ ConnectBTname);
            	final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";     
 			    UUID uuid = UUID.fromString(SPP_UUID);     
 			    BluetoothSocket socket = btDev.createRfcommSocketToServiceRecord(uuid);     
 			    socket.connect(); 
 			    PsText.setText("您当前选中连接的设备为："+ ConnectBTname);
            } catch (IOException e) {     
                e.printStackTrace();     
            }     
            break; 
           }    	  	
    }
    
    	public void enterLC(View view){
    		
    		if((ConnectBTname!=null)&(connectBTaddress!=null)){
    			Intent tTntent = new Intent();
    			tTntent.setClass(ChatActivity.this,LoveChats.class);
    			startActivity(tTntent);	

    		}
    		else
    		{
    			showDialog(3); 
    		}
    	}
    	
  	//自定义弹框形式
 	 protected Dialog onCreateDialog(int id) { 

 		 	//简单提示，并提供Button供用户执行
 	        if (id == 1) {  
 	            return new AlertDialog.Builder(ChatActivity.this)  
 	            		//设置弹框的内容
 	                    .setMessage("加载中，请稍后…………")  
 	                    //设置弹框的标题
 	                    .setTitle("正在搜索周边蓝牙！") 

 	                     //设置取消按键
 	                    .setNegativeButton("取消",  
 	                            new DialogInterface.OnClickListener() {  
 	  
 	                                public void onClick(DialogInterface dialog1,  
 	                                        int which) { 
 	                                	//以下是“取消”按键的操作
 	                                	BA.cancelDiscovery();
 	                                	 final ArrayAdapter adapter1 = new ArrayAdapter
 	                              		      (ChatActivity.this,android.R.layout.simple_list_item_1, discover_list);
 	                              		      LV1.setAdapter(adapter1);	
 	                                    dialog1.dismiss();  //去掉弹框
 	  
 	                                }  
 	                            }).create();          	
 	        }  
 	        
 		 	//简单提示，并提供Button供用户执行
 	        if (id == 2) {  
 	            return new AlertDialog.Builder(ChatActivity.this)  
 	            		//设置弹框的内容
 	                    .setMessage("确定建立链接？")  
 	                    //设置弹框的标题
 	                    .setTitle("蓝牙连接") 
 	                    //设置确定按键
 	                    .setPositiveButton("确定",  
 	                            new DialogInterface.OnClickListener() {  
 	  
 	                                public void onClick(DialogInterface dialog,  
 	                                        int which) { 
 	                                	//以下是“确定”按键的操作
 	                                	connectBT();
 	                                    dialog.dismiss();  	                                     	  
 	                                }  
 	                            })  
 	                     //设置取消按键
 	                    .setNegativeButton("取消",  
 	                            new DialogInterface.OnClickListener() {  
 	  
 	                                public void onClick(DialogInterface dialog,  
 	                                        int which) { 
 	                                	//以下是“取消”按键的操作
 	                                    dialog.dismiss();  //去掉弹框
 	  
 	                                }  
 	                            }).create();          	
 	        }  
 	       
 		 	//简单提示，并提供Button供用户执行
 	        if (id == 3) {  
 	            return new AlertDialog.Builder(ChatActivity.this)  
 	            		//设置弹框的内容
 	                    .setMessage("请确定已选择通讯设备？")  
 	                    //设置弹框的标题
 	                    .setTitle("………………………………") 
 	                    //设置确定按键
 	                    .setPositiveButton("确定",  
 	                            new DialogInterface.OnClickListener() {  
 	  
 	                                public void onClick(DialogInterface dialog,  
 	                                        int which) { 
 	                                	//以下是“确定”按键的操作
 	                                    dialog.dismiss();  	                                     	  
 	                                }  
 	                            }).create();    	                         	
 	        }  
 	   
 	        
 	        return null;  
 	  
 	    }  
      
}
