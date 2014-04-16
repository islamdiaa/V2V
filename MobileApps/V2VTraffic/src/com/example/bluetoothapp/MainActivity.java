package com.example.bluetoothapp;
import com.example.bluetoothapp.R;
import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	
	private BluetoothDevice Client;
	ArrayList<String> discovered = new ArrayList();
	ArrayList<BluetoothDevice> devices = new ArrayList();
	AlertDialog.Builder builder2;// = new AlertDialog.Builder(this);
	AlertDialog.Builder builder;
	AlertDialog alert,alert2;
    protected void onCreate(Bundle savedInstanceState) {
    	Client = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	mblue = BluetoothAdapter.getDefaultAdapter();
    	int REQUEST_ENABLE_BT = 1;
    	//final TextView er = (TextView)findViewById(R.id.Error);
    	if(mblue == null){}
    		// er.setText("No Bluetooth!");
    	else if (mblue.isEnabled()) {
    		//er.setText(mblue.getAddress() + " " + mblue.getName());
    	}
    	else{
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}

    	
    	 
    	   
    	    
    	
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

   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    public void goto_updates(View sender)
    {
    	
    	if(Client!=null){
    	Intent small = new Intent(this,UpdatesActivity.class);
    	small.putExtra("Device", Client);
    	startActivity(small);
    	}
    }
				
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
				    
					public void onReceive(Context context, Intent intent)
				    {
				    	
				        String action = intent.getAction();
				        // When discovery finds a device
				        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				            // Get the BluetoothDevice object from the Intent
				            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				            // Add the name and address to an array adapter to show in a ListView
				            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				            //alert.dismiss();
				            
				        	//final TextView er = (TextView)findViewById(R.id.Error);
				        	discovered.add(device.getName() + " " + device.getAddress());
				        	devices.add(device);
				        	//er.setText(er.getText() + "\n" + device.getAddress());
				        	/*if(device.getAddress().equals("00:15:83:0C:BF:EB") == true)
				        	//if(device.getAddress().equals("9C:2A:70:49:61:B0") == true)
				        	{
				        		Client = device;
				        		er.setText("Connected to Target Device!");
				        	}*/
				        }
				    }
				};
}


