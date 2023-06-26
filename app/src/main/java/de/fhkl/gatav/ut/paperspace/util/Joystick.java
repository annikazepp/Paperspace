package de.fhkl.gatav.ut.paperspace.util;

import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Color;

public class Joystick {

    private static Joystick joystickSteuerung = new Joystick(250,850,150,80);

    public static Joystick getJoystickSteuerung() {
        return joystickSteuerung;
    }

    private int outerCircleRadius;
    private int innerCircleRadius;
    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;
    private Paint  outerCirclePaint;
    private Paint  innerCirclePaint;
    private double joystickCenterToTouchDistance;
    private boolean isPressed = false;
    private double actuatorX;
    private double actuatorY;
    public Joystick(int centerPositionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius){
        //outer and inner circle of the joystick
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        //Radius
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        //paint
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.BLACK);
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }
    public void draw(Canvas c){
        c.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint
        );
        c.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint
        );
    }
    public void update(){
        updateInnerCirclePosition();
    }
    private void updateInnerCirclePosition(){
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX * outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY * outerCircleRadius);

        // Calculate the distance between the inner circle and the outer circle center
        double distance = Math.sqrt(Math.pow(innerCircleCenterPositionX - outerCircleCenterPositionX, 2) +
              Math.pow(innerCircleCenterPositionY - outerCircleCenterPositionY, 2));

        // If the distance exceeds the outer circle radius, normalize the position
        if (distance > outerCircleRadius) {
            double ratio = outerCircleRadius / distance;
            innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + (innerCircleCenterPositionX - outerCircleCenterPositionX) * ratio);
            innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + (innerCircleCenterPositionY - outerCircleCenterPositionY) * ratio);
        }
    }


    public boolean isPressed(double touchPositionX, double touchPositionY){
        joystickCenterToTouchDistance = Math.sqrt(
                Math.pow(outerCircleCenterPositionX - touchPositionX, 2) +
                        Math.pow(outerCircleCenterPositionY - touchPositionY, 2));
        return joystickCenterToTouchDistance < outerCircleRadius;


    }
    public void setIsPressed(boolean isPressed){
        this.isPressed = isPressed;
    }
    public boolean getIsPressed(){
        return isPressed;
    }
    public void setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Math.sqrt(Math.pow(deltaX, 2)) +
                Math.pow(deltaY, 2);

        if (deltaDistance < outerCircleRadius) {
            actuatorX = deltaX / outerCircleRadius;
            actuatorY = deltaY / outerCircleRadius;
        } else {
            actuatorX = deltaX / deltaDistance * outerCircleRadius;
            actuatorY = deltaY / deltaDistance * outerCircleRadius;
        }
    }
    public void resetActuator(){
        actuatorX = 0.0;
        actuatorY = 0.0;
    }
    public double getActuatorX(){
        return actuatorX;
    }
    public double getActuatorY(){
        return actuatorY;
    }
}