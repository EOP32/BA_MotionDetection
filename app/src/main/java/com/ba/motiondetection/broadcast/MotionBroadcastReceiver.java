package com.ba.motiondetection.broadcast;

import static com.ba.motiondetectionlib.model.Constants.DROP_MOTION;
import static com.ba.motiondetectionlib.model.Constants.RECEIVE_MOTION;
import static com.ba.motiondetectionlib.model.Constants.SCOOP_MOTION;
import static com.ba.motiondetectionlib.model.Constants.SEND_MOTION;
import static com.ba.motiondetectionlib.model.Constants.STRING_EXTRA_IDENTIFIER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ba.motiondetectionlib.model.Constants;
import com.ba.motiondetectionlib.model.MotionType;

public class MotionBroadcastReceiver extends BroadcastReceiver {

    private BroadcastListener listener;

    public MotionBroadcastReceiver(BroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String motion = intent.getStringExtra(STRING_EXTRA_IDENTIFIER);
        Log.d(Constants.TAG, "Broadcast received of type " + motion);

        switch (motion) {
            case SEND_MOTION:
                listener.onMotionBroadcastReceived(MotionType.SEND);
                break;
            case RECEIVE_MOTION:
                listener.onMotionBroadcastReceived(MotionType.RECEIVE);
                break;
            case DROP_MOTION:
                listener.onMotionBroadcastReceived(MotionType.DROP);
                break;
            case SCOOP_MOTION:
                listener.onMotionBroadcastReceived(MotionType.SCOOP);
                break;
            default:
                break;
        }
    }
}
