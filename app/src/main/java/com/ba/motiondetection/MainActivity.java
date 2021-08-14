package com.ba.motiondetection;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ba.motiondetectionlib.broadcast.MotionBroadcastReceiver;
import com.ba.motiondetectionlib.broadcast.MotionDetectionListener;
import com.ba.motiondetectionlib.model.Constants;
import com.ba.motiondetectionlib.model.MotionType;
import com.ba.motiondetectionlib.service.ServiceController;

public class MainActivity extends AppCompatActivity implements MotionDetectionListener {

    private ServiceController serviceController;
    private MotionBroadcastReceiver motionBroadcastReceiver;

    private CardView sendView;
    private CardView receiveView;
    private CardView dropView;
    private CardView scoopView;

    private TextView sendText;
    private TextView receiveText;
    private TextView dropText;
    private TextView scoopText;

    private int sendDetected = 0;
    private int receiveDetected = 0;
    private int dropDetected = 0;
    private int scoopDetected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceController = new ServiceController(this);
        motionBroadcastReceiver = new MotionBroadcastReceiver(this);

        sendView = findViewById(R.id.sendView);
        receiveView = findViewById(R.id.receiveView);
        dropView = findViewById(R.id.dropView);
        scoopView = findViewById(R.id.scoopView);

        sendText = findViewById(R.id.textSend);
        receiveText = findViewById(R.id.textReceive);
        dropText = findViewById(R.id.textDrop);
        scoopText = findViewById(R.id.textScoop);

        registerReceiver(motionBroadcastReceiver, new IntentFilter(Constants.INTENT_IDENTIFIER));
    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceController.startDetectionService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceController.stopDetectionService();
        unregisterReceiver(motionBroadcastReceiver);
    }

    @Override
    public void motionDetected(MotionType type) {
        reset();

        switch (type) {
            case SEND:
                sendDetected++;
                sendText.setText(sendDetected);
                sendView.setCardBackgroundColor(Color.GREEN);
                break;
            case RECEIVE:
                receiveDetected++;
                receiveText.setText(receiveDetected);
                receiveView.setCardBackgroundColor(Color.GREEN);
                break;
            case DROP:
                dropDetected++;
                dropText.setText(dropDetected);
                dropView.setCardBackgroundColor(Color.GREEN);
                break;
            case SCOOP:
                scoopDetected++;
                scoopText.setText(scoopDetected);
                scoopView.setCardBackgroundColor(Color.GREEN);
                break;
            default:
                break;
        }

        Log.d(Constants.TAG, "motionDetected: " + type);
    }

    private void reset() {
        sendView.setCardBackgroundColor(Color.WHITE);
        receiveView.setCardBackgroundColor(Color.WHITE);
        dropView.setCardBackgroundColor(Color.WHITE);
        scoopView.setCardBackgroundColor(Color.WHITE);
    }
}