package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.List;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.objects.BigAsteroid;
import de.fhkl.gatav.ut.paperspace.objects.DarkAsteroid;
import de.fhkl.gatav.ut.paperspace.objects.Explosion;
import de.fhkl.gatav.ut.paperspace.objects.Hole;
import de.fhkl.gatav.ut.paperspace.objects.Asteroid;
import de.fhkl.gatav.ut.paperspace.objects.Circle;
import de.fhkl.gatav.ut.paperspace.objects.Shot;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;
import de.fhkl.gatav.ut.paperspace.powerUps.PowerUps;

public class GameContent {

    // CONSTANTS
    private static final int FULL_HEALTH = 5; // LEBEN SPACESHIP
        private int health = FULL_HEALTH;

        // Asteroid
    private static final int MAX_ASTEROIDS = 10; //TODO WERT?
    private static final int MAX_DARK_ASTEROIDS = 3; // TODO WERT?

    private static final int MAX_BIG_ASTEROIDS = 2; //TODO WERT?
    private final float ASTEROIDS_FREQUENCY = 0.5f; // zu 50% entsteht ein Asteroid
    private final float minSpawnDistanceBetweenAsteroids = 1.5f; //TODO WERT?

        // Power Up
    private static final float POWERUP_FREQUENCY = 0.5f; // TODO WERT

    // OBJECTS
    private final SpaceShip player;

        // Joysticks
    private final Joystick steuerungJoystick;
    private final Joystick directionJoystick;

        // Asteroiden Liste
    private List<Asteroid> asteroidList = new ArrayList<>();
    private List<DarkAsteroid> darkAsteroidsList = new ArrayList<>();

    private List<BigAsteroid> bigAsteroidList = new ArrayList<>();

        // Shot Liste
    private List<Shot> shotList = new ArrayList<>();

        // Extras
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private ArrayList<Hole> holes = new ArrayList<>();

        // Power ups
    private ArrayList<PowerUps> powerUpsList = new ArrayList<>();

    private boolean isGhost = false;
    private boolean isStrongShot = false;
    private boolean isx2Score = false;
    public boolean getIsGhost(){
        return isGhost;
    }
    public boolean getIsStrongShot(){
        return isStrongShot;
    }
    public boolean getIsx2Score(){
        return isx2Score;
    }


        // Objects to Remove
    ArrayList<Circle> objectsToRemove = new ArrayList<>();



    //counts the fps for shoot cooldown
    int fps_count = 0;


    //SOUND
    SoundPool soundPool = new SoundPool.Builder().setMaxStreams(100).build(); // TODO MAXStreams Anpassen
    private MediaPlayer mLoch;
    private int explosionSoundId;
    private int crashSoundId;


    private Context context;

    // Treffer Asteroiden
    private int score = 0;


