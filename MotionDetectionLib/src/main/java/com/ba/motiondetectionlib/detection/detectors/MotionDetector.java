package com.ba.motiondetectionlib.detection.detectors;

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

import com.ba.motiondetectionlib.detection.DetectionBroadcaster;
import com.ba.motiondetectionlib.detection.MotionSensorSource;
import com.ba.motiondetectionlib.detection.SensorDataListener;
import com.ba.motiondetectionlib.model.MotionType;

public abstract class MotionDetector implements DetectionBroadcaster, SensorDataListener {
    private final Context context;
    private final Intent intent;

    public MotionDetector(Context context, Intent intent, MotionSensorSource source) {
        this.context = context;
        this.intent = intent;
        source.addSensorDataListener(this);
    }

    @Override
    public void sendBroadcast(MotionType motionType) {
        intent.setAction(INTENT_IDENTIFIER);

        switch (motionType) {
            case SEND:
                intent.putExtra(STRING_EXTRA_IDENTIFIER, SEND_MOTION);
                break;
            case RECEIVE:
                intent.putExtra(STRING_EXTRA_IDENTIFIER, RECEIVE_MOTION);
                break;
            case DROP:
                intent.putExtra(STRING_EXTRA_IDENTIFIER, DROP_MOTION);
                break;
            case SCOOP:
                intent.putExtra(STRING_EXTRA_IDENTIFIER, SCOOP_MOTION);
                break;
            default:
                return;
        }

        context.sendBroadcast(intent);
        Log.d(TAG, "Motion detection broadcast sent. " + motionType);
    }

    long timestamp() {
        return System.currentTimeMillis();
    }
}
