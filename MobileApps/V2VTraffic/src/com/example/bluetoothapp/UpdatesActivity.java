package com.example.bluetoothapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UpdatesActivity extends Activity {

	BluetoothDevice Client;
	private ConnectedThread Writer;
	private ConnectThread connect;
	private BluetoothSocket ClientSocket;
	Geocoder gc= new Geocoder(this, Locale.getDefault());
    String serviceString = Context.LOCATION_SERVICE;
   
    public void getGPS(){
        
    	TextView myLocationText1 = (TextView)findViewById(R.id.gpstext);
        LocationManager locationManager = (LocationManager) this.getSystemService(serviceString);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
        	new AlertDialog.Builder(this)
            .setTitle("GPS Off")
            .setMessage("Could you please turn on GPS?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                    Intent intent= new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
             })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                    // do nothing
                }
             }).show();
                }else
                {
                        LocationListener listener= new LocationListener() {
	                @Override
	                		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	                        // TODO Auto-generated method stub
	                       
	                } 
	                @Override
	                		public void onProviderEnabled(String arg0) {
	                        // TODO Auto-generated method stub
	                       
	                }
	                @Override
	                		public void onProviderDisabled(String arg0) {
	                        // TODO Auto-generated method stub
	                       
	                }
	                @Override
	                		public void onLocationChanged(Location location) {


                    			TextView myLocationText = (TextView)findViewById(R.id.gpstext);
		                        String latLongString ="No Location Found";
		                        if (location != null) {
		                        double lat = location.getLatitude();
		                        double lng = location.getLongitude();
		                        double speed=location.getSpeed();
		                        String addressString=null ;
		                        if(Geocoder.isPresent()){
		                        try{
//		                        	address=gc.getFromLocation(lat, lng, 10);
//		                        }catch(IOException e){
//		                        	Log.e("TAG","Geo exception",e);
//		                        }
		                        	List<Address> addresses = gc.getFromLocation(lat, lng, 1);
		                        	StringBuilder sb = new StringBuilder();
		                        	if (addresses.size() > 0) {
		                        	Address address = addresses.get(0);
		                        	for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
		                        	sb.append(address.getAddressLine(i)).append("\n");
		                        	sb.append(address.getLocality()).append("\n");
		                        	sb.append(address.getPostalCode()).append("\n");
		                        	sb.append(address.getCountryName());
		                        	}
		                        	 addressString = sb.toString();
		                        	} catch (IOException e) {}
		                        	}
		                        
//		                        	
		                        
		                        latLongString = "Lat: " + lat + "\nLong: " + lng+ "\nSpeed: "+speed+"\nAddress:"+addressString;
		                        }
		                        myLocationText.setText("GPS Location: " + latLongString);
		                   
	                		}
                        };
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                }
    }
    
    public synchronized void connect(BluetoothDevice device) {
        connect = new ConnectThread(device);
        connect.start();
    }
    
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        // Start the thread to manage the connection and perform transmissions
        Writer = new ConnectedThread(socket);
        Writer.start();
    }

	private class ConnectThread extends Thread {
	    private BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    @SuppressLint("NewApi")
		public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	        mmSocket = null;
	        
	        UUID myuuid = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");
	        
	        final TextView er = (TextView)findViewById(R.id.Error);
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server
	            tmp = device.createRfcommSocketToServiceRecord(myuuid);
	        } catch (IOException e) {}
	        ClientSocket = mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	    	
	        final TextView er = (TextView)findViewById(R.id.Error);
	        final TextView er1 = (TextView)findViewById(R.id.editText1);
	       // er1.setText(" ---- ");
	
	        //System.out.println("Ana henaaa\n");
	        //mblue.cancelDiscovery();
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();

	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) {}
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        //manageConnectedSocket(mmSocket);
        	connected(mmSocket,Client);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) {}
	    }
	}
	
	
	
	public class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
        	final TextView er = (TextView)findViewById(R.id.Error);
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) {}
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;

        	//er.setText(er.getText() + "\n" + socket.toString());
	    }
	 
		public void run() {
		byte[] buffer = new byte[1024];  // buffer store for the stream
		int bytes; // bytes returned from read()
		
		// Keep listening to the InputStream until an exception occurs
			while (true) {
			    	write("LOOOL".getBytes());
			        try {
						bytes = mmInStream.read(buffer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			  
		}
			
			/* Call this from the main activity to send data to the remote device */
			
			public void write(byte[] bytes) {
			
			final TextView er = (TextView)findViewById(R.id.Error);
			try {mmOutStream.write(bytes);}catch (IOException e) {}
			}
			
			/* Call this from the main activity to shutdown the connection */
			public void cancel() {
			try {
			    mmSocket.close();
			} catch (IOException e) { }
			}
			}
			
			public void send(View sender)
		    {
		    	final TextView er = (TextView)findViewById(R.id.Error);
		    	final TextView lol = (TextView)findViewById(R.id.editText1);
		
		    	
		    	Writer.write(lol.getText().toString().getBytes());
		    	lol.setText("");
		    	
		    }
			@Override
				public boolean onCreateOptionsMenu(Menu menu) {
					getMenuInflater().inflate(R.menu.main, menu);
					return true;
				}
			
			    protected void onCreate(Bundle savedInstanceState) {
			        super.onCreate(savedInstanceState);
			        setContentView(R.layout.updates);
			        Bundle extras = getIntent().getExtras();
			        if (extras != null)
			            Client  = extras.getParcelable("Device");
				    connect(Client);
				   // getGPS();
			    }
			        
}