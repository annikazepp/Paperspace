package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.objects.Joystick;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;

/**
 * @class MainGameActvity repräsentiert das Hauptspiel der App und enthält Ansichtsklasse SpaceView
 */
public class MainGameActivity extends AppCompatActivity {
    private SpaceView spaceView;
    private Joystick joystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Ansicht des Hauptspiels wird angezeigt
         */

        joystick = Joystick.getJoystick();

        RelativeLayout layout = new RelativeLayout(this);
        setContentView(layout);

        spaceView = new SpaceView(this);
        layout.addView(spaceView);

        View myView = new View(this);
        layout.addView(myView);

        myView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                SpaceShip player = SpaceShip.getPlayer();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (joystick.isPressed((double) event.getX(), (double) event.getY())) {
                            joystick.setIsPressed(true);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if(joystick.getIsPressed()){
                            joystick.setActuator((double) event.getX(), (double) event.getY());
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        joystick.setIsPressed(false);
                        joystick.resetActuator();
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}

