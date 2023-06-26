package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;

public class Asteroid implements de.fhkl.gatav.ut.paperspace.objects.Drawable {

    /**
     * Eigenschaften der Klasse Asteroid
     */
    private float x, y; // Koordinaten des Asteroiden
    private float destinationX, destinationY; // Zielkoordinaten, zu denen sich der Asteroid bewegt
    private float speedX, speedY; // Geschwindigkeit des Asteroiden //TODO WEG?
    private double rotX, rotY; //Rotation der Asteroiden um die eigene Achse //TODO
    private float speed; //Geschwindigkeit Asteroiden TODO ? zu const
    private float widthAsteroid, heightAsteroid; // Breite und Höhe Asteroid

    private float size; //TODO?
    private float scale = 1.0f; //Skalierungsfaktor für Bitmap

    // Bitmap
    private Bitmap asteroidBitmap;


    // Constants
    public static final int MIN_SIZE = 150; //TODO ANPASSEN?
    public static final int MAX_SIZE = 300;
    public static final int MIN_SPEED = 3;
    public static final int MAX_SPEED = 6;

    private static final int DAMAGE = 1;

    private static final int[] ASTEROID_IMAGES ={ //TODO verschiedene Asteroiden Bilder anpassen?
            R.drawable.asteroid_1,
            R.drawable.asteroid_2,
            R.drawable.asteroid_3,
            R.drawable.asteroid_4,
            R.drawable.asteroid_5,
            R.drawable.asteroid_6,
            R.drawable.asteroid_7
    };


    private Random random = new Random();
    private float screenWidth, screenHeight; // Bildschirmgröße
    private Context context;


    /**
     * Konstruktor - initialisiert Asteroiden zufällig
     * @param screenHeight
     * @param screenWidth
     * @param context
     */
    public Asteroid(float screenHeight, float screenWidth, Context context) {

        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        this.context = context;

        // Zufällige Geschwindigkeit in X- und Y-Richtung //TODO?
        this.speedX = random.nextFloat() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED; // Geschwindigkeit zwischen MIN und MAX
        this.speedY = random.nextFloat() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED;

        this.speed = random.nextFloat() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED; //TODO?

        // Zufälliges Bild Asteroid
        int imageIndex = random.nextInt(ASTEROID_IMAGES.length);
        asteroidBitmap = BitmapFactory.decodeResource(context.getResources(), ASTEROID_IMAGES[imageIndex]);

        // Zufällige Größe Asteroiden-Bitmaps
        this.size = generateRandomSize();

        // Bitmaps Skalieren
        float scale = (float) size / Math.max(asteroidBitmap.getWidth(),asteroidBitmap.getHeight());
        this.asteroidBitmap = Bitmap.createScaledBitmap(asteroidBitmap, (int)(asteroidBitmap.getWidth() * scale), (int) (asteroidBitmap.getHeight() * scale),true);

        this.widthAsteroid = asteroidBitmap.getWidth();
        this.heightAsteroid = asteroidBitmap.getHeight();
    }

    // Getter-Setter
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public float getWidthAsteroid() {
        return asteroidBitmap.getWidth();
    }

    public float getHeightAsteroid() {
        return heightAsteroid;
    }

    public double getDamage() {
        return DAMAGE;
    }


    /**
     * Zeichnet Asteroiden
     * @param canvas Zeichenfläche, auf die zu zeichnen ist
     */
    public void draw(Canvas canvas){
        canvas.drawBitmap(asteroidBitmap, x,y,null);
    }


    /**
     * Überprüft, ob Asteroiden kollidieren
     * @param otherAsteroid
     * @return true, wenn Asteroiden zusammenstoßen
     */
    public boolean collidesWith(Asteroid otherAsteroid) {
        float distanceX = otherAsteroid.x - this.x;
        float distanceY = otherAsteroid.y - this.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        return distance <= (this.size + otherAsteroid.size)/2;
    }

    /**
     * Berechnet die Abstoßungsreaktion zwischen diesem Asteroiden und einem anderen Asteroiden
     * @param otherAsteroid
     */
    //TODO könnte noch Verbessert werden
    public void bounceOff(Asteroid otherAsteroid) {

        // Berechnen der Richtung vom aktuellen Asteroiden zum anderen Asteroiden
        float directionX = otherAsteroid.x - this.x;
        float directionY = otherAsteroid.y - this.y;

        // Berechnen der Distanz zwischen den beiden Asteroiden
        float distance = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Berechnen der normalisierten Richtungswerte
        float normalizedDirectionX = directionX / distance;
        float normalizedDirectionY = directionY / distance;

        // Berechnen der Impulsgröße basierend auf der Masse der Asteroiden
        float impulseMagnitude = 5.0f / (this.getWidthAsteroid() + otherAsteroid.getWidthAsteroid());

        // Anwenden des Impulses auf die Positionen der Asteroiden
        this.x -= impulseMagnitude * otherAsteroid.getWidthAsteroid() * normalizedDirectionX;
        this.y -= impulseMagnitude * otherAsteroid.getHeightAsteroid() * normalizedDirectionY;
        otherAsteroid.x += impulseMagnitude * this.getWidthAsteroid() * normalizedDirectionX;
        otherAsteroid.y += impulseMagnitude * this.getHeightAsteroid() * normalizedDirectionY;
    }

    /**
     * Bewegt Asteroid basierend auf seiner Geschwindigkeit und Zielrichtung
     */
    public void move() {
        // Berechnung der Richtung zum Ziel
        double dx = destinationX - x;
        double dy = destinationY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double directionX = dx / distance;
        double directionY = dy / distance;

        // Bewegung des Asteroiden in Richtung des Ziels
        x += directionX * speed;
        y += directionY * speed;
    }


    /**
     * Generiert eine zufällige Größe für Asteroiden
     */
    private int generateRandomSize() {
        return random.nextInt(MAX_SIZE - MIN_SIZE + 1) + MIN_SIZE;
    }


    public void update() { //TODO?
    }

    /**
     * Überprüft, ob Asteroid außerhalb des Sichtbereichs ist
     * @return true, wenn x oder y Koordinaten des Asteroiden außerhalb des Display ist
     */
    public boolean outOfView() {
        //TODO Asteroiden fliegen nach draußen und werden dann gelöscht - momentan werden sie sobald sie am rand ankommen gelöscht
        // d.h. kein Übergang -> mit code untendrunter fliegen sie raus und verschwinden dann, aber falls ein Asteroid gerade erzeugt wird
        // "steckt" sichtbarer Asteroid fest und bewegt sich nicht
        //return x+widthAsteroid < 0 || x > screenWidth || y+heightAsteroid < 0 || y > screenHeight;
        return x < 0 || x > screenWidth || y < 0 || y > screenHeight;
    }

    /**
     * Setzt Startposition des Asteroiden
     * @param spawnX Startwert x
     * @param spawnY Startwert y
     */
    public void setPosition(float spawnX, float spawnY) {
        this.x = spawnX;
        this.y = spawnY;
    }

    /**
     * Setzt Zielposition
     * @param destX Zielwert x
     * @param destY Zielwert y
     */
    public void setDestination(float destX, float destY) {
        this.destinationX = destX;
        this.destinationY = destY;
    }
}


