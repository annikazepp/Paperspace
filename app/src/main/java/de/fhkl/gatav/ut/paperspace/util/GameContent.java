package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.List;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.objects.Explosion;
import de.fhkl.gatav.ut.paperspace.objects.Hole;
import de.fhkl.gatav.ut.paperspace.objects.Asteroid;
import de.fhkl.gatav.ut.paperspace.objects.Circle;
import de.fhkl.gatav.ut.paperspace.objects.Shot;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;

public class GameContent {
    // Constants
    private static final int MAX_ASTEROIDS = 10; //TODO WERT?
    private static final int FULL_HEALTH = 5; // LEBEN SPACESHIP

    // Objects
    private final SpaceShip player;
    private final Joystick steuerungJoystick;
    private final Joystick directionJoystick;
    private final float ASTEROIDS_FREQUENCY = 0.5f; // zu 50% entsteht ein Asteroid
    private final float minSpawnDistanceBetweenAsteroids = 1.5f; //TODO WERT?
    private final float HOLE_FREQUENCY = 0.3f;

    //counts the fps for shoot cooldown
    int fps_count = 0;
    SoundPool soundPool = new SoundPool.Builder().setMaxStreams(20).build(); // TODO MAXStreams Anpassen

    // Treffer Asteroiden
    private int score = 0;
    private List<Asteroid> asteroidList = new ArrayList<Asteroid>();
    private List<Shot> shotList = new ArrayList<Shot>();
    private Explosion explosion;
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private Hole hole;
    private ArrayList<Hole> holes = new ArrayList<>();

    ArrayList<Asteroid> asteroidToRemove = new ArrayList<>();
    ArrayList<Shot> shotsToRemove = new ArrayList<>();
    private Context context;
    //SOUND
    private MediaPlayer mExplosion;
    private MediaPlayer mLaserShoot;
    private int explosionSoundId;
    private int shootSoundId;
    private int health = FULL_HEALTH;


    public GameContent(Context context, Joystick steuerungJoystick, Joystick directionJoystick) {
        this.context = context;

        // Joysticks
        this.steuerungJoystick = steuerungJoystick;
        this.directionJoystick = directionJoystick;

        // Objekte
        player = new SpaceShip(context, steuerungJoystick, directionJoystick, 2 * 500, 500);


        //Sounds
        mLaserShoot = MediaPlayer.create(context, R.raw.lasershoot);
        shootSoundId = soundPool.load(context, R.raw.lasershoot, 1);

        mExplosion = MediaPlayer.create(context, R.raw.hitboom);
        // Explosionssound in den Sound Pool laden
        explosionSoundId = soundPool.load(context, R.raw.hitboom, 1);

    }

    public int getScore() {
        return score;
    }

    /**
     * Schaden durch treffer eines Asteroiden wird von Leben abgezogen
     *
     * @param dmg Schaden des Asteroiden
     */
    public void damage(double dmg) {
        health -= dmg;
    }

