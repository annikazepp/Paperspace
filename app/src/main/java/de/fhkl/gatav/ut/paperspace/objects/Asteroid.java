package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;

public class Asteroid implements de.fhkl.gatav.ut.paperspace.objects.Drawable {

    //Asteroid in general
    private float x, y; // Position des Asteroiden
    private float speedX, speedY; // Geschwindigkeit des Asteroiden
    private float width,heigth;

    public float getWidth() {
        return asteroidBitmap.getWidth();
    }

    public float getHeigth() {
        return asteroidBitmap.getHeight();
    }

    private float size= 10;
    private float scale = 1.0f; //TODO

    private int velocity;

    // drawables
    Drawable asteroidDrawable;
    private Bitmap asteroidBitmap;

    private int imageResource;
    private int[] asteroidImages ={ //TODO verschiedene Asteroiden
            R.drawable.asteroid_1,
            R.drawable.asteroid_2,
            R.drawable.asteroid_3,
            R.drawable.asteroid_4,
            R.drawable.asteroid_5,
            R.drawable.asteroid_6,
            R.drawable.asteroid_7
    };

    // Constants
    public static final int MIN_SIZE = 150; //TODO ANPASSEN?
    public static final int MAX_SIZE = 300;

    public static final int MIN_SPEED = 1;
    public static final int MAX_SPEED = 10;


    //Construtor
    private Random random;
    private int screenWidth, screenHeight; // Bildschirmgröße

    public Asteroid(int screenWidth, int screenHeight, Context context) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.random = new Random();

        // Zufällige Position innerhalb des Bildschirms
        this.x = random.nextFloat() * screenWidth;
        this.y = random.nextFloat() * screenHeight;

        // Zufällige Geschwindigkeit in X- und Y-Richtung
        this.speedX = random.nextFloat() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED; // Geschwindigkeit zwischen MIN und MAX
        this.speedY = random.nextFloat() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED;



        // Zufälliges Bild TODO ?
        asteroidBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid_1);
        //this.imageResource = asteroidImages[random.nextInt(asteroidImages.length)];
        //this.asteroidDrawable = context.getResources().getDrawable(imageResource);

        // Zufällige Größe Asteroiden-Bitmaps
        this.size = generateRandomSize(MIN_SIZE, MAX_SIZE);

        // Bitmaps Skalieren
        float scale = (float) size / Math.max(asteroidBitmap.getWidth(),asteroidBitmap.getHeight());
        this.asteroidBitmap = Bitmap.createScaledBitmap(asteroidBitmap, (int)(asteroidBitmap.getWidth() * scale), (int) (asteroidBitmap.getHeight() * scale),true);
        this.width = asteroidBitmap.getWidth();
        this.heigth = asteroidBitmap.getHeight();


    }

    // Getter-Setter
    public float getSize() {
        return size;
    }
    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }









    public void draw(Canvas c){
        c.drawBitmap(asteroidBitmap, x,y,null);
    }






    public int getAsteroidWidth(){
        return -1;
        //TODO
    }

    //true wenn sie sich berühren
    public boolean collidesWith(Asteroid otherAsteroid) {
        RectF aRect = this.getBounds();
        RectF oARect = otherAsteroid.getBounds();
        if(RectF.intersects(aRect,oARect)){
            return true;
        }
        return false;
        /**
        float distanceX = otherAsteroid.x - this.x;
        float distanceY = otherAsteroid.y - this.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance <= (this.size + otherAsteroid.size)/2;
         */

    }
    public boolean collidesWith(SpaceShip spaceShip) {
        float distanceX = spaceShip.getX() - this.x;
        float distanceY = spaceShip.getY() - this.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance <= (this.size+ spaceShip.getSize())/2;

    }



    //TODO könnte noch Verbessert werden
    // Berechnet die Abstoßungsreaktion zwischen diesem Asteroiden und einem anderen Asteroiden
    public void bounceOff(Asteroid otherAsteroid) {
        // Calculate the direction from this asteroid to the other asteroid
        float directionX = otherAsteroid.x - this.x;
        float directionY = otherAsteroid.y - this.y;

        // Calculate the distance between the two asteroids
        float distance = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Calculate the normalized direction values
        float normalizedDirectionX = directionX / distance;
        float normalizedDirectionY = directionY / distance;

        // Calculate the relative velocity of the two asteroids
        float relativeVelocityX = this.speedX - otherAsteroid.speedX;
        float relativeVelocityY = this.speedY - otherAsteroid.speedY;

        // Calculate the impulse magnitude based on the relative velocity and the masses of the asteroids
        float impulseMagnitude = 2.0f * (relativeVelocityX * normalizedDirectionX + relativeVelocityY * normalizedDirectionY) /
                (this.size + otherAsteroid.size);

        // Apply the impulse to the velocities of the asteroids
        this.speedX -= impulseMagnitude * otherAsteroid.size * normalizedDirectionX;
        this.speedY -= impulseMagnitude * otherAsteroid.size * normalizedDirectionY;
        otherAsteroid.speedX += impulseMagnitude * this.size * normalizedDirectionX;
        otherAsteroid.speedY += impulseMagnitude * this.size * normalizedDirectionY;
    }

    public void move() {
        // Bewege den Asteroiden basierend auf seiner Geschwindigkeit
        x += speedX;
        y += speedY;

        // Überprüfe die Kollision mit den Bildschirmrändern
        if (x < 0 || x > screenWidth) {
            speedX = -speedX; // Ändere die Richtung, wenn der Asteroid den Rand erreicht
        }
        

        if (y < 0 || y > screenHeight) {
            speedY = -speedY;
        }

    }

    public boolean isShot() {
        //TODO
        return false;
    }
    private int generateRandomSize(int minSize, int maxSize) {
        Random random = new Random();
        return random.nextInt(maxSize - minSize + 1) + minSize;
    }

    public void update() { //TODO
    }

    public RectF getBounds() {
        return new RectF(x,y,x+width,y+heigth);
    }
}

