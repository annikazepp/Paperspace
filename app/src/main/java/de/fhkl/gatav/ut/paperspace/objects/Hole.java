package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import de.fhkl.gatav.ut.paperspace.R;

public class Hole {

    private float x,y;

    private Bitmap holeBitmap;

    public Hole(Context context){
        holeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.loch);
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(holeBitmap, x, y, null);
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

}