package de.fhkl.gatav.ut.paperspace.objects;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;

public class Asteroid {
    private float x, y; // Position des Asteroiden
    private float speedX, speedY; // Geschwindigkeit des Asteroiden
    private float size;

    private int[] asteroidImages ={ //TODO verschiedene Asteroiden
            R.drawable.meteorit_1,
            R.drawable.meteorit_2
    };

    private int imageResource;

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public float getSize() {
        return size;
    }

    private int screenWidth, screenHeight; // Bildschirmgröße

    private Random random;
    private float scale = 1.0f; //TODO





    public Asteroid(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();

        // Initialisiere die Position und Geschwindigkeit des Asteroiden
        // Zufällige Position innerhalb des Bildschirms
        this.x = random.nextFloat() * screenWidth;
        this.y = random.nextFloat() * screenHeight;

        // Zufällige Geschwindigkeit in X- und Y-Richtung
        this.speedX = random.nextFloat() * 10 - 5; // Geschwindigkeit zwischen -5 und 5
        this.speedY = random.nextFloat() * 10 - 5;

        // Zufällige Größe Asteroid
        this.size = random.nextFloat() *150 + 50; //TODO

        // Zufälliges Bild
        this.imageResource = asteroidImages[random.nextInt(asteroidImages.length)];

    }

    public void update() {
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    //true wenn sie sich berühren
    public boolean collidesWith(Asteroid otherAsteroid) {
        float distanceX = otherAsteroid.x - this.x;
        float distanceY = otherAsteroid.y - this.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance <= (this.size+ otherAsteroid.size)/2;

    }

    //TODO könnte noch Verbessert werden
    // Berechnet die Abstoßungsreaktion zwischen diesem Asteroiden und einem anderen Asteroiden
    public void bounceOff(Asteroid otherAsteroid) {
        // Berechne die Richtung von diesem Asteroiden zum anderen Asteroiden
        float directionX = otherAsteroid.x - this.x;
        float directionY = otherAsteroid.y - this.y;

        // Berechne die Distanz zwischen den beiden Asteroiden
        float distance = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Berechne die normierten Richtungswerte
        float normalizedDirectionX = directionX / distance;
        float normalizedDirectionY = directionY / distance;

        // Berechne die neue Geschwindigkeit für diesen Asteroiden, basierend auf der Abstoßungsformel
        float newSpeedX = this.speedX - 2 * (this.speedX * normalizedDirectionX + this.speedY * normalizedDirectionY) * normalizedDirectionX;
        float newSpeedY = this.speedY - 2 * (this.speedX * normalizedDirectionX + this.speedY * normalizedDirectionY) * normalizedDirectionY;

        // Setze die neuen Geschwindigkeiten
        this.speedX = newSpeedX;
        this.speedY = newSpeedY;

    }
}

