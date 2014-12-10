package coolis.glasscontroller;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by WayneLI on 1/12/14.
 */
public class GestureContainer extends View {

    float x1,x2;
    float y1, y2;
    Context context;

    public int motionCode;

    public GestureContainer(Context context) {
        super(context);
        this.context = context;
    }

    public GestureContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public GestureContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }



    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction())
        {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                //if left to right sweep event on screen
                if (x1 < x2 && Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
                    motionCode = 0;
                    Log.e("Action", "Left to Right Swap Performed");
                }

                // if right to left sweep event on screen
                if (x1 > x2 && Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
                    motionCode = 1;
                    Log.e("Action", "Right to Left Swap Performed");
                }

                // if UP to Down sweep event on screen
                if (y1 < y2 && Math.abs(x1 - x2) < Math.abs(y1 - y2)) {
                    motionCode = 2;
                    Log.e("Action", "UP to Down Swap Performed");
                }

                //if Down to UP sweep event on screen
                if (y1 > y2 && Math.abs(x1 - x2) < Math.abs(y1 - y2)) {
                    motionCode = 3;
                    Log.e("Action", "Down to UP Swap Performed");
                }
                Writter writter = new Writter(Integer.toString(motionCode));
                new Thread(writter).start();
                break;
            }
        }
        return true;
    }

    public class Writter implements Runnable {
        private String output;
        Writter(String output) {this.output = output;}
        @Override
        public void run() {
            MainActivity.writeToServer(this.output);
        }
    };
}
