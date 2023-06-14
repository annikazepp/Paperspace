package de.fhkl.gatav.ut.paperspace.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import de.fhkl.gatav.ut.paperspace.R;

public class MainGameActivity extends AppCompatActivity {
private GameView gameView;
    private ImageView spaceship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game_activity);

        spaceship = findViewById(R.id.spaceship);

        gameView = new GameView(this, spaceship);
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

}
