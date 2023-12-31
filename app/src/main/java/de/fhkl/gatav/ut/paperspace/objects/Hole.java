package de.fhkl.gatav.ut.paperspace.objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import de.fhkl.gatav.ut.paperspace.R;

public class Hole extends Circle {

    public Hole(Context context, double positionX, double positionY){
        super(context, Color.BLUE,positionX, positionY);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.loch);
        setRadius(bitmap.getWidth()/6);
    }

    @Override
    public void update() {
        // NICHT BENÖTIGT
    }
}