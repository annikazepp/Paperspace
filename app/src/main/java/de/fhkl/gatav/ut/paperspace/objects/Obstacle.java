package de.fhkl.gatav.ut.paperspace.objects;


import javax.microedition.khronos.opengles.GL10;

public abstract class Obstacle extends SpaceObject {

    @Override
    public abstract void draw(GL10 gl);

    @Override
    public abstract void update(float fracSec);

}
