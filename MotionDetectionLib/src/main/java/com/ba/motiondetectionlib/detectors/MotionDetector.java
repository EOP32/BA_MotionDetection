package com.ba.motiondetectionlib.detectors;

import static com.ba.motiondetectionlib.model.Constants.DROP_MOTION;
import static com.ba.motiondetectionlib.model.Constants.INTENT_IDENTIFIER;
import static com.ba.motiondetectionlib.model.Constants.RECEIVE_MOTION;
import static com.ba.motiondetectionlib.model.Constants.SCOOP_MOTION;
import static com.ba.motiondetectionlib.model.Constants.SEND_MOTION;
import static com.ba.motiondetectionlib.model.Constants.STRING_EXTRA_IDENTIFIER;
import static com.ba.motiondetectionlib.model.Constants.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ba.motiondetectionlib.model.MotionType;

public class MotionDetector implements MotionDetectionListener {
    private final Context context;

    public MotionDetector(Context context) {
        this.context = context;
    }

    @Override
    public void onMotionDetected(MotionType type) {
        switch (type) {
            case SEND:
                sendBroadcast(SEND_MOTION);
                break;
            case RECEIVE:
                sendBroadcast(RECEIVE_MOTION);
                break;
            case DROP:
                sendBroadcast(DROP_MOTION);
                break;
            case SCOOP:
                sendBroadcast(SCOOP_MOTION);
                break;
            default:
                break;
        }
    }

    private void sendBroadcast(String motion) {
        Intent intent = new Intent();
        intent.setAction(INTENT_IDENTIFIER);
        intent.putExtra(STRING_EXTRA_IDENTIFIER, motion);
        context.sendBroadcast(intent);
        Log.d(TAG, "Motion detection broadcast sent. " + motion);
    }

    long timestamp() {
        return System.currentTimeMillis();
    }
}
