package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import de.fhkl.gatav.ut.paperspace.R;

public class Shot {
    Bitmap shotBitmap;
    float x,y;
    public Shot(Context context, float x, float y){
        shotBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.effect_purple);
        this.x = x;
        this.y = y;

    }

    public Bitmap getShotBitmap(){
        return shotBitmap;
    }

    public void draw(Canvas c) {
        c.drawBitmap(shotBitmap, x,y,null);
    }
}
