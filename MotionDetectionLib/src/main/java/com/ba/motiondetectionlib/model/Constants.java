package com.ba.motiondetectionlib.model;

////////////////////////////////////////////////////////////////////////////////////////////////////
//              a class that contains all global constants                                        //
////////////////////////////////////////////////////////////////////////////////////////////////////

public class Constants {

    // ------> general constants for motion detection
    public static final String INTENT_IDENTIFIER = "com.ba.motiondetectionlib";
    public static final String STRING_EXTRA_IDENTIFIER = "com.ba.motiondetectionlib.intent.string.extra";

    public static final String SEND_MOTION = "Motion_Detection_Send";
    public static final String RECEIVE_MOTION = "Motion_Detection_Receive";
    public static final String DROP_MOTION = "Motion_Detection_Drop";
    public static final String SCOOP_MOTION = "Motion_Detection_Scoop";

    // ------> general constants for motion detection
    public static final float MIN_GRAVITY_VALUE = 6f;
    public static final float MIN_HORIZONTAL_ACCELERATION_VALUE = 8f;
    public static final float MIN_VERTICAL_ACCELERATION_VALUE = 5f;
    public static final float MIN_ROTATION_VALUE = 7f;
    public static final long MAX_TIME_DIFF = 500;

    // ------> specific SEND constants
    public static final float MIN_SEND_ROTATION_VALUE = 3.5f;

    // ------> specific RECEIVE constants
    public static final long MAX_TIME_DIFF_RECEIVE = 150;

    // ------> tag for logging
    public static final String TAG = "MotionTag";
}
