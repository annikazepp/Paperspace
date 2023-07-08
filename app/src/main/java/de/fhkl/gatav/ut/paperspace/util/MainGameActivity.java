package de.fhkl.gatav.ut.paperspace.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @class MainGameActvity repräsentiert das Hauptspiel der App und enthält die Ansichtsklasse GameView
 */
public class MainGameActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout des Spiels wird angezeigt
        gameView = new GameView(this);
        setContentView(gameView);
    }

     @Override
     protected void onPause() {
     super.onPause();
     gameView.pause();
     }
}

