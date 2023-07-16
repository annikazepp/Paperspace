package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.BitmapFactory;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.util.GameView;

public class DarkAsteroid extends Asteroid{

    public DarkAsteroid(Context context, double positionX, double positionY) {
        super(context, positionX, positionY);

        this.velocityX = random.nextDouble() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED;
        this.velocityY = random.nextDouble() * (MAX_SPEED - MIN_SPEED ) + MIN_SPEED;


        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dark_asteroid);
        setRadius(bitmap.getWidth()/2);
    }
    @Override
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

        // Am Bildschirmrand?
        // LINKS RECHTS -> Wird am Rand abgestoßen
        if (positionX < 0) {
            positionX = 0;
            velocityX = - velocityX;
        } else if (positionX > GameView.screenWidth) {
            positionX = GameView.screenWidth;
            velocityX = - velocityX ;
        }
        // OBEN UNTEN -> Wird am Rand abgestoßen
        if(positionY < 0){
            positionY = 0;
            velocityY = -velocityY;
        }else if( positionY > GameView.screenHeight){
            positionY = GameView.screenHeight;
            velocityY = - velocityY;
        }
    }

}
