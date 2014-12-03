package com.key.topfinder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;
import com.key.topfinder.adapter.DeveloperAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by KeY on 12/2/14.
 */
public class ResultsActivity extends Activity {

    //    pass parameters between ResultActivity and MainActivity
    public static final String SEARCH = "search";
    private String mPlatform = "Android";

    //    set up initial screen of ResultsActivity
    private CardScrollView mCardScroller;
    private List<CardBuilder> mCards;
    private GestureDetector mGestureDetector;

    //    create a temporary search method
    private void findDevelopers(String platform) {
        for (int i = 1; i <= 10; i++) {

            CardBuilder card = new CardBuilder(this, CardBuilder.Layout.COLUMNS_FIXED);
            card.setIcon(R.drawable.logo);
//            card.setIcon(R.drawable.ic_person_50);
            card.setText(platform + " " + Integer.toString(i));
            card.setTimestamp(platform);

//            CardBuilder card = new CardBuilder(this);
//            card.setText(platform+" "+Integer.toString(i));
//            card.setTimestamp(platform);
//            card.setImageLayout(CardBuilder.ImageLayout.LEFT);
//            card.addImage(R.drawable.ic_person_50);

            mCards.add(card);
        }
        mCardScroller.setSelection(0);
    }


    //    attach developer menu
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.developer, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }


    /**
     * set up gesture detector
     */
    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN) {
                    finish();
                }
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });

        return gestureDetector;
    }


    /**
     * Menu handler.
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case R.id.developer_fav:
                    Toast.makeText(getApplicationContext(), "Favorite", Toast.LENGTH_LONG).show();
                    break;
                case R.id.developer_hire:
                    Toast.makeText(getApplicationContext(), "Message", Toast.LENGTH_LONG).show();
                    break;
                case R.id.go_back:
                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        mCardScroller = new CardScrollView(this);
        mCards = new ArrayList<CardBuilder>();

        if (getIntent().hasExtra(SEARCH)) {
            mPlatform = getIntent().getStringExtra(SEARCH);
        }

        findDevelopers(mPlatform);
        mCardScroller.setAdapter(new DeveloperAdapter(mCards));

        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOptionsMenu();
            }
        });

        mGestureDetector = createGestureDetector(this);
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

}
