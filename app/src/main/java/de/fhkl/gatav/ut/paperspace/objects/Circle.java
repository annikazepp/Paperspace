package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import de.fhkl.gatav.ut.paperspace.util.GameView;

/**
 * Circle ist abstrakt
 * GameObjects werden gezeichnet
 */
public abstract class Circle extends GameObject{
    protected double radius;
    protected Paint paint;

    protected Bitmap bitmap;
    protected double rX;
    public Circle(Context context, int color, double positionX, double positionY) {
        super(positionX, positionY);

        // Set colors of circle
        paint = new Paint();
        paint.setColor(color);
    }

    public void setRadius(double radius){
        this.radius = radius;
    }

    /**
     * Überprüft, ob Asteroid außerhalb des Sichtbereichs ist
     * @return true, wenn x oder y Koordinaten des Asteroiden außerhalb des Display ist
     */

        //TODO Asteroiden fliegen nach draußen und werden dann gelöscht - momentan werden sie sobald sie am rand ankommen gelöscht
        // d.h. kein Übergang -> mit code untendrunter fliegen sie raus und verschwinden dann, aber falls ein Asteroid gerade erzeugt wird
        // "steckt" sichtbarer Asteroid fest und bewegt sich nicht

    public boolean isOutOfView() {
            //return this.positionX + radius < 0 || this.positionX - radius > GameView.screenWidth || this.positionY + radius < 0 || this.positionY - radius > GameView.screenHeight;
            return this.positionX < 0 || this.positionX > GameView.screenWidth || this.positionY < 0 || this.positionY > GameView.screenHeight;
    }

    public void draw(Canvas canvas) {
        canvas.save(); // Aktueller Zustand
        canvas.rotate((float) rX,(float) positionX, (float) positionY);
        //canvas.drawCircle((float) positionX, (float) positionY, bitmap.getHeight()/2, paint);
        canvas.drawBitmap(bitmap, (float) positionX-bitmap.getWidth()/2, (float) positionY-bitmap.getHeight()/2, null);
        canvas.restore();
    }

    /**
     * isColliding checks if two circle objects are colliding, based on their positions and radii.
     * @param obj1
     * @param obj2
     * @return true, when colliding
     */
    public static boolean isColliding(Circle obj1, Circle obj2) {
        double distance = getDistanceBetweenObjects(obj1, obj2);
        double distanceToCollision = obj1.getRadius() + obj2.getRadius();
        return distance < distanceToCollision;
    }

    public double getRadius() {
        return radius;
    }

}
