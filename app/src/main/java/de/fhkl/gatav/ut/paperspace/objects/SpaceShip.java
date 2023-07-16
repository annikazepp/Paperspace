package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.util.GameLoop;
import de.fhkl.gatav.ut.paperspace.util.GameView;
import de.fhkl.gatav.ut.paperspace.util.Joystick;

public class SpaceShip extends Circle {
    private static final double SPEED_PIXELS_PER_SECOND = 600.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;

    private final Joystick joystickSteuerung;
    private final Joystick joystickRotation;

    public SpaceShip(Context context, Joystick joystickSteuerung, Joystick joystickRotation, double positionX, double positionY) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY);
        this.joystickSteuerung = joystickSteuerung;
        this.joystickRotation = joystickRotation;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship);
        setRadius(bitmap.getHeight()/2.0);
    }

    public void update() {
        // Update velocity based on actuator of joystick
        velocityX = joystickSteuerung.getActuatorX()*MAX_SPEED;
        velocityY = joystickSteuerung.getActuatorY()*MAX_SPEED;

        // Update position
        positionX = positionX + velocityX;
        positionY = positionY + velocityY;

        // Update direction
        if (velocityX != 0 || velocityY != 0) {
            // Normalize velocity to get direction (unit vector of velocity)
            double distance = getDistanceBetweenPoints(0, 0, velocityX, velocityY);
            directionX = velocityX/distance;
            directionY = velocityY/distance;
        }

        // So wird die Richtung des Schiffs wenn Joystick nicht bewegt wird nicht zurückgesetzt
        if(joystickRotation.getActuatorX() != 0 || joystickRotation.getActuatorY() != 0) {
            double angle = Math.atan2(joystickRotation.getActuatorX(), -joystickRotation.getActuatorY());
            rX = Math.toDegrees(angle);
        }

        // Am Bildschirmrand?
        // LINKS RECHTS -> Auf anderen Seite wieder raus
        if (positionX < 0) {
            positionX = GameView.screenWidth;
        } else if (positionX > GameView.screenWidth) {
            positionX = 0;
        }
        // OBEN UNTEN -> Auf anderen Seite wieder raus
        if(positionY < 0){
            positionY = GameView.screenHeight;
        }else if( positionY > GameView.screenHeight){
            positionY = 0;
        }
    }
}
