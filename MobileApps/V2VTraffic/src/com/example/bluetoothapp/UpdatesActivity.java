package com.example.bluetoothapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class UpdatesActivity extends FragmentActivity {
	BluetoothDevice Client;
	TextView myLocationText;
	String latLongString="None"; 
	private ConnectedThread Writer;
	private ConnectThread connect;
	private BluetoothSocket ClientSocket;
	Geocoder gc= new Geocoder(this, Locale.getDefault());
    String serviceString = Context.LOCATION_SERVICE;
    Location lastlocation;
    String CurrentAddress = "NONE";
    public static final int MESSAGE_READ = 2;
    double lat,lastLat ;
	double lng ,lastLng;
	double chosenlng ,chosenLng;
    public void getGPS(){
        
    	TextView myLocationText1 = (TextView)findViewById(R.id.gpstext);
         LocationManager locationManager = (LocationManager) this.getSystemService(serviceString);
         lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         
         if(lastlocation != null)
         {
        	  lastLat = lastlocation.getLatitude();
        	 lastLng = lastlocation.getLongitude(); 
        	  latLongString = "Lat: " +lastLat+ "\nLong: " + lastLng ;
        	
         }
         myLocationText1.setText("GPS last Location: " + latLongString);
 	
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
                    			 myLocationText = (TextView)findViewById(R.id.gpstext);
		                        
			                        if (location != null) {
			                        	//current = location;
			                        	 lat = location.getLatitude();
			                        	 lng = location.getLongitude();
			                        	double speed=location.getSpeed()*3.6;
			                        	List <Address> addressString=null ;
			                        	
//			                        	if(Geocoder.isPresent()){
//			                        		try{
//			                        			addressString = gc.getFromLocation(lat, lng, 10);//getlocation name
//			                        			CurrentAddress = addressString.toString();
//			                        		}catch(IOException e){
//			                        			Log.e("TAG","Geo exception",e);
//			                        		}
//			                        	}
//			                        	try {
//											//send(lat,lng,speed);
//										} catch (JSONException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
			                        	latLongString = "Lat: " + String.valueOf(lat) + "\nLong: " + String.valueOf(lng) + "\nSpeed: "+String.valueOf(speed)+"\nAddress:"+addressString;
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
	        final TextView er = (TextView)findViewById(R.id.Error);
	        final TextView er1 = (TextView)findViewById(R.id.editText1);

	        try {
	            mmSocket.connect();

	        } catch (IOException connectException) {
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) {}
	            return;
	        }
	 
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
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) {}
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	    public void updatemsgbox(final String msg)
	    {
	    	runOnUiThread(new Runnable() {
	    	     @Override
	    	     public void run() {
	    	         final TextView msgbox = (TextView)findViewById(R.id.recmsg);
	    	 			msgbox.setText(msgbox.getText() + "\n" + msg);
	    	    }
	    	});
	    }
		public void run() {

		byte[] buffer = new byte[1024];  // buffer store for the stream
		int bytes;
			while (true) {
			        try {
						bytes = mmInStream.read(buffer);
		                byte[] readBuf = (byte[]) buffer;
						String readMessage = new String(readBuf, 0, bytes);
						updatemsgbox(readMessage);
						} catch (IOException e) {
						e.printStackTrace();
					}
			}
			  
		}
			
			/* Call this from the main activity to send data to the remote device */
			
			public void write(byte[] bytes) {
			
			try {
				
				mmOutStream.write(bytes);
				}
			catch (IOException e) {}
			}
			
			/* Call this from the main activity to shutdown the connection */
			public void cancel() {
			try {
			    mmSocket.close();
			} catch (IOException e) { }
			}
			}
			
			public void send(View sender) throws JSONException
		    {
		    	final TextView msg = (TextView)findViewById(R.id.editText1);
		    	JSONObject json = new JSONObject();
				json.put("msg",msg.getText().toString());
				if(lastlocation != null)
				{
					json.put("Latitude",lastlocation.getLatitude());
					json.put("Logitude",lastlocation.getLongitude());
//					if(CurrentAddress != null)
//						json.put("Address", CurrentAddress);
				}
//					json.put("Latitude",lat);
//					json.put("Logitude",lng);
//					json.put("Speed",speed);
//									
		    	try {
					Writer.write(json.toString().getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		    	msg.setText("");
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
			        chosenlng=extras.getDouble("lat");
			        chosenlng=extras.getDouble("lng");
			        if (extras != null)
			            Client  = extras.getParcelable("Device");
				    connect(Client);
				   getGPS();
				   
			    }
			  
			    public void gotomapactivity (View sender) {
			    	 Intent i = new Intent(new Intent(UpdatesActivity.this, MapActivity.class));
			    	 i.putExtra("lat", lastLat);
				    	i.putExtra("lng", lastLng);
			    	 startActivityForResult(i, 0);
//			    	Intent small = new Intent(this,MapActivity.class);
//			    	small.putExtra("lat", lastLat);
//			    	small.putExtra("lng", lastLng);
//			    	startActivity(small);
//			    	
			    	
			    	
				}
			    protected void onActivityResult(int requestCode, int resultCode,
			            Intent data) {
			        if (requestCode == 0) {
			            if (resultCode == RESULT_OK) {
			                // A contact was picked.  Here we will just display it
			                // to the user.
			                startActivity(new Intent(data));
			            }
			        }
			    }
}