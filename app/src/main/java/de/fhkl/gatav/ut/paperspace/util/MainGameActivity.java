package de.fhkl.gatav.ut.paperspace.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.objects.Asteroid;

public class MainGameActivity extends AppCompatActivity {
private GameView gameView;
    private ImageView spaceshipView;

    private ArrayList<Asteroid> asteroids;
    private ArrayList<ImageView> asteroidViews;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game_activity);

        spaceshipView = findViewById(R.id.spaceship);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        asteroids = new ArrayList<>();
        asteroidViews = new ArrayList<>();

        //Anzahl der gewünschten Asteroiden
        int anzAsteroids = 10;

        for(int i = 0; i <anzAsteroids; i++){
            Asteroid asteroid = new Asteroid(screenWidth, screenHeight);
            asteroids.add(asteroid);

            ImageView asteroidView = new ImageView(this);
            asteroidView.setImageResource(asteroids.get(i).getImageResource());
            asteroidViews.add(asteroidView);

            // Füge den ImageView zur Layout-Ansicht hinzu
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            addContentView(asteroidView, params);
        }



        // Aktualisiere die Bewegung der Asteroiden in regelmäßigen Abständen
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(int i= 0; i<asteroids.size(); i++) {
                    Asteroid asteroid = asteroids.get(i);
                    asteroid.update();

                    // Aktualisiere die Position des ImageView entsprechend der Asteroidenposition
                    ImageView asteroidView = asteroidViews.get(i);
                    asteroidView.setX(asteroid.getX());
                    asteroidView.setY(asteroid.getY());

                    // Überprüfe Kollisionen mit anderen Asteroiden
                    for (int j = 0; j < asteroids.size(); j++) {
                        if (j != i) {
                            Asteroid otherAsteroid = asteroids.get(j);
                            if (asteroid.collidesWith(otherAsteroid)) {
                                asteroid.bounceOff(otherAsteroid);
                            }
                        }
                    }
                }
                handler.postDelayed(this, 16); // Aktualisiere alle 16 Millisekunden (ca. 60 FPS)
            }
        });

        gameView = new GameView(this, spaceshipView);

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
