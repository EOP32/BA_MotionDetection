package com.ba.motiondetectionlib.service;

import android.content.Context;
import android.content.Intent;

public class ServiceFacade implements IServiceFacade {
    private Context context;

    public ServiceFacade(Context context) {
        this.context = context;
    }

    @Override
    public void startDetectionService() {
        context.startService(new Intent(context, MotionDetectionService.class));
    }

    @Override
    public void stopDetectionService() {
        context.stopService(new Intent(context, MotionDetectionService.class));
    }
}
