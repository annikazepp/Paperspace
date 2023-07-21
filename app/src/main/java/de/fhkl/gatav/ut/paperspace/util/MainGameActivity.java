package de.fhkl.gatav.ut.paperspace.util;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;

/**
 * MainGameActvity repräsentiert das Hauptspiel der App und enthält die Ansichtsklasse GameView
 */
public class MainGameActivity extends AppCompatActivity {
    private GameView gameView;
    private MediaPlayer mBackground2;
    private MediaPlayer mGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout des Spiels wird angezeigt
        gameView = new GameView(this);
        setContentView(gameView);

        if(MainActivity.isSoundOn) {
            mBackground2 = MediaPlayer.create(this, R.raw.background2);
            mBackground2.start();
            mBackground2.setLooping(true);
        }
    }

     @Override
     protected void onPause() {
         super.onPause();
         gameView.pause();

         if(MainActivity.isSoundOn) {
            mBackground2.stop();

            mGameOver = MediaPlayer.create(this, R.raw.gameover);
            mGameOver.start();
        }
     }
}

