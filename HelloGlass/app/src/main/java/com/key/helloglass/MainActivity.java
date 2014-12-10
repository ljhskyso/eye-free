package com.key.helloglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;
import com.key.helloglass.adapter.DeveloperAdapter;

import java.util.ArrayList;
import java.util.List;

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

    private CardScrollView mCardScroller;
    private List<CardBuilder> mCards;
    private GestureDetector mGestureDetector;
//    private View mView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

//        mView = buildView();

        mCardScroller = new CardScrollView(this);
//        mCardScroller.setAdapter(new CardScrollAdapter() {
//            @Override
//            public int getCount() {
//                return 1;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return mView;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                return mView;
//            }
//
//            @Override
//            public int getPosition(Object item) {
//                if (mView.equals(item)) {
//                    return 0;
//                }
//                return AdapterView.INVALID_POSITION;
//            }
//        });
        mCards = new ArrayList<CardBuilder>();
        createCards();
        mCardScroller.setAdapter(new DeveloperAdapter(mCards));


        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.SELECTED);

                String[] url = {getString(R.string.url_google), getString(R.string.url_cnn), getString(R.string.url_nytimes)};


                openOnlineWebsite(url[position]);
            }
        });

//        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);
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

//    /**
//     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
//     */
//    private View buildView() {
//        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
//
//        card.setText(R.string.hello_world);
//        return card.getView();
//    }

    //    --------------------------------------------
    private void openOnlineWebsite(String url) {
        Log.d("webview", "Starting openOnlineWebsite");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setClassName("com.google.glass.browser", "com.google.glass.browser.WebBrowserActivity");
        startActivity(intent);

        Log.d("webview", "Ending openOnlineWebsite");

    }

    private void createCards() {

//        for (int i = 0; i < url.length; i++) {
//            CardBuilder card = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED);
//            card.setIcon(draw[i]);
//            card.setText(name[i]);
//            card.setTimestamp(url[i]);
//            mCards.add(card);
//        }

        //        Google
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED);
        card.setIcon(R.drawable.google);
        card.setText(R.string.name_google);
        card.setTimestamp(R.string.url_google);
        mCards.add(card);

        //        CNN
        card = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED);
        card.setIcon(R.drawable.cnn);
        card.setText(R.string.name_cnn);
        card.setTimestamp(R.string.url_cnn);
        mCards.add(card);

        //        NYTimes
        card = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED);
        card.setIcon(R.drawable.nytimes);
        card.setText(R.string.name_nytimes);
        card.setTimestamp(R.string.url_nytimes);
        mCards.add(card);
    }

//    -------------------------------------------------------

//    /**
//     * set up gesture detector
//     */
//    private GestureDetector createGestureDetector(Context context) {
//        GestureDetector gestureDetector = new GestureDetector(context);
//
//        //Create a base listener for generic gestures
//        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
//            @Override
//            public boolean onGesture(Gesture gesture) {
//                if (gesture == Gesture.TAP) {
//                    openOptionsMenu();
//                    return true;
//                } else if (gesture == Gesture.TWO_TAP) {
//                    // do something on two finger tap
//                    return true;
//                } else if (gesture == Gesture.SWIPE_RIGHT) {
//                    // do something on right (forward) swipe
//                    return true;
//                } else if (gesture == Gesture.SWIPE_LEFT) {
//                    // do something on left (backwards) swipe
//                    return true;
//                } else if (gesture == Gesture.SWIPE_DOWN) {
//                    finish();
//                }
//                return false;
//            }
//        });
//
//        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
//            @Override
//            public void onFingerCountChanged(int previousCount, int currentCount) {
//                // do something on finger count changes
//            }
//        });
//
//        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
//            @Override
//            public boolean onScroll(float displacement, float delta, float velocity) {
//                // do something on scrolling
//                return true;
//            }
//        });
//
//        return gestureDetector;
//    }


}
