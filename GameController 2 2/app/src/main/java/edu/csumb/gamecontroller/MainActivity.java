package edu.csumb.gamecontroller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.json.JSONObject;

public class MainActivity extends Activity implements SensorEventListener {

    private IOSocket socket;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set orientation to landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        connect();

        setContentView(R.layout.activity_main);

        //final Button firebutton = (Button) findViewById(R.id.fireButton);
        final ImageButton button1 = (ImageButton) findViewById(R.id.button1);
        final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        final ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton button4 = (ImageButton) findViewById(R.id.button4);

        button1.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sendFire("btn1");
                        //Log.i("AIPSERVER", "Message sent to server: fire!");
                        return true;
                    }
                }
        );

        button2.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sendFire("btn2");
                        //Log.i("AIPSERVER", "Message sent to server: fire!");
                        return true;
                    }
                }
        );
        button3.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sendFire("btn3");
                        //Log.i("AIPSERVER", "Message sent to server: fire!");
                        return true;
                    }
                }
        );
        button4.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sendFire("btn4");
                        //Log.i("AIPSERVER", "Message sent to server: fire!");
                        return true;
                    }
                }
        );

        /*final SeekBar speedSlider = (SeekBar) findViewById(R.id.speed);

        speedSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sendSpeed(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );*/

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

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

        sendOrientation(event.values[0], event.values[1], event.values[2]);
        // get the angle around the z-axis rotated

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    void sendFire(String input) {
        // get the angle around the z-axis rotated
        try {

            JSONObject message = new JSONObject(new String("{}"));

            if (socket.isConnected()) {
                socket.emit(input, message);
                //Log.i("AIPSERVER", "Message sent to server: fire!");
                System.out.println(input);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*void sendFire() {
        // get the angle around the z-axis rotated
        try {

            JSONObject message = new JSONObject(new String("{}"));

            if (socket.isConnected()) {
                socket.emit("fire", message);
                //Log.i("AIPSERVER", "Message sent to server: fire!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    /*void sendSpeed(int speed) {
        try {

            JSONObject message = new JSONObject(new String("{speed:"+ speed/10f+"}"));

            if (socket.isConnected()) {
                socket.emit("speed", message);
                Log.i("AIPSERVER", "Message sent to server: speed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    void sendOrientation(float deltaAlpha, float deltaBeta, float deltaGamma) {
        try {


            String messageContent = new String("{alpha: "+deltaAlpha+ ",beta: " + deltaBeta +", gamma:" + deltaGamma+ "}");

            JSONObject message = new JSONObject(messageContent);

            if (socket.isConnected()) {
                socket.emit("orientation", message);
                Log.i("AIPSERVER", "Message sent to server: " + message.getString("alpha"));
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
