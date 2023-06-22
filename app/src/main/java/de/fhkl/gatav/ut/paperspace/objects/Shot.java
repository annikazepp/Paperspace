package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import de.fhkl.gatav.ut.paperspace.R;

public class Shot {
    Bitmap shotBitmap;
    float x,y;
    float screenWidth,screenHeight;
    float speedX,speedY;

    boolean obsolete = false; //if shot is obsolete

    float screentolerance; //TODO how much the shot needs to leave the screen to disappear
    public Shot(int screenWidth, int screenHeight,Context context, float x, float y, float speedX, float speedY){
        shotBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.effect_purple);
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        //setze screentolerance auf höchsten Wert + 10%
        if(this.screenHeight>this.screenWidth){
            this.screentolerance = this.screenHeight + this.screenHeight/100*10;
        }
        else{
            this.screentolerance = this.screenWidth + this.screenWidth/100*10;
        }

    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public Bitmap getShotBitmap(){
        return shotBitmap;
    }

    public float getWidth() {
        return shotBitmap.getWidth();
    }

    public void draw(Canvas c) {
        c.drawBitmap(shotBitmap, x,y,null);
    }

    public void move() {
        // Bewege den Shot basierend auf seiner Geschwindigkeit.
        x += speedX;
        y += speedY;

        // Überprüfe die Kollision mit den Bildschirmrändern

        if (x < 0-screentolerance || x > screenWidth+screentolerance) {
            obsolete = true;
        }

        if (y < 0-screentolerance || y > screenHeight+screentolerance) {
            obsolete = true;
        }
    }
    public boolean isobsolete() {
        return obsolete;
    }

}
