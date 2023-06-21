package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;

public class SpaceShip {
    //TODO eventuell abstrakte Klasse erzeugen

    /**
     * Eigenschaften der Klasse SpaceShip
     */
    private float x,y; // Koordinaten des Asteroiden
    private float speed; //Geschwindigkeit SpaceShip
    private float width, height;  //Breite und Höhe Spaceship

    //Bitmap
    private Bitmap spaceshipBitmap;


    // Constants
    private static final int FULL_HEALTH = 5;

    // Condition
    private int health = FULL_HEALTH;




    private Random random = new Random();
    private int screenWidth, screenHeight; // Bildschirmgröße

    // Constructor
    public SpaceShip(int screenWidth, int screenHeight, Context context){

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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
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
    public void update(){

    }

    /**
     * Schaden durch treffer eines Asteroiden wird von Leben abgezogen
     * @param dmg Schaden des Asteroiden
     */
    public void damage(double dmg){
        health -=dmg;
    }

    public void resetHealth(){
        health = FULL_HEALTH;
    } //TODO gibt es möglichkeit?


}
