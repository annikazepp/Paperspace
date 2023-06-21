package de.fhkl.gatav.ut.paperspace.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.fhkl.gatav.ut.paperspace.R;

public class GameOver extends AppCompatActivity {

    private ImageButton returnButton;
    private ImageButton exitButton;

    private TextView dScore, dPersonalBestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over); // TODO -> GameOver-Bildschirm unter Layout einstellen

        int score = getIntent().getExtras().getInt("score");

        SharedPreferences pref = getSharedPreferences("MyPref",0);
        int personalBestScore = pref.getInt("personalBestScore",0);
        SharedPreferences.Editor editor = pref.edit();
        if(score > personalBestScore){
            personalBestScore = score;
            editor.putInt("personalBestScore", personalBestScore);
            editor.commit();
        }

        dScore = (TextView) findViewById(R.id.score);
        dPersonalBestScore = (TextView) findViewById(R.id.personalBest);
        dScore.setText("" + score);
        dPersonalBestScore.setText(""+personalBestScore);


        returnButton = findViewById(R.id.return_button); // in game_over.xml definiert
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, MainActivity.class);
                startActivity(intent); //Zurück zum StartBildschirm
            }

        });

        exitButton = findViewById(R.id.exit_button); // in game_over.xml definiert
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity(); //CLOSE APP
            }

        });

    }
}
