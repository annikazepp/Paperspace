package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import de.fhkl.gatav.ut.paperspace.R;

/**
 * Asteroid
 */
public class Asteroid extends Circle {

    public static final int MIN_SPEED = 3;
    public static final int MAX_SPEED = 6;
    private int damage;

    private static final int[] ASTEROID_IMAGES ={ //TODO verschiedene Asteroiden Bilder anpassen? ZUSCHNEIDEN
            R.drawable.asteroid_1,
            R.drawable.asteroid_2,
            R.drawable.asteroid_3,
            R.drawable.asteroid_4,
            R.drawable.asteroid_5
    };

    public Asteroid(Context context, double positionX, double positionY) {
        super(context, ContextCompat.getColor(context, R.color.asteroid), positionX, positionY);
        this.velocityX = random.nextDouble() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED;
        this.velocityY = random.nextDouble() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED;

        // Zufälliges Bild Asteroid
        int imageIndex = random.nextInt(ASTEROID_IMAGES.length);
        bitmap = BitmapFactory.decodeResource(context.getResources(), ASTEROID_IMAGES[imageIndex]);

        this.damage = 1;

        setRadius(bitmap.getWidth()/2);
    }

    /**
     * Berechnet die Abstoßungsreaktion zwischen zwei Asteroiden
     * @param otherAsteroid
     */
    //TODO könnte noch Verbessert werden
    public void bounceOff(Asteroid otherAsteroid) {
        double abstandX = this.getPositionX() - otherAsteroid.getPositionX();
        double abstandY = this.getPositionY() - otherAsteroid.getPositionY();
        double abstand = Math.sqrt(abstandX * abstandX + abstandY * abstandY);

        // Berechne die Abstoßkraft basierend auf dem Abstand
        double abstoßkraft = (this.getRadius() + otherAsteroid.getRadius()) - abstand;

        // Berechne die Richtung der Abstoßkraft
        double richtungX = abstandX / abstand;
        double richtungY = abstandY / abstand;

        // Teile die Abstoßkraft auf die Kreise auf
        double abstoßkraftHalb = abstoßkraft / 2.0;
        double abstoßkraftX1 = abstoßkraftHalb * richtungX;
        double abstoßkraftY1 = abstoßkraftHalb * richtungY;
        double abstoßkraftX2 = -abstoßkraftHalb * richtungX;
        double abstoßkraftY2 = -abstoßkraftHalb * richtungY;

        // Aktualisiere die Position der Kreise entsprechend der Abstoßkraft
        this.setPosition( this.getPositionX() + abstoßkraftX1, this.getPositionY() + abstoßkraftY1);
        otherAsteroid.setPosition(otherAsteroid.getPositionX() + abstoßkraftX2, otherAsteroid.getPositionY() + abstoßkraftY2);
    }

    public void update(){ //
        // Berechnung der Richtung zum Ziel
        double dx = directionX - positionX;
        double dy = directionY - positionY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double directionX = dx / distance;
        double directionY = dy / distance;

        // Bewegung des Asteroiden in Richtung des Ziels
        positionX += directionX * velocityX;
        positionY += directionY * velocityY;
    }

    public double getDamage() {
        return damage;
    }
}



