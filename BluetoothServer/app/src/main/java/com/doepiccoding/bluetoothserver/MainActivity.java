package com.doepiccoding.bluetoothserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final int DISCOVERABLE_REQUEST_CODE = 0x1;
	private boolean CONTINUE_READ_WRITE = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Always make sure that Bluetooth server is discoverable during listening...
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		android.util.Log.e("TrackingFlow", "Creating thread to start listening...");
		new Thread(reader).start();
        new Thread(connectWritter).start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(writter_socket != null){
			try{
				os.close();
                writter_socket.close();
			}catch(Exception e){}
		}
        if(reader_socket != null){
            try{
                is.close();
                reader_socket.close();
            }catch(Exception e){}
        }
        CONTINUE_READ_WRITE = false;
	}
	
	private BluetoothSocket reader_socket;
    private BluetoothSocket writter_socket;
	private InputStream is;
	private OutputStreamWriter os;
	private Runnable reader = new Runnable() {
		public void run() {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			UUID uuid = UUID.fromString("4e5d48e0-75df-11e3-981f-0800200c9a65");
			try {
				BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord("BLTServer", uuid);
				android.util.Log.e("Reader", "Listening...");
                reader_socket = serverSocket.accept();
				android.util.Log.e("Reader", "Socket accepted...");
				is = reader_socket.getInputStream();
                android.util.Log.e("Reader", "Start reading...");
				int bufferSize = 1024;
				int bytesRead = -1;
				byte[] buffer = new byte[bufferSize];
				//Keep reading the messages while connection is open...
				while(CONTINUE_READ_WRITE){
					final StringBuilder sb = new StringBuilder();
					bytesRead = is.read(buffer);
					if (bytesRead != -1) {
					String result = "";
					while ((bytesRead == bufferSize) && (buffer[bufferSize-1] != 0)){
						result = result + new String(buffer, 0, bytesRead - 1);
						bytesRead = is.read(buffer);
						}
						result = result + new String(buffer, 0, bytesRead - 1);
						sb.append(result);
                        if (writter_socket != null) {
                            Writter writter = new Writter(sb.toString());
                            new Thread(writter).start();
                        }
					}
					android.util.Log.e("Reader", "Message from Client " + sb.toString());
				}
			} catch (IOException e) {e.printStackTrace();}
		}
	};

    private Runnable connectWritter = new Runnable() {
        @Override
        public void run() {
            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                UUID uuid = UUID.fromString("4e5d48e0-75df-11e3-981f-0800200c9a66");
                BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord("BLTServer", uuid);
                android.util.Log.e("Writter", "Listening...");
                writter_socket = serverSocket.accept();
                android.util.Log.e("Writter", "Socket accepted...");
                os = new OutputStreamWriter(writter_socket.getOutputStream());
            } catch(IOException e) {e.printStackTrace();}
        }
    };

    private class Writter implements Runnable {
        String output;
        Writter(String output) {
            this.output = output;
        }

        @Override
        public void run() {
            try {
                os.write(this.output + "\n");
                os.flush();
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
