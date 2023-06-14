package de.fhkl.gatav.ut.paperspace.objects;

import java.util.Random;

public class Comet {
    private float x;
    private float y;
    private float size;
    private float speed;

    public Comet(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.size = 30; // Change the size as needed
        this.speed = speed;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }


    public void update(int screenWidth, int screenHeight) {
        // Update comet logic here
        x -= speed;
        if (x < 0) {
            x = screenWidth;
            y = new Random().nextInt(screenHeight);
        }
    }
}