    // DRAW OBJECTS
    public void draw(Canvas canvas) {
        for(Hole hole : holes) {
            hole.draw(canvas);
        }

        player.draw(canvas);

        for (Asteroid asteroid : asteroidList) {
            asteroid.draw(canvas);
        }

        for (Shot shot : shotList) {
            shot.draw(canvas);
        }
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), (float) explosions.get(i).eX, (float) explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 8) {
                explosions.remove(i);
            }
        }
    }

    public void update() {
        // update stoppen, wenn Player tot
        if (getHealth() <= 0) {
            return;
        }

        // Update game state
        steuerungJoystick.update();
        directionJoystick.update();
        player.update();

        fps_count++;


        if (directionJoystick.getActuatorX() != 0 || directionJoystick.getActuatorY() != 0) {
            if (fps_count > 20) {
                fps_count = 0;
                shoot(directionJoystick);
                //mLaserShoot.start();
                //soundPool.play(shootSoundId, 30, 30, 1, 0, 1.0f);

            }
        }

        if (MAX_ASTEROIDS > asteroidList.size()) {
            addAsteroid();
        }

        for (Asteroid asteroid : asteroidList) {
            asteroid.update();
            if (asteroid.isOutOfView()) {
                asteroidToRemove.add(asteroid);
            }
        }

        for (Shot shot : shotList) {
            shot.update();
            if (shot.isOutOfView()) {
                shotsToRemove.add(shot);
            }
        }

        // getroffene Asteroiden/Shots entfernen
        asteroidList.removeAll(asteroidToRemove);
        shotList.removeAll(shotsToRemove);
        // Listen leeren
        asteroidToRemove.clear();
        shotsToRemove.clear();

        // CHECK KOLLISION
        for (Asteroid asteroid : asteroidList) {
            checkCollision(asteroid, player);
        }

        for(Asteroid asteroid : asteroidList) {
            for (Asteroid otherAsteroid : asteroidList) {
                if (asteroid != otherAsteroid) {
                    checkCollision(asteroid, otherAsteroid);
                }
            }
        }

        for(Asteroid asteroid : asteroidList) {
            for (Shot shot : shotList) {
                checkCollision(asteroid, shot);
            }
        }

        for(Asteroid asteroid : asteroidList){
            for(Hole hole : holes){
                checkCollision(asteroid,hole);
            }
        }

    }

    private void checkCollision(Asteroid asteroid, Circle obj2) {
        if (!Circle.isColliding(asteroid, obj2)) {  // Wenn keine Kollision, wird Ausführung der Methode abgebrochen
            return;
        }
        if (obj2 instanceof SpaceShip) {
                damage(asteroid.getDamage());
                asteroidToRemove.add(asteroid);
                // Explosion
                startExplosion(obj2);
        }

        if(obj2 instanceof Asteroid){
            asteroid.bounceOff((Asteroid) obj2);
        }

        if(obj2 instanceof Shot){
            shotsToRemove.add((Shot) obj2);
            asteroidToRemove.add(asteroid);
            // Explosion
            startExplosion(asteroid);
            // Loch
            addHole(asteroid.getPositionX(), asteroid.getPositionY());
            // Score
            score++;
        }

        if(obj2 instanceof Hole){
            asteroidToRemove.add(asteroid);
        }
    }

    private void startExplosion(Circle obj) {
        // TODO POSITION MUSS ANGEPASST WERDEN
        explosion = new Explosion(context, obj.getPositionX()- obj.getRadius(), obj.getPositionY()-obj.getRadius());
        explosions.add(explosion);
       // mExplosion.start();
        // Sound
        // TODO
       soundPool.play(explosionSoundId, 30, 30, 1, 0, 1.0f);
    }


    public void shoot(Joystick joystickRotation) {
        double xDirection = joystickRotation.getActuatorX();
        double yDirection = joystickRotation.getActuatorY();

        Shot shot = new Shot(context, player, xDirection, yDirection, directionJoystick);
        shotList.add(shot);
    }

    /**
     Zu 30% wird ein Loch hinzugefügt
     @param x x-Koordinate des Asteroiden
     @param y y-Koordinate des Asteroiden
     */
    private void addHole(double x, double y) {
        if (Math.random() <= HOLE_FREQUENCY) {
            hole = new Hole(context, x,y);
            holes.add(hole);
        }
    }

    private void addAsteroid() {
        // Wahrscheinlichkeit, dass Asteroiden erzeugt werden
        if (Math.random() > ASTEROIDS_FREQUENCY) {
            return;
        }

        for (int i = 0; i < MAX_ASTEROIDS - asteroidList.size(); i++) {
            double spawnOffset = 1.0; //TODO WERT?

            // Calculate spawn position
            double spawnX, spawnY;
            // Calculate destination position
            double destX, destY;

            // Determine the side of the screen where the asteroid will spawn
            int side = (int) (Math.random() * 4); // 0: top, 1: right, 2: bottom, 3: left


            // Spawn on the top side
            if (side == 0) {
                spawnX = (Math.random() * GameView.screenWidth);
                spawnY = -spawnOffset;
                destX = (Math.random() * GameView.screenWidth); //TODO anpassen?
                destY = GameView.screenHeight + spawnOffset;
            }
            // Spawn on the right side
            else if (side == 1) {
                spawnX = GameView.screenWidth + spawnOffset;
                spawnY = (Math.random() * GameView.screenHeight);
                destX = -spawnOffset; //TODO anpassen?
                destY = (Math.random() * GameView.screenHeight);
            }
            // Spawn on the bottom side
            else if (side == 2) {
                spawnX = (Math.random() * GameView.screenWidth);
                spawnY = GameView.screenHeight + spawnOffset;
                destX = (Math.random() * GameView.screenWidth);
                destY = -spawnOffset;
            }
            // Spawn on the left side
            else {
                spawnX = -spawnOffset;
                spawnY = (Math.random() * GameView.screenHeight);
                destX = GameView.screenWidth + spawnOffset;
                destY = (Math.random() * GameView.screenHeight);
            }

            boolean positionOk = true;

            //TODO wenn check Distanc wenn player sich bewegen kann
            // check distance to player
            double minPlayerDistance = 0.5f * spawnOffset + 0.5f * player.getRadius() + 5.0;
            if (Math.abs(spawnX - player.getPositionX()) < minPlayerDistance &&
                    Math.abs(spawnY - player.getPositionY()) < minPlayerDistance)
                positionOk = false;    // Distance to player too small -> invalid position


            // Check distance to other asteroids
            for (Asteroid asteroid : asteroidList) {
                double minDistance = 0.5f * asteroid.getRadius() + 0.5f * asteroid.getRadius() + minSpawnDistanceBetweenAsteroids;
                if (Math.abs(spawnX - asteroid.getPositionX()) < minDistance &&
                        Math.abs(spawnY - asteroid.getPositionY()) < minDistance) {
                    positionOk = false;    // Distance too small -> invalid position
                    break;
                }
            }


            if (!positionOk) {
                continue; // Invalid spawn position -> try again next time
            }


            destY -= spawnY;
            destX -= spawnX;


            Asteroid asteroid = new Asteroid(context, spawnX, spawnY);
            asteroid.setDestination(destX, destY);
            asteroidList.add(asteroid);
        }
    }

    public int getHealth() {
        return health;
    }
}


