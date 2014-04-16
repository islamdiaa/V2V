package com.example.bluetoothapp;
import com.example.bluetoothapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	private BluetoothDevice Client;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	mblue = BluetoothAdapter.getDefaultAdapter();
    	int REQUEST_ENABLE_BT = 1;
    	final TextView er = (TextView)findViewById(R.id.Error);
    	if(mblue == null)
    		 er.setText("No Bluetooth!");
    	else if (mblue.isEnabled()) {
    		er.setText(mblue.getAddress() + " " + mblue.getName());
    	}
    	else{
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }


   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public BluetoothAdapter mblue;
    
    public void disov(View sender)
    { 
    	final TextView er = (TextView)findViewById(R.id.Error);
    	boolean tr = mblue.startDiscovery();
    	if(tr == true)
    	{
    		er.setText("Discovering !!");
    	}
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter);
    	
    }

    public void goto_updates(View sender)
    {
    	Intent small = new Intent(this,UpdatesActivity.class);
    	small.putExtra("Device", Client);
    	startActivity(small);
    }
				private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
				    @SuppressLint("NewApi")
					public void onReceive(Context context, Intent intent)
				    {
				        String action = intent.getAction();
				        // When discovery finds a device
				        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				            // Get the BluetoothDevice object from the Intent
				            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				            // Add the name and address to an array adapter to show in a ListView
				            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			
				        	final TextView er = (TextView)findViewById(R.id.Error);
				        	er.setText(er.getText() + "\n" + device.getAddress());
				        	if(device.getAddress().equals("00:15:83:0C:BF:EB") == true)
				        	//if(device.getAddress().equals("9C:2A:70:49:61:B0") == true)
				        	{
				        		Client = device;
				        		er.setText("Connected to Target Device!");
				        	}
				        }
				    }
				};
}


