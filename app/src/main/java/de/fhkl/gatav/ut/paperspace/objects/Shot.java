package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.util.GameLoop;
import de.fhkl.gatav.ut.paperspace.util.Joystick;

public class Shot extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 1150.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;

    public Shot(Context context, SpaceShip player, double xDirection, double yDirection, Joystick rotationJoystick) {
        super(
                context,
                ContextCompat.getColor(context, R.color.shot),
                player.getPositionX(),
                player.getPositionY()
        );

        double angle = Math.atan2(-rotationJoystick.getActuatorX(), rotationJoystick.getActuatorY());
        double joysticAngle = Math.toDegrees(angle);
        this.rX = joysticAngle; // Rotation wird beim erzeugen festgelegt, dann nicht mehr verändert

        velocityX = xDirection * MAX_SPEED;
        velocityY = yDirection * MAX_SPEED;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.effect_purple);
        setRadius(bitmap.getHeight() / 2);
    }

    @Override
    public void update() {
        positionX = positionX + velocityX;
        positionY = positionY + velocityY;
    }
}
