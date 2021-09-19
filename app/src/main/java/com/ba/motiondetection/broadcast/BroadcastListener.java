package com.ba.motiondetection.broadcast;

import com.ba.motiondetectionlib.model.MotionType;

public interface BroadcastListener {
    void onMotionBroadcastReceived(MotionType motionType);
}
