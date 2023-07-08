package de.fhkl.gatav.ut.paperspace.util;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;

/**
 * Startbildschirm der App
 */
public class MainActivity extends AppCompatActivity {

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
                startActivity(intent);
                finish();
        });
    }
}



