/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package safe.girl.just.girl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.MotionEvent;
import android.gesture.GestureOverlayView;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CreateGestureActivity extends Activity implements Spinner.OnItemSelectedListener {
    private static final float LENGTH_THRESHOLD = 120.0f;

    private Gesture mGesture;
    private View mDoneButton;
    private String name = "" ;
    private Spinner spinner ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.create_gesture);

        mDoneButton = findViewById(R.id.done);
        spinner = (Spinner)findViewById(R.id.gesture_name) ;
        spinner.setOnItemSelectedListener(this);

        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        overlay.addOnGestureListener(new GesturesProcessor());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            final GestureOverlayView overlay =
                    (GestureOverlayView) findViewById(R.id.gestures_overlay);
            overlay.post(new Runnable() {
                public void run() {
                    overlay.setGesture(mGesture);
                }
            });

            mDoneButton.setEnabled(true);
        }
    }


    /**
     * 添加手势*/
    @SuppressWarnings({"UnusedDeclaration"})
    public void addGesture(View v) {
        if (mGesture != null) {
//            final Spinner input = (Spinner) findViewById(R.id.gesture_name);
            name = getName() ;
//            final CharSequence name = input.getT;
            /*if (name.equals("")) {
                input.setError(getString(R.string.error_missing_name));
                return;
            }*/
            Toast.makeText(CreateGestureActivity.this,name,Toast.LENGTH_SHORT).show();
            final GestureLibrary store = GestureBuilderActivity.getStore();
            store.addGesture(name, mGesture);
            store.save();

            setResult(RESULT_OK);

            final String path = new File(Environment.getExternalStorageDirectory(),
                    "gestures").getAbsolutePath();
            Toast.makeText(this, getString(R.string.save_success, path), Toast.LENGTH_LONG).show();
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
        
    }
    
    @SuppressWarnings({"UnusedDeclaration"})
    public void cancelGesture(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**手势监听*/
    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mDoneButton.setEnabled(false);
            mGesture = null;
        }
        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }
        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(false);
            }
            mDoneButton.setEnabled(true);
        }
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }
    }


    /**spinner 选择监听*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent ;
        switch ((int)spinner.getItemIdAtPosition(position)){
            case 0:         //报警
                name = "报警" ;
                break;
            case 1:         //语音识别
                name = "语音识别" ;
                break;
            case 2:         //紧急求救
                name = "紧急求救" ;
                break;
            case 3:         //模拟声音
                name = "模拟声音" ;
                break;
            case 4:         //闪光灯
                name = "闪光灯" ;
                break;
        }
    }
    private String getName(){
        return name ;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        name = "报警" ;
    }
}
