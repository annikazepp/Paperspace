package de.fhkl.gatav.ut.paperspace.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;

public class GameOver extends AppCompatActivity {

    private ImageButton returnButton;
    private ImageButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over); // TODO -> GameOver-Bildschirm unter Layout einstellen

        returnButton = findViewById(R.id.return_button); // in game_over.xml definiert
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, MainGameActivity.class);
                startActivity(intent);
            }

        });

        exitButton = findViewById(R.id.exit_button); // in game_over.xml definiert
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

    }
}
