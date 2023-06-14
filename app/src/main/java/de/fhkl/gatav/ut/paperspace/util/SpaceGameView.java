package de.fhkl.gatav.ut.paperspace.util;



import static de.fhkl.gatav.paperspace.util.Utilities.normalize;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.objects.Asteroid;
import de.fhkl.gatav.ut.paperspace.objects.Obstacle;
import de.fhkl.gatav.ut.paperspace.objects.SpaceObject;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;


public class SpaceGameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Bitmap background;

    private SurfaceHolder surfaceHolder;

    private Thread gameThread;
    private boolean runningRenderLoop = false;
    public boolean gameover = false;

    private Thread timeThread;


    private int gameMode = 0; //TODO

    private float gameWidth = -1;
    private float gameHeight = -1;

    private Paint scorePaint = new Paint();

    {
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(20);
    }

    private GameContent gameContent;
    public Context context;  // activity context

    private static final int obstacleCount = 15;
    private static final float minSpawnDistanceToPlayer = 1.5f;
    private static final float minSpawnDistanceBetweenObstacles = 1.5f;
    private static final float asteroidMinScale = 0.8f;
    private static final float asteroidMaxScale = 1.0f;

    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private SpaceShip ship = new SpaceShip();


    public SpaceGameView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        scorePaint.setTextSize(20f * context.getResources().getDisplayMetrics().density);

        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);

        gameContent = new GameContent();
        setRenderer(renderer); //TODO brauch man das?

    }


    /**
     * Aktualisiert die grafische Darstellung; wird von Gameloop aufgerufen
     *
     * @param canvas Zeichenfläche
     */
    void updateGraphics(Canvas canvas) {
        // Layer 0 ( background)
        canvas.drawBitmap(background, 0, 0, null);

        //Layer 1
        canvas.save();
        canvas.translate((canvas.getWidth() - gameContent.getGameWidth()) / 2,
                (canvas.getHeight() - gameContent.getGameHeight()) / 2);
        draw(canvas);
        canvas.restore();

        //Layer 2 Score
        String scoreText = String.format("Punkte: %d", gameContent.getCollectedScore());
        Rect scoreTextBounds = new Rect();
        scorePaint.getTextBounds(scoreText, 0, scoreText.length(), scoreTextBounds);
        float textWidth = Math.max(scorePaint.measureText(scoreText), scorePaint.measureText(scoreText));

        canvas.save();
        canvas.translate(gameWidth - textWidth, scoreTextBounds.height());
        canvas.drawText(scoreText, 0, 0, scorePaint);
        canvas.translate(0, (int) (scoreTextBounds.height() * 1.5));
        canvas.drawText(scoreText, 0, 0, scorePaint);
        if (gameMode == 1) {   // game running
            canvas.translate(0, (int) (scoreTextBounds.height() * 1.5));
            canvas.drawText(scoreText, 0, 0, scorePaint);


        }

        /**
         * Aktualisiert den Spielzustand; wird von Gameloop aufgerufen
         * @param fracsec Teil einer Sekunde, der seit dem letzten Update vergangen ist
         */
        void updateContent(float fracsec) {
            if(gameContent != null)
                gameContent.update(fracsec);
        }
    }


    private class GameContent {

        /**
         * Breite und Höhe des Spielfeldes in Pixel
         */
        private int gameWidth = -1;
        private int gameHeight = -1;
        public int getGameWidth() { return gameWidth; }
        public int getGameHeight() { return gameHeight; }


        long lastFrameTime;

        public GameContent() {
            lastFrameTime = System.currentTimeMillis();
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            // update time calculation
            long delta = System.currentTimeMillis() - lastFrameTime;
            float fracSec = (float) delta / 1000;
            lastFrameTime = System.currentTimeMillis();

            // scene updates
            updateShip(fracSec);
            updateObstacles(fracSec);


            // clear screen and depth buffer
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            GL11 gl11 = (GL11) gl;

            // load local system to draw scene items
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl11.glLoadMatrixf(modelViewScene, 0);

            ship.draw(gl);
            for (Obstacle obstacle: obstacles) {
                obstacle.draw(gl);
            }
        }


        private void updateShip(float fracSec) {
            ship.update(fracSec);
            // keep ship within window boundaries
            if (ship.getX() < boundaryLeft + ship.scale / 2)
                ship.setX(boundaryLeft + ship.scale / 2);
            if (ship.getX() > boundaryRight - ship.scale / 2)
                ship.setX(boundaryRight - ship.scale / 2);
            if (ship.getZ() < boundaryBottom + ship.scale / 2)
                ship.setZ(boundaryBottom + ship.scale / 2);
            if (ship.getZ() > boundaryTop - ship.scale / 2)
                ship.setZ(boundaryTop - ship.scale / 2);
        }


        private boolean areColliding(SpaceObject obj1, SpaceObject obj2) {
            float obj1X = obj1.getX();
            float obj1Y = obj1.getY();
            float obj2X = obj2.getX();
            float obj2Y = obj2.getY();
            float squaredHitDistance = ((obj1.scale + obj2.scale) / 2) * ((obj1.scale + obj2.scale) / 2);
            float squaredDistance = (obj1X - obj2X) * (obj1X - obj2X) + (obj1Y - obj2Y) * (obj1Y - obj2Y);

            if(squaredDistance < squaredHitDistance)
                return true;
            return false;
        }


        private void updateObstacles(float fracSec) {
            ArrayList<Obstacle> obstaclesToBeRemoved = new ArrayList<Obstacle>();

            // position update on all obstacles
            for(Obstacle obstacle : obstacles) {
                obstacle.update(fracSec);
            }


            // check for obstacles that flew out of the viewing area and remove
            // or deactivate them
            for (Obstacle obstacle : obstacles) {
                // offset makes sure that the obstacles don't get deleted or set
                // inactive while visible to the player.
                float offset = obstacle.scale;

                if ((obstacle.getX() > gameWidth + offset)
                        || (obstacle.getX() < gameWidth - offset) //TODO?
                        || (obstacle.getY() > gameHeight + offset)
                        || (obstacle.getY() < gameHeight - offset))//TODO?
                        obstaclesToBeRemoved.add(obstacle);
                }


            // obstacle collision with space ship
            for (Obstacle obstacle : obstacles) {
                if (areColliding(ship, obstacle)) {
                    if (obstacle instanceof Obstacle) {
                        ship.damage(0.1f); // add some damage to the ship
                        obstaclesToBeRemoved.add(obstacle);
                    }
                }
            }
            // remove obsolete obstacles
            for (Obstacle obstacle: obstaclesToBeRemoved) {
                obstacles.remove(obstacle);
            }
            obstaclesToBeRemoved.clear();


            // obstacle collision handling with each other
            for (int i=0; i<=obstacles.size()-2; i++) {
                Obstacle obstacle = obstacles.get(i);
                float ax = obstacle.getX();
                float ay = obstacle.getY();

                // check for collision with other obstacle
                for(int j=i+1; j<=obstacles.size()-1; j++) {
                    Obstacle otherObstacle = obstacles.get(j);
                    float oax = otherObstacle.getX();
                    float oay = otherObstacle.getY();

                    if(areColliding(obstacle, otherObstacle)) {
                        // collisions: let them bounce off each other
                        // http://de.wikipedia.org/wiki/ -> ElastischerStoß

                        float cv1[] = obstacle.velocity;
                        float cv2[] = otherObstacle.velocity;
                        float csv1 = cv1[2] / cv1[0]; // slope of velocity 1
                        float csv2 = cv2[2] / cv2[0]; // slope of velocity 2

                        float csz = (oay - ay) / (oax - ax); // central slope between centers
                        float cst = -1.0f / csz; // tangent slope perpendicular to central line

                        // calculate vt for both velocities
                        float cvt1[] = new float[3];
                        float cvt2[] = new float[3];
                        cvt1[0] = cv1[0] * (csz - csv1) / (csz - cst);
                        cvt1[2] = cvt1[0] * cst;

                        cvt2[0] = cv2[0] * (csz - csv2) / (csz - cst);
                        cvt2[2] = cvt2[0] * cst;

                        // calculate vz for both velocities
                        float cvz1[] = new float[3];
                        float cvz2[] = new float[3];
                        cvz1[0] = cv1[0] * (cst - csv1) / (cst - csz);
                        cvz1[2] = cvz1[0] * csz;

                        cvz2[0] = cv2[0] * (cst - csv2) / (cst - csz);
                        cvz2[2] = cvz2[0] * csz;

                        // asteroid-asteroid
                        // => inclined central elastic collision with identical masses
                        if(obstacle.getClass() == otherObstacle.getClass()) {
                            cv1[0] = cvt1[0] + cvz2[0];
                            cv1[2] = cvt1[2] + cvz2[2];

                            cv2[0] = cvt2[0] + cvz1[0];
                            cv2[2] = cvt2[2] + cvz1[2];
                        }

                        if(obstacle instanceof Asteroid) ((Asteroid)obstacle).angularVelocity *= -1.0f;
                        if(otherObstacle instanceof Asteroid) ((Asteroid)otherObstacle).angularVelocity *= -1.0f;
                    }
                }
            }
            // remove obsolete obstacles
            for (Obstacle obstacle: obstaclesToBeRemoved) {
                obstacles.remove(obstacle);
            }
            obstaclesToBeRemoved.clear();


            // Spawn new asteroid obstacles to match the target obstacle count
            if (obstacleCount > obstacles.size()) {
                for (int i = 0; i < obstacleCount - obstacles.size(); ++i) {
                    // determine what kind of obstacle is spawned next
                    //int type = Math.random() < 0.85?1:2;  // 1 Asteroid, 2 BorgCube

                    float scale = (float)Math.random() * (asteroidMaxScale - asteroidMinScale) + asteroidMinScale;


                    float spawnX = 0.0f;
                    float spawnY = 0.0f;
                    float spawnOffset = scale * 0.5f;
                    float velocity[] = new float[3];

                    // determine source and destination quadrant
                    int sourceCode = ((Math.random()<0.5?0:1)<<1) | (Math.random()<0.5?0:1);  // source quadrant
                    int destCode = sourceCode ^ 3;	// destination quadrant is opposite of source
                    //Log.d("Code", sourceCode+" "+destCode);

                    /* sourceCode, destCode
                     * +----+----+
                     * | 00 | 01 |
                     * +----+----+
                     * | 10 | 11 |
                     * +----+----+
                     */

                    //TODO
                    // calculate source vertex position, <0.5 horizontal, else vertical
                    if(Math.random()<0.5){  // horizontal placing, top or bottom
                        spawnX = (sourceCode&1)>0?gameWidth*(float)Math.random() : gameHeight*(float)Math.random();
                    }
                    else{  // vertical placing, left or right
                        spawnY = (sourceCode&2)>0?boundaryBottom*(float)Math.random() : boundaryTop*(float)Math.random();
                        spawnX = (sourceCode&1)>0?boundaryRight+spawnOffset : boundaryLeft-spawnOffset;
                    }

                    // calculate destination vertex position, <0.5 horizontal, else vertical
                    if(Math.random()<0.5){  // horizontal placing, top or bottom
                        velocity[2] = (destCode&2)>0?boundaryBottom-spawnOffset : boundaryTop+spawnOffset;
                        velocity[0] = (destCode&1)>0?boundaryRight*(float)Math.random() : boundaryLeft*(float)Math.random();
                    }
                    else{  // vertical placing, left or right
                        velocity[2] = (destCode&2)>0?boundaryBottom*(float)Math.random() : boundaryTop*(float)Math.random();
                        velocity[0] = (destCode&1)>0?boundaryRight+spawnOffset : boundaryLeft-spawnOffset;
                    }

                    // calculate velocity
                    velocity[0] -= spawnX;
                    velocity[2] -= spawnY;
                    normalize(velocity);


                    boolean positionOk = true;

                    // check distance to other obstacles
                    for(Obstacle obstacle: obstacles) {
                        float minDistance = 0.5f * scale + 0.5f * obstacle.scale + minSpawnDistanceBetweenObstacles;
                        if(Math.abs(spawnX - obstacle.getX()) < minDistance
                                && Math.abs(spawnY - obstacle.getZ()) < minDistance)
                            positionOk = false;	// Distance too small -> invalid position
                    }

                    // check distance to player
                    float minPlayerDistance = 0.5f * scale + 0.5f * ship.scale + minSpawnDistanceToPlayer;
                    if(Math.abs(spawnX - ship.getX()) < minPlayerDistance &&
                            Math.abs(spawnY - ship.getZ()) < minPlayerDistance)
                        positionOk = false;	// Distance to player too small -> invalid position

                    if (!positionOk)
                        continue; // Invalid spawn position -> try again next time


                        Asteroid newAsteroid = new Asteroid();
                        newAsteroid.scale = scale;
                        newAsteroid.randomizeRotationAxis();
                        newAsteroid.angularVelocity = 50;
                        newAsteroid.setPosition(spawnX, 0, spawnY);
                        newAsteroid.velocity = velocity;
                        obstacles.add(newAsteroid);


                }
            }
        }




    }

    // Called when surface is created or the viewport gets resized
    // set projection matrix
    // precalculate modelview matrix
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int border = 0;                                                     // darf's ein wenig Rand sein?
        gameWidth = width - border;                                         // hinzufügen
        gameHeight = (int)((float)gameWidth / ((float) width / height));    // Höhe entsprechend anpassen


        SpaceGameView spaceGameView = new SpaceGameView(getContext());

        gameover = false;
        //TODO gameMode=0;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /**
     * Wird aufgerufen, wenn die Zeichenfläche erzeugt wird
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Gameloop anwerfen
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Gameloop, ruft {@link #updateContent(float)} und {@link #updateGraphics(Canvas)} auf
     * und ermittelt die seit dem letzten Schleifendurchlauf vergangene Zeit (wird zum zeitlich
     * korrekten Aktualisieren des Spielzustandes benötigt)
     */
    @Override
    public void run() {
        runningRenderLoop = true;

        long lastTime = System.currentTimeMillis();

        while(runningRenderLoop) {
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - lastTime;
            float fracsec = (float)delta / 1000f;
            lastTime = currentTime;

            if(!gameOver)
                updateContent(fracsec); // kompletten Spielzustand aktualisieren

            if(gameContent!=null && gameContent.getCollectedTargets() >= maxCollectedTargets) {
                gameMode = 2;
                gameOver = true; // Game over
            }

            Canvas canvas = surfaceHolder.lockCanvas();
            if(canvas == null) continue;

            updateGraphics(canvas); // Neu zeichnen

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

}

