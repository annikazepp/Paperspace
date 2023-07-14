package de.fhkl.gatav.ut.paperspace.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;

/**
 * Startbildschirm der App
 */
public class MainActivity extends AppCompatActivity {

    private MediaPlayer mIntro;

    protected static boolean isSoundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout festlegen
        setContentView(R.layout.activity_main); // TODO STARTBILDSCHIRM ANPASSEN
        // Play Button konfigurieren und Klick-Event hinzufügen
        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> {
                // Bei Klick auf Play Button wird die MainGameActivity gestartet
                Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
                //intent.putExtra("SOUND", isSoundOn);
                startActivity(intent);
                finish();
        });

        // Initialisiere Sound
        ImageButton soundOnOff = findViewById(R.id.soundOnOff);
        soundOnOff.setOnClickListener(v -> toggleSound());
        updateSoundButton();
        if(isSoundOn) {
            startIntro();
        }
    }

    // Wenn App geschlossen wird
    @Override
    protected void onStop() {
        super.onStop();
        stopIntro();
    }

    private void toggleSound(){
        isSoundOn = !isSoundOn;
        updateSoundButton();

        if(isSoundOn){
            startIntro();
        }else{
            stopIntro();
        }
    }

    private void updateSoundButton(){
        ImageButton soundOnOff = findViewById(R.id.soundOnOff);
        soundOnOff.setImageResource(isSoundOn? R.drawable.ton_an : R.drawable.mute);
    }

    private void startIntro(){
        mIntro = MediaPlayer.create(this, R.raw.intro);
        mIntro.setLooping(true);
        mIntro.start();
    }

    private void stopIntro(){
        if(mIntro != null && mIntro.isPlaying()){
            mIntro.stop();
            mIntro.release();
            mIntro = null;
        }
    }
}



