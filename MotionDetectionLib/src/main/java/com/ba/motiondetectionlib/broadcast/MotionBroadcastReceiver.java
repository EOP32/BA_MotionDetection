package com.ba.motiondetectionlib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.DROP_MOTION;
import static com.ba.motiondetectionlib.model.Constants.RECEIVE_MOTION;
import static com.ba.motiondetectionlib.model.Constants.SCOOP_MOTION;
import static com.ba.motiondetectionlib.model.Constants.SEND_MOTION;
import static com.ba.motiondetectionlib.model.Constants.STRING_EXTRA_IDENTIFIER;

public class MotionBroadcastReceiver extends BroadcastReceiver {

    private MotionDetectionListener listener;

    public MotionBroadcastReceiver(MotionDetectionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String motion = intent.getStringExtra(STRING_EXTRA_IDENTIFIER);

        switch (motion) {
            case SEND_MOTION:
                listener.motionDetected(MotionType.SEND);
                break;
            case RECEIVE_MOTION:
                listener.motionDetected(MotionType.RECEIVE);
                break;
            case DROP_MOTION:
                listener.motionDetected(MotionType.DROP);
                break;
            case SCOOP_MOTION:
                listener.motionDetected(MotionType.SCOOP);
                break;
            default:
                break;
        }
    }
}
