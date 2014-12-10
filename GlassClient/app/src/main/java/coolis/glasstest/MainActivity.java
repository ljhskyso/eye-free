package coolis.glasstest;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;
    private static final String DEBUG_TAG = "Gestures";
    private static final int DISCOVERABLE_REQUEST_CODE = 0x1;
    private boolean CONTINUE_READ_WRITE = true;

    /**
     * "Hello World!" {@link View} generated by {@link #buildView()}.
     */
    private View mView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mView = buildView();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        setContentView(mCardScroller);
        new Thread(reader).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText(R.string.hello_world);
        return card.getView();
    }

    private BluetoothSocket socket;
    private OutputStreamWriter os;
    private InputStream is;

    private Runnable reader = new Runnable() {

        @Override
        public void run() {
            UUID uuid = UUID.fromString("400001101-0000-1000-8000-00805F9B34FB");
            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();;
                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                Log.e("Paired Device:", Integer.toString(pairedDevices.size()));
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        Log.e("Paired devices: ", (device.getName() + "\n" + device.getAddress()));
                        socket = device.createRfcommSocketToServiceRecord(uuid);
                        socket.connect();
                        Log.e("Client tracking", "Connected...");
                        is = socket.getInputStream();
                        int bufferSize = 1024;
                        int bytesRead = -1;
                        byte[] buffer = new byte[bufferSize];
                        Log.e("Client tracking", "Start reading");
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
                            }
                            android.util.Log.e("Client tracking", "Read: " + sb.toString());
                            //Show message on UIThread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

            } catch (Exception e) {e.printStackTrace();}
        }
    };

}
