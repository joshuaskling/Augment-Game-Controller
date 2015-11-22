package edu.csumb.gamecontroller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.json.JSONObject;

public class MainActivity extends Activity implements SensorEventListener {

    private IOSocket socket;
    private SensorManager mSensorManager;
    long time = System.currentTimeMillis();
    long positionTimer = System.currentTimeMillis();
    public static Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        //set orientation to landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        connect();

        setContentView(R.layout.activity_main);

        final ImageButton button1 = (ImageButton) findViewById(R.id.button1);
        final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        final ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton button4 = (ImageButton) findViewById(R.id.button4);

        //setContentView(R.layout.main);
        final RelativeLayout textView = (RelativeLayout)findViewById(R.id.joystickLayout);
        // this is the view on which you will listen for touch events
        final View touchView = findViewById(R.id.joystickLayout);

        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (System.currentTimeMillis() - time > 200) {

                    //textView.setText("Touch coordinates : " +
                    //        String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                    float x = (event.getX() - 250);
                    float y = (event.getY() - 250);

                    //time = System.currentTimeMillis();
                    //System.out.println(time);
                        //System.out.println("X: " + x + " Y: " + y);
                        sendMovement(x, y);
                        time = System.currentTimeMillis();

                }
                return true;
            }

        });

        button1.setOnTouchListener(
                new View.OnTouchListener(){
                    @Override
                    public boolean onTouch (View v, MotionEvent event){
                        //darken button when pressed
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                //button1.setBackgroundResource(R.drawable.x_button_small_pressed);
                                vibrator.vibrate(30);
                                sendFire("btn1", "down");
                                //Log.i("AIPSERVER", "Message sent to server: fire!");
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                //v.getBackground().clearColorFilter();
                                //v.invalidate();
                                //button1.setImageResource(R.drawable.x_button_small);
                                sendFire("btn1", "up");
                                break;
                            }
                        }
                    return true;
                    }
                }
        );
        button2.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //darken button when pressed
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                //v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                                //v.invalidate();
                                vibrator.vibrate(30);
                                sendFire("btn2", "down");
                                //Log.i("AIPSERVER", "Message sent to server: fire!");
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                //v.getBackground().clearColorFilter();
                                //v.invalidate();
                                sendFire("btn2", "down");
                                break;
                            }
                        }
                        return true;
                    }
                }
        );
        button3.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        //darken button when pressed
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                //v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                                //v.invalidate();
                                vibrator.vibrate(30);
                                sendFire("btn3", "down");
                                //Log.i("AIPSERVER", "Message sent to server: fire!");
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                //v.getBackground().clearColorFilter();
                                //v.invalidate();
                                sendFire("btn3", "up");
                                break;
                            }
                        }

                        return true;
                    }
                }
        );
        button4.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //darken button when pressed
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                //v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                                //v.invalidate();
                                vibrator.vibrate(30);
                                sendFire("btn4", "down");
                                //Log.i("AIPSERVER", "Message sent to server: fire!");
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                //v.getBackground().clearColorFilter();
                                //v.invalidate();
                                sendFire("btn4", "up");
                                break;
                            }
                        }
                        return true;
                    }
                }
        );

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }


    //stick listeners


    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        // for the system's orientation sensor registered listeners

    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (System.currentTimeMillis()-positionTimer > 200) {
            sendOrientation(event.values[0], event.values[1], event.values[2]);
            // get the angle around the z-axis rotated
            positionTimer = System.currentTimeMillis();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    void sendFire(String input, String state) {
        // get the angle around the z-axis rotated
        try {

            JSONObject message = new JSONObject(new String("{state: " + state + "}"));

            if (socket.isConnected()) {
                socket.emit(input, message);
                //Log.i("AIPSERVER", "Message sent to server: fire!");
                System.out.println(input);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void sendOrientation(float deltaAlpha, float deltaBeta, float deltaGamma) {


            try {

                String messageContent = new String("{alpha: " + deltaAlpha + ",beta: " + deltaBeta + ", gamma:" + deltaGamma + "}");

                JSONObject message = new JSONObject(messageContent);

                if (socket.isConnected()) {
                    socket.emit("orientation", message);
                    Log.i("AIPSERVER", "Message sent to server: " + message.getString("alpha"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    void sendMovement(float x, float y) {
        try {

            String messageContent = new String("{x: "+x+ ",y: " + y + "}");

            JSONObject message = new JSONObject(messageContent);

            if (socket.isConnected()) {
                socket.emit("joystick", message);
                //Log.i("AIPSERVER", "Message sent to server: " + message.getString("movement"));
                System.out.println("X: " + x + " Y: " + y);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void connect() {

        socket = new IOSocket("http://aipservers.com:3000", new MessageCallback() {

            @Override
            public void onMessage(String message) {
                // Handle simple messages
            }

            @Override
            public void onConnect() {
                // Socket connection opened
            }

            @Override
            public void onDisconnect() {
                // Socket connection closed
            }

            @Override
            public void on(String event, JSONObject... data) {

            }

            @Override
            public void onMessage(JSONObject json) {

            }

            @Override
            public void onConnectFailure() {

            }
        });

        socket.connect();
    }
}
