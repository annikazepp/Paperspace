package de.fhkl.gatav.ut.paperspace.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;



public class MainActivity extends AppCompatActivity {

    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // TODO -> Start-Bildschirm unter Layout einstellen

        playButton = findViewById(R.id.play_button); // in activity_main.xml definiert
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
                startActivity(intent);
            }

        });

    }




}