package coolis.glasscontroller;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    private static final String DEBUG_TAG = "Gestures";
    private GestureContainer container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (GestureContainer) findViewById(R.id.gesture_container);
    }
}
