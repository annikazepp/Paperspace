package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @class MainGameActvity repräsentiert das Hauptspiel der App und enthält Ansichtsklasse SpaceView
 */
public class MainGameActivity extends AppCompatActivity {
    private SpaceView spaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Ansicht des Hauptspiels wird angezeigt
         */
        spaceView = new SpaceView(this);
        setContentView(spaceView);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

}