    public GameContent(Context context, Joystick steuerungJoystick, Joystick directionJoystick) {
        this.context = context;

        // Joysticks
        this.steuerungJoystick = steuerungJoystick;
        this.directionJoystick = directionJoystick;

        // Objekte
        player = new SpaceShip(context, steuerungJoystick, directionJoystick, 2 * 500, 500);

        // Sounds
        crashSoundId = soundPool.load(context, R.raw.crash, 1);
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

        for (DarkAsteroid darkAsteroid : darkAsteroidsList){
            darkAsteroid.draw(canvas);
        }
        for (Asteroid asteroid : asteroidList) {
            asteroid.draw(canvas);
        }

        for (PowerUps powerUp : powerUpsList){
            powerUp.draw(canvas);
        }
        for (BigAsteroid bigAsteroid : bigAsteroidList){
            bigAsteroid.draw(canvas);
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
        // STOPPE UPDATE WENN HEALTH = 0
        if (getHealth() <= 0) {
            return;
        }

        // Update game state
        steuerungJoystick.update();
        directionJoystick.update();
        player.update();

        // WENN JOYSTICK SICH BEWEGT -> SHOOT
        fps_count++;
        if (directionJoystick.getActuatorX() != 0 || directionJoystick.getActuatorY() != 0) {
            if (fps_count > 20) {
                fps_count = 0;
                shoot(directionJoystick);
            }
        }

        // ADD ASTEROIDS
        addAsteroid();

        for (Asteroid asteroid : asteroidList) {
            asteroid.update();
            if (asteroid.isOutOfView()) {
                objectsToRemove.add(asteroid);
            }
        }

        //TODO Asteroids werden nicht entfernt wenn sie den Bildschirm verlassen
        for(DarkAsteroid darkAsteroid : darkAsteroidsList){
            darkAsteroid.update();
        }

        for (BigAsteroid bigAsteroid : bigAsteroidList){
            bigAsteroid.update();

        }

        for (Shot shot : shotList) {
            shot.update();
            if (shot.isOutOfView()) {
                objectsToRemove.add(shot);
            }
        }

        // getroffene Asteroiden/Shots entfernen
        asteroidList.removeAll(objectsToRemove);
        darkAsteroidsList.removeAll(objectsToRemove);
        bigAsteroidList.removeAll(objectsToRemove);
        shotList.removeAll(objectsToRemove);
        powerUpsList.removeAll(objectsToRemove);
        // Listen leeren
        objectsToRemove.clear();

        // CHECK KOLLISION
        // ASTEROID UND ...
        for (Asteroid asteroid : asteroidList) {
            // ASTEROID - PLAYER
            checkCollision(asteroid, player);
            // ASTEROID - ASTEROID
            for (Asteroid otherAsteroid : asteroidList) {
                if (asteroid != otherAsteroid) {
                    checkCollision(asteroid, otherAsteroid);
                }
            }
            // ASTEROID - DARK ASTEROID
            for(DarkAsteroid darkAsteroid : darkAsteroidsList){
                checkCollision(asteroid, darkAsteroid);
            }
            //ASTEROID - BIG ASTEROID
            for(BigAsteroid bigAsteroid : bigAsteroidList){
                checkCollision(asteroid, bigAsteroid);
            }
            // ASTEROID - SHOT
            for (Shot shot : shotList) {
                checkCollision(asteroid, shot);
            }
        }

        // DARK ASTEROID UND ...
        for(DarkAsteroid darkAsteroid : darkAsteroidsList){
            //PLAYER
            checkCollision(darkAsteroid,player);
            //DARK ASTEROID
            for (DarkAsteroid otherAsteroid : darkAsteroidsList) {
                if (darkAsteroid != otherAsteroid) {
                    checkCollision(darkAsteroid, otherAsteroid);
                }
            }
            //BIG ASTEROID
            for(BigAsteroid bigAsteroid : bigAsteroidList){
                checkCollision(darkAsteroid,bigAsteroid);
            }
            //SHOT
            for (Shot shot : shotList) {
                checkCollision(darkAsteroid, shot);
            }
        }

        //BIG ASTEROID UND ...
        for(BigAsteroid bigAsteroid : bigAsteroidList){
            //PLAYER
            checkCollision(bigAsteroid,player);
            //BIG ASTEROID
            for (BigAsteroid otherAsteroid : bigAsteroidList) {
                if (bigAsteroid != otherAsteroid) {
                    checkCollision(bigAsteroid, otherAsteroid);
                }
            }
            //SHOT
            for (Shot shot : shotList) {
                checkCollision(bigAsteroid, shot);
            }
        }

        // SPIELER UND HOLE
        for(Hole hole : holes){
            checkCollision(player,hole);
        }

        // SPIELER UND POWERUP
        for(PowerUps powerUp : powerUpsList){
            checkCollision(player,powerUp);
        }


        // POWER UPS nach gewissen Zeit entfernen
            // Wenn nicht eingesammelt
        removePowerUp();
            // Funktion entfernen
        //resetPowerUp();

    }

    private void removePowerUp(){
        long currentTime = System.currentTimeMillis();

        for(PowerUps powerUp : powerUpsList){
            long elapsedTime = currentTime - powerUp.getCreationTime();
            if(elapsedTime >= powerUp.getExpirationTime()){
                objectsToRemove.add(powerUp);
            }
        }
    }

    private void checkCollision(Circle obj1, Circle obj2) {
        if (!Circle.isColliding(obj1, obj2)) {  // Wenn keine Kollision, wird Ausführung der Methode abgebrochen
            return;
        }
        // ASTEROID UND ...
        if(obj1 instanceof Asteroid && !(obj1 instanceof DarkAsteroid)) {
            Asteroid asteroid = (Asteroid) obj1;
            // .. SPACESHIP
            if (obj2 instanceof SpaceShip) {
                // TODO BILD ÄNDERN WENN GHOST?
                if(!isGhost) {
                    damage(asteroid.getDamage());
                    objectsToRemove.add(asteroid);
                    // EXPLOSION
                    startExplosion(obj2);
                    // SOUND
                    if (MainActivity.isSoundOn) {
                        if (crashSoundId != 0) {
                            soundPool.play(crashSoundId, 1, 1, 0, 0, 1);
                        }
                    }
                }
            }

            // .. ASTEROID
            if (obj2 instanceof Asteroid || obj2 instanceof DarkAsteroid || obj2 instanceof BigAsteroid) {
                asteroid.bounceOff((Asteroid) obj2);
            }

            // .. SHOT
            if (obj2 instanceof Shot) {
                if(!isStrongShot) {
                    objectsToRemove.add((Shot) obj2);
                }
                objectsToRemove.add(asteroid);
                // Explosion
                startExplosion(asteroid);
                if(MainActivity.isSoundOn) { //TODO WENN SOUND AUS AUCH HIER AUS?
                    if (explosionSoundId != 0) {
                        soundPool.play(explosionSoundId, 1, 1, 0, 0, 1);
                    }
                }
                // Score
                if(!isx2Score) {
                    score++;
                }else{
                    score +=2;
                }
                // Power Up
                addPowerUp(asteroid.getPositionX(), asteroid.getPositionY());
            }
        }
        // SPACESHIP UND ...
        if(obj1 instanceof SpaceShip){
            // ... LOCH
            if (obj2 instanceof Hole) {
                if(!isGhost) {
                    health = 0;
                }
            }
            // ... POWER UP
            if(obj2 instanceof PowerUps){
                PowerUps.PowerUp selectedPowerUp = ((PowerUps) obj2).getPowerUp();
                switch (selectedPowerUp){
                    case HEART:
                        health++;
                        break;
                    case STRONG_SHOT:
                        if(!isStrongShot) { // WENN NOCH NICHT AKTIVIERT
                            isStrongShot = true;
                            startDuration(System.currentTimeMillis(), PowerUps.PowerUp.STRONG_SHOT);
                        }
                        break;
                    case GHOST:
                        if(!isGhost) {
                            isGhost = true;
                            startDuration(System.currentTimeMillis(), PowerUps.PowerUp.GHOST);
                        }
                        break;
                    case X2SCORE:
                        if(!isx2Score) {
                            isx2Score = true;
                            startDuration(System.currentTimeMillis(), PowerUps.PowerUp.X2SCORE);
                        }
                }
                objectsToRemove.add((PowerUps) obj2);
            }
        }

        // DARK ASTEROID UND..
        if(obj1 instanceof DarkAsteroid){
            DarkAsteroid darkAsteroid = (DarkAsteroid) obj1;
            // SCHUSS
            if(obj2 instanceof Shot){
                objectsToRemove.add((Shot) obj2);
                objectsToRemove.add(darkAsteroid);
                // Explosion
                startExplosion(obj2);
                //if(soundsloaded == 2) {
                 //   soundPool.play(explosionSoundId, 30, 30, 1, 0, 1.0f);
               // }
                // Loch
                addHole(darkAsteroid.getPositionX(), darkAsteroid.getPositionY());
                // Score
                if(!isx2Score) {
                    score++;
                }else{
                    score +=2;
                }

            }
        }
        //BIG ASTEROID UND ...
        if(obj1 instanceof BigAsteroid){
            BigAsteroid bigAsteroid = (BigAsteroid) obj1;
            //SHOT
            if(obj2 instanceof Shot){
                objectsToRemove.add((Shot) obj2);
                objectsToRemove.add(bigAsteroid);
                //Explosion
                startExplosion(obj2);
                //if(soundsloaded == 2) {
                //   soundPool.play(explosionSoundId, 30, 30, 1, 0, 1.0f);
                // }
                // OTHER ASTEROIDS
                bigAsteroidHit(bigAsteroid.getPositionX(),bigAsteroid.getPositionY());
                // Score
                score++;
            }
        }

    }
    private void startDuration(long currentTimeMillis, PowerUps.PowerUp type) {
        Thread countThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long endtime;
                switch (type){
                    case GHOST:
                        endtime = currentTimeMillis + PowerUps.PowerUp.GHOST.getDuration();
                        while(System.currentTimeMillis() < endtime){
                            // TUE NICHTS
                        }
                        isGhost = false;
                        break;
                    case STRONG_SHOT:
                        endtime = currentTimeMillis + PowerUps.PowerUp.STRONG_SHOT.getDuration();
                        while(System.currentTimeMillis() < endtime){
                            // TUE NICHTS
                        }
                        isStrongShot = false;
                        break;
                    case X2SCORE:
                        endtime = currentTimeMillis + PowerUps.PowerUp.X2SCORE.getDuration();
                        while(System.currentTimeMillis() < endtime){
                            // TUE NICHTS
                        }
                        isx2Score = false;
                        break;
                    default:
                        break;
                }
            }
        });
        countThread.start();
    }

    private void startExplosion(Circle obj) {
        // TODO POSITION MUSS ANGEPASST WERDEN
        Explosion explosion = new Explosion(context, obj.getPositionX()- obj.getRadius(), obj.getPositionY()-obj.getRadius());
        explosions.add(explosion);
    }


    public void shoot(Joystick joystickRotation) {
        double xDirection = joystickRotation.getActuatorX();
        double yDirection = joystickRotation.getActuatorY();

        Shot shot = new Shot(context, player, xDirection, yDirection, directionJoystick);
        shotList.add(shot);
    }

    private void addPowerUp(double x, double y){
        if(Math.random() <= POWERUP_FREQUENCY) {
            PowerUps powerUp = new PowerUps(context, x, y);
            powerUpsList.add(powerUp);
        }
    }

    /**
     Zu 5% wird ein Loch hinzugefügt
     @param x x-Koordinate des Asteroiden
     @param y y-Koordinate des Asteroiden
     */
    private void addHole(double x, double y) {
        //if (Math.random() <= HOLE_FREQUENCY) {
            Hole hole = new Hole(context, x,y);
            holes.add(hole);
            // SOUND
            if(MainActivity.isSoundOn) {
                mLoch = MediaPlayer.create(context, R.raw.loch);
                mLoch.start();
            }
        //}
    }

    private void bigAsteroidHit(double x, double y){
        Asteroid asteroid = new Asteroid(context, x, y);
        asteroid.setDestination(1, 1);
        asteroidList.add(asteroid);
        Asteroid asteroid1 = new Asteroid(context, x, y);
        asteroid.setDestination(-1, 1);
        asteroidList.add(asteroid1);
        Asteroid asteroid2 = new Asteroid(context, x, y);
        asteroid.setDestination(1, -1);
        asteroidList.add(asteroid2);
        Asteroid asteroid3 = new Asteroid(context, x, y);
        asteroid.setDestination(-1, -1);
        asteroidList.add(asteroid3);


    }

    private void addAsteroid() {
        if (MAX_ASTEROIDS > asteroidList.size() || MAX_DARK_ASTEROIDS > darkAsteroidsList.size()) {

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

                    if(MAX_DARK_ASTEROIDS > darkAsteroidsList.size()) {
                        DarkAsteroid darkAsteroid = new DarkAsteroid(context, spawnX, spawnY);
                        darkAsteroid.setDestination(destX, destY);
                        darkAsteroidsList.add(darkAsteroid);
                    }

                    if(MAX_BIG_ASTEROIDS > bigAsteroidList.size()) {
                        BigAsteroid bigAsteroid = new BigAsteroid(context, spawnX, spawnY);
                        bigAsteroid.setDestination(destX,destY);
                        bigAsteroidList.add(bigAsteroid);
                    }

            }
        }
    }
    public int getHealth() {
        return health;
    }
}


