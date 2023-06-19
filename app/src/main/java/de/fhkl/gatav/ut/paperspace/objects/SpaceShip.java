package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;

public class SpaceShip {
    //TODO eventuell abstrakte Klasse erzeugen

    // spaceShip in general
    private float x,y; //position spaceShip
    private float speed;
    private int size = 3; //TODO Size festlegen/berechnen
    private float width,heigth;

    public float getWidth() {
        return spaceshipBitmap.getWidth();
    }

    public float getHeigth() {
        return spaceshipBitmap.getHeight();
    }
    private Bitmap spaceshipBitmap;


    // Constants
    private int FULL_HEALTH = 5;

    // Condition
    private int health = FULL_HEALTH;
    boolean isAlive = true;
    boolean collisionDetected = false;

    public void setCollisionDetected(boolean collisionDetected) {
        this.collisionDetected = collisionDetected;
    }

    private Random random;
    private int screenWidth, screenHeight; // Bildschirmgröße

    // Constructor
    public SpaceShip(int screenWidth, int screenHeight, Context context){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.random = new Random();

        // Start in der Mitte des Bildschirms
        this.x = screenWidth/2;
        this.y = screenHeight/2;
        this.speed = 10; //TODO anderer Wert?

        spaceshipBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship2);

        this.heigth = getHeigth();
        this.width = getWidth();

    }

    //Getter-Setter
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getSize() {
        return size;
    }
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    // Methoden
    public void draw(Canvas c){ //TODO
        // An der aktuellen Position Spaceship
        c.drawBitmap(spaceshipBitmap, x, y, null);
    }

    //TODO
    public void update(){



    }




    public void damage(double dmg){
        health -=dmg;
    }

    public void resetHealth(){
        health = FULL_HEALTH;
    }

    public RectF getBounds() {
        return new RectF(x,y,x+width,y+heigth);
    }

    public boolean isCollisionDetected() {
        return collisionDetected;
    }
}
