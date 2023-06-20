package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Random;

import de.fhkl.gatav.ut.paperspace.objects.Asteroid;
import de.fhkl.gatav.ut.paperspace.objects.Drawable;
import de.fhkl.gatav.ut.paperspace.objects.Shot;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;


public class GameContent implements Drawable {

    private int gameWidth; // in Konstruktor initalisiert
    private int gameHeight;
    Random random = new Random();

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }



    // Objects TODO
    private SpaceShip spaceShip;

    private ArrayList<Shot> shots;

    private ArrayList<Asteroid> asteroids;

    private Context context;

    // Constants
    private final int MAX_ASTEROIDS = 10; // TODO anderer Wert?
    private final float ASTEROIDS_FREQUENCY = 0.5f; // zu 50% entsteht ein Asteroid //TODO UNNÖTIG?
    private final float asteroidMinScale = 0.8f; //TODO WERT?
    private final float asteroidMaxScale = 1.0f; //TODO WERT?
    private final float minSpawnDistanceToPlayer = 1.5f; //TODO WERT?
    private final float minSpawnDistanceBetweenAsteroids = 1.5f; //TODO WERT?

    public int getHealthSpaceShip(){
        return spaceShip.getHealth();
    }

    private boolean isShot =false;
    private boolean gameOver = false;

    public boolean isGameOver() {
        return gameOver;
    }

    //Constructor
    public GameContent(Context context) {
        this.context = context;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.gameWidth = displayMetrics.widthPixels;
        this.gameHeight = displayMetrics.heightPixels;


        asteroids = new ArrayList<>();
        spaceShip = new SpaceShip(gameWidth,gameHeight,context);
        shots = new ArrayList<>();


    }

    // TODO Steuerung SpaceShip



    //Spielinhalt Zeichnen
    public void draw(Canvas c) { //TODO
        // Spaceship zeichnen
        spaceShip.draw(c);

        // TODO Draw Shot. Denkt daran, dass dem Shot einen bewegungsvektor mit festgelegter länge (speed) übergeben werden muss

        /*
        Shot shot = new Shot(context, spaceShip.getWidth()/2, spaceShip.getY());
        shots.add(shot);
        isShot = true;
        shot.draw(c);
*/

        // Draw Asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(c);
        }

    }

    @Override
    public void update() {
        //TODO GameOver hier gut?
        if (getHealthSpaceShip() == 0){
            gameOver = true;
        }

        if(getHealthSpaceShip() != 0) {

            ArrayList<Asteroid> asteroidToRemove = new ArrayList<>();
        for(Shot shot:shots){
            //move shots
            shot.move();

        }
            for (Asteroid asteroid : asteroids) {
                //move Asteroids
                asteroid.move();

                //check out of area
                if (asteroid.outOfView()) {
                    asteroidToRemove.add(asteroid);
                }

                //TODO abgeschossene
                if (asteroid.isShot()) {
                    asteroidToRemove.add(asteroid);
                }
            }

            /**
             // getroffene Asteroiden entfernen
             asteroids.removeAll(asteroidToRemove);
             // Liste leeren
             asteroidToRemove.clear();
             */

            // Überprüfe die Kollision Asteroid - Asteroid
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid asteroid = asteroids.get(i);

                for (int j = 0; j < asteroids.size(); j++) {
                    if (j != i) {
                        Asteroid otherAsteroid = asteroids.get(j);

                        if (asteroid.collidesWith(otherAsteroid)) {
                            asteroid.bounceOff(otherAsteroid);
                        }
                    }
                }
            }

            // Kollision Asteroid - Spaceship
            for (Asteroid asteroid : asteroids) {
                if (checkCollision(spaceShip, asteroid)) {
                    spaceShip.damage(asteroid.getDamage());
                    asteroidToRemove.add(asteroid);
                    if(spaceShip.getHealth() == 0){
                        gameOver = true;
                    }
                    //TODO Explosion? "Loch im Blatt"?
                }
            }

        //Kollision Shot - Asteroid
        for(Shot shot : shots){
            for(Asteroid asteroid : asteroids){
                if(checkShotCollision(shot, asteroid)){
                    //TODO remove Shot and give asteroid damage(add to remove)
                }
            }
        }

            // getroffene Asteroiden entfernen
            asteroids.removeAll(asteroidToRemove);
            // Liste leeren
            asteroidToRemove.clear();

            // Neue Asteroiden hinzufügen
            addAsteroids();


            // TODO überhaupt benötigt? Kann eig wahrscheinlich weg
            for (Asteroid asteroid : asteroids) {
                asteroid.update();
            }
        }
    }

    public boolean checkCollision(SpaceShip spaceship, Asteroid asteroid){
        double distance = Math.sqrt(Math.pow(spaceship.getX() - asteroid.getX(), 2) + Math.pow(spaceship.getY() - asteroid.getY(), 2));
        // Überprüfe, ob die Distanz kleiner ist als die kombinierten Radien von Raumschiff und Asteroid
        if (distance < spaceship.getWidth()/2 + asteroid.getWidthAsteroid()/2) {
            return true; // Kollision erfolgt
        } else {
            return false; // Keine Kollision
        }
    }

    public boolean checkShotCollision(Shot shot, Asteroid asteroid){
        double distance = Math.sqrt(Math.pow(shot.getX() - asteroid.getX(), 2) + Math.pow(shot.getY() - asteroid.getY(), 2));
        //Überprüfe, ob der Shot den Radius des Asteroids trifft
        if (distance < shot.getWidth()/2 + asteroid.getWidthAsteroid()/2) {
            return true; // Kollision erfolgt
        } else {
            return false; // Keine Kollision
        }
    }


    // Asteroiden hinzufügen
    private void addAsteroids() {


            if (MAX_ASTEROIDS > asteroids.size()) {
                /**
                if (Math.random() > ASTEROIDS_FREQUENCY) {
                    return;
                }

                for (int i = 0; i < MAX_ASTEROIDS - asteroids.size(); i++) {
                    Asteroid asteroid = new Asteroid(gameWidth, gameHeight, context);
                    asteroids.add(asteroid);
                }
                 */
            }


            for (int i = 0; i < MAX_ASTEROIDS - asteroids.size(); i++) {

                float scale = (float) Math.random() * (asteroidMaxScale - asteroidMinScale) + asteroidMinScale;
                float spawnOffset = scale * 0.5f;

                int sourceDirection = Math.random() < 0.5 ? -1 : 1;  // -1 for left, 1 for right
                int destDirection = -sourceDirection;  // Opposite direction of the source

                // Calculate spawn position
                float spawnX, spawnY;

                // Determine the side of the screen where the asteroid will spawn
                int side = (int) (Math.random() * 4); // 0: top, 1: right, 2: bottom, 3: left


                // Spawn on the top side
                if (side == 0) {
                    spawnX = (float) (Math.random() * getGameWidth());
                    spawnY = -spawnOffset;
                }
                // Spawn on the right side
                else if (side == 1) {
                    spawnX = getGameWidth() + spawnOffset;
                    spawnY = (float) (Math.random() * getGameHeight());
                }
                // Spawn on the bottom side
                else if (side == 2) {
                    spawnX = (float) (Math.random() * getGameWidth());
                    spawnY = getGameHeight() + spawnOffset;
                }
                // Spawn on the left side
                else {
                    spawnX = -spawnOffset;
                    spawnY = (float) (Math.random() * getGameHeight());
                }


                // Randomly determine if the obstacle will spawn on the top or bottom boundary
                //boolean spawnOnTop = Math.random() < 0.5;

                //if (spawnOnTop) {
                   // spawnY = 50;
                   // spawnX = (float) ((sourceDirection == -1) ? getGameWidth() * Math.random() : 1.0f * Math.random());
                //} else {
                    //spawnY = getGameHeight();
                   //spawnX = (float) ((sourceDirection == -1) ? getGameWidth() * Math.random() : 1.0f * Math.random());
                //}

                boolean positionOk = true;
/**
                // check distance to player
                float minPlayerDistance = 0.5f * scale + 0.5f * spaceShip.getWidth() + minSpawnDistanceToPlayer;
                if (Math.abs(spawnX - spaceShip.getX()) < minPlayerDistance &&
                        Math.abs(spawnY - spaceShip.getY()) < minPlayerDistance)
                    positionOk = false;    // Distance to player too small -> invalid position
*/
                // Check distance to other asteroids
                for (Asteroid asteroid : asteroids) {

                    float minDistance = 0.5f * scale + 0.5f * asteroid.getWidthAsteroid() + minSpawnDistanceBetweenAsteroids;
                    if (Math.abs(spawnX - asteroid.getX()) < minDistance &&
                            Math.abs(spawnY - asteroid.getY()) < minDistance) {
                        positionOk = false;    // Distance too small -> invalid position
                        break;
                    }
                }

                //if (!positionOk) {
                //    continue; // Invalid spawn position -> try again next time
               // }


                /**
                // Calculate destination position
                float destX, destY;

                if (spawnOnTop) {
                    destY = getGameHeight();
                    destX = (float) ((destDirection == -1) ? getGameWidth() * Math.random() : 1.0 * Math.random());
                } else {
                    destY = 0;
                    destX = (float) ((destDirection == -1) ? getGameWidth() * Math.random() : 1.0 * Math.random());
                }
                 */


                Asteroid asteroid = new Asteroid(gameWidth, gameHeight, context);
                asteroid.setPosition(spawnX, spawnY);
                asteroids.add(asteroid);
            }



            }
        }




