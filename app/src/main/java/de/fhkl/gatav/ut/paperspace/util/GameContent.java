package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import de.fhkl.gatav.ut.paperspace.objects.Asteroid;
import de.fhkl.gatav.ut.paperspace.objects.Drawable;
import de.fhkl.gatav.ut.paperspace.objects.Shot;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;


public class GameContent implements Drawable {

    private int gameWidth; // in Konstruktor initalisiert
    private int gameHeight;

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }



    // Objects TODO
    private SpaceShip spaceShip;
    //private ArrayList<Shot> shots;
    private ArrayList<Asteroid> asteroids;

    private Context context;

    // Constants
    private int MAX_ASTEROIDS = 10; // TODO anderer Wert?
    private float ASTEROIDS_FREQUENCY = 0.5f; // zu 50% entsteht ein Asteroid

    public int getHealthSpaceShip(){
        return spaceShip.getHealth();
    }

    private boolean spaceshipCollied = false;
    private boolean isShot =false;

    //Constructor
    public GameContent(Context context) {
        this.context = context;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.gameWidth = displayMetrics.widthPixels;
        this.gameHeight = displayMetrics.heightPixels;


        asteroids = new ArrayList<>();
        spaceShip = new SpaceShip(gameWidth,gameHeight,context);
        //shots = new ArrayList<>();


    }

    // TODO Steuerung SpaceShip



    //Spielinhalt Zeichnen
    public void draw(Canvas c) { //TODO
        // Spaceship zeichnen
        spaceShip.draw(c);

        // Draw Shot
        /**
        Shot shot = new Shot(context, spaceShip.getWidth()/2, spaceShip.getY());
        shots.add(shot);
        isShot = true;
        shot.draw(c);
         */

        // Draw Asteroids
        addAsteroids();
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(c);
        }

    }

    @Override
    public void update() {

        ArrayList<Asteroid> asteroidToRemove = new ArrayList<>();

        // move asteroids
        for(Asteroid asteroid :asteroids){
            asteroid.move();

            //TODO abgeschossene
            if(asteroid.isShot()) {
                asteroidToRemove.add(asteroid);
            }
        }

        // Überprüfe die Kollision mit anderen Asteroiden
        for(int i= 0; i<asteroids.size(); i++) {
            Asteroid asteroid = asteroids.get(i);
            for (int j = 0; j < asteroids.size(); j++) {
                if (j != i) {
                    Asteroid otherAsteroid = asteroids.get(j);
                    if (asteroid.collidesWith(otherAsteroid)) {
                        asteroid.bounceOff(otherAsteroid);
                    }
                }
            }

            //Überprüfe KOLLISION MIT ASTEROID UND SPACESHIP
            if(checkCollision(spaceShip, asteroid)){
                    spaceShip.damage(asteroid.getDamage());
                    asteroidToRemove.add(asteroid);
                    //TODO Explosion? "Loch im Blatt"?
            }
        }

        // getroffene Asteroiden entfernen
        asteroids.removeAll(asteroidToRemove);


        //
        for (Asteroid asteroid : asteroids) {
            asteroid.update();
        }

    }

    public boolean checkCollision(SpaceShip spaceship, Asteroid asteroid){
        double distance = Math.sqrt(Math.pow(spaceship.getX() - asteroid.getX(), 2) + Math.pow(spaceship.getY() - asteroid.getY(), 2));
        // Überprüfe, ob die Distanz kleiner ist als die kombinierten Radien von Raumschiff und Asteroid
        if (distance < spaceship.getWidth()/2 + asteroid.getWidth()/2) {
            return true; // Kollision erfolgt
        } else {
            return false; // Keine Kollision
        }
    }



    // Asteroiden hinzufügen
    private void addAsteroids() {
        if (MAX_ASTEROIDS > asteroids.size()) {
            if (Math.random() > ASTEROIDS_FREQUENCY) {
                return;
            }

            for (int i = 0; i < MAX_ASTEROIDS - asteroids.size(); i++) {
                Asteroid asteroid = new Asteroid(gameWidth, gameHeight, context);
                asteroids.add(asteroid);
            }
        }

    }

}
