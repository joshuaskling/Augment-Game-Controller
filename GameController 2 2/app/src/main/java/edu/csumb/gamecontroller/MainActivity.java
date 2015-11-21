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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.sql.Time;

public class MainActivity extends Activity implements SensorEventListener {

    private IOSocket socket;
    private SensorManager mSensorManager;

    //Time now = new Time();
    //now.setToNow();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                //textView.setText("Touch coordinates : " +
                //        String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                float x = (event.getX()-250);
                float y = (event.getY()-250);

                if (Math.abs(x) < 250 && Math.abs(y) < 250) {
                    //System.out.println("X: " + x + " Y: " + y);
                    sendMovement(x, y);
                }
                return true;
            }
        });

        //stick drag stuff
        //final ImageButton joystick = (ImageButton) findViewById(R.id.stick);
        /*
        joystick.setOnTouchListener(new View.OnTouchListener()
        {

            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE )
                {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(v.getWidth(),  v.getHeight());
                    params.setMargins((int)event.getRawX() - v.getWidth()/2, (int)(event.getRawY() - v.getHeight()), (int)event.getRawX() - v.getWidth()/2, (int)(event.getRawY() - v.getHeight()));
                    v.setLayoutParams(params);
                }
                return false;
            }
        });*/

            button1.setOnTouchListener(
                    new View.OnTouchListener()

            {
                @Override
                public boolean onTouch (View v, MotionEvent event){
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

    void sendOrientation(float deltaAlpha, float deltaBeta, float deltaGamma) {
        try {

            String messageContent = new String("{alpha: "+deltaAlpha+ ",beta: " + deltaBeta +", gamma:" + deltaGamma+ "}");

            JSONObject message = new JSONObject(messageContent);

            if (socket.isConnected()) {
                socket.emit("orientation", message);
                //Log.i("AIPSERVER", "Message sent to server: " + message.getString("alpha"));
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
                socket.emit("movement", message);
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
