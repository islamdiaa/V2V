package com.example.grouptalk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;



import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private BluetoothDevice Client;
	ArrayList<String> discovered = new ArrayList();
	ArrayList<BluetoothDevice> devices = new ArrayList();
	AlertDialog.Builder builder2;// = new AlertDialog.Builder(this);
	AlertDialog.Builder builder;
	AlertDialog alert,alert2;




@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //
    mblue = BluetoothAdapter.getDefaultAdapter();
	int REQUEST_ENABLE_BT = 1;
	//final TextView er = (TextView)findViewById(R.id.Error);
	 if (!mblue.isEnabled()) {
	
	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	 }
    //
    setContentView(R.layout.activity_main);
}


	 public void AlertTimeDelay(long time){
	    	builder2 = new AlertDialog.Builder(this);
	        Handler handler = new Handler(); 
	        handler.postDelayed(new Runnable() {           
	            public void run() {                
	                alert.dismiss();   
	            	//final TextView er = (TextView)findViewById(R.id.Error);   
	                builder2.setTitle("Available Devices");
	                String tmp[] = new String[discovered.size()];
	                for(int i = 0 ; i < discovered.size(); i++)
	                	tmp[i] = discovered.get(i);
	                builder2.setItems(tmp, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int item) {
	                        // Do something with the selection
	                        Client = devices.get(item);
	                    	//er.setText("Target Device: " + Client.getName() + " " + Client.getAddress());  
	                        discovered.clear();
	                        devices.clear();
	                    }
	                });
	                alert2 = builder2.create();
	                alert2.show();
	            }
	        }, time); 
	    }

	  public BluetoothAdapter mblue;
	    
	    public void disov(View sender) throws InterruptedException
	    { 
	    	//final TextView er = (TextView)findViewById(R.id.Error);
	    	boolean tr = mblue.startDiscovery();
	    	if(tr == true)
	    	{
	    		 	builder = new AlertDialog.Builder(this);
	    	   	    builder.setTitle("Discovering ...");
	    	   	    alert = builder.create();
	    	   	    alert.show(); 
	    	   	    AlertTimeDelay(5000);
	    	   	   
	    	}
	    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    	registerReceiver(mReceiver, filter);
	    	devices.clear();
	    	discovered.clear();
	    }
	    
	    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    
			public void onReceive(Context context, Intent intent)
		    {
		    	
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        	discovered.add(device.getName() + " " + device.getAddress());
		        	devices.add(device);
		        	
		        }
		    }
		};
		
		
		  public void goto_updates(View sender)
		    {
		    	
		    	if(Client!=null){
		    	Intent small = new Intent(this,stream.class);
		    	small.putExtra("Device", Client);
		    	startActivity(small);
		    	}
		    }

 }