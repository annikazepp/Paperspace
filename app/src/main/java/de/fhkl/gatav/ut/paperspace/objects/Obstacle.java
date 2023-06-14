package de.fhkl.gatav.ut.paperspace.objects;

import android.graphics.Canvas;

import javax.microedition.khronos.opengles.GL10;

public abstract class Obstacle extends SpaceObject {

    @Override
    public abstract void draw(Canvas canvas);

    @Override
    public abstract void update(float fracSec);

}
