package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.util.Joystick;

public class SpaceShip {
    //TODO eventuell abstrakte Klasse erzeugen

    /**
     * Eigenschaften der Klasse SpaceShip
     */
    private static SpaceShip player;
    private static float x,y; // Koordinaten des Spaceship
    private float speed; //Geschwindigkeit SpaceShip
    private float width, height;  //Breite und Höhe Spaceship

    private static double velocityX;
    private static double velocityY;

    //Bitmap
    private Bitmap spaceshipBitmap;


    // Constants

    private static final int MAX_SPEED = 1;






    private Random random = new Random();
    private int screenWidth, screenHeight; // Bildschirmgröße

    /**
     * 1 Spieler wird erzeugt
     * @param screenWidth
     * @param screenHeight
     * @param context
     */

    public static void createPlayer(int screenWidth, int screenHeight, Context context) {
        if (player != null) {
            return;
        }
        player = new SpaceShip(screenWidth, screenHeight, context);
    }

    public static SpaceShip getPlayer() {
        return player;
    }



    // Constructor
    private SpaceShip(int screenWidth, int screenHeight, Context context){

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Start in der Mitte des Bildschirms
        this.x = screenWidth/2;
        this.y = screenHeight/2;
        this.speed = 10; //TODO anderer Wert?

        spaceshipBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship2);

        this.height = getHeight();
        this.width = getWidth();
    }

    //Getter-Setter
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }



    public float getWidth() {
        return spaceshipBitmap.getWidth();
    }
    public float getHeight() {
        return spaceshipBitmap.getHeight();
    }

    /**
     * Zeichnet Raumschiff auf
     * @param canvas Leinwand
     */
    public void draw(Canvas canvas){
        canvas.drawBitmap(spaceshipBitmap, x, y, null);
    }

    //TODO UPDATE SPACESHIP ?
    public static void update(Joystick joystickSteuerung){
        velocityX = joystickSteuerung.getActuatorX()*MAX_SPEED;
        velocityY = joystickSteuerung.getActuatorY()*MAX_SPEED;
        x += velocityX;
        y += velocityY;
    }


}

