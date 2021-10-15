package com.ba.motiondetection.ui;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ba.motiondetection.R;
import com.ba.motiondetection.broadcast.BroadcastListener;
import com.ba.motiondetection.broadcast.MotionBroadcastReceiver;
import com.ba.motiondetectionlib.model.Constants;
import com.ba.motiondetectionlib.model.MotionType;
import com.ba.motiondetectionlib.service.IServiceFacade;
import com.ba.motiondetectionlib.service.ServiceFacade;

public class MainActivity extends AppCompatActivity implements BroadcastListener {

    private IServiceFacade serviceFacade;
    private MotionBroadcastReceiver motionBroadcastReceiver;

    private CardView sendView;
    private CardView receiveView;
    private CardView dropView;
    private CardView scoopView;

    private TextView sendText;
    private TextView receiveText;
    private TextView dropText;
    private TextView scoopText;

    private int sendDetected;
    private int receiveDetected;
    private int dropDetected;
    private int scoopDetected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceFacade = new ServiceFacade(this);
        motionBroadcastReceiver = new MotionBroadcastReceiver(this);

        sendView = findViewById(R.id.sendView);
        receiveView = findViewById(R.id.receiveView);
        dropView = findViewById(R.id.dropView);
        scoopView = findViewById(R.id.scoopView);

        sendText = findViewById(R.id.sendNumber);
        receiveText = findViewById(R.id.receiveNumber);
        dropText = findViewById(R.id.dropNumber);
        scoopText = findViewById(R.id.scoopNumber);

        sendDetected = 0;
        receiveDetected = 0;
        dropDetected = 0;
        scoopDetected = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceFacade.startDetectionService();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_IDENTIFIER);
        registerReceiver(motionBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(motionBroadcastReceiver);
        serviceFacade.stopDetectionService();
    }

    private void reset() {
        sendView.setCardBackgroundColor(Color.WHITE);
        receiveView.setCardBackgroundColor(Color.WHITE);
        dropView.setCardBackgroundColor(Color.WHITE);
        scoopView.setCardBackgroundColor(Color.WHITE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMotionBroadcastReceived(MotionType motionType) {
        reset();

        switch (motionType) {
            case SEND:
                sendView.setCardBackgroundColor(Color.GREEN);
                sendDetected++;
                sendText.setText(String.valueOf(sendDetected));
                break;
            case RECEIVE:
                receiveView.setCardBackgroundColor(Color.GREEN);
                receiveDetected++;
                receiveText.setText(String.valueOf(receiveDetected));
                break;
            case DROP:
                dropView.setCardBackgroundColor(Color.GREEN);
                dropDetected++;
                dropText.setText(String.valueOf(dropDetected));
                break;
            case SCOOP:
                scoopView.setCardBackgroundColor(Color.GREEN);
                scoopDetected++;
                scoopText.setText(String.valueOf(scoopDetected));
                break;
            default:
                break;
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}