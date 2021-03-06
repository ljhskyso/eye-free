package coolis.glasscontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private GestureContainer container;
    private boolean CONTINUE_READ_WRITE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (GestureContainer) findViewById(R.id.gesture_container);
        //Always make sure that Bluetooth server is discoverable during listening...
        new Thread(connectWritter).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket != null){
            try{
                os.close();
                socket.close();
            }catch(Exception e){}
            CONTINUE_READ_WRITE = false;
        }
    }

    private BluetoothSocket socket;
    private static OutputStreamWriter os;

    private Runnable connectWritter = new Runnable() {

        @Override
        public void run() {
            UUID uuid = UUID.fromString("4e5d48e0-75df-11e3-981f-0800200c9a65");
            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();;
                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        Log.e("Paired devices: ", (device.getName() + "\n" + device.getAddress()));
                        socket = device.createRfcommSocketToServiceRecord(uuid);
                        socket.connect();
                        Log.e("Client tracking", "Connected...");
                        os = new OutputStreamWriter(socket.getOutputStream());
                    }
                }


            } catch (Exception e) {e.printStackTrace();}
        }
    };

    public static void writeToServer(String output) {
        try {
            if (os != null) {
                os.write(output + "\n");
                os.flush();
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}