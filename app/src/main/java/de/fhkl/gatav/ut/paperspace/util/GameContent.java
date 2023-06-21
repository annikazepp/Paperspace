package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.objects.Asteroid;
import de.fhkl.gatav.ut.paperspace.objects.Drawable;
import de.fhkl.gatav.ut.paperspace.objects.Explosion;
import de.fhkl.gatav.ut.paperspace.objects.Shot;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;
import de.fhkl.gatav.ut.paperspace.objects.Joystick;

/**
 * Enthält Spielinhalt und Logik
 */
public class GameContent implements Drawable {

    private int gameWidth; // in Konstruktor initalisiert
    private int gameHeight;

    /**
     * Treffer Asteroiden
     */
    private int score = 0; // Zählt Treffer


    // Objects
    private SpaceShip spaceShip;
    private ArrayList<Shot> shots;
    private ArrayList<Asteroid> asteroids;
    private Explosion explosion;
    private ArrayList<Explosion> explosions;

    private Joystick joystick;

    Random random = new Random();
    private Context context;

    // SOUND
    private MediaPlayer mLaserShoot;
    SoundPool soundPool = new SoundPool.Builder().setMaxStreams(10).build();
    private int explosionSoundId;


    // Constants
    private final int MAX_ASTEROIDS = 10; // TODO anderer Wert?
    private final float ASTEROIDS_FREQUENCY = 0.5f; // zu 50% entsteht ein Asteroid
    private final float asteroidMinScale = 0.8f; //TODO WERT?
    private final float asteroidMaxScale = 1.0f; //TODO WERT?
    private final float minSpawnDistanceToPlayer = 1.5f; //TODO WERT?
    private final float minSpawnDistanceBetweenAsteroids = 1.5f; //TODO WERT?


    private boolean isShot = false; //TODO kann weg?

    /**
     *Initialisiert Space Objekte
     * @param context
     */
    public GameContent(Context context) {
        this.context = context;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.gameWidth = displayMetrics.widthPixels;
        this.gameHeight = displayMetrics.heightPixels;

        //Sounds
        mLaserShoot = MediaPlayer.create(context, R.raw.lasershoot);
        //mLaserShoot.start();

        // Explosionssound in den Sound Pool laden
        explosionSoundId = soundPool.load(context, R.raw.hitboom, 1);


        asteroids = new ArrayList<>();
        spaceShip = new SpaceShip(gameWidth, gameHeight, context);
        shots = new ArrayList<>();
        explosions = new ArrayList<>();
        joystick = new Joystick(250,850,200,100);
    }

    //Getter-Setter
    public int getHealthSpaceShip(){
        return spaceShip.getHealth();
    }
    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public int getScore() {
        return score;
    }
    //TODO Steuerung SpaceShip


    /**
     * Zeichnet Spielinhalte auf Leinwand
     * @param c Zeichenfläche, auf die zu zeichnen ist
     */
    public void draw(Canvas c) { //TODO

        // Spaceship zeichnen
        spaceShip.draw(c);

        //TODO Draw Shot. Dem Shot muss ein Bewegungsvektor mit festgelegter Länge (speed) übergeben werden
        /**
        Shot shot = new Shot(context, spaceShip.getWidth()/2, spaceShip.getY());
        shots.add(shot);
        isShot = true;
        shot.draw(c);
         */

        // Draw Asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(c);
        }

