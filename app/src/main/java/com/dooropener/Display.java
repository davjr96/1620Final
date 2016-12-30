package com.dooropener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.teubi.raspberrypi.io.GPIO;
import co.teubi.raspberrypi.io.GPIOStatus;
import co.teubi.raspberrypi.io.PORTFUNCTION;


public class Display extends Activity  implements GPIO.PortUpdateListener,GPIO.ConnectionEventListener{
    TextView textView;
    GPIO gpioPort;
    private int pin =24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        this.gpioPort = new GPIO(new GPIO.ConnectionInfo("128.143.47.166", 8000,
                "webiopi", "raspberry"));

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        pin = intent.getIntExtra("PIN",0);
        final Button button = (Button) findViewById(R.id.button);

        textView =  (TextView)findViewById(R.id.textView);
        textView.setTextSize(20);
        textView.setText(name);

        gpioPort.setFunction(pin, PORTFUNCTION.OUTPUT);

        this.gpioPort.addPortUpdateListener(this);
        (new Thread(this.gpioPort)).start();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gpioPort.setValue(pin, 1);
                Handler myHandler = new Handler();
                myHandler.postDelayed(mMyRunnable, 5000);
            }
        });
    }
    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            gpioPort.setValue(pin, 0);
        }
    };

    @Override
    public void onConnectionFailed(String message) {

    }
    @Override
    public void onPortUpdated(GPIOStatus stat) {

    }
}
