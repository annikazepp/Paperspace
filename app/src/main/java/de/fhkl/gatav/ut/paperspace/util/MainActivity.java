package de.fhkl.gatav.ut.paperspace.util;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;

/**
 * @class MainActivity Hauptschnittstelle der Anwendung, die Startbildschirm enthält und bei Klick des playButton die MainGameActivity öffnet */
public class MainActivity extends AppCompatActivity {

    private Button playButton;
    private MediaPlayer mLaserShoot;
    private MediaPlayer mExplosion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Layout wird festgelegt
         */
        setContentView(R.layout.activity_main); // TODO -> Start-Bildschirm unter Layout einstellen

        /**
         * Spiel wird nach drücken des Play - Button gestartet
         * (durch onClick)
         */
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * neue Aktivität wird gestartet -> MainGameActivity
                 */
                Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
                startActivity(intent);
            }

        });

        //Sounds
        mLaserShoot = MediaPlayer.create(this, R.raw.lasershoot);
        mLaserShoot.start();

        mExplosion = MediaPlayer.create(this, R.raw.hitboom);
        mExplosion.start();

    }
}