        for(int i=0; i < explosions.size(); i++){
            c.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 8){
                explosions.remove(i);
            }
        }

        // Draw Joystick
        joystick.draw(c);
    }


    /**
     * aktualisiert Spielinhalt
     * wird regelmäßig aufgerufen, um Bewegungen, Kollisionen usw. zu verarbeiten
     */
    @Override
    public void update() {

        ArrayList<Asteroid> asteroidToRemove = new ArrayList<>();

        for(Shot shot:shots){
            //move shots
            shot.move();

        }
        for(Asteroid asteroid :asteroids){
            //move Asteroids
            asteroid.move();

            //check out of area
            if(asteroid.outOfView()){
                asteroidToRemove.add(asteroid);
            }
        }

        // Überprüfe die Kollision Asteroid - Asteroid
        for(int i = 0; i < asteroids.size(); i++) {
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
        for(Asteroid asteroid : asteroids){
            if(checkSpacshipCollision(spaceShip, asteroid)){
                spaceShip.damage(asteroid.getDamage());
                asteroidToRemove.add(asteroid);
                // Explosion // TODO Explosion hier lassen? oder nur bei SHOT?
                explosion = new Explosion(context, asteroid.getX(), asteroid.getY());
                explosions.add(explosion);
                // Sound
                soundPool.play(explosionSoundId, 30, 30, 1,0,1.0f);
                //TODO "Loch im Blatt"?
            }
        }

        //Kollision Shot - Asteroid
        for(Shot shot : shots){
            for(Asteroid asteroid : asteroids){
                if(checkShotCollision(shot, asteroid)){
                    shots.remove(shot);
                    asteroidToRemove.add(asteroid); //TODO oder damage
                    //Explosion
                    explosion = new Explosion(context, asteroid.getX(), asteroid.getY());
                    explosions.add(explosion);
                    //Punkte
                    score++;
                }
            }
        }

        for(Shot shot: shots){
            if (shot.isobsolete()){
                shots.remove(shot);
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

    /**
     * Überprüft, ob es eine Kollision zwischen Asteroid und Spaceship gibt
     * @param spaceship Player
     * @param asteroid
     * @return Kollision true, wenn distance ist kleiner als kombinierter Radius ist
     */
    public boolean checkSpacshipCollision(SpaceShip spaceship, Asteroid asteroid){
        double distance = Math.sqrt(Math.pow(spaceship.getX() - asteroid.getX(), 2) + Math.pow(spaceship.getY() - asteroid.getY(), 2));
        // Überprüfe, ob die Distanz kleiner ist als die kombinierten Radien von Raumschiff und Asteroid
        return (distance < spaceship.getWidth()/2 + asteroid.getWidthAsteroid()/2);
    }

    /**
     * Überprüft, ob Shot Asteroiden getroffen hat
     * @param shot Schuss Spaceship
     * @param asteroid
     * @return Treffer true, wenn distance ist kleiner als kombinierter Radius ist
     */
    public boolean checkShotCollision(Shot shot, Asteroid asteroid){
        double distance = Math.sqrt(Math.pow(shot.getX() - asteroid.getX(), 2) + Math.pow(shot.getY() - asteroid.getY(), 2));
        //Überprüfe, ob der Shot den Radius des Asteroids trifft
        return (distance < shot.getWidth()/2 + asteroid.getWidthAsteroid()/2);
    }


    /**
     * fügt Asteroiden zum Spiel hinzu, wenn MAX_ASTEROIDS größer ist, als aktuelle Anzahl
     */
    private void addAsteroids() {
        if (MAX_ASTEROIDS > asteroids.size()) {
            // Wahrscheinlichkeit, dass Asteroiden erzeugt werden //TODO SINNVOLL?
            if (Math.random() > ASTEROIDS_FREQUENCY) {
             return;
             }

            for (int i = 0; i < MAX_ASTEROIDS - asteroids.size(); i++) {
                float scale = (float) Math.random() * (asteroidMaxScale - asteroidMinScale) + asteroidMinScale;
                float spawnOffset = scale * 0.5f;

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




                boolean positionOk = true;

                //TODO wenn check Distanc wenn player sich bewegen kann
                /*
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



                if (!positionOk) {
                    continue; // Invalid spawn position -> try again next time
                }



                // Calculate destination position
                float destX, destY;

                // Spawn on the top or bottom side
                if (side == 0 || side == 2) {
                    destX = (float) (Math.random() * getGameWidth()); //TODO anpassen?
                    destY = -spawnOffset;
                }
                // Spawn on the right or left side
                else{
                    destX = getGameWidth() + spawnOffset;
                    destY = (float) (Math.random() * getGameHeight());
                }

                destY-= spawnY;
                destX-= spawnX;


                Asteroid asteroid = new Asteroid(gameHeight, gameWidth, context);
                asteroid.setPosition(spawnX, spawnY);
                asteroid.setDestination(destX, destY);
                asteroids.add(asteroid);
            }
        }
    }


    public boolean isGameOver() {
        return getHealthSpaceShip() == 0;
    }


}




