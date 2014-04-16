package com.example.grouptalk;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class stream extends FragmentActivity {
	private Button startButton,stopButton;

	public byte[] buffer;
	
	private int port=50005;
	AudioRecord recorder;

	private int sampleRate = 44100;
	private int channelConfig = AudioFormat.CHANNEL_IN_MONO;    
	private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;       
	int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
	private boolean status = true;
	private boolean lock = true;

	
	BluetoothDevice Client;
	TextView myLocationText;
	String latLongString="None"; 
	private ConnectedThread Writer;
	private ConnectThread connect;
	private BluetoothSocket ClientSocket;
	
    String serviceString = Context.LOCATION_SERVICE;
   
    String CurrentAddress = "NONE";
    public static final int MESSAGE_READ = 2;

	private final OnClickListener stopListener = new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
	                status = false;
	                lock=false;
	                recorder.release();
	                Log.d("VS","Recorder released");
	    }

	};

	private final OnClickListener startListener = new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
	    	status = true;
	    	if(lock){        
	                startStreaming();  }         
	    }

	};

	public void startStreaming() {


	    Thread streamThread = new Thread(new Runnable() {

	        @Override
	        public void run() {
	        
	                byte[] buffer = new byte[512];

	                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);
	                Log.d("VS", "Recorder initialized");

	                recorder.startRecording();
	                while(true) {
	                	if(status == true){
	                    //reading data from MIC into buffer
	                    minBufSize = recorder.read(buffer, 0, buffer.length);
	                   
	                    Writer.write(buffer);
	                	}

	                }



	            
	           
	        }

	    });
	    streamThread.start();
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
	        
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server
	            tmp = device.createRfcommSocketToServiceRecord(myuuid);
	        } catch (IOException e) {}
	        ClientSocket = mmSocket = tmp;
	    }
	 
	    public void run() {
	       

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
	        
        	
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) {}
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	  
		public void run() {

		byte[] buffer = new byte[1024];  // buffer store for the stream
		int bytes;
			while (true) {
			        try {
						bytes = mmInStream.read(buffer);
		                byte[] readBuf = (byte[]) buffer;
						
						
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
			
			
			@Override
				public boolean onCreateOptionsMenu(Menu menu) {
					getMenuInflater().inflate(R.menu.main, menu);
					return true;
				}
			
			    protected void onCreate(Bundle savedInstanceState) {
			        super.onCreate(savedInstanceState);
			        setContentView(R.layout.stream_activity);
			        Bundle extras = getIntent().getExtras();
			        if (extras != null)
			            Client  = extras.getParcelable("Device");
				    connect(Client);

				    startButton = (Button) findViewById (R.id.start_button);
				    stopButton = (Button) findViewById (R.id.stop_button);

				    startButton.setOnClickListener (startListener);
				    stopButton.setOnClickListener (stopListener);

				   
				    System.out.println("minBufSize: " + minBufSize);
			    }
			  
			  
			        
}