package com.ba.motiondetectionlib.model;

////////////////////////////////////////////////////////////////////////////////////////////////////
//              a class that contains all global constants                                        //
////////////////////////////////////////////////////////////////////////////////////////////////////

public class Constants {
    // ------> general constants for motion detection
    public static final String INTENT_IDENTIFIER = "com.ba.motiondetection.intent.action";
    public static final String STRING_EXTRA_IDENTIFIER = "com.ba.motiondetection.intent.string.extra";

    public static final String SEND_MOTION = "Motion_Detection_Send";
    public static final String RECEIVE_MOTION = "Motion_Detection_Receive";
    public static final String DROP_MOTION = "Motion_Detection_Drop";
    public static final String SCOOP_MOTION = "Motion_Detection_Scoop";

    // ------> general constants for motion detection
    public static final float MIN_GENERAL_GRAVITY_VALUE = 7f;
    public static final long MAX_GENERAL_TIME_DIFF = 500;
    public static final float MIN_GENERAL_ACCELERATION_VALUE = 12f;

    // ------> SEND motion constants
    public static final float MIN_SEND_ACCELERATION_VALUE = 7f;

    // ------> RECEIVE constants
    public static final float MIN_ROTATION_VALUE = 7f;
    public static final long MAX_TIME_DIFF_RECEIVE = 150;

    // ------> DROP constants
    public static final float MIN_DROP_ACCELERATION_VALUE = -15f;

    // ------> SCOOP constants: we use general constants from above

    // ------> tag for logging
    public static final String TAG = "MotionTag";
}
