package de.fhkl.gatav.ut.paperspace.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainGameActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SpaceGameView(this));
    }


}
