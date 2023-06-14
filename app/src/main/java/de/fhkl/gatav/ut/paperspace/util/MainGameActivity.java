package de.fhkl.gatav.ut.paperspace.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainGameActivity extends AppCompatActivity {
private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
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
