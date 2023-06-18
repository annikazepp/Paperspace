package de.fhkl.gatav.ut.paperspace.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainGameActivity extends AppCompatActivity {
    private SpaceView spaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spaceView = new SpaceView(this);
        setContentView(spaceView);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }
    /**
     @Override
     protected void onResume() {
     super.onResume();
     spaceView.resume();
     }

     @Override
     protected void onPause() {
     super.onPause();
     spaceView.pause();
     }
     */
}
