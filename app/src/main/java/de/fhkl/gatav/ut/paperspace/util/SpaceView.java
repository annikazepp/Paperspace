package de.fhkl.gatav.ut.paperspace.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;

import de.fhkl.gatav.ut.paperspace.R;

import de.fhkl.gatav.ut.paperspace.objects.Joystick;
import de.fhkl.gatav.ut.paperspace.objects.SpaceShip;


/**
 * @class SpaceView benutzerdefinierte Ansichtsklasse, die eigentliches Spiel darstellt
 * Zeichnet Spielgrafiken und zeigt das Spiel auf dem Bildschirm an
 */
public class SpaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    /**
     * Oberflächenverwaltung
     */
    private SurfaceHolder surfaceHolder;
    private Thread gameThread;

    /**
     * Spielstatus verfolgen
     */
    private boolean isRunning = false;
    private boolean gameOver = false; //TODO WEG?

    /**
     * Hintergrund, Lebenssymbol und Score
     */
    private Bitmap backgroundBitmap;
    private Bitmap healthBitmap;
    private Paint scorePaint;
    private static final int TEXT_SIZE = 50; //TODO WERT?
    private Joystick joystick;

    /**
     * für den Spielinhalt
     */
    private GameContent gameContent;


    /**
     * Initialisiert Oberfläche und gameContent
     * Bilder werden geladen
     * @param context Spieleumgebung
     */
    public SpaceView(Context context){
        super(context);

        joystick = Joystick.getJoystick();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameContent = new GameContent(context);

        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        healthBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLUE); //TODO Farbe anpassen
        scorePaint.setTextSize(TEXT_SIZE);
    }

    /**
     * wird aufgerufen, wenn Oberfläche erstellt wird
     * GameLoop wird gestartet
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startGameLoop();
    }

    //TODO ?
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // NICHT BENÖTIGT
    }

    /**
     * wird aufgerufen, wenn Oberfläche zerstört wird
     * GameLoop wird gestoppt
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopGameLoop();
    }

    /**
     * startet die Spiel-Loop
     * neuen Thread wird erstellt, der die "run()" Methode der Klasse aufruft
     */
    public void startGameLoop(){
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * stoppt Spiel-Loop
     * wartet auf das Beenden des Threads
     */
    public void stopGameLoop(){
        isRunning = false;
        try {
            gameThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * aktualisiert Spielinhalt aus GameContent
     */
    void updateContent(){
        if(gameContent!= null){
            gameContent.update();
        }
    }

    /**
     * Zeichnet Spielinhalt auf
     * @param canvas Leinwand
     */
    void drawContent(Canvas canvas){
        if(canvas !=null) {
            // Hintergrund
            canvas.drawBitmap(backgroundBitmap, null, new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), null);

            // GameContent (Player, Asteroiden) - Spielinhalte zeichnen
            gameContent.draw(canvas);

            // Lebenssymbole
           int life = gameContent.getHealthSpaceShip();
            for(int i = life; i>=1; i--){
                canvas.drawBitmap(healthBitmap,
                        gameContent.getGameWidth() - (healthBitmap.getWidth() +5) * i ,40,null);
            }
            // Score
            // TODO in SpaceView? Koordinaten anpassen
            canvas.drawText("Score: " + gameContent.getScore(),  (scorePaint.getTextScaleX() + 15) ,85, scorePaint);
        }
    }

    /**
     * startet, wenn das Spielvorbei ist eine neue Acitivity GameOver
     */
    //TODO dauert manchmal zulange
    private void startGameOverActivity() {
        Intent gameOverIntent = new Intent(getContext(), GameOver.class);
        gameOverIntent.putExtra("score", gameContent.getScore());
        getContext().startActivity(gameOverIntent);
        ((Activity) getContext()).finish();

    }

    /**
     * enthält Hauptspiel-Loop, hier wird Spielinhalt aktualisiert und gezeichnet
     */
    @Override
    public void run() {

        while(isRunning) {
            Canvas canvas = null;
            try{
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    updateContent();
                    drawContent(canvas);
                }
            }finally {
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            if(gameContent.isGameOver()) {
                startGameOverActivity();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        SpaceShip player = SpaceShip.getPlayer();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (joystick.isPressed((double) event.getX(), (double) event.getY())) {
                    joystick.setIsPressed(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(joystick.getIsPressed()){
                    joystick.setActuator((double) event.getX(), (double) event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                joystick.setIsPressed(false);
                joystick.resetActuator();
                return true;
        }
        return super.onTouchEvent(event);
    };
